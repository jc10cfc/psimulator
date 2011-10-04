/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.CommandShell.prikazy.cisco;

import datoveStruktury.CiscoStavy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz;

/**
 * Zakladni napoveda, jake prikazy to umi.
 * @author Stanislav Řehák
 */
public class CiscoOtaznik extends AbstraktniPrikaz{

    public CiscoOtaznik(AbstraktniPocitac pc, CommandShell kon, List<String> slova, CiscoStavy stav) {
        super(pc,kon,slova);
        this.stav = stav;
        vykonejPrikaz();
    }

    CiscoStavy stav;


    @Override
    protected void vykonejPrikaz() {
        List napoveda = new ArrayList<String>();
        switch (stav) {
                case USER:
                    kon.posliRadek("Exec commands:");
                    napoveda.add("  enable           Turn on privileged commands");
                    napoveda.add("  exit             Exit from the EXEC");
                    napoveda.add("  ping             Send echo messages");
                    napoveda.add("  show             Show running system information");
                    napoveda.add("  traceroute       Trace route to destination");
                    break;

                case ROOT:
                    kon.posliRadek("Exec commands:");
                    napoveda.add("  configure        Enter configuration mode");
                    napoveda.add("  disable          Turn off privileged commands");
                    napoveda.add("  enable           Turn on privileged commands");
                    napoveda.add("  ping             Send echo messages");
                    napoveda.add("  show             Show running system information");
                    napoveda.add("  traceroute       Trace route to destination");
                    break;

                case CONFIG:
                    kon.posliRadek("Configure commands:");
                    napoveda.add("  interface              Select an interface to configure");
                    napoveda.add("  ip                     Global IP configuration subcommands");
                    napoveda.add("  exit                   Exit from configure mode");
                    napoveda.add("  access-list            Add an access list entry");
                    break;

                case IFACE:
                    kon.posliRadek("Interface configuration commands:");
                    napoveda.add("  exit                    Exit from interface configuration mode");
                    napoveda.add("  ip                      Interface Internet Protocol config commands");
                    napoveda.add("  no                      Negate a command or set its defaults");
                    napoveda.add("  shutdown                Shutdown system elements");
            }
            posliList(napoveda);
    }

    /**
     * pomocna metoda pro vypis povolenych prikazu
     * @param n seznam, ktery se bude prochazet po prvcich a posilat uzivateli
     */
    private void posliList(List n) {
        Collections.sort(n);
        for (Object s : n) {
            kon.posliPoRadcich((String) s, 50);
        }
    }
}
