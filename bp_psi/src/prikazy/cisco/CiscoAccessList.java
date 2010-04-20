/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * http://www.samuraj-cz.com/clanek/cisco-ios-8-access-control-list/
 */

package prikazy.cisco;

import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;

/**
 *
 * @author haldyr
 */
public class CiscoAccessList extends CiscoPrikaz {

    boolean no;

    public CiscoAccessList(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no) {
        super(pc, kon, slova);
        this.no = no;
    }

//    access-list 7 permit 10.10.10.0 0.0.0.31
//    access-list 7 permit 10.10.20.0 0.0.0.31

    @Override
    protected boolean zpracujRadek() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void vykonejPrikaz() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}