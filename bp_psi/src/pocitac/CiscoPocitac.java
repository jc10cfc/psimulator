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
     * Vypis metody prijmiEthernetove().
     */
    private boolean debug = false;

    /**
     * Ethernetove prijima nebo odmita me poslany pakety.
     * @param p
     * @param rozhr rozhrani pocitace, kterej ma paket prijmoutm, tzn. tohodle pocitace
     * @param ocekavana adresa, kterou na rozhrani ocekavam
     * @return true, kdyz byl paket prijmut, jinak false
     *
     * Ocekavana Ip se vubec nebere v potaz. Prijme paket pouze tehdy, pokud cil paketu je primo na lokalni
     * rozhrani nebo vi kam ho poslat dal dle routovaci tabulky.
     *
     */
    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana) {

        if (debug) vypis("prijmiEthernetove() zacatek");
        SitoveRozhrani sr = null;
        if ((sr = najdiMeziRozhranima(p.cil)) != null) {
            if (debug) vypis("nasel jsem rozhrani kam to poslat: "+sr.jmeno);
            prijmiPaket(p);
            return true;
        }

        RoutovaciTabulka.Zaznam zaznam = null;
        if ((zaznam = routovaciTabulka.najdiSpravnejZaznam(p.cil)) != null) {
            if (debug) vypis("nasel jsem zaznam kam to poslat: "+zaznam);
            prijmiPaket(p);
            return true;
        }

        return false;
    }
}
