/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

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
        System.out.println("navratovy kod: "+ret);
    }

}
