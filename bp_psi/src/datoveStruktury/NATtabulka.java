/*
 * Doresit:
 * staticky NAT - jen rucne pridana pravidla
 * natovani z internetu - kontrola kdy natovat (neni nastaven pool atd..)
 */
package datoveStruktury;

import java.util.ArrayList;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.SitoveRozhrani;

/**
 * Datova struktura pro NATovaci tabulku.
 * @author haldyr
 */
public class NATtabulka {

    List<NATzaznam> tabulka;
    /**
     * Seznam soukromych (inside) rozhrani.
     */
    List<SitoveRozhrani> inside;
    /**
     * Verejne (outside) rozhrani.
     */
    SitoveRozhrani verejne;
    /**
     * true, kdyz se vse preklada na 1 IP; false, kdyz se vybira IP z poolu
     */
    public boolean overload;
    /**
     * seznam seznamAccess-listu
     * (= kdyz zdrojova IP patri do nejakeho seznamAccess-listu, tak se bude zrovna natovat)
     */
    List<AccessList> seznamAccess;
    /**
     * cislo aktivniho pristupoveho listu, dle ktereho se kontroluje pristup
     */
    public int cisloAccess;
    /**
     * citac, odkud mam rozdavat porty
     */
    private int citacPortu = 1025;
    /**
     * Zde si drzim odkaz na vsechny pridane pooly.
     */
    public List<PoolList> seznamPoolu;
    /**
     * Aktualne nastaveny pool, kdyz je null, tak to znamena, ze neni
     */
    public PoolList aktualniPool = null;
    /**
     * Jmeno aktualniho poolu, kdyz je nastaven neexistujici pool, tak se sem ulozi ten binec, jaky zadal uzivatel.
     */
    public String aktualniPoolJmeno = "";

