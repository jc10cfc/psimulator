/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy.cisco;

import datoveStruktury.CiscoStavy;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;
import prikazy.AbstraktniPrikaz;
import static datoveStruktury.CiscoStavy.*;

/**
 * Trida pro rozhodovani u prikazu ip.
 * @author haldyr
 */
public class CiscoIp extends CiscoPrikaz {

    AbstraktniPrikaz prikaz;
    CiscoStavy stav;
    SitoveRozhrani rozhrani; // kvuli prikazu 'ip nat inside/outside'

    /**
     * Konstruktor pro prikaz 'ip nat inside/outside'.
     * @param pc
     * @param kon
     * @param slova
     * @param no
     * @param stav
     * @param rozhrani
     */
    public CiscoIp(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no, CiscoStavy stav, SitoveRozhrani rozhrani) {
        super(pc, kon, slova, no);
        this.stav = stav;
        this.rozhrani = rozhrani;
        dal();
    }

    public CiscoIp(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no, CiscoStavy stav) {
        super(pc, kon, slova, no);
        this.stav = stav;

        dal();
    }

    /**
     * Metoda zastresujici par veci.
     */
    private void dal() {
        debug = false;

        boolean zpracuj = zpracujRadek();
//        ladici(zpracuj ? "ok-vytvoren dalsi prikaz" : "chyba: asi invalid input");
    }

    @Override
    protected boolean zpracujRadek() {

        String dalsi = dalsiSlovo();

        if (no) {
            dalsi = dalsiSlovo();
        }

        if(dalsi.length() == 0) {
            incompleteCommand();
            return false;
        }

        if (stav == CONFIG || (debug && stav == ROOT)) {

            if (kontrolaBezVypisu("route", dalsi, 5)) {
                prikaz = new CiscoIpRoute(pc, kon, slova, no);
                return true;
            }

            if (kontrolaBezVypisu("nat", dalsi, 3)) {
                prikaz = new CiscoIpNat(pc, kon, slova, no);
                return true;
            }

            if (kontrolaBezVypisu("classless", dalsi, 2)) {
                if (no) {
                    pc.routovaciTabulka.classless = false;
                } else {
                    pc.routovaciTabulka.classless = true;
                }
                return true;
            }
        }

        if (stav == IFACE) {
            if (kontrolaBezVypisu("address", dalsi, 3)) {
                prikaz = new CiscoIpAddress(pc, kon, slova, no, rozhrani);
                return true;
            }

            if (kontrolaBezVypisu("nat", dalsi, 2)) {
                prikaz = new CiscoIpNatRozhrani(pc, kon, slova, no, rozhrani);
                return true;
            }
        }

        if (dalsi.length() != 0 && ambiguous == false) { // jestli to je prazdny, tak to uz vypise kontrolaBezVypisu
            invalidInputDetected();
        }

        return false;
    }

    @Override
    protected void vykonejPrikaz() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
