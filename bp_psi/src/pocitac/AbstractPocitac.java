/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */

public class AbstractPocitac {
    Komunikace komunikace;
    ParserPrikazu parser;

    public AbstractPocitac(){
        komunikace=new Komunikace(3567, this);

    }

}
