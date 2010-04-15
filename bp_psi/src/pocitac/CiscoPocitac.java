/*
 * UDELAT
 *  - implementovat prijmiEthernetove(...)
 */

package pocitac;

import datoveStruktury.*;
import datoveStruktury.WrapperRoutovaciTabulkyCisco;

/**
 *
 * @author haldyr
 */
public class CiscoPocitac extends AbstractPocitac{

    private WrapperRoutovaciTabulkyCisco wrapper;

    public CiscoPocitac(String jmeno, int port) {
        super(jmeno,port);
        wrapper = new WrapperRoutovaciTabulkyCisco(this);
    }

    public WrapperRoutovaciTabulkyCisco getWrapper() {
        return wrapper;
    }

    /**
     * Ethernetove prijima nebo odmita me poslany pakety.
     * @param p
     * @param rozhr rozhrani pocitace, kterej ma paket prijmoutm, tzn. tohodle pocitace
     * @param ocekavana adresa, kterou na rozhrani ocekavam
     * @return true, kdyz byl paket prijmut, jinak false
     */
    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana) {
        if (ocekavana.jeStejnaAdresa(rozhr.ip)) { //adresa souhlasi - muzu to prijmout
            prijmiPaket(p);
            return true;
        } else { //adresa nesouhlasi, mozna by se to dalo ale poslat dal
            return false;
        }
    }
}
