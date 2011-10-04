/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.CommandShell.prikazy.linux;
import pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz;
import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;

/**
 *
 * @author Tomáš Pitřinec
 */
public class LinuxIpLink extends AbstraktniPrikaz{
    public LinuxIpLink(AbstraktniPocitac pc, CommandShell kon, List<String> slova, LinuxIp puv) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
        this.puv=puv;
    }
    private LinuxIp puv;

    private void parsujPrikaz() {

    }

    @Override
    protected void vykonejPrikaz() {
        kon.printWithSimulatorName("Prikaz link neni v simulatoru zatim podporovan.");
    }

}
