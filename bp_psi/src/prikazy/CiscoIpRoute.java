/*
 * TODO: Dodelat zpracovani klasik + no
 */

package prikazy;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstractPocitac;
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
    private boolean pridej;

    private IpAdresa adresat;
    private IpAdresa brana;
    private SitoveRozhrani rozhrani;

    public CiscoIpRoute(AbstractPocitac pc, Konsole kon, List<String> slova, boolean pridej) {
        super(pc, kon, slova);
        this.pridej = pridej;
        pc.vypis("kostruktor: pridej="+this.pridej);
        this.adresat = null;
        this.brana = null;
        this.rozhrani = null;
        debug = false;

        boolean pokracovat = zpracujRadek();
        if (debug) pc.vypis(pokracovat ? "pokracuji" : "nepokracuji");
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    @Override
    protected void vykonejPrikaz() {
        if (debug) pc.vypis("pridej="+pridej);
        if (pridej) {
            if (brana != null) {
                if (debug) pc.vypis("prikaz ip route na branu");
                ((CiscoPocitac) pc).getWrapper().pridejZaznam(adresat, brana);
            } else {
                if (rozhrani == null) {
                    pc.vypis("chyba jako hrom!! Rozhrani je null");
                    return;
                }
                if (debug) pc.vypis("prikaz ip route na rozhrani");
                ((CiscoPocitac) pc).getWrapper().pridejZaznam(adresat, rozhrani);
            }
        } else { // mazu
            if (debug) pc.vypis("prikaz no ip route na mazani");
            int n = ((CiscoPocitac) pc).getWrapper().smazZaznam(adresat, brana, rozhrani);
            if (debug && n != 0) {
                pc.vypis("Nepodarilo se zmazat z RT vubec nic!");
            }
        }
    }

    // ip route 'adresat' 'maska cile' 'kam poslat'
    // ip route 0.0.0.0 0.0.0.0 192.168.2.254
    // ip route 192.168.2.0 255.255.255.192 fastEthernet 0/0
    @Override
    protected boolean zpracujRadek() {

        if (pridej == false) {
            if (debug) pc.vypis("prikaz no, pridej="+pridej);
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

//            ((CiscoPocitac) pc).getWrapper().pridejZaznam(adresat, brana);
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

//            ((CiscoPocitac) pc).getWrapper().pridejZaznam(adresat, sr);

        } else { // prazdny
            if (pridej) {
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



}
