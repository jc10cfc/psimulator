/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import java.util.LinkedList;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

/**
 * Parser prikazu pro linux, zde se volaji prikazy dle toho, co poslal uzivatel.
 * @author neiss
 */
public class LinuxParserPrikazu extends ParserPrikazu {

    public LinuxParserPrikazu(AbstractPocitac pc, Konsole kon) {
        super(pc, kon);
    }    

    @Override
    public void zpracujRadek(String s) {
        //        System.out.println("zacatek metody zpracujRadek()");
        AbstraktniPrikaz pr;
        radek = s;
        slova = new LinkedList<String>();

        rozsekejLepe();

        if (slova.size() < 1) {
            return;
        }

        if (slova.get(0).equals("")) {
            return; // prazdny Enter
        }

        boolean nepokracuj = spolecnePrikazy(); //tady se zkousi, jestli prikaz nepatri ke spolecnejm prikazum,
        if (nepokracuj) {                           //jestli ano, tak se spusti a konci
            return;
        }

        if (slova.get(0).equals("exit")) {      //tady se zkousej jednotlivy prikazy
            pr = new Exit(pc, kon, slova);
        } else if (slova.get(0).equals("ifconfig")) {
            pr = new Ifconfig(pc, kon, slova);
        } else {
            kon.posliRadek("bash: " + slova.get(0) + ": command not found");
        }
    }
}
