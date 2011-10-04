/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy.linux;

import prikazy.*;
import pocitac.AbstraktniPocitac;
import pocitac.apps.CommandShell.CommandShell;

/**
 * Parser prikazu pro linux, zde se volaji jednotlivé příkazy.
 * @author Tomáš Pitřinec
 */
public class LinuxParserPrikazu extends ParserPrikazu {

    /**
     * Volan jen v konstruktoru CommandShell.
     * @param pc
     * @param kon
     */
    public LinuxParserPrikazu(AbstraktniPocitac pc, CommandShell kon) {
        super(pc, kon);
    }

    boolean ladici = true;
    
    /**
     * Prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej() rozseka na jednotlivy slova.
     * Pak se testuje, jestli prvni slovo je nazev nejakyho podporovanyho prikazu, jestlize ne, tak se vypise
     * "command not found", jinak se preda rizeni tomu spravnymu prikazu.
     * @param s
     */
    @Override
    public void zpracujRadek(String s) {
        AbstraktniPrikaz pr;
        radek = s;
        rozsekej();

        if (slova.size() < 1) {
            return;
        }

        if (slova.get(0).equals("")) {
            return; // prazdny Enter
        }

        boolean nepokracuj = spolecnePrikazy(ladici); //tady se zkousi, jestli prikaz nepatri ke spolecnejm prikazum,
        if (nepokracuj) {                           //jestli ano, tak se spusti a konci
            return;
        }

        if (slova.get(0).equals("exit")) {      //tady se zkousej jednotlivy prikazy
            pr = new LinuxExit(pc, kon, slova);
        } else if (slova.get(0).equals("ifconfig")) {
            pr = new LinuxIfconfig(pc, kon, slova);
        } else if (slova.get(0).equals("route")) {
            pr = new LinuxRoute(pc, kon, slova);
        } else if (slova.get(0).equals("ping")) {
            pr = new LinuxPing(pc, kon, slova);
        } else if (slova.get(0).equals("cat")) {
            pr = new LinuxCat(pc, kon, slova);
        } else if (slova.get(0).equals("echo")) {
            pr = new LinuxEcho(pc, kon, slova);
        } else if (slova.get(0).equals("iptables")) {
            pr = new LinuxIptables(pc, kon, slova);
        } else if (slova.get(0).equals("ip")) {
            pr = new LinuxIp(pc, kon, slova);
        } else if (slova.get(0).equals("traceroute")) {
            pr = new LinuxTraceroute(pc, kon, slova);
        } else if (slova.get(0).equals("man")) {
            pr = new LinuxMan(pc, kon, slova);
        } else if (slova.get(0).equals("help")) {
            pr = new LinuxHelp(pc, kon, slova);
        } else {
            kon.posliRadek("bash: " + slova.get(0) + ": command not found");
        }
    }
}
