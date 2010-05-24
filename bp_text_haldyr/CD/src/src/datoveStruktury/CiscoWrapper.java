package datoveStruktury;

import vyjimky.WrapperException;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.SitoveRozhrani;

/** 
 * Trida reprezentujici wrapper nad routovaci tabulkou pro system cisco.
 * Tez bude sefovat zmenu v RT dle vlastnich rozhrani.
 * Cisco samo o sobe ma tez 2 tabulky: <br />
 *      1. zadane uzivatelem (tato trida) <br />
 *      2. vypocitane routy z tabulky c. 1 (trida RoutovaciTabulka)
 * @author haldyr
 */
public class CiscoWrapper {

    /**
     * Jednotlive radky wrapperu.
     */
    private List<CiscoZaznam> radky;
    AbstraktniPocitac pc;
    /**
     * Odkaz na routovaci tabulku, ktera je wrapperem ovladana.
     */
    RoutovaciTabulka routovaciTabulka;
    /**
     * ochrana proti smyckam v routovaci tabulce.
     * Kdyz to projede 50 rout, tak se hledani zastavi s tim, ze smula..
     */
    int citac = 0;
    private boolean debug = false;

    public CiscoWrapper(AbstraktniPocitac pc) {
        radky = new ArrayList<CiscoZaznam>();
        this.pc = pc;
        this.routovaciTabulka = pc.routovaciTabulka;
    }

    /**
     * Vnitrni trida pro reprezentaci CiscoZaznamu ve wrapperu.
     * Adresat neni null, ale bud rozhrani nebo brana je vzdy null.
     */
    public class CiscoZaznam {

        private IpAdresa adresat; // s maskou
        private IpAdresa brana;
        private SitoveRozhrani rozhrani;
        private boolean connected = false;

        private CiscoZaznam(IpAdresa adresat, IpAdresa brana) {
            this.adresat = adresat;
            this.brana = brana;
        }

        private CiscoZaznam(IpAdresa adresat, SitoveRozhrani rozhrani) {
            this.adresat = adresat;
            this.rozhrani = rozhrani;
        }

        /**
         * Pouze pro ucely vypisu RT!!! Jinak nepouzivat!
         * @param adresat
         * @param brana
         * @param rozhrani
         */
        private CiscoZaznam(IpAdresa adresat, IpAdresa brana, SitoveRozhrani rozhrani) {
            this.adresat = adresat;
            this.brana = brana;
            this.rozhrani = rozhrani;
        }

        public IpAdresa getAdresat() {
            return adresat;
        }

        public IpAdresa getBrana() {
            return brana;
        }

        public SitoveRozhrani getRozhrani() {
            return rozhrani;
        }

        private void setConnected() {
            this.connected = true;
        }

        public boolean isConnected() {
            return connected;
        }

        @Override
        public String toString() {
            String s = adresat.vypisAdresu() + " " + adresat.vypisMasku() + " ";
            if (brana == null) {
                s += rozhrani.jmeno;
            } else {
                s += brana.vypisAdresu();
            }
            return s;
        }

