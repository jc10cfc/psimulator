/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.cisco;

import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;

/**
 * Trida pro vypis podporovanych prikazu v moji implementaci.
 * @author Stanislav Řehák
 */
public class CiscoHelp extends CiscoPrikaz {

    boolean english;

    public CiscoHelp(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean english) {
        super(pc, kon, slova);
        this.english = english;
        vykonejPrikaz();
    }

    @Override
    protected boolean zpracujRadek() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void vykonejPrikaz() {
        String s ="";

        if (english == false) {
            s += "Tento prikaz neni na realnem cisco stroji. \nZde je pouzit pouze pro napovedu, co je v tomto systemu " +
                "implementovano. \nTato implementace ma nekolik prikazu navic oproti realnemu ciscu:\n" +
                " help - pro vypis teto napovedy\n" +
                " kill - pro opusteni konzole z jakehokoliv stavu cisca\n" +
                " save/uloz - pro ulozeni aktualni konfigurace vsech pocitacu do XML souboru, " +
                "bez parametru se to ulozi do " +
                Main.Main.konfigurak+"\n\n" +

                "Celkem funguji tyto prikazy:\n";
        } else {
            s += "There is no such command in real cisco. \n" +
                    "It is used only for a hint. This implementation has more commands then real cisco:\n" +
                    " help_en - writes this hint\n" +
                    " kill - for leaving console from any state of cisco\n" +
                    " save - for saving current configuration of all computers to XML file, " +
                    "without paramater it saves to "+Main.Main.konfigurak+"\n\n" +

                    "These commands are implemented:\n";
        }
   
        s +=    "\nuser mode\n" +
                "  show ip nat translations\n"+
                "  show ip route\n" +
                "  traceroute\n" +
                "  ping\n" +
                "  enable\n" +
                "  exit\n" +

                "\nprivilege mode\n" +
                "  configure terminal\n" +
                "  disable\n" +
                "  show ip nat translations\n"+
                "  show ip route\n" +
                "  show running-config\n" +
                "  traceroute\n" +
                "  ping\n" +
                "  exit\n" +

                "\nconfigure mode\n" +
                "  (no) ip route\n" +
                "  (no) ip nat pool\n" +
                "  (no) ip nat inside source list\n" +
                "  (no) ip nat inside source static\n" +
                "  access-list\n" +
                "  interface\n" +
                "  end\n" +
                "  exit\n" +
                
                "\nconfigure interface mode\n" +
                "  (no) ip address\n" +
                "  (no) ip nat inside\n" +
                "  (no) ip nat outside\n" +
                "  (no) shutdown\n" +
                "  end\n" +
                "  exit\n\n";
                        
        kon.posliPoRadcich(s, 10);
    }
}