    public NATtabulka() {
        tabulka = new ArrayList<NATzaznam>();
        inside = new ArrayList<SitoveRozhrani>();
        seznamAccess = new ArrayList<AccessList>();
        seznamPoolu = new ArrayList<PoolList>();
        overload = false;
        cisloAccess = -1;
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
     * Trida reprezentujici jeden seznamAccess-list.
     */
    public class AccessList {

        IpAdresa ip;
        int cislo;

        public AccessList(IpAdresa ip, int cislo) {
            this.ip = ip;
            this.cislo = cislo;
        }
    }

    public class PoolList {

        String jmeno = "";
        List<IpAdresa> pool;
        /**
         * Ukazuje na prvni volnou IpAdresu z poolu.
         */
        IpAdresa ukazatel = null;

        public PoolList() {
            pool = new ArrayList<IpAdresa>();
        }

        /**
         * Vrati prvni IpAdresu z poolu nebo null, kdyz je pool prazdny.
         * @return
         */
        public IpAdresa prvni() {
            if (pool.size() == 0) {
                return null;
            }
            return pool.get(0);
        }

        /**
         * Vrati dalsi IpAdresu z poolu. Kdyz uz jsem na posledni, tak vracim null (DHU).
         * @return
         */
        private IpAdresa dalsi() {
            int n = -1;
            for (IpAdresa ip : pool) {
                n++;
                if (ip.jeStejnaAdresa(ukazatel)) {
                    break; // n = index ukazatele
                }
            }
            if (n + 1 == pool.size()) {
                return null;
            }
            return pool.get(n + 1);
        }

        /**
         * Vrati dalsi IP z poolu nebo null, pokud uz neni dalsi IP.
         * @return
         */
        private IpAdresa dejIp() {
            IpAdresa vrat = ukazatel;
            ukazatel = dalsi();
            return vrat;
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

        if (aktualniPool == null) {
            return 1;
        }

        if (doslyIpPoolu(zdroj)) {
            return 2;
        }

        if (verejne == null) {
            return 5;
        }

        if ((!vystupni.jmeno.equals(verejne.jmeno)) || vstupniJeInside == false) {
            return 3;
        }

        if (!jeVAccessListu(zdroj)) {
            return 4;
        }        

        return 0;
    }

    /**
     * Vrati IpAdresu, ktera se pouzije jako zdrojova pri odeslani paketu.
     * @param ip
     * @param natovani - true, kdyz natuju, false - kdyz se jen ptam, zda je volno v poolu
     * @return Adresu - na kterou se to ma prelozit <br />
     *         null - kdyz dosel pool IP adress, tak se ma vratit odesilateli Destination Host Unreachable,
     *                null by to melo vratit pouze pri natovani==false
     */
    private IpAdresa zanatujZdrojovouIpAdresu(IpAdresa ip, boolean natovani) {
        for (NATzaznam zaznam : tabulka) { // porovnavam i podle portu (mohou byt NATy za sebou..)
            if (zaznam.in.jeStejnaAdresa(ip) && zaznam.in.port == ip.port) {
                return zaznam.out;
            }
        }

        // nenasel jsem stejnej zaznam, tak vygenerujem novy zaznam do tabulky
        IpAdresa vrat = dejIpZPoolu();
        vrat.port = citacPortu++;
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

    /****************************************** seznamAccess-list ***************************************************/

    /**
     * Prida do seznamAccess-listu dalsi pravidlo. <br />
     * Pocitam s tim, ze ani jedno neni null.
     * @param adresa
     */
    public void pridejAccessList(IpAdresa adresa, int cislo) {
        for (AccessList zaznam : seznamAccess) {
            if (zaznam.ip.equals(adresa)) { // kdyz uz tam je, tak nic nedelat
                return;
            }
        }
        seznamAccess.add(new AccessList(adresa, cislo));
    }

    /**
     * Smaze vsechny seznamAccess-listy s danym cislem.
     * @param cislo
     */
    public void smazAccessList(int cislo) {
        List<AccessList> smazat = new ArrayList<AccessList>();
        for (AccessList zaznam : seznamAccess) {
            if (zaznam.cislo == cislo) {
                smazat.add(zaznam);
            }
        }

        for (AccessList zaznam : smazat) {
            seznamAccess.remove(zaznam);
        }
    }

    /**
     * Zkontroluje seznamAccess-listy, zda se ma zdrojova IP natovat.
     * @param zdroj
     * @return true, kdyz ano (je v uvedena v seznamAccess-listech)
     *         false, kdyz ne (nespada do rozsahu zadneho seznamAccess-listu)
     */
    private boolean jeVAccessListu(IpAdresa zdroj) {
        for (AccessList zaznam : seznamAccess) {
            if (zaznam.cislo == cisloAccess && zdroj.jeVRozsahu(zaznam.ip)) {
                return true;
            }
        }
        return false;
    }

    /****************************************** IP pool *********************************************************/
    
    /**
     * Prida pool.
     * @param start
     * @param konec
     * @param prefix
     * @param jmeno, neni null ani ""
     * @return 0 - ok nastavi pool <br />
     *         1 - kdyz je prvni IP vetsi nez druha IP (%End address less than start address) <br />
     *         2 - pool s timto jmenem je prave pouzivan, tak nic. (%Pool ovrld in use, cannot redefine) <br />
     *         3 - kdyz je spatna maska (% Invalid input detected) <br />
     *         4 - kdyz je start a konec v jine siti (%Start and end addresses on different subnets)
     */
    public int pridejPool(IpAdresa start, IpAdresa konec, int prefix, String jmeno) {
        if (start.dejLongIP() > konec.dejLongIP()) {
            return 1;
        }

        if (aktualniPool.jmeno.equals(jmeno)) {
            return 2;
        }

        if (prefix > 32 || prefix < 1) {
            return 3;
        }

        start.nastavMasku(prefix);

        if (!konec.jeVRozsahu(start)) {
            return 4;
        }

        // smaznout stejne se jmenujici pool
        smazPool(jmeno);

        // tady pridej pool
        PoolList novyPool = new PoolList();
        novyPool.jmeno = jmeno;

        // pridavam IP adresy do poolu.
        IpAdresa ukazatel = start;
        do {
            novyPool.pool.add(ukazatel);
            ukazatel = IpAdresa.vratOJednaVetsi(ukazatel);
            
        } while (ukazatel.dejLongIP() < konec.dejLongIP() && ukazatel.jeVRozsahu(start));

        return 0;
    }

    /**
     * Najde pool se stejnym jmenem a nastavi ho jako aktualni. Dale nasype tyto adresy na rozhrani<br />
     * Dale nastavi aktualniPoolJmeno, kdyz zadal uzivatel nejaky neexistujici pool, tak se sem ulozi ten nesmysl.
     * (= je to jen pro vypis pres 'show running-config' <br />
     * Kdyz s takovym jmenem zadny nenajde, tak nastavi null a pak se nemuze natovat a vraci se Destination Host Unreachable
     * @param jmeno
     */
    public void nastavAktivniPool(String jmeno) {
        for (PoolList pool : seznamPoolu) {
            if (pool.jmeno.equals(jmeno)) {
                aktualniPool = pool;
                aktualniPoolJmeno = pool.jmeno;

                if (verejne.seznamAdres.size() != 0) { // smazu vsechny ostatni krom prvni nastavene
                    verejne.seznamAdres = verejne.seznamAdres.subList(0, 1); 
                }
                for (IpAdresa ip : pool.pool) {
                    verejne.seznamAdres.add(ip);
                }

                return;
            }
        }
        aktualniPool = null;
        aktualniPoolJmeno = jmeno;
    }

    /**
     * Smaze pool podle jmena.
     * @param jmeno
     * @return 0 - ok smazal se takovy pool <br />
     *         1 - pool s takovym jmenem neni. (%Pool jmeno not found)
     */
    public int smazPool(String jmeno) {
        PoolList smaznout = null;
        for (PoolList pool : seznamPoolu) {
            if (pool.jmeno.equals(jmeno)) {
                smaznout = pool;
            }
        }
        if (smaznout == null) {
            return 1;
        }
        seznamPoolu.remove(smaznout);
        return 0;
    }

    /**
     * Vrati pro overload prvni IP z poolu, jinak dalsi volnou IP. Pri testovani vrati null,
     * kdyz uz neni volna IP v poolu. TO je ale osetreno metodou mamNAtovat(), tak uz pri
     * samotnem natovani by to null nikdy vracet nemelo.
     * @return
     */
    private IpAdresa dejIpZPoolu() {
        if (overload) {
            return aktualniPool.prvni();
        } else {
            return aktualniPool.dejIp();
        }
    }

    /**
     * Zjistuje, zda dosli IP v poolu. Pri overloadu to nenastane nikdy (vrati false)
     * @param ip
     * @return true - IP v poolu jsou vycerpany <br />
     *         false - jeste tam jsou volne IP
     */
    private boolean doslyIpPoolu(IpAdresa ip) {
        if (overload) {
            return false;
        }
        if (zanatujZdrojovouIpAdresu(ip, false) == null) {
            return true;
        }
        return false;
    }

    /****************************************** Linux *********************************************************/

    /**
     * Nastavi Linux pocitac pro natovani.
     * Pocitam s tim, ze ani pc ani rozhrani neni null.
     * @param pc
     * @param verejne, urci ze je tohle rozhrani verejne a ostatni jsou automaticky soukroma.
     */
    public void nastavLinuxNAT(AbstractPocitac pc, SitoveRozhrani verejne) {

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
        seznamAccess.clear();
        int cislo = 1;
        pridejAccessList(new IpAdresa("0.0.0.0", 0), cislo);
        cisloAccess = cislo;

        // osefovani IP poolu
        overload = true;
        seznamPoolu.clear();
        String jmenoPoolu = "pool";
        pridejPool(verejne.vratPrvni(), verejne.vratPrvni(), 24, jmenoPoolu);
        nastavAktivniPool(jmenoPoolu);
    }
}
