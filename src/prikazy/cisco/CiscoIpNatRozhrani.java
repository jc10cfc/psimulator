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
 * @author Stanislav Řehák
 */
public class CiscoIpNatRozhrani extends CiscoPrikaz {

    SitoveRozhrani rozhrani;
    boolean inside = false;
    boolean outside = false;

    /**
     *
     * @param pc
     * @param kon
     * @param slova
     * @param rozhrani, ktere se nastavuje v tom prikazu
     */
    public CiscoIpNatRozhrani(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no, SitoveRozhrani rozhrani) {
        super(pc, kon, slova, no);
        this.rozhrani = rozhrani;

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
                pc.natTabulka.smazRozhraniInside(rozhrani);
            } else if (outside) {
                if (pc.natTabulka.vratVerejne().jmeno.equals(rozhrani.jmeno)) {
                    pc.natTabulka.smazRozhraniOutside();
                }
            }
            return;
        }

        if (inside) {
            pc.natTabulka.pridejRozhraniInside(rozhrani);
        } else if (outside) {
            if (pc.natTabulka.vratVerejne() != null && ! pc.natTabulka.vratVerejne().jmeno.equals(rozhrani.jmeno)) {
                kon.posliRadek(jmenoProgramu + ": Implementace nepovoluje mit vice nastavenych verejnych rozhrani. "
                        + "Takze se rusi aktualni verejne: " + pc.natTabulka.vratVerejne().jmeno+ " a nastavi se "+rozhrani.jmeno);
            }
            pc.natTabulka.nastavRozhraniOutside(rozhrani);

        }
    }
}
