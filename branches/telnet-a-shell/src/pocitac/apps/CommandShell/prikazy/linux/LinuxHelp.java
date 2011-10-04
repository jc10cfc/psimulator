/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.CommandShell.prikazy.linux;

import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz;

/**
 * Pomocný příkaz help. Implementován jen zde v simulátoru, na skutečném linuxu není.
 * @author Tomáš Pitřinec
 */
public class LinuxHelp extends AbstraktniPrikaz{
    public LinuxHelp(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(pc,kon,slova);
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        kon.printLine("Tento prikaz na realnem pocitaci s linuxem neni. Zde je pouze pro informaci, jake prikazy jsou v tomto simulatoru implementovany.");
        kon.printLine("");
        kon.printLine("Simulator ma oproti skutecnemu pocitaci navic tyto prikazy:");
        kon.printLine("uloz / save   ulozeni stavijici virtualni site do souboru");
        kon.printLine("              napr. uloz ./konfiguraky/sit.xml   - ulozi se relativne k ceste, ze ktere je spusten server");
        kon.printLine("help          vypsani teto napovedy");
        kon.printLine("");
        kon.printLine("Z linuxovych prikazu jsou podporovany tyto:");
        kon.printLine("ifconfig      parametry adresa, netmask, up, down");
        kon.printLine("route         akce add, del; parametry -net, -host, dev, gw, netmask");
        kon.printLine("iptables      jen pro pridani pravidla k natovani");
        kon.printLine("              napr: iptables -t nat -A POSTROUTING -o eth1 -j MASQUERADE");
        kon.printLine("ping          prepinace -c, -i, -s, -t");
        kon.printLine("              prednastaven na 4 pakety");
        kon.printLine("traceroute    jen napr. traceroute 1.1.1.1");
        kon.printLine("exit");
        kon.printLine("ip            podprikazy addr a route");
        kon.printLine("echo, cat     jen na zapisovani a cteni souboru /proc/sys/net/ipv4/ip_forward");
        kon.printLine("");
    }

}
