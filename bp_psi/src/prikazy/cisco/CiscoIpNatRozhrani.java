/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy.cisco;

import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;
import static Main.Main.*;

/**
 * Cisco trida pro nastavovani inside/outside rozhrani.
 * Outside rozhrani muze byt pouze jedno. V cisco jde sice i vice, ale pro nasi praci to neni potreba.
 * @author haldyr
 */
public class CiscoIpNatRozhrani extends CiscoPrikaz {

    SitoveRozhrani rozhrani;
    boolean inside = false;
    boolean outside = false;
    boolean no;

    /**
     *
     * @param pc
     * @param kon
     * @param slova
     * @param rozhrani, ktere se nastavuje v tom prikazu
     */
    public CiscoIpNatRozhrani(AbstraktniPocitac pc, Konsole kon, List<String> slova, SitoveRozhrani rozhrani, boolean no) {
        super(pc, kon, slova);
        this.rozhrani = rozhrani;
        this.no = no;

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    @Override
    protected boolean zpracujRadek() {

        if (no == true) {
            if (!kontrola("ip", dalsiSlovo(), 2)) {
                return false;
            }
        }

        if (!kontrola("nat", dalsiSlovo(), 2)) {
            return false;
        }

        String side = dalsiSlovo();
        if (side.equals("")) {
            ambiguousCommand();
            return false;
        }
        if (side.startsWith("i")) {
            if (kontrola("inside", side, 1)) {
                inside = true;
            } else {
                return false;
            }
        } else {
            if (kontrola("outside", side, 1)) {
                outside = true;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void vykonejPrikaz() {

        if (no) {
            if (inside) {
                pc.NATtabulka.smazRozhraniInside(rozhrani);
            } else if (outside) {
                if (pc.NATtabulka.vratVerejne().jmeno.equals(rozhrani.jmeno)) {
                    pc.NATtabulka.smazRozhraniOutside();
                }
            }
            return;
        }

        if (inside) {
            pc.NATtabulka.pridejRozhraniInside(rozhrani);
        } else if (outside) {
            if (pc.NATtabulka.vratVerejne() != null) {
                kon.posliRadek(jmenoProgramu + ": Implementace nepovoluje mit vice nastavenych verejnych rozhrani. "
                        + "Takze se rusi aktualni verejne: " + pc.NATtabulka.vratVerejne().jmeno+ " a nastavi se "+rozhrani.jmeno);
            }
            pc.NATtabulka.nastavRozhraniOutside(rozhrani);

        }
    }
}
