/*
 * Hotovo:
 * Dodelat zpracovani klasik + no
 * %No matching route to delete
 * Pak to predelat v parseru.
 *
 * Dodelat:
 * 
 */

package pocitac.apps.CommandShell.prikazy.cisco;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.SitoveRozhrani;
import vyjimky.SpatnaAdresaException;

/**
 * Trida pro zpracovani prikazu ip route / no ip route.<br />
 * Bud rozhrani nebo brana je vzdy null (po radnem zpracovani).
 * @author Stanislav Řehák
 */
public class CiscoIpRoute extends CiscoPrikaz {
    
    private IpAdresa adresat;
    private IpAdresa brana;
    private SitoveRozhrani rozhrani;

    public CiscoIpRoute(AbstraktniPocitac pc, CommandShell kon, List<String> slova, boolean no) {
        super(pc, kon, slova, no);
        this.adresat = null;
        this.brana = null;
        this.rozhrani = null;

        debug = false;
        if (debug) pc.vypis("CiscoIpRoute");
        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    // ip route 'adresat' 'maska cile' 'kam poslat'
    // ip route 0.0.0.0 0.0.0.0 192.168.2.254
    // ip route 192.168.2.0 255.255.255.192 fastEthernet 0/0
    // no ip route ...
    /**
     * Vim, ze mi prisel prikaz '(no) ip route'
     * @return
     */
    @Override
    protected boolean zpracujRadek() {

        if (no == true) {
            if (debug) pc.vypis("prikaz no, pridej="+no);
            dalsiSlovo();
        }

        dalsiSlovo(); // route
        
        try {
            String adr = dalsiSlovo();
            String maska = dalsiSlovo();

            if (adr.isEmpty() || maska.isEmpty()) {
                incompleteCommand();
                return false;
            }
            adresat = new IpAdresa(adr, maska);
        } catch (Exception e) { // SpatnaMaskaException, SpatnaAdresaException
            invalidInputDetected();
            return false;
        }

        if (!adresat.jeCislemSite()) {
            kon.printLine("%Inconsistent address and mask");
            return false;
        }

        if (IpAdresa.jeZakazanaIpAdresa(adresat.vypisAdresu())) {
            kon.printLine("%Invalid destination prefix");
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
                kon.printLine("%Invalid next hop address");
                return false;
            }

        } else if (!dalsi.equals("")) { // na rozhrani
            String posledni = dalsiSlovo();
            dalsi += posledni; // nemuze byt null

            rozhrani = pc.najdiRozhrani(dalsi);
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
                kon.printLine("%No matching route to delete");
            }
        }
    }
}