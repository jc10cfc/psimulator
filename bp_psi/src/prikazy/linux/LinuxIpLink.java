/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.linux;
import java.util.List;
import pocitac.*;
import prikazy.*;
/**
 *
 * @author neiss
 */
public class LinuxIpLink extends AbstraktniPrikaz{
    public LinuxIpLink(AbstraktniPocitac pc, Konsole kon, List<String> slova, LinuxIp puv) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
        this.puv=puv;
    }
    private LinuxIp puv;

    private void parsujPrikaz() {

    }

    @Override
    protected void vykonejPrikaz() {

    }

}
