/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import datoveStruktury.*;
import java.util.LinkedList;
import java.util.List;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.apps.CommandShell.prikazy.linux.LinuxParserPrikazu;
/**
 *
 * @author Tomáš Pitřinec
 */
public class LinuxPocitac extends AbstraktniPocitac {

    public LinuxPocitac(String jmeno, int port) {
        super(jmeno,port);

        if(LinuxPocitac.commandList.isEmpty())
            LinuxPocitac.naplnCommandList();
    }

    /**
     * statická proměná s názvy příkazů počítače, využito pro doplnění tabulatorem
     */
    public static LinkedList<String> commandList = new LinkedList<String>();
    /**
     * metoda která naplní commandList, měla by se spustit jen jednou a to při
     * vytvoření první instance pocitace.
     * Později se může upravit na čtení ze souboru, ale to je asi zbytečné,
     * protože když někdo přidá příkaz tak se aplikace stejně znovu kompiluje...
     */
    public static void naplnCommandList(){
        // pro lepsi orientaci nechat abecedne serazene
        commandList.add("cat");
        commandList.add("echo");
        commandList.add("exit");
        commandList.add("help");
        commandList.add("ifconfig");
        commandList.add("ip");
        commandList.add("ipaddr");
        commandList.add("iplink");
        commandList.add("iproute");
        commandList.add("iptables");
        commandList.add("man");
        commandList.add("ping");
        commandList.add("route");
        commandList.add("traceroute");
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

    @Override
    public void nastavKonsoli(CommandShell konsole) {
        konsole.setParser(new LinuxParserPrikazu(this, konsole));
        konsole.prompt=this.jmeno + ":~# ";
    }

    @Override
    public List<String> getCommandList() {
        return LinuxPocitac.commandList;
    }


}
