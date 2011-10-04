/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac.apps.CommandShell.prikazy.cisco;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.SitoveRozhrani;
import vyjimky.SpatnaAdresaException;
import vyjimky.SpatnaMaskaException;

/**
 * Trida pro prikaz 'ip address' ve stavu IFACE.
 * @author Stanislav Řehák
 */
public class CiscoIpAddress extends CiscoPrikaz {

    SitoveRozhrani rozhrani;
    IpAdresa adr;
    boolean noBezAdresy = false;

    public CiscoIpAddress(AbstraktniPocitac pc, CommandShell kon, List<String> slova, boolean no, SitoveRozhrani rozhrani) {
        super(pc, kon, slova, no);
        this.rozhrani = rozhrani;

        debug = false;
        ladici("konstruktor CiscoIpAddress");

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    /**
     * Vim, ze mi prislo '(no) ip address
     * @return
     */
    @Override
    protected boolean zpracujRadek() {
        //ip address 192.168.2.129 255.255.255.128
        if (no) { // vim, ze ve slova je 'no ip address'
            dalsiSlovo();
        }

        dalsiSlovo(); // address
        ladici("po address");

        String ip = dalsiSlovo();

        if (ip.isEmpty() && no){
            noBezAdresy = true;
            return true;
        }

        String maska = dalsiSlovo();
        if (jePrazdny(ip) || jePrazdny(maska)) {
            return false;
        }

        if (IpAdresa.jeZakazanaIpAdresa(ip)) {
            kon.printLine("Not a valid host address - " + ip);
            return false;
        }

        try {
            ladici("vytvarim IP");
            adr = new IpAdresa(ip, maska);
        } catch (SpatnaMaskaException e) {
            String[] pole = maska.split("\\.");
            String s = "";
            int i;
            for (String bajt : pole) {
                try {
                    i = Integer.parseInt(bajt);
                    s += Integer.toHexString(i);

                } catch (NumberFormatException exs) {
                    invalidInputDetected();
                    return false;
                }
            }
            kon.printLine("Bad mask 0x" + s.toUpperCase() + " for address " + ip);
            return false;
        } catch (SpatnaAdresaException e) {
            invalidInputDetected();
            return false;
        } catch (Exception e) {
            if (debug) e.printStackTrace();
            invalidInputDetected();
            return false;
        }

        if (adr.dej32BitAdresu() == 0) {
            kon.printLine("Not a valid host address - " + ip);
            return false;
        }

        if (adr.jeCislemSite() || adr.jeBroadcastemSite() || adr.dej32BitMasku() == 0) {
            // Router(config-if)#ip address 147.32.120.0 255.255.255.0
            // Bad mask /24 for address 147.32.120.0
            kon.printLine("Bad mask /" + adr.pocetBituMasky() + " for address " + adr.vypisAdresu());
            return false;
        }

        ladici("konec");
        if (dalsiSlovo().length() != 0) {
            invalidInputDetected();
            return false;
        }
        return true;
    }

    @Override
    protected void vykonejPrikaz() {

        if (no) {
            if (noBezAdresy) {
                rozhrani.zmenPrvniAdresu(null);
                return;
            }

            if (rozhrani.vratPrvni().dej32BitAdresu() != adr.dej32BitAdresu()) {
                kon.printLine("Invalid address");
                return;
            }
            if (rozhrani.vratPrvni().dej32BitMasku() != adr.dej32BitMasku()) {
                kon.printLine("Invalid address mask");
                return;
            }

            rozhrani.zmenPrvniAdresu(null);
            return;
        }
        
        rozhrani.zmenPrvniAdresu(adr);
    }
}
