/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import java.util.LinkedList;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

/**
 * Parser prikazu pro cisco, zde se volaji prikazy dle toho, co poslal uzivatel.
 * @author haldyr
 */
public class CiscoParserPrikazu extends ParserPrikazu {

    public CiscoParserPrikazu(AbstractPocitac pc, Konsole kon) {
        super(pc, kon);
    }

    @Override
    public void zpracujRadek(String s) {
        
        AbstraktniPrikaz prikaz;
        radek = s;
        slova = new LinkedList<String>();
        
        rozsekejLepe();

        if (slova.size() < 1) {
            return;
        }

        if (slova.get(0).equals("")) {
            return; // prazdny Enter
        }

        if (slova.get(0).equals("exit")) {
            prikaz = new Exit(pc, kon, slova);
        } else if (slova.get(0).equals("ifconfig")) {
            prikaz = new Ifconfig(pc, kon, slova);
        } else {
            kon.posliRadek("% Unknown command or computer name, or unable to find computer address");
        }
    }
}
