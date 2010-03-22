/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import datoveStruktury.IpAdresa;
import java.util.List;
import java.util.Random;
import pocitac.AbstractPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.LinuxPocitac;

/**
 * Tady bude ping, kterej bude volanej jak ve tride LinuxPing, tak i CiscoPing.
 * @author haldyr
 */
public class Ping extends AbstraktniPrikaz {

    // je tady proto, ze tento 'abstraktni prikaz' nemuze tusit
    // na ktere pozici ve 'slova' se nachazi pingana IP
    String ip;

    // v podstate ty 'slova' tady jsou uplne zbytecny
    public Ping(AbstractPocitac pc, Konsole kon, List<String> slova, String ip) {
        super(pc, kon, slova);
        this.ip = ip;
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        IpAdresa adresa = new IpAdresa(ip); // predpokladam, ze se tam konaj kontroly na spravnost IP
        int ret = pc.posliPing(adresa);
        System.out.println("navratovy kod: " + ret);


        if (ret == 0) {
            String s = "";
            if (pc instanceof LinuxPocitac) {
                s += "PING " + adresa.vypisIP() + " 56(84) bytes of data.\n";
                for (int i = 0; i < 4; i++) {
                    s += "64 bytes from " + adresa.vypisIP() + ": icmp_seq=" + i + " ttl=255 time=118 ms\n";
                }
                s += "--- " + adresa.vypisIP() + " ping statistics ---\n"
                        + "1 packets transmitted, 1 received, 0% packet loss, time 0ms\n"
                        + "rtt min/avg/max/mdev = 2.284/2.284/2.284/0.000 ms\n";




            }

            if (pc instanceof CiscoPocitac) {

                s += "\nType escape sequence to abort.\n";
                s += "Sending 5, 100-byte ICMP Echos to " + adresa.vypisIP() + ", timeout is 2 seconds:\n";
                s += "!!!!\n";
                s += "Success rate is 100 percent (5/5), round-trip min/avg/max = 1/2/4 ms\n";

            }
            kon.posli(s);
        }
    }
}
