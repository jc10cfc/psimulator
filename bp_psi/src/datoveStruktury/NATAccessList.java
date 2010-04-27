/*
 * http://www.samuraj-cz.com/clanek/cisco-ios-8-access-control-list/
 */

package datoveStruktury;

import java.util.ArrayList;
import java.util.List;

/**
 * Datova struktura pro seznam Access-listu.
 * Kazdy access-list obsahuje jmeno (cislo 1-2699) a IpAdresu,
 * ktera definuje rozsah pristupnych IpAdres.
 * @author haldyr
 */
public class NATAccessList {

    public List<AccessList> seznam;
    NATtabulka natTabulka;

    public NATAccessList(NATtabulka tab) {
        seznam = new ArrayList<AccessList>();
        this.natTabulka = tab;
    }

    /**
     * Prida do seznamu Access-listu na spravnou pozici dalsi pravidlo. <br />
     * Je to razeny dle cislo access-listu.
     * Pocitam s tim, ze ani jedno neni null.
     * @param adresa
     * @param cislo
     */
    public void pridejAccessList(IpAdresa adresa, int cislo) {
        for (AccessList zaznam : seznam) {
            if (zaznam.ip.equals(adresa)) { // kdyz uz tam je, tak nic nedelat
                return;
            }
        }
        int index = 0;
        for (AccessList access : seznam) {
            if (cislo < access.cislo) {
                break;
            }
            index++;
        }
        seznam.add(index, new AccessList(adresa.vratKopii(), cislo));
    }

    /**
     * Smaze vsechny seznam-listy s danym cislem.
     * @param cislo
     */
    public void smazAccessList(int cislo) {
        List<AccessList> smazat = new ArrayList<AccessList>();
        for (AccessList zaznam : seznam) {
            if (zaznam.cislo == cislo) {
                smazat.add(zaznam);
            }
        }

        for (AccessList zaznam : smazat) {
            seznam.remove(zaznam);
        }
    }

    /**
     * Smaze vsechny access-listy
     */
    public void smazAccessListyVsechny() {
        seznam.clear();
    }

    /**
     * Vrati prvni access-list do ktereho spada ip.
     * Kdyz zadny takovy nenajde, tak vrati null.
     * @param ip
     * @return
     */
    public AccessList vratAccessListIP(IpAdresa ip) {
        for (AccessList access : seznam) {
            if (ip.jeVRozsahu(access.ip)) {
                return access;
            }
        }
        return null;
    }

    /**
     * Trida reprezentujici jeden seznam-list.
     */
    public class AccessList {

        public IpAdresa ip;
        public int cislo;

        public AccessList(IpAdresa ip, int cislo) {
            this.ip = ip;
            this.cislo = cislo;
        }
    }

}
