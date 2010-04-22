/*
 * http://www.samuraj-cz.com/clanek/cisco-ios-8-access-control-list/
 */

package prikazy.cisco;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;

/**
 * Trida pro zpracovani prikazu 'access-list 7 permit 1.1.1.0 0.0.0.31
 * @author haldyr
 */
public class CiscoAccessList extends CiscoPrikaz {

    boolean no;
    int access;
    IpAdresa adr;

    public CiscoAccessList(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no) {
        super(pc, kon, slova);
        this.no = no;

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

//    access-list 7 permit 10.10.10.0 0.0.0.31
//    access-list 7 permit 10.10.20.0 0.0.0.31

    @Override
    protected boolean zpracujRadek() {

        if (no) {
            if(!kontrola("access-list", dalsiSlovo(), 2)) {
                return false;
            }
        }

        try {
            access = Integer.parseInt(dalsiSlovo());
        } catch (NumberFormatException e) {
            invalidInputDetected();
            return false;
        }

        if (no) {
            return true;
        }

        if (!kontrola("permit", dalsiSlovo(), 1)) {
            return false;
        }

        IpAdresa wildcard;
        try {
            adr = new IpAdresa(dalsiSlovo());
            wildcard = new IpAdresa(dalsiSlovo());
        } catch (Exception e) {
            invalidInputDetected();
            return false;
        }

        String maska = IpAdresa.vratMaskuZWildCard(wildcard);
        
        try {
            adr.nastavMasku(maska);
        } catch (Exception e) {
            invalidInputDetected();
            return false;
        }

        return true;
    }

    @Override
    protected void vykonejPrikaz() {

        if (no) {
            pc.natTabulka.lAccess.smazAccessList(access);
            return;
        }

        pc.natTabulka.lAccess.pridejAccessList(adr, access);
    }


}