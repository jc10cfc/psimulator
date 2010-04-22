/*
 * UDELAT:
 * TODO: Prepsat metodu prijmiEthernetove()
 */


package pocitac;

import datoveStruktury.*;
import datoveStruktury.CiscoWrapper;

/**
 *
 * @author haldyr
 */
public class CiscoPocitac extends AbstraktniPocitac{

    private CiscoWrapper wrapper;

    public CiscoPocitac(String jmeno, int port) {
        super(jmeno,port);
        wrapper = new CiscoWrapper(this);
    }

    public CiscoWrapper getWrapper() {
        return wrapper;
    }

    /**
     * Vypis metody prijmiEthernetove().
     */
    private boolean debug = false;

    /**
     * Ethernetove prijima nebo odmita me poslany pakety.
     * @param p
     * @param rozhr rozhrani pocitace, kterej ma paket prijmout, tzn. tohodle pocitace
     * @param ocekavana adresa, kterou odesilaci pocitac na tomto rozhrani ocekava
     * @param sousedni adresa, se kterou mi to poslal ten sousedni pocitac. Linuxu je to jedno, ale
     * pro cisco to je jeden z parametru, podle kteryho se rozhoduje, jestli paket prijme
     * @return true, kdyz byl paket prijmut, jinak false
     *
     * Ocekavana Ip se vubec nebere v potaz. Prijme paket pouze tehdy, pokud cil paketu je primo na lokalni
     * rozhrani nebo vi kam ho poslat dal dle routovaci tabulky.
     *
     */
    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana, IpAdresa sousedni) {

        if (debug) vypis("prijmiEthernetove() zacatek");
        SitoveRozhrani sr = null;
        if ((sr = najdiMeziRozhranima(p.cil)) != null) {
            if (debug) vypis("nasel jsem rozhrani kam to poslat: "+sr.jmeno);
            prijmiPaket(p, rozhr);
            return true;
        }

        RoutovaciTabulka.Zaznam zaznam = null;
        if ((zaznam = routovaciTabulka.najdiSpravnejZaznam(p.cil)) != null) {
            if (debug) vypis("nasel jsem zaznam kam to poslat: "+zaznam);
            prijmiPaket(p, rozhr);
            return true;
        }

        return false;
    }
}
