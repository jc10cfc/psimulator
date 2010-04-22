/*
 * Doresit:
 * staticky NAT - jen rucne pridana pravidla
 * natovani z internetu - kontrola kdy natovat (neni nastaven pool atd..)
 * doresit metody pro linux
 *
 *
 */
package datoveStruktury;

import datoveStruktury.NATPool.Pool;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.SitoveRozhrani;

/**
 * Datova struktura pro NATovaci tabulku. <br />
 * Pri natovani zavolat mamNatovat(), kdyz to vrati 0, tak zavolat metodu zanatuj(). <br />
 * Pri odnatovani zavolat mamOdnatovat(), kdyz to vrati true, tak zavolat metodu odnatuj().
 * @author haldyr
 */
public class NATtabulka {

    AbstraktniPocitac pc; //odkaz na pocitac, kterymu tabulka prislusi

    List<NATzaznam> tabulka;
    /**
     * seznam poolu IP.
     */
    public NATPool natSeznamPoolu;
    /**
     * seznam seznamAccess-listu
     * (= kdyz zdrojova IP patri do nejakeho seznamAccess-listu, tak se bude zrovna natovat)
     */
    public NATAccessList natSeznamAccess;
    /**
     * seznam prirazenych poolu k access-listum
     */
    public NATPoolAccess natSeznamPoolAccess;
    /**
     * Seznam soukromych (inside) rozhrani.
     */
    List<SitoveRozhrani> inside;
    /**
     * Verejne (outside) rozhrani.
     */
    SitoveRozhrani verejne;
    
    /**
     * citac, odkud mam rozdavat porty
     */
    private int citacPortu = 1025;

    boolean debug = true;

    public NATtabulka(AbstraktniPocitac pc) {
        this.pc=pc;
        tabulka = new ArrayList<NATzaznam>();
        inside = new ArrayList<SitoveRozhrani>();
        natSeznamAccess = new NATAccessList(this);
        natSeznamPoolu = new NATPool(this);
        natSeznamPoolAccess = new NATPoolAccess(this);
    }

