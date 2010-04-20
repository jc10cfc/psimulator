/*
 * Hotovo:
 * Dodelat zpracovani klasik + no
 * %No matching route to delete
 * Pak to predelat v parseru.
 *
 * Dodelat:
 * 
 */

package prikazy;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;
import vyjimky.SpatnaAdresaException;

/**
 * Trida pro zpracovani prikazu ip route / no ip route.<br />
 * Bud rozhrani nebo brana je vzdy null (po radnem zpracovani).
 * @author haldyr
 */
public class CiscoIpRoute extends CiscoPrikaz {

    /**
     * Rika, zda pridavam (true) nebo mazu (false).
     */
    private boolean no;

    private IpAdresa adresat;
    private IpAdresa brana;
    private SitoveRozhrani rozhrani;

    private boolean debug = false;


    public CiscoIpRoute(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no) {
        super(pc, kon, slova);
        this.no = no;
        this.adresat = null;
        this.brana = null;
        this.rozhrani = null;

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    // ip route 'adresat' 'maska cile' 'kam poslat'
    // ip route 0.0.0.0 0.0.0.0 192.168.2.254
    // ip route 192.168.2.0 255.255.255.192 fastEthernet 0/0
    // no ip route ...
    @Override
    protected boolean zpracujRadek() {

        if (no == true) {
            if (debug) pc.vypis("prikaz no, pridej="+no);
            // tady osetrit, ze ve slova bude 'no ip route ..'
            if (! kontrola("ip", dalsiSlovo(), 2)) {
                return false;
            }
        }

        if (! kontrola("route", dalsiSlovo(), 2)) {
            return false;
        }

        try {
            adresat = new IpAdresa(dalsiSlovo(), dalsiSlovo());
        } catch (Exception e) { // SpatnaMaskaException, SpatnaAdresaException
            invalidInputDetected();
            return false;
        }

        if (!adresat.jeCislemSite()) {
            kon.posliRadek("%Inconsistent address and mask");
            return false;
        }

        if (IpAdresa.jeZakazanaIpAdresa(adresat.vypisAdresu())) {
            kon.posliRadek("%Invalid destination prefix");
            return false;
        }

        String dalsi = dalsiSlovo();

        if (zacinaCislem(dalsi)) { // na branu
            try {
                brana = new IpAdresa(dalsi);
            } catch (SpatnaAdresaException e) {
                invalidInputDetected();
                return false;
            }

            if (IpAdresa.jeZakazanaIpAdresa(brana.vypisAdresu())) {
                kon.posliRadek("%Invalid next hop address");
                return false;
            }

        } else if (!dalsi.equals("")) { // na rozhrani
            String posledni = dalsiSlovo();
            dalsi += posledni; // nemuze byt null

            for (SitoveRozhrani iface : pc.rozhrani) {
                if (iface.jmeno.equalsIgnoreCase(dalsi)) {
                    rozhrani = iface;
                }
            }
            if (rozhrani == null) { // rozhrani nenalezeno
                invalidInputDetected();
                return false;
            }

        } else { // prazdny
            if (no == false) {
                incompleteCommand();
                return false;
            }
        }

        if (!dalsiSlovo().equals("")) { // border za spravnym 'ip route <adresat> <maska> <neco> <bordel>'
            invalidInputDetected();
            return false;
        }

        return true;
    }

    @Override
    protected void vykonejPrikaz() {
        if (debug) pc.vypis("pridej="+no);
        if (no == false) {
            if (brana != null) { // na branu
                ((CiscoPocitac) pc).getWrapper().pridejZaznam(adresat, brana);
            } else { // na rozhrani
                if (rozhrani == null) {
                    return;
                }
                ((CiscoPocitac) pc).getWrapper().pridejZaznam(adresat, rozhrani);
            }
        } else { // mazu
            int n = ((CiscoPocitac) pc).getWrapper().smazZaznam(adresat, brana, rozhrani);
            if (n == 1) {
                kon.posliRadek("%No matching route to delete");
            }
        }
    }
}