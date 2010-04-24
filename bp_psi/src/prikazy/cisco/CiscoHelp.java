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
 * @author haldyr
 */
public class CiscoHelp extends CiscoPrikaz {

    public CiscoHelp(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        vykonejPrikaz();
    }

    @Override
    protected boolean zpracujRadek() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void vykonejPrikaz() {
        String s ="";
        s += "Tento prikaz na realnem cisco stroji neni. \nZde je pouzit pouze pro napovedu, co je v tomto systemu " +
                "implementovano. \nTato implementace ma nekolik prikazu navic oproti realnemu ciscu:\n" +
                " help - pro vypis teto napovedy\n" +
                " kill - pro opusteni konzole z jakehokoliv stavu cisca\n" +
                " save/uloz - pro ulozeni aktualni konfigurace vsech pocitacu do XML konfiguraku\n\n" +
                
                "Celkem funguji tyto prikazy:\n" +
                
                "\nuzivatelsky mod\n" +
                "  show ip nat translations\n"+
                "  show ip route\n" +
                "  traceroute\n" +
                "  ping\n" +
                "  enable\n" +
                "  exit\n" +

                "\nprivilegovany mod\n" +
                "  configure terminal\n" +
                "  disable\n" +
                "  show ip nat translations\n"+
                "  show ip route\n" +
                "  show running-config\n" +
                "  access-list\n" +
                "  traceroute\n" +
                "  ping\n" +
                "  exit\n" +

                "\nkonfiguracni mod\n" +
                "  ip route\n" +
                "  ip nat pool\n" +
                "  ip nat inside source list\n" +
                "  ip nat inside source list static\n" +
                "  interface\n" +
                "  end\n" +
                "  exit\n" +
                
                "\nnastavovani rozhrani mod\n" +
                "  ip address\n" +
                "  shutdown\n" +
                "  end\n" +
                "  exit\n\n\n" +
                

                "k nastavovacim prikazum je zde moznost negace pres prikaz 'no'\n\n";
        kon.posliPoRadcich(s, 10);
    }


}