    /**
     * True, pokud je uz tam je zdrojova ip v tabulce.
     * @param in
     * @return
     */
    private boolean jeTamZdrojova(IpAdresa in) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.in.jeStejnaAdresa(in) && zaznam.in.port == in.port) {
                return true;
            }
        }
        return false;
    }

    /**
     * True, pokud je uz tam je prelozena ip v tabulce.
     * @param out
     * @return
     */
    private boolean jeTamPrelozena(IpAdresa out) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.out.jeStejnaAdresa(out) && zaznam.out.port == out.port) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vrati pozici pro pridani do tabulky.
     * Radi se to dle out adresy vzestupne.
     * @param out
     * @return index noveho zaznamu
     */
    private int dejIndexVTabulce(IpAdresa out) {
        int index = 0;
        for (NATzaznam zaznam : tabulka) {
            if (out.dejLongIP() > zaznam.out.dejLongIP()) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * Reprezentuje jeden radek v NAT tabulce.
     */
    public class NATzaznam {
        
        IpAdresa in;
        IpAdresa out;
        boolean staticke;

        public NATzaznam(IpAdresa in, IpAdresa out, boolean staticke) {
            this.in = in;
            this.out = out;
            this.staticke = staticke;
        }
    }

    /**
     * Vrati verejne rozhrani.
     * @return
     */
    public SitoveRozhrani vratVerejne() {
        return verejne;
    }

    /**
     * Vrati seznam inside rozhrani.
     * @return
     */
    public List<SitoveRozhrani> vratInside() {
        return inside;
    }

    /**
     * Vrati paket se prelozenou zdrojovou IP adresou.
     * @param paket
     * @return
     */
    public Paket zanatuj(Paket paket) {
        paket.zdroj = zanatujZdrojovouIpAdresu(paket.zdroj, true);
        return paket;
    }

    /**
     * Pokud je zaznam pro cilovou adresu v NAT tabulce, tak se to prelozi na spravnou soukromou.
     * Jinak se vrati paket zpet nezmenen.
     * @param paket
     * @return
     */
    public Paket odnatuj(Paket paket) {
        IpAdresa prelozena = odnatujZdrojovouIpAdresu(paket.cil);
        if (debug) pc.vypis("XXXXXX puvodni paket:   "+paket);
        if (prelozena == null) {
            return paket;
        }
        paket.cil = prelozena;
        if (debug) pc.vypis("XXXXXX prelozeny paket: "+paket);
        return paket;
    }



    /**
     * Dle teto metody se bude pocitac rozhodovat, co delat s paketem.
     * Nevola se hned zanatuj, pac musime rozlisovat, kdy natovat, kdy nenatovat a kdy vratit Destination Host Unreachable.
     * @param zdroj
     * @return 0 - ano natovat se bude <br />
     *         1 - ne, nemam pool - vrat zpatky Destination Host Unreachable <br />
     *         2 - ne, dosli IP adresy z poolu - vrat zpatky Destination Host Unreachable
     *         3 - ne, vstupni neni soukrome nebo vystupni neni verejne <br />
     *         4 - ne, zdrojova Ip neni v seznamAccess-listech, tak nechat normalne projit bez natovani <br />
     *         5 - ne, neni nastaveno outside rozhrani
     */
    public int mamNatovat(IpAdresa zdroj, SitoveRozhrani vstupni, SitoveRozhrani vystupni) {

        boolean vstupniJeInside = false;
        for (SitoveRozhrani iface : inside) {
            if (iface.jmeno.equals(vstupni.jmeno)) {
                vstupniJeInside = true;
            }
        }
        if (verejne == null) {
            return 5;
        }
        if ((!vystupni.jmeno.equals(verejne.jmeno)) || vstupniJeInside == false) {
            return 3;
        }
        //-----------------------------------------------------------------------------

        // neni v access-listech
        NATAccessList.AccessList acc = natSeznamAccess.vratAccessListIP(zdroj);
        if (acc == null) {
            return 4;
        }

        // kdyz neni prirazen pool
        Pool pool = natSeznamPoolu.vratPoolZAccessListu(acc);
        if (pool == null) {
            return 1;
        }

        IpAdresa adr = pool.dejIp(true);
        if (adr == null) {
            return 2;
        }

        return 0;
    }

    /**
     * Kontrola, jestli paket prisel do pocitace z verejne site.
     * @param prichoziRozhrani
     * @return true - paket z verejne site <br />
     *         false - paket odjinud - ne-odnatovavat nebo kdyz neni nastavene outside rozhrani
     */
    public boolean mamOdnatovat(SitoveRozhrani prichoziRozhrani) {
        if (verejne == null) {
            if (debug) pc.vypis("Verejne je null; prichozi rozhrani: "+prichoziRozhrani.jmeno+" pc:"+pc.jmeno);
            return false;
        }
        if (prichoziRozhrani.jmeno.equals(verejne.jmeno)) {
            if (debug) pc.vypis("prichozi rozhrani '"+prichoziRozhrani.jmeno+"' je verejne, natuji; verejne je "+verejne.jmeno);
            if (debug) pc.vypis("mamOdnatovat vraci true");
            return true;
        }
        return false;
    }

    /**
     * Vrati IpAdresu, ktera se pouzije jako zdrojova pri odeslani paketu.
     * Nejdriv se projde natovaci tabulka, jestli to tam uz neni. Kdyz neni,
     * tak se zkusi vygenerovat novy zaznam. Musi mit ale spravne nakonfigurovany access-listy+pooly.
     * Tato metoda se vola, kdyz uz vim, ze ma prirazeny pool+access-list, tak uz to ma vzdycky vratit prelozenou adresu.
     * @param ip
     * @param natovani - true, kdyz natuju, false - kdyz se jen ptam, zda je volno v poolu
     * @return Adresu - na kterou se to ma prelozit <br />
     *         null - kdyz dosel pool IP adress, tak se ma vratit odesilateli Destination Host Unreachable,
     *                null by to melo vratit pouze pri natovani==false nebo kdyz neni zadnej pouzitelnej pool
     */
    private IpAdresa zanatujZdrojovouIpAdresu(IpAdresa ip, boolean natovani) {
        // nejdriv kontroluju, jestli uz to nahodou nema zaznam v NATtabulce
        for (NATzaznam zaznam : tabulka) { // porovnavam i podle portu (mohou byt NATy za sebou..)
            if (zaznam.in.jeStejnaAdresa(ip) && zaznam.in.port == ip.port) {
                return zaznam.out;
            }
        }

        NATAccessList.AccessList access = natSeznamAccess.vratAccessListIP(ip);
        Pool pool = natSeznamPoolu.vratPoolZAccessListu(access);
        IpAdresa vrat = natSeznamPoolu.dejIpZPoolu(pool);

        vrat.port = citacPortu++; // velikost integeru je dostacujici, tak neresim, ze se porad navysuje jedno pocitadlo
        if (natovani == true) { // jen kdyz opravdu pridavam
            pridejZaznamDoNATtabulky(ip, vrat);
        } else { // kdyz jen testuju, tak si vratim citac zpatky
            citacPortu--;
        }
        return vrat;
    }

    /**
     * Mrkne se do tabulky a vrati prislusny zaznam pokud existuje.
     * @param ip
     * @return null - pokud neexistuje zaznam pro danou ip
     */
    private IpAdresa odnatujZdrojovouIpAdresu(IpAdresa ip) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.out.jeStejnaAdresa(ip) && zaznam.out.port == ip.port) {
                return zaznam.in;
            }
        }
        return null;
    }

    /**
     * Prida staticke pravidlo do tabulky.
     * @param in zdrojova IP urcena pro preklad
     * @param out nova (prelozena) adresa
     * @return 0 - ok, zaznam uspesne pridan <br />
     *         1 - chyba, in adresa tam uz je (% in already mapped (in -> out))
     *         2 - chyba, out adresa tam uz je (% similar static entry (in -> out) already exists)
     */
    public int pridejStatickePravidloCisco(IpAdresa in, IpAdresa out) {

        if(jeTamZdrojova(in)) {
            return 1;
        }
        if(jeTamPrelozena(out)) {
            return 2;
        }

        int index = dejIndexVTabulce(out);
        tabulka.add(index, new NATzaznam(in, out, true));

        return 0;
    }

    /**
     * Prida staticke pravidlo do NAT tabulky. Nic se nekontroluje.
     * @param in zdrojova IP
     * @param out nova zdrojova (prelozena)
     */
    public void pridejStatickePravidloLinux(IpAdresa in, IpAdresa out) {
        tabulka.add(new NATzaznam(in, out, true));
    }

    /**
     * Prida zaznam do natovaci tabulky. Pouziva se to pri dynamickym natovani.
     * Nic se nekontroluje.
     * @param in zdrojova IP
     * @param out nova zdrojova (prelozena)
     */
    private void pridejZaznamDoNATtabulky(IpAdresa in, IpAdresa out) {
        tabulka.add(new NATzaznam(in, out, false));
    }

    /****************************************** nastavovani rozhrani ***************************************************/

    /**
     * Prida inside rozhrani. <br />
     * Neprida se pokud uz tam je rozhrani se stejnym jmenem. <br />
     * Pro pouziti prikazu 'ip nat inside'.
     * @param iface
     */
    public void pridejRozhraniInside(SitoveRozhrani iface) {
        for (SitoveRozhrani sr : inside) { // nepridavam uz pridane
            if (sr.jmeno.equals(iface.jmeno)) {
                return;
            }
        }
        inside.add(iface);
    }

   /**
    * Nastavi outside rozhrani.
    * @param iface
    */
    public void nastavRozhraniOutside(SitoveRozhrani iface) {
        natSeznamPoolu.updateIpNaRozhrani();
        verejne = iface;
    }

    /**
     * Smaze toto rozhrani z inside listu.
     * @param iface
     */
    public void smazRozhraniInside(SitoveRozhrani iface) {
        SitoveRozhrani smazat = null;
        for (SitoveRozhrani sr : inside) {
            if (sr.jmeno.equals(iface.jmeno)) {
                smazat = sr;
            }
        }
        if (smazat != null) {
            inside.remove(smazat);
        }
    }

    public void smazRozhraniOutside() {
        verejne.smazVsechnyIpKromPrvni();
        verejne = null;
    }

    /****************************************** Linux *********************************************************/

    /**
     * Nastavi Linux pocitac pro natovani.
     * Pocitam s tim, ze ani pc ani rozhrani neni null.
     * Jestli jsem to dobre pochopil, tak tohle je ten zpusob natovani, kdy se vsechny pakety jdouci
     * ven po nejakym rozhrani prekladaj na nejakou verejnou adresu, a z toho rozhrani zase zpatky.
     * Prikaz napr: "iptables -t nat -I POSTROUTING -o eth2 -j MASQUERADE" - vsechny pakety jdouci ven
     * po rozhrani eth2 se prekladaj.
     * @param pc
     * @param verejne, urci ze je tohle rozhrani verejne a ostatni jsou automaticky soukroma.
     */
    public void nastavLinuxNAT(SitoveRozhrani verejne) {

        // nastaveni rozhrani
        inside.clear();
        for (SitoveRozhrani iface : pc.rozhrani) {
            if (iface.jmeno.equals(verejne.jmeno)) {
                continue; // preskakuju verejny
            }
            // vsechny ostatni nastrkam do inside
            pridejRozhraniInside(iface);
        }
        nastavRozhraniOutside(verejne);

        // osefovani access-listu
        int cislo = 1;
        natSeznamAccess.smazAccessListyVsechny();
        natSeznamAccess.pridejAccessList(new IpAdresa("0.0.0.0", 0), cislo);

        // osefovani IP poolu
        String pool = "ovrld";
        natSeznamPoolu.smazPoolVsechny();
        natSeznamPoolu.pridejPool(verejne.vratPrvni(), verejne.vratPrvni(), 24, pool);
        
        natSeznamPoolAccess.smazPoolAccessVsechny();
        natSeznamPoolAccess.pridejPoolAccess(cislo, pool, true);
    }
}
