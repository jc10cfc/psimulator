package datoveStruktury;

import vyjimky.WrapperException;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import datoveStruktury.RoutovaciTabulka.Zaznam;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.SitoveRozhrani;

/**
 * Trida reprezentujici wrapper nad routovaci tabulkou pro system cisco.
 * Tez bude sefovat zmenu v RT dle vlastnich rozhrani.
 * Cisco samo o sobe ma tez 2 tabulky: <br />
 *      1. zadane uzivatelem (tato trida) <br />
 *      2. vypocitane routy z tabulky c. 1 (trida RoutovaciTabulka)
 * @author haldyr
 */
public class WrapperRoutovaciTabulkyCisco {

    private List<CiscoZaznam> radky;
    AbstractPocitac pc;
    RoutovaciTabulka routovaciTabulka;

    public WrapperRoutovaciTabulkyCisco(AbstractPocitac pc) {
        radky = new ArrayList<CiscoZaznam>();
        this.pc = pc;
        this.routovaciTabulka = pc.routovaciTabulka;
    }

    public class CiscoZaznam {

        // ip route 192.168.2.0 255.255.255.192 192.168.2.126
        // ip route 192.168.100.0 255.255.255.0 FastEthernet0/1
        private IpAdresa adresat; // s maskou
        private IpAdresa brana;
        private SitoveRozhrani rozhrani;

        private CiscoZaznam(IpAdresa adresat, IpAdresa brana) {
            this.adresat = adresat;
            this.brana = brana;
        }

        private CiscoZaznam(IpAdresa adresat, SitoveRozhrani rozhrani) {
            this.adresat = adresat;
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

        @Override
        public String toString() {
            String s = adresat.vypisAdresu() + " " + adresat.vypisMasku() + " ";
            if (brana == null) {
                s += rozhrani.jmeno;
            } else {
                s += brana.vypisAdresu();
            }
            s += "\n";
            return s;
        }
    }

    /**
     * Tato metoda bude aktualizovat RoutovaciTabulku dle tohoto wrapperu.
        ip route 147.32.125.100 255.255.255.128 1.1.1.1
        ip route 1.1.0.0 255.255.0.0 FastEthernet0/1
     */
    public void update() { // TODO: zde chyba??
        // smazu RT
        routovaciTabulka.smazVsechnyZaznamy();

        // pridam routy na nahozene rozhrani
        for (SitoveRozhrani iface : pc.rozhrani) {
            if (iface.jeNahozene()) {
                routovaciTabulka.pridejZaznam(iface.ip, iface);
            }
        }

        // propocitam a pridam routy s prirazenyma rozhranima
        for (CiscoZaznam zaznam : radky) { // prochazim radky a jdu propocitavat
            if (zaznam.rozhrani != null) { // kdyz to je na rozhrani
                routovaciTabulka.pridejZaznam(zaznam.adresat, zaznam.rozhrani);
            } else { // kdyz to je na branu
                SitoveRozhrani odeslat = najdiRozhraniProBranu(zaznam.brana);
                if (odeslat != null) {
//                    System.out.println("nasel jsem pro "+zaznam.adresat.vypisAdresu() + " rozhrani "+odeslat.jmeno);
                    routovaciTabulka.pridejZaznamBezKontrol(zaznam.adresat, zaznam.brana, odeslat);
                    // TODO: zrusit pridejZaznamBezKontrol()
                } else {
//                    System.out.println("nenasel jsem pro "+ zaznam);
                }

            }
        }

        
    }

    public void neco(){
        System.out.println("PPPPPPPPPPPPPPPPPPPPPp");
        IpAdresa ip = new IpAdresa("1.1.1.1");
        SitoveRozhrani sr = najdiRozhraniProBranu(ip);
        if (sr == null) {
            System.out.println("sr je null");
        } else {
            System.out.println("sr je "+sr.jmeno);
        }
    }