        /**
         * CiscoZaznamy se rovnaji pokud adresat ma stejnou adresu i masku &&
         * ( se takto rovnaji i brany ) || ( rozhrani se jmenuji stejne nehlede na velikost pismen )
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != CiscoZaznam.class) {
                return false;
            }

            if (adresat.equals(((CiscoZaznam) obj).adresat)) {
                if (brana != null && ((CiscoZaznam) obj).brana != null) {
                    if (brana.equals(((CiscoZaznam) obj).brana)) {
                        return true;
                    }
                } else if (rozhrani != null && ((CiscoZaznam) obj).rozhrani != null) {
                    if (rozhrani.jmeno.equalsIgnoreCase(((CiscoZaznam) obj).rozhrani.jmeno)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.adresat != null ? this.adresat.hashCode() : 0);
            hash = 37 * hash + (this.brana != null ? this.brana.hashCode() : 0);
            hash = 37 * hash + (this.rozhrani != null ? this.rozhrani.hashCode() : 0);
            return hash;
        }
    }

    /**
     * Tato metoda bude aktualizovat RoutovaciTabulku dle tohoto wrapperu.
     */
    public void update() {
        // smazu RT
        routovaciTabulka.smazVsechnyZaznamy();

        // nastavuju citac
        this.citac = 0;

        // pridam routy na nahozena rozhrani
        for (SitoveRozhrani iface : pc.rozhrani) {
            if (iface.jeNahozene() && iface.vratPrvni() != null) { // prvni IP je null, kdyz tam neni zadna nastavena
                routovaciTabulka.pridejZaznam(iface.vratPrvni(), iface, true);
            }
        }

        // propocitam a pridam routy s prirazenyma rozhranima
        for (CiscoZaznam zaznam : radky) {
            if (zaznam.rozhrani != null) { // kdyz to je na rozhrani
                if (zaznam.rozhrani.jeNahozene()) {
                    routovaciTabulka.pridejZaznam(zaznam.adresat, zaznam.rozhrani);
                }
            } else { // kdyz to je na branu
                SitoveRozhrani odeslat = najdiRozhraniProBranu(zaznam.brana);
                if (odeslat != null) {
                    if (odeslat.jeNahozene()) {
//                    System.out.println("nasel jsem pro "+zaznam.adresat.vypisAdresu() + " rozhrani "+odeslat.jmeno);
                        routovaciTabulka.pridejZaznamBezKontrol(zaznam.adresat, zaznam.brana, odeslat);
                    }
                } else {
//                    System.out.println("nenasel jsem pro "+ zaznam);
                }
            }
        }
    }

    /**
     * Vrati rozhrani, na ktere se ma odesilat, kdyz je zaznam na branu.
     * Tato metoda pocita s tim, ze v RT uz jsou zaznamy pro nahozena rozhrani.
     * @param brana
     * @return kdyz nelze nalezt zadne rozhrani, tak vrati null
     */
    SitoveRozhrani najdiRozhraniProBranu(IpAdresa brana) {
        SitoveRozhrani iface = null;

        citac++;
        if (citac >= 101) {
            return null; // ochrana proti smyckam
        }
        for (int i = radky.size() - 1; i >= 0; i--) { // prochazim opacne (tedy vybiram s nevyssim poctem jednicek)

            // kdyz to je na rozsah vlastniho rozhrani
            //mrknout se jestli to tady vadi!
            iface = routovaciTabulka.najdiSpravnyRozhrani(brana);
            if (iface != null) {
                return iface;
            }

            // kdyz to je na branu
            CiscoZaznam zaznam = radky.get(i);
            if (brana.jeVRozsahu(zaznam.adresat)) {
                if (zaznam.rozhrani != null) { // 172.18.1.0 255.255.255.0 FastEthernet0/0
                    return zaznam.rozhrani;
                }
                return najdiRozhraniProBranu(zaznam.brana);
            }
        }
        return null;
    }

    /**
     * Pridava do wrapperu novou routu na branu.
     * @param adresa
     * @param brana
     */
    public void pridejZaznam(IpAdresa adresa, IpAdresa brana) {
        CiscoZaznam z = new CiscoZaznam(adresa, brana);
        pridejZaznam(z);
    }

    /**
     * Pridava do wrapperu novou routu na rozhrani.
     * @param adresa
     * @param rozhrani
     */
    public void pridejZaznam(IpAdresa adresa, SitoveRozhrani rozhrani) {
        CiscoZaznam z = new CiscoZaznam(adresa, rozhrani);
        pridejZaznam(z);
    }

    /**
     * Prida do wrapperu novou routu na rozhrani. Pote updatuje RT je-li potreba.
     * V teto metode se kontroluje, zda adresat je cislem site.
     * @param zaznam, ktery chci vlozit
     */
    private void pridejZaznam(CiscoZaznam zaznam) {

        if (!zaznam.getAdresat().jeCislemSite()) { // vyjimka pro nacitani z konfiguraku, jinak to je osetreno v parserech
            throw new WrapperException("Adresa " + zaznam.getAdresat().vypisAdresu() + " neni cislem site!");
        }

        for (CiscoZaznam z : radky) { // zaznamy ulozene v tabulce se uz znovu nepridavaji
            if (zaznam.equals(z)) {
                return;
            }
        }

        radky.add(dejIndexPozice(zaznam, true), zaznam);
        update();
    }

