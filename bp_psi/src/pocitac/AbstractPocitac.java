/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import datoveStruktury.IpAdresa;
import datoveStruktury.RoutovaciTabulka;
import java.util.ArrayList;
import java.util.List;

/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */
public abstract class AbstractPocitac {

    public Komunikace komunikace;
    public List<SitoveRozhrani> rozhrani; //kvuli vypisum to musi bejt verejny
    public String jmeno; //jmeno pocitace
    public RoutovaciTabulka routovaciTabulka;

    @Deprecated
    public AbstractPocitac(String jmeno) {
        vypis("Pouziva se deprecated metoda AbstractPocitac(String jmeno)");
        komunikace = new Komunikace(3567, this);
        rozhrani = new ArrayList<SitoveRozhrani>();
        this.jmeno = jmeno;
    }

    public AbstractPocitac(String jmeno, int port) {
        this.jmeno = jmeno;
        rozhrani = new ArrayList<SitoveRozhrani>();
        komunikace = new Komunikace(port, this);
        routovaciTabulka = new RoutovaciTabulka();
    }

    @Deprecated
    public AbstractPocitac(int port) {
        vypis("Pouziva se deprecated metoda AbstractPocitac(int port)");
        komunikace = new Komunikace(port, this);
        rozhrani = new ArrayList<SitoveRozhrani>();
    }

    /**
     * Prida rozhrani iface do seznamu rozhrani.
     * @param iface
     */
    public void pridejRozhrani(SitoveRozhrani iface) {
        rozhrani.add(iface);
    }

    @Deprecated
    public void nastavJmeno(String jm) {
        vypis("Pouziva se deprecated metoda nastavJmeno(String jm)");
        this.jmeno = jm;
    }

    public SitoveRozhrani najdiRozhrani(String jmeno) {
        if (jmeno == null) {
            return null;
        }
        for (SitoveRozhrani rozhr : rozhrani) {
            if (rozhr.jmeno.equals(jmeno)) {
                return rozhr;
            }
        }
        return null;
    }

    /**
     * Tahle metoda vypisuje na standartni vystup. Pouzivat pro vypisy v Komunikaci, Konsoli i Parseru atd.
     * pro snadnejsi debugovani, aby se vedelo, co kterej pocitac dela.
     * @param ret
     */
    public void vypis(String ret) {
        System.out.println("(" + jmeno + ":) " + ret);
    }

    private boolean jsemVCili(IpAdresa cil) {
        for (SitoveRozhrani iface : rozhrani) { // zvazit pouziti metody equals - neco se tam dela s maskou, tak nevim
            if (iface.ip.vypisIP().equals(cil.vypisIP())) {
                System.out.println("Ping paket dorazil do cile.");
                return true;
            }
        }
        return false;
    }

    // bud pole bitu (pokud bude potreba vic nez 1 informace), jinak klasicky int
    public int posliPing(IpAdresa cil) {
        int ret = -1;

        if (jsemVCili(cil)) {
            // ping paket dorazil do cile
            // konec
            return 0;
        }

        SitoveRozhrani sr = routovaciTabulka.najdiSpravnyRozhrani(cil);
        if (sr == null) {
            // neni pro to pravidlo zaznam v routovaci tabulce
            // konec
            return 1;
        }
        if (sr.pripojenoK == null) {
            // neni fyzicky pripojeno nikam
            // konec
            return 2;
        }

        ret = sr.pripojenoK.getPc().prijmiPing(cil);
        return ret;
    }

    public int prijmiPing(IpAdresa cil) {
        int ret = -1;
        if (jsemVCili(cil)) {
            // ping paket dorazil do cile
            // konec
            return 0;
        }

        ret = posliPing(cil);
        return ret;
    }

    // zatim pomocna metoda, pak se muze smazat
    public void vypisRozhrani() {

        for (SitoveRozhrani iface : rozhrani) {
            System.out.println("(" + jmeno + ":) " + iface.jmeno);
            if (iface.ip != null) {
                System.out.println("(" + jmeno + ":) " + iface.ip.vypisIP());
                System.out.println("(" + jmeno + ":) " + iface.ip.vypisMasku());
            }
            System.out.println("(" + jmeno + ":) " + iface.macAdresa);
            if (iface.pripojenoK != null) {
                System.out.println("(" + jmeno + ":) " + iface.pripojenoK.jmeno);
            }
            System.out.println("(" + jmeno + ":)");
        }
    }
}