    /* 51.51.51.9 21.21.21.244
    ip route 3.3.3.0 255.255.255.0 2.2.2.2
    ip route 8.0.0.0 255.0.0.0 9.9.9.254
    ip route 9.9.9.0 255.255.255.0 172.18.1.99
    ip route 11.11.0.0 255.255.0.0 223.1.1.1
    ip route 11.11.0.0 255.255.0.0 223.255.255.2
    ip route 13.0.0.0 255.0.0.0 6.6.6.6
    ip route 18.18.18.0 255.255.255.0 51.51.51.9
    ip route 51.51.51.0 255.255.255.0 21.21.21.244
    ip route 80.80.80.0 255.255.255.0 8.1.1.1
    ip route 172.18.1.0 255.255.255.0 FastEthernet0/0
    ip route 192.168.9.0 255.255.255.0 2.2.2.2
     *
     * pozor na: ip route 1.1.1.0 255.255.255.0 1.1.1.22
     */
// TODO: vypis routovaci tabulky
    // pocitam, ze v RT jsou uz routy pro vlastni rozhrani
    /**
     * Vrati rozhrani, na ktere se ma odesilat, kdyz je zaznam na branu.
     * Tato metoda pocita s tim, ze v RT uz jsou zaznamy pro nahozena rozhrani.
     * @param brana
     * @return kdyz nelze nalezt zadne rozhrani, tak vrati null
     */
    SitoveRozhrani najdiRozhraniProBranu(IpAdresa brana) {
        SitoveRozhrani iface = null;
        boolean hledat = true;

        for (CiscoZaznam zaznam : radky) {
            if (hledat == false) {
                break;
            }

            // kdyz to je na rozhrani
            iface = routovaciTabulka.najdiSpravnyRozhrani(brana);
            if (iface != null) {
                return iface;
            }

            // 
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

        if (!zaznam.getAdresat().jeCislemSite()) {
            throw new WrapperException("Adresa " + zaznam.getAdresat().vypisAdresu() + " neni cislem site!");
        }

        radky.add(dejIndexPozice(zaznam), zaznam);

        update();
    }

    // 1/ no ip route IP maska + a volitelne treti zaznam, kdyz je bez 3.zaznamu a tak se smaze vsechno co sedi dle 1. a 2.
    //        + prekontrolovat vsechny routy a zrusit jim pripadne rozhrani (kdyz budou nedostupny)
    /**
     * Smaze zaznam z wrapperu + aktualizuje RT. Rozhrani maze podle jmena!
     * Muze byt zadana bud adresa nebo adresa+brana nebo adresa+rozhrani.
     * @param adresa
     * @param brana
     * @param rozhrani
     * @return 0 = ok, 1 = nic se nesmazalo
     */
    public int smazZaznam(IpAdresa adresa, IpAdresa brana, SitoveRozhrani rozhrani) {
        int i = 0;

        if (adresa == null) {
            return 1;
        }
        if (brana != null && rozhrani != null) {
            return 1;
        }

        for (CiscoZaznam z : radky) {

            if (!z.adresat.equals(adresa)) {
                i++;
                continue;
            }

            if (brana == null && rozhrani == null) {
                radky.remove(i);

            } else if (brana != null && rozhrani == null) {
                if (z.brana.equals(brana)) {
                    radky.remove(i);
                }
            } else if (brana == null && rozhrani != null) {
                if (z.rozhrani.jmeno.equals(rozhrani.jmeno)) {
                    i++;
                }
            }
            i++;
        }

        update();

        return 0;
    }

    // 2/ clear ip route *
    /**
     * Smaze vsechny zaznamy ve wrapperu + zaktualizuje RT.
     */
    public void smazVsechnyZaznamy() {
        radky.clear();
        update();
    }

    /**
     * Vrati pozici, na kterou se bude pridavat zaznam do wrapperu.
     * Je to razeny dle integeru cile.
     * @param pridavany, zaznam, ktery chceme pridat
     * @return
     */
    private int dejIndexPozice(CiscoZaznam pridavany) {
        int i = 0;
        for (CiscoZaznam cz : radky) {
            if (pridavany.adresat.dej32BitAdresu() < cz.adresat.dej32BitAdresu()) {
                break;
            }
            i++;
        }
        return i;
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
            s += "ip route " + z.adresat.vypisAdresu() + " " + z.adresat.vypisMasku() + " ";
            if (z.rozhrani == null) {
                s += z.brana.vypisAdresu();
            } else {
                s += z.rozhrani.jmeno;
            }
            s += "\n";
        }
        return s;
    }

    /*
     *
    Gateway of last resort is not set

    51.0.0.0/24 is subnetted, 1 subnets
    S       51.51.51.0 [1/0] via 21.21.21.244
    18.0.0.0/24 is subnetted, 1 subnets
    S       18.18.18.0 [1/0] via 51.51.51.9
    21.0.0.0/24 is subnetted, 1 subnets
    C       21.21.21.0 is directly connected, FastEthernet0/0
    172.18.0.0/24 is subnetted, 1 subnets
    S       172.18.1.0 is directly connected, FastEthernet0/0
    192.168.2.0/30 is subnetted, 1 subnets
    C       192.168.2.8 is directly connected, FastEthernet0/1
     */
    /**
     * Vrati vypis routovaci tabulky.
     * @return
     */
    public String vypisRT() {
        String s = "";
        boolean connected;

        s += "Codes: C - connected, S - static\n\n";
        s += "Gateway of last resort is not set\n";
        s += "Gateway of last resort is 0.0.0.0 to network 0.0.0.0\n\n";

        String n = "\n\n";

        for (int i = 0; i < routovaciTabulka.pocetZaznamu(); i++) {
            Zaznam zaznam = routovaciTabulka.vratZaznam(i);

            connected = false;
            for (SitoveRozhrani iface : pc.rozhrani) {
                if (zaznam.getAdresat().jeNadsiti(iface.ip)) {
                    connected = true;
                }
            }
            if (connected) { //C       21.21.21.0 is directly connected, FastEthernet0/0
                s += "C       " + zaznam.getAdresat().vypisAdresu() + " is directly connected, " + zaznam.getRozhrani().jmeno + "\n";
            } else { //S       18.18.18.0 [1/0] via 51.51.51.9
                s += "S       " + zaznam.getAdresat().vypisAdresu();
                if (zaznam.getBrana() != null) {
//                System.out.println("tadyyyy: "+zaznam.getAdresat().vypisAdresu() + " " + zaznam.getAdresat().vypisMasku());
                    s += " [1/0] via " + zaznam.getBrana().vypisAdresu() + "\n";
                } else {
                    s += " is directly connected, " + zaznam.getRozhrani().jmeno + "\n";
                }
            }

//            n += zaznam.getAdresat().vypisAdresu() + " " + zaznam.getAdresat().vypisMasku() + " ";
//            if (zaznam.getBrana() != null) {
//                n += zaznam.getBrana().vypisAdresu();
//            }
//            n += "\n";
        }

//        s += n;
        return s;
    }
}
