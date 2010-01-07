/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import prikazy.ParserPrikazu;

/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */

public class AbstractPocitac {
    Komunikace komunikace;

    public AbstractPocitac(){
        komunikace=new Komunikace(3567, this);

    }

}
