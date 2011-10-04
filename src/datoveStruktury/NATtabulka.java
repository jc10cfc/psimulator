/*
 * Hotovo:
 * staticky NAT - jen rucne pridana pravidla
 * Co delat, kdyz uz jsou dynamicke zaznamy v tabulce a uzivatel zrusi pooly a accesslisty, mam odnatovat?
 *              - zaznamy se po 10s smazou a pak uz to nepujde, tak asi netreba resit
 * natovani z internetu - kontrola kdy natovat (neni nastaven pool atd..)
 * doresit metody pro linux
 *
 * 
 *
 */
package datoveStruktury;

import datoveStruktury.NATAccessList.AccessList;
import datoveStruktury.NATPool.Pool;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.SitoveRozhrani;
import static pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz.*;

/**
 * Datova struktura pro NATovaci tabulku. <br />
 * Pri natovani zavolat mamNatovat(), kdyz to vrati 0, tak zavolat metodu zanatuj(). <br />
 * Pri odnatovani zavolat mamOdnatovat(), kdyz to vrati true, tak zavolat metodu odnatuj().
 * @author Stanislav Řehák
 */
public class NATtabulka {

    AbstraktniPocitac pc; //odkaz na pocitac, kterymu tabulka prislusi
    List<NATzaznam> tabulka;
    /**
     * seznam poolu IP.
     */
    public NATPool lPool;
    /**
     * seznam seznamAccess-listu
     * (= kdyz zdrojova IP patri do nejakeho seznamAccess-listu, tak se bude zrovna natovat)
     */
    public NATAccessList lAccess;
    /**
     * seznam prirazenych poolu k access-listum
     */
    public NATPoolAccess lPoolAccess;
    /**
     * Seznam soukromych (inside) rozhrani.
     */
    List<SitoveRozhrani> inside;
    /**
     * Verejne (outside) rozhrani.
     */
    SitoveRozhrani verejne;
    private boolean linux_nastavena_maskarada = false;
    /**
     * citac, odkud mam rozdavat porty
     */
//    private int citacPortu = 1025;
    boolean debug = false;


    public NATtabulka(AbstraktniPocitac pc) {
        this.pc = pc;
        tabulka = new ArrayList<NATzaznam>();
        inside = new ArrayList<SitoveRozhrani>();
        lAccess = new NATAccessList(this);
        lPool = new NATPool(this);
        lPoolAccess = new NATPoolAccess(this);
    }

