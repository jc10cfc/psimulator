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

    /**
     * Volan jen v konstruktoru konsole.
     * @param pc
     * @param kon
     */
    public LinuxParserPrikazu(AbstractPocitac pc, Konsole kon) {
        super(pc, kon);
    }    
    
    /**
     * Prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej() rozseka na jednotlivy slova.
     * Pak se testuje, jestli prvni slovo je nazev nejakyho podporovanyho prikazu, jestlize ne, tak se vypise
     * "command not found", jinak se preda rizeni tomu spravnymu prikazu.
     * @param s
     */
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
            pr = new LinuxExit(pc, kon, slova);
        } else if (slova.get(0).equals("ifconfig")) {
            pr = new LinuxIfconfig(pc, kon, slova);
//        } else if (slova.get(0).equals("stifconfig")) {
//            pr = new LinuxIfconfigStarej(pc, kon, slova);
        } else if (slova.get(0).equals("route")) {
            pr = new LinuxRoute(pc, kon, slova);
        } else if (slova.get(0).equals("ping")) {
            pr = new LinuxPing(pc, kon, slova);
        } else if (slova.get(0).equals("cat")) {
            pr = new LinuxCat(pc, kon, slova);
        } else if (slova.get(0).equals("echo")) {
            pr = new LinuxEcho(pc, kon, slova);
        } else {
            kon.posliRadek("bash: " + slova.get(0) + ": command not found");
        }
    }
}
