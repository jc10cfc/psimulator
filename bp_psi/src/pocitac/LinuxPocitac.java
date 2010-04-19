/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import datoveStruktury.*;
/**
 *
 * @author neiss
 */
public class LinuxPocitac extends AbstractPocitac {
    
    public LinuxPocitac(String jmeno, int port) {
        super(jmeno,port);
        ip_forward=false; // Linux defaultne nepreposila. Pak, myslim, zpatky nic neposle a paket proste zahodi.
    }

    /**
     * Ethernetove prijima nebo odmita me poslany pakety.
     * @param p
     * @param rozhr rozhrani pocitace, kterej ma paket prijmout, tzn. tohodle pocitace
     * @param ocekavana adresa, kterou odesilaci pocitac na tomto rozhrani ocekava
     * @param sousedni adresa, se kterou mi to poslal ten sousedni pocitac. Linuxu je to jedno, ale
     * pro cisco to je jeden z parametru, podle kteryho se rozhoduje, jestli paket prijme
     * @return true, kdyz byl paket prijmut, jinak false
     */
    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana, IpAdresa sousedni){
        if (rozhr.obsahujeStejnouAdresu(ocekavana)) { //adresa souhlasi - muzu to prijmout
            prijmiPaket(p, rozhr);
            return true;
        } else {//adresa nesouhlasi, zpatky se musi poslat host unreachable
            return false;
        }
    }

    
}