    /**
     * Malinko prasacka metoda pro pridani zaznamu do RT pouze pro vypis!
     * @param zaznam
     */
    private void pridejRTZaznamJenProVypis(RoutovaciTabulka.Zaznam zaznam) {
        CiscoZaznam ciscozaznam = new CiscoZaznam(zaznam.getAdresat(), zaznam.getBrana(), zaznam.getRozhrani());
        if (zaznam.jePrimoPripojene()) {
            ciscozaznam.setConnected();
        }
        radky.add(dejIndexPozice(ciscozaznam, false), ciscozaznam);
    }

    /**
     * Smaze zaznam z wrapperu + aktualizuje RT. Rozhrani maze podle jmena!
     * Muze byt zadana bud adresa nebo adresa+brana nebo adresa+rozhrani.
     *
     * no ip route IP MASKA DALSI? <br />
     * IP a MASKA je povinne, DALSI := { ROZHRANI | BRANA } <br />
     *
     * @param adresa
     * @param brana
     * @param rozhrani
     * @return 0 = ok, 1 = nic se nesmazalo
     */
    public int smazZaznam(IpAdresa adresa, IpAdresa brana, SitoveRozhrani rozhrani) {
        int i = -1;

        if (adresa == null) {
            return 1;
        }
        if (brana != null && rozhrani != null) {
            return 1;
        }

        // maze se zde pres specialni seznam, inac to hazi concurrent neco vyjimku..
        List<CiscoZaznam> smazat = new ArrayList();

        for (CiscoZaznam z : radky) {
            i++;

            if (!z.adresat.equals(adresa)) {
                continue;
            }

            if (brana == null && rozhrani == null) {
                smazat.add(radky.get(i));
            } else if (brana != null && rozhrani == null && z.brana != null) {
                if (z.brana.equals(brana)) {
                    smazat.add(radky.get(i));
                }
            } else if (brana == null && rozhrani != null) {
                if (z.rozhrani.jmeno.equals(rozhrani.jmeno)) {
                    smazat.add(radky.get(i));
                }
            }
        }

        if (smazat.size() == 0) {
            return 1;
        }

        for (CiscoZaznam zaznam : smazat) {
            radky.remove(zaznam);
        }

        update();

        return 0;
    }

    /**
     * Smaze vsechny zaznamy ve wrapperu + zaktualizuje RT
     * Prikaz 'clear ip route *'
     */
    public void smazVsechnyZaznamy() {
        radky.clear();
        update();
    }

    /**
     * Vrati pozici, na kterou se bude pridavat zaznam do wrapperu.
     * Je to razeny dle integeru cile.
     * @param pridavany, zaznam, ktery chceme pridat
     * @param nejminBituVMasce rika, jestli chceme radit nejdrive zaznamy maskou o mensim poctu 1,
     * pouziva se pri normalnim vkladani do wrapperu, false pro vypis RT
     * @return
     */
    private int dejIndexPozice(CiscoZaznam pridavany, boolean nejminBituVMasce) {
        int i = 0;
        for (CiscoZaznam cz : radky) {
            if (jeMensiIP(pridavany.adresat, cz.adresat, nejminBituVMasce)) {
                break;
            }
            i++;
        }
        return i;
    }

    /**
     * Vrati true, pokud je prvni adresa mensi nez druha, pokud se rovnaji, tak rozhoduje maska.
     * @param prvni
     * @param druha
     * @return
     */
    private boolean jeMensiIP(IpAdresa prvni, IpAdresa druha, boolean nejminBituVMasce) {

        // kdyz maj stejny IP a ruzny masky
        if (prvni.vypisAdresu().equals(druha.vypisAdresu())) {
            if (nejminBituVMasce) { // pro pridani do wrapperu
                if (prvni.pocetBituMasky() < druha.pocetBituMasky()) {
                    return true;
                }
            } else { // pro vypis RT
                if (prvni.pocetBituMasky() > druha.pocetBituMasky()) {
                    return true;
                }
            }
        }
        if (prvni.dejLongIP() < druha.dejLongIP()) {
            return true;
        }
        return false;
    }

