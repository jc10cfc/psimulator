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
 * Datova struktura pro NATovaci tabulku.
 * @author haldyr
 */
public class NATtabulka {

    List<NATzaznam> tabulka;
    /**
     * seznam poolu IP.
     */
    public NATPool NATseznamPoolu;
    /**
     * seznam seznamAccess-listu
     * (= kdyz zdrojova IP patri do nejakeho seznamAccess-listu, tak se bude zrovna natovat)
     */
    public NATAccessList NATseznamAccess;
    /**
     * seznam prirazenych poolu k access-listum
     */
    public NATPoolAccess NATseznamPoolAccess;
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

    public NATtabulka() {
        tabulka = new ArrayList<NATzaznam>();
        inside = new ArrayList<SitoveRozhrani>();
        NATseznamAccess = new NATAccessList(this);
        NATseznamPoolu = new NATPool(this);
        NATseznamPoolAccess = new NATPoolAccess(this);
    }

    /**
     * Reprezentuje jeden radek v NAT tabulce.
     */
    public class NATzaznam {

        IpAdresa in;
        IpAdresa out;

        public NATzaznam(IpAdresa in, IpAdresa out) {
            this.in = in;
            this.out = out;
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
        if (prelozena == null) {
            return paket;
        }
        paket.cil = prelozena;
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
        NATAccessList.AccessList acc = NATseznamAccess.vratAccessListIP(zdroj);
        if (acc == null) {
            return 4;
        }

        // kdyz neni prirazen pool
        Pool pool = NATseznamPoolu.vratPoolZAccessListu(acc);
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

        NATAccessList.AccessList access = NATseznamAccess.vratAccessListIP(ip);
        Pool pool = NATseznamPoolu.vratPoolZAccessListu(access);
        IpAdresa vrat = NATseznamPoolu.dejIpZPoolu(pool);

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
                return zaznam.out;
            }
        }
        return null;
    }

    /**
     * Prida zaznam do natovaci tabulku. Nic se nekontroluje.
     * @param in zdrojova IP
     * @param out nova zdrojova (prelozena)
     */
    private void pridejZaznamDoNATtabulky(IpAdresa in, IpAdresa out) {
        tabulka.add(new NATzaznam(in, out));
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
        verejne = null;
    }

    /****************************************** Linux *********************************************************/

    /**
     * Nastavi Linux pocitac pro natovani.
     * Pocitam s tim, ze ani pc ani rozhrani neni null.
     * @param pc
     * @param verejne, urci ze je tohle rozhrani verejne a ostatni jsou automaticky soukroma.
     */
    public void nastavLinuxNAT(AbstraktniPocitac pc, SitoveRozhrani verejne) {

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
        NATseznamAccess.smazAccessListyVsechny();
        NATseznamAccess.pridejAccessList(new IpAdresa("0.0.0.0", 0), cislo);

        // osefovani IP poolu
        String pool = "ovrld";
        NATseznamPoolu.smazPoolVsechny();
        NATseznamPoolu.pridejPool(verejne.vratPrvni(), verejne.vratPrvni(), 24, pool);
        
        NATseznamPoolAccess.smazPoolAccessVsechny();
        NATseznamPoolAccess.pridejPoolAccess(cislo, pool, true);
    }
}