    /**
     * True, pokud je uz tam je zdrojova ip v tabulce.
     * @param in adresa, kterou chceme porovnavat
     * @param staticke udava, jestli se ma hledat jen mezi statickymi (true) nebo dynamickymi (false)
     * @return
     */
    private boolean jeTamZdrojova(IpAdresa in, boolean staticke) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke == staticke && zaznam.in.jeStejnaAdresaSPortem(in)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True, pokud je uz tam je prelozena ip v tabulce.
     * @param out adresa, kterou chceme porovnavat
     * @param staticke udava, jestli se ma hledat jen mezi statickymi (true) nebo dynamickymi (false)
     * @return
     */
    private boolean jeTamPrelozena(IpAdresa out, boolean staticke) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke == staticke && zaznam.out.jeStejnaAdresaSPortem(out)) {
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
            if (out.dejLongIP() < zaznam.out.dejLongIP()) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * Hleda mezi statickymi pravidly, jestli tam je zaznam pro danou IP.
     * @param zdroj
     * @return zanatovana IP <br />
     *         null pokud nic nenaslo
     */
    public IpAdresa najdiStatickePravidloIn(IpAdresa zdroj) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke && zaznam.in.jeStejnaAdresa(zdroj)) {
                return zaznam.out;
            }
        }
        return null;
    }

    /**
     * Hleda mezi statickymi pravidly, jestli tam je zaznam pro danou IP.
     * @param zdroj
     * @return odnatovana IP <br />
     *         null pokud nic nenaslo
     */
    public IpAdresa najdiStatickePravidloOut(IpAdresa zdroj) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke && zaznam.out.jeStejnaAdresa(zdroj)) {
                return zaznam.in;
            }
        }
        return null;
    }

    /**
     * Vrati true, pokud muze odnatovat adr pomoci statickeho nebo dynamickeho pravidla.
     * Jinak false.
     * @param adr
     * @return
     */
    public boolean mamZaznamOutProIp(IpAdresa adr) {
        IpAdresa ip = odnatujZdrojovouIpAdresu(adr);
        if (ip == null) {
            return false;
        }
        return true;
    }

    /**
     * Reprezentuje jeden radek v NAT tabulce.
     */
    public class NATzaznam {

        /**
         * Zdrojova ip.
         */
        IpAdresa in;
        /**
         * Zdrojova prelozena ip.
         */
        IpAdresa out;
        /**
         * Potreba pro vypis v ciscu. Je null, kdyz se vkladaji staticka pravidla.
         */
        IpAdresa cil;
        /**
         * Vlozeno staticky - true, dynamicky - false.
         */
        boolean staticke;
        /**
         * Cas vlozeni v ms (pocet ms od January 1, 1970)
         */
        long cas;

        public NATzaznam(IpAdresa in, IpAdresa out, boolean staticke) {
            this.in = in;
            this.out = out;
            this.cil = null;
            this.staticke = staticke;
            this.cas = System.currentTimeMillis();
        }

        public NATzaznam(IpAdresa in, IpAdresa out, IpAdresa cil, boolean staticke) {
            this.in = in;
            this.out = out;
            this.cil = cil;
            this.staticke = staticke;
            this.cas = System.currentTimeMillis();
        }

        public IpAdresa vratIn() {
            return in;
        }

        public IpAdresa vratOut() {
            return out;
        }

        public long vratCas() {
            return cas;
        }

        public boolean jeStaticke() {
            return staticke;
        }

        /**
         * Obnovi zaznam na dalsich 10s.
         */
        public void touch() {
            this.cas = System.currentTimeMillis();
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
     * Vrati tabulku.
     * @return
     */
    public List<NATzaznam> vratTabulku() {
        return tabulka;
    }

    /**
     * Vrati paket se prelozenou zdrojovou IP adresou.
     * @param paket
     * @return
     */
    public Paket zanatuj(Paket paket) {
        paket.zdroj = zanatujZdrojovouIpAdresu(paket, true);
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
        if (debug) {
            pc.vypis("puvodni paket:   " + paket);
        }
        if (prelozena == null) {
            return paket;
        }
        paket.cil = prelozena;
        if (debug) {
            pc.vypis("prelozeny paket: " + paket);
        }
        smazStareDynamickeZaznamy();
        return paket;
    }

    /**
     * Vrati true, pokud je mozne danou adresu prelozit. Jinak vrati false.
     * @param adr
     * @return true - musi byt adr v access-listu, k nemu priraznem pool s alespon 1 volnou IP adresou. <br />
     */
    public boolean lzePrelozit(IpAdresa adr) {

        AccessList access = lAccess.vratAccessListIP(adr);
        if (access == null) {
            return false;
        }

        Pool pool = lPool.vratPoolZAccessListu(access);
        if (pool == null) {
            return false;
        }

        IpAdresa nova = pool.dejIp(true);
        if (nova == null) {
            return false;
        }

        return true;
    }

    /**
     * Dle teto metody se bude pocitac rozhodovat, co delat s paketem.
     * Nevola se hned zanatuj, pac musime rozlisovat, kdy natovat, kdy nenatovat a kdy vratit Destination Host Unreachable.
     * @param zdroj
     * @return 0 - ano natovat se bude <br />
     *         1 - ne, nemam pool - vrat zpatky Destination Host Unreachable <br />
     *         2 - ne, dosli IP adresy z poolu - vrat zpatky Destination Host Unreachable
     *         3 - ne, vstupni neni soukrome nebo vystupni neni verejne <br />
     *         4 - ne, zdrojova Ip neni v seznamu access-listu, tak nechat normalne projit bez natovani <br />
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

        if (najdiStatickePravidloIn(zdroj) != null) {
            return 0;
        }

        // neni v access-listech
        NATAccessList.AccessList acc = lAccess.vratAccessListIP(zdroj);
        if (acc == null) {
            return 4;
        }

        // kdyz neni prirazen pool
        Pool pool = lPool.vratPoolZAccessListu(acc);
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
            if (debug) {
                pc.vypis("Verejne je null; prichozi rozhrani: " + prichoziRozhrani.jmeno + " pc:" + pc.jmeno);
            }
            return false;
        }
        if (prichoziRozhrani.jmeno.equals(verejne.jmeno)) {
            if (debug) {
                pc.vypis("prichozi rozhrani '" + prichoziRozhrani.jmeno + "' je verejne, natuji; verejne je " + verejne.jmeno);
            }
            return true;
        }
        if (debug) {
            pc.vypis("prichozi rozhrani '" + prichoziRozhrani.jmeno + "', nenatuji; verejne je " + verejne.jmeno);
        }
        return false;
    }

    /**
     * Vrati IpAdresu, ktera se pouzije jako zdrojova pri odeslani paketu.
     * Nejdriv se projde natovaci tabulka, jestli to tam uz neni. Kdyz neni,
     * tak se zkusi vygenerovat novy zaznam. Musi mit ale spravne nakonfigurovany access-listy+pooly.
     * Tato metoda se vola, kdyz uz vim, ze ma prirazeny pool+access-list, tak uz to ma vzdycky vratit prelozenou adresu.
     * V teto metode se take mazou stare dynamicke zaznamy - jako prvni.
     * @param ip
     * @param natovani - true, kdyz natuju, false - kdyz se jen ptam, zda je volno v poolu
     * @return Adresu - na kterou se to ma prelozit <br />
     *         null - kdyz dosel pool IP adress, tak se ma vratit odesilateli Destination Host Unreachable,
     *                null by to melo vratit pouze pri natovani==false nebo kdyz neni zadnej pouzitelnej pool
     */
    private IpAdresa zanatujZdrojovouIpAdresu(Paket paket, boolean natovani) {

        smazStareDynamickeZaznamy();
        IpAdresa ip = paket.zdroj;

        // nejdriv prochazim staticka pravidla
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke && zaznam.in.jeStejnaAdresa(ip)) {
                IpAdresa vrat = zaznam.out.vratKopii();
                vrat.port = ip.port;
                return vrat;
            }
        }

        // nejdriv kontroluju, jestli uz to nahodou nema dynamicky zaznam v NATtabulce
        for (NATzaznam zaznam : tabulka) { // porovnavam i podle portu (mohou byt NATy za sebou..)
            if (!zaznam.staticke && zaznam.in.jeStejnaAdresaSPortem(ip)) {
                zaznam.touch();
                return zaznam.out;
            }
        }

        // staticky ani dynamicky zaznam neni, tak vygenerujeme novy
        AccessList access = lAccess.vratAccessListIP(ip);
        Pool pool = lPool.vratPoolZAccessListu(access);
        IpAdresa vrat = lPool.dejIpZPoolu(pool);

        vrat.port = vygenerujPort(vrat);
        if (natovani == true) { // jen kdyz opravdu pridavam
            // kopiruju si novou IP, pri pridavani do tabulku se prepisovaly zaznamy
            pridejZaznamDynamcikyDoNATtabulky(ip.vratKopii(), vrat.vratKopii(), paket.cil.vratKopii());
        }
        return vrat;
    }

    /**
     * Mrkne se do tabulky a vrati prislusny zaznam pokud existuje.
     * Kdyz je v tabulce staticky zaznam, tak to prelozi na adresu o stejnem portu.
     * @param ip
     * @return null - pokud neexistuje zaznam pro danou ip
     */
    private IpAdresa odnatujZdrojovouIpAdresu(IpAdresa ip) {
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke) {
                if (zaznam.out.jeStejnaAdresa(ip)) {
                    IpAdresa vrat = zaznam.in;
                    vrat.port = ip.port;
                    return vrat;
                }
            }
        }

        for (NATzaznam zaznam : tabulka) {
            if (zaznam.out.jeStejnaAdresaSPortem(ip)) {
                return zaznam.in;
            }
        }
        return null;
    }

    /**
     * Prida zaznam do natovaci tabulky. Pouziva se to pri dynamickym natovani.
     * @param in zdrojova IP
     * @param out nova zdrojova (prelozena)
     */
    private void pridejZaznamDynamcikyDoNATtabulky(IpAdresa in, IpAdresa out, IpAdresa cil) {
        tabulka.add(new NATzaznam(in, out, cil, false));
    }

    /**
     * Smaze stare (starsi nez 10s) dynamicke zaznamy v tabulce.
     * @return pocet dynamickych zaznamu, ktere se smazaly
     */
    public int smazStareDynamickeZaznamy() {
        long now = System.currentTimeMillis();
        List<NATzaznam> smaznout = new ArrayList<NATzaznam>();
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke == false) { // jen dynamicke zaznamy
                if (now - zaznam.vratCas() > 10000) {
                    smaznout.add(zaznam);
                }
            }
        }
        int pocet = smaznout.size();
        for (NATzaznam z : smaznout) {
            tabulka.remove(z);
        }
        return pocet;
    }

    /**
     * Vygeneruje unikatni port pro prelozeny zaznam.
     * Generuje v rozsahu 1025-65536 <br />
     * @param vrat 
     * @return
     */
    public int vygenerujPort(IpAdresa vrat) {
        int port = 333;
        port = 1025 + (int)(Math.random() * 65536);

        for (NATzaznam z: tabulka) {
            if (vrat.jeStejnaAdresa(z.out) && z.out.port == port) return vygenerujPort(vrat);
        }

        return port;
    }

    /****************************************** staticke natovani ******************************************************/

    /**
     * Nasype IpAdresy ze statickych pravidel na dane rozhrani.
     * Kdyz je rozhrani null, tak se nic neudela.
     * Mimo tridu NATtabulka by se to melo pouzivat jen pri cteni z konfiguraku.
     * @param iface verejne rozhrani (outside)
     */
    public void pridejIpAdresyZeStatickychPravidel(SitoveRozhrani iface) {
        if (iface == null) {
            return;
        }
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke) {
                iface.seznamAdres.add(zaznam.out.vratKopii());
            }
        }
    }

    /**
     * Smaze vsechny staticke zaznamy, ktere maji odpovidajici in a out.
     * Dale aktualizuje verejne rozhrani co se IP tyce. Nejdrive smaze vsechny krom prvni,
     * a pak postupne prida ze statickych a pak i z poolu.
     * @return 0 - alespon 1 zaznam se smazal <br />
     *         1 - nic se nesmazalo, pac nebyl nalezen odpovidajici zaznam (% Translation not found)
     */
    public int smazStatickyZaznam(IpAdresa in, IpAdresa out) {

        List<NATzaznam> smaznout = new ArrayList<NATzaznam>();
        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke && in.jeStejnaAdresa(zaznam.in) && out.jeStejnaAdresa(zaznam.out)) {
                smaznout.add(zaznam);
            }
        }

        if (smaznout.size() == 0) {
            return 1;
        }

        for (NATzaznam z : smaznout) {
            tabulka.remove(z);
        }
        verejne.smazVsechnyIpKromPrvni();
        pridejIpAdresyZeStatickychPravidel(verejne);
        lPool.updateIpNaRozhrani();

        return 0;
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
        lPool.updateIpNaRozhrani();
    }

    /**
     * Smaze toto rozhrani z inside listu.
     * Kdyz to rozhrani neni v inside, tak se nestane nic.
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

    /**
     * Smaze vsechny inside rozhrani.
     */
    public void smazRozhraniInsideVsechny() {
        inside.clear();
    }

    /**
     * Smaze vsechny IP (krom prvni) + smaze verejne rozhrani.
     */
    public void smazRozhraniOutside() {
        verejne.smazVsechnyIpKromPrvni();
        verejne = null;
    }

    /****************************************** Cisco *********************************************************/
    /**
     * Prida staticke pravidlo do tabulky.
     * Razeno vzestupne dle out adresy.
     * @param in zdrojova IP urcena pro preklad
     * @param out nova (prelozena) adresa
     * @return 0 - ok, zaznam uspesne pridan <br />
     *         1 - chyba, in adresa tam uz je (% in already mapped (in -> out)) <br />
     *         2 - chyba, out adresa tam uz je (% similar static entry (in -> out) already exists)
     */
    public int pridejStatickePravidloCisco(IpAdresa in, IpAdresa out) {

        if (jeTamZdrojova(in, true)) {
            return 1;
        }
        if (jeTamPrelozena(out, true)) {
            return 2;
        }

        if (verejne != null) {
            verejne.seznamAdres.add(out.vratKopii());
        }

        int index = dejIndexVTabulce(out);
        tabulka.add(index, new NATzaznam(in, out, true));

        return 0;
    }

    /**
     * Vrati vypis vsech zaznamu v tabulce.
     * Nejdrive to vypise dynamicka pravidla, pak staticka.
     * Nejdriv se smazou stare dynamcike zaznamy.
     * @return
     */
    public String vypisZaznamyCisco() {
        smazStareDynamickeZaznamy();

        String s = "";
        if (tabulka.size() == 0) {
            s += "\n\n";
            return s;
        }
        
        s += zarovnej("Pro Inside global", 24) + zarovnej("Inside local", 20);
        s += zarovnej("Outside local", 20) + zarovnej("Outside global", 20);
        s += "\n";

        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke == false) {
                s += zarovnej("icmp " + zaznam.out.vypisAdresuSPortem(), 24)
                        + zarovnej(zaznam.in.vypisAdresuSPortem(), 20)
                        + zarovnej(zaznam.cil.vypisAdresuSPortem(), 20)
                        + zarovnej(zaznam.cil.vypisAdresuSPortem(), 20)+"\n";
            }
        }

        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke) {
                s += zarovnej("--- " + zaznam.out.vypisAdresu(), 24)
                        + zarovnej(zaznam.in.vypisAdresu(), 20)
                        + zarovnej("---", 20)
                        + zarovnej("---", 20)+"\n";
            }
        }

        return s;
    }

    /**
     * Pomocny servisni vypis.
     * Nejdriv se smazou stare dynamcike zaznamy.
     * @return
     */
    public String vypisZaznamyDynamicky() {
        smazStareDynamickeZaznamy();
        String s = "";

        for (NATzaznam zaznam : tabulka) {
            if (zaznam.staticke == false) {
                s += zaznam.in.vypisAdresuSPortem() + "\t" + zaznam.out.vypisAdresuSPortem() + "\n";
            }
        }
        return s;
    }

    /****************************************** Linux *********************************************************/
    /**
     * Nastavi Linux pocitac pro natovani. Kdyz uz je nastavena, nic nedela.
     * Pocitam s tim, ze ani pc ani rozhrani neni null.
     * Jestli jsem to dobre pochopil, tak tohle je ten zpusob natovani, kdy se vsechny pakety jdouci
     * ven po nejakym rozhrani prekladaj na nejakou verejnou adresu, a z toho rozhrani zase zpatky.
     * Prikaz napr: "iptables -t nat -I POSTROUTING -o eth2 -j MASQUERADE" - vsechny pakety jdouci ven
     * po rozhrani eth2 se prekladaj.
     * @param pc
     * @param verejne, urci ze je tohle rozhrani verejne a ostatni jsou automaticky soukroma.
     */
    public void nastavLinuxMaskaradu(SitoveRozhrani verejne) {

        if (linux_nastavena_maskarada) {
            return;
        }

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
        lAccess.smazAccessListyVsechny();
        lAccess.pridejAccessList(new IpAdresa("0.0.0.0", 0), cislo);

        // osefovani IP poolu
        String pool = "ovrld";
        lPool.smazPoolVsechny();
        lPool.pridejPool(verejne.vratPrvni(), verejne.vratPrvni(), 24, pool);

        lPoolAccess.smazPoolAccessVsechny();
        lPoolAccess.pridejPoolAccess(cislo, pool, true);

        linux_nastavena_maskarada = true;
    }

    /**
     * Zrusi linux DNAT. Kdyz neni nastavena, nic nedela.
     */
    public void zrusLinuxMaskaradu() {

        lAccess.smazAccessListyVsechny();
        lPool.smazPoolVsechny();
        lPoolAccess.smazPoolAccessVsechny();
        smazRozhraniOutside();
        smazRozhraniInsideVsechny();
        linux_nastavena_maskarada = false;
    }

    public boolean jeNastavenaLinuxovaMaskarada() {
        return linux_nastavena_maskarada;
    }

    /**
     * Nastavi promennou na true.
     */
    public void nastavZKonfigurakuLinuxBooleanTrue() {
        linux_nastavena_maskarada = true;
    }

    /**
     * Prida staticke pravidlo do NAT tabulky. Nic se nekontroluje.
     * @param in zdrojova IP
     * @param out nova zdrojova (prelozena)
     */
    public void pridejStatickePravidloLinux(IpAdresa in, IpAdresa out) {
        verejne.seznamAdres.add(out.vratKopii());
        tabulka.add(new NATzaznam(in, out, true));
    }
}