    /**
     * Vrati CiscoZaznam na indexu.
     * @param index
     * @return
     */
    public CiscoZaznam vratZaznam(int index) {
        return radky.get(index);
    }

    /**
     * Vrati pocet zaznamu ve wrapperu.
     * @return
     */
    public int size() {
        return radky.size();
    }

    /**
     * Pro vypis pres 'sh run'
     * @return
     */
    public String vypisRunningConfig() {
        String s = "";
        for (CiscoZaznam z : radky) {
            s += "ip route " + z + "\n";
        }
        return s;
    }

    /**
     * Vrati vypis routovaci tabulky.
     * Kasle se na tridni vypisy pro adresaty ze A rozsahu, protoze se v laborce takovy rozsah nepouziva.
     * @return
     */
    public String vypisRT() {
        String s = "";

        if (debug) {
            s += "Codes: C - connected, S - static\n\n";
        } else {
            s += "Codes: C - connected, S - static, R - RIP, M - mobile, B - BGP\n"
                    + "       D - EIGRP, EX - EIGRP external, O - OSPF, IA - OSPF inter area\n"
                    + "       N1 - OSPF NSSA external type 1, N2 - OSPF NSSA external type 2\n"
                    + "       E1 - OSPF external type 1, E2 - OSPF external type 2\n"
                    + "       i - IS-IS, su - IS-IS summary, L1 - IS-IS level-1, L2 - IS-IS level-2\n"
                    + "       ia - IS-IS inter area, * - candidate default, U - per-user static route\n"
                    + "       o - ODR, P - periodic downloaded static route\n\n";
        }

        CiscoWrapper wrapper = ((CiscoPocitac) pc).getWrapper();
        boolean defaultGW = false;
        String brana = null;
        for (int i = 0; i < wrapper.size(); i++) {
            if (wrapper.vratZaznam(i).adresat.equals(new IpAdresa("0.0.0.0", 0))) {
                if (wrapper.vratZaznam(i).brana != null) {
                    brana = wrapper.vratZaznam(i).brana.vypisAdresu();
                }
                defaultGW = true;
            }
        }

        s += "Gateway of last resort is ";
        if (defaultGW) {
            if (brana != null) {
                s += brana;
            } else {
                s += "0.0.0.0";
            }
            s += " to network 0.0.0.0\n\n";
        } else {
            s += "not set\n\n";
        }

        // vytvarim novy wrapperu kvuli zabudovanemu razeni
        CiscoWrapper wrapper_pro_razeni = new CiscoWrapper(pc);
        for (int i = 0; i < routovaciTabulka.pocetZaznamu(); i++) {
            wrapper_pro_razeni.pridejRTZaznamJenProVypis(routovaciTabulka.vratZaznam(i));
        }

        for (CiscoZaznam czaznam : wrapper_pro_razeni.radky) {
            s += vypisZaznamDoRT(czaznam);
        }

        return s;
    }

    /**
     * Vrati vypis cisco zaznamu ve spravnem formatu pro RT
     * @param zaznam
     * @return
     */
    private String vypisZaznamDoRT(CiscoZaznam zaznam) {
        String s = "";

        if (zaznam.isConnected()) { //C       21.21.21.0 is directly connected, FastEthernet0/0
            s += "C       " + zaznam.getAdresat().vypisCisloSite() + "/" + zaznam.getAdresat().pocetBituMasky() + " is directly connected, " + zaznam.getRozhrani().jmeno + "\n";
        } else { //S       18.18.18.0 [1/0] via 51.51.51.9
            if (zaznam.getAdresat().equals(new IpAdresa("0.0.0.0", 0))) {
                s += "S*      ";
            } else {
                s += "S       ";
            }
            s += zaznam.getAdresat().vypisAdresu() + "/" + zaznam.getAdresat().pocetBituMasky();
            if (zaznam.getBrana() != null) {
                s += " [1/0] via " + zaznam.getBrana().vypisAdresu();
            } else {
                s += " is directly connected, " + zaznam.getRozhrani().jmeno;
            }
            s += "\n";
        }

        return s;
    }
}
