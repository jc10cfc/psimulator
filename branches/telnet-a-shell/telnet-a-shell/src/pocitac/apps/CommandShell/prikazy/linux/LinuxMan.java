/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.CommandShell.prikazy.linux;

import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz;

/**
 *
 * @author Tomáš Pitřinec
 */
public class LinuxMan extends AbstraktniPrikaz{

    public LinuxMan(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(pc, kon,slova);
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        kon.printWithSimulatorName("Manualove stranky nejsou v simulatoru dostupne. Doporucuji pouzit manualove " +
                "stranky na webu, napriklad: http://linux.die.net/man/, nebo pouzit google.");

        kon.printWithSimulatorName("Seznam prikazu implementovanych v tomto pocitaci vypisete " +
                "zvlastnim prikazem help.");
    }

}
