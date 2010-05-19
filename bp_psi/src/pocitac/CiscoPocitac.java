/*
 * UDELAT:
 * Prepsat metodu prijmiEthernetove() HOTOVO
 */
package pocitac;

import datoveStruktury.*;
import datoveStruktury.CiscoWrapper;

/**
 *
 * @author haldyr
 */
public class CiscoPocitac extends AbstraktniPocitac {

    private CiscoWrapper wrapper;

    public CiscoPocitac(String jmeno, int port) {
        super(jmeno, port);
        wrapper = new CiscoWrapper(this);
    }

    /**
     * Vrati CiscoWrapper = ovladac routovaci tabulky.
     * @return
     */
    public CiscoWrapper getWrapper() {
        return wrapper;
    }
    /**
     * Vypis metody prijmiEthernetove().
     */
    private boolean debug = false;

    /**
     * Ethernetove prijima nebo odmita prichozi pakety.
     * @param p
     * @param rozhr vstupni rozhrani pocitace, kterej ma paket prijmout, tzn. tohodle pocitace
     * @param ocekavana adresa, kterou odesilaci pocitac na tomto rozhrani ocekava
     * @param sousedni adresa, od ktereho mi prisel ARP request. Linuxu je to jedno, ale
     * pro cisco to je jeden z parametru, podle kteryho se rozhoduje, jestli paket prijme
     * @return true, kdyz byl paket prijmut, jinak false
     *
     * Kdyz nejde odpovedet sousedovi na ARP request (nemam na nej routu), tak paket neprijmu. <br />
     * Kdyz mohu odpovedet na ARP && (paket je pro me || vim kam ho poslat dal), tak prijmu. <br />
     * V ostatnich pripadech neprijimam.
     */
    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana, IpAdresa sousedni) {

        if (routovaciTabulka.najdiSpravnejZaznam(sousedni) == null) {
            ladici("nemuzu odpovedet na arp dotaz sousedovi => neprijimam");
            return false; // kdyz nemuzu odpovedet na arp dotaz sousedovi, tak smula
        }

        if (rozhr.obsahujeStejnouAdresu(ocekavana)) { //adresa souhlasi == je to pro me
            ladici("paket je pro me => prijimam");
            prijmiPaket(p, rozhr);
            return true;
        }

        if (najdiMeziRozhranima(p.cil) != null) { // kdyz vim, kam to poslat dal
            ladici("vim, kam to poslat dal => prijimam");
            prijmiPaket(p, rozhr);
            return true;
        }
        
        // jinak zahazuju
        ladici("mohu odpovedet sousedovi na arp dotaz, neni to pro me a ja nevim kam s tim, tak to radsi neprijmu");
        return false;
    }

    /**
     * Vypisuje pouze kdyz je debug=true.
     * @param s
     */
    public void ladici(String s) {
        if (debug) {
            vypis("Ethernet: " + s);
        }
    }
}
