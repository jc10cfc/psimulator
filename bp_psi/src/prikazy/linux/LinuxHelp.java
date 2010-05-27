/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.linux;

import java.util.List;
import pocitac.*;
import prikazy.AbstraktniPrikaz;

/**
 *
 * @author neiss
 */
public class LinuxHelp extends AbstraktniPrikaz{
    public LinuxHelp(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc,kon,slova);
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        kon.posliRadek("Tento prikaz na realnem pocitaci s linuxem neni. Zde je pouze pro informaci, jake prikazy jsou v tomto simulatoru implementovany.");
        kon.posliRadek("");
        kon.posliRadek("Simulator ma oproti skutecnemu pocitaci navic tyto prikazy:");
        kon.posliRadek("uloz / save   ulozeni stavijici virtualni site do souboru");
        kon.posliRadek("              napr. uloz ./konfiguraky/sit.xml   - ulozi se relativne k ceste, ze ktere je spusten server");
        kon.posliRadek("help          vypsani teto napovedy");
        kon.posliRadek("");
        kon.posliRadek("Z linuxovych prikazu jsou podporovany tyto:");
        kon.posliRadek("ifconfig      parametry adresa, netmask, up, down");
        kon.posliRadek("route         akce add, del; parametry -net, -host, dev, gw, netmask");
        kon.posliRadek("iptables      jen pro pridani pravidla k natovani");
        kon.posliRadek("              napr: iptables -t nat -A POSTROUTING -o eth1 -j MASQUERADE");
        kon.posliRadek("ping          prepinace -c, -i, -s, -t");
        kon.posliRadek("              prednastaven na 4 pakety");
        kon.posliRadek("traceroute    jen napr. traceroute 1.1.1.1");
        kon.posliRadek("exit");
        kon.posliRadek("ip            podprikazy addr a route");
        kon.posliRadek("echo, cat     jen na zapisovani a cteni souboru /proc/sys/net/ipv4/ip_forward");
        kon.posliRadek("");
    }

}
