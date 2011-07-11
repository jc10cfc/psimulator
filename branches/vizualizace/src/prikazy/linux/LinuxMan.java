/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.linux;

import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;
import prikazy.AbstraktniPrikaz;

/**
 *
 * @author Tomáš Pitřinec
 */
public class LinuxMan extends AbstraktniPrikaz{

    public LinuxMan(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon,slova);
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        kon.posliServisne("Manualove stranky nejsou v simulatoru dostupne. Doporucuji pouzit manualove " +
                "stranky na webu, napriklad: http://linux.die.net/man/, nebo pouzit google.");

        kon.posliServisne("Seznam prikazu implementovanych v tomto pocitaci vypisete " +
                "zvlastnim prikazem help.");
    }

}
