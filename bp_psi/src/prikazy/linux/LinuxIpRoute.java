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
public class LinuxIpRoute extends AbstraktniPrikaz {

    public LinuxIpRoute(AbstraktniPocitac pc, Konsole kon, List<String> slova, LinuxIp puv) {
        super(pc, kon, slova);
        vykonejPrikaz();
        this.puv=puv;
    }
    
    private LinuxIp puv;

    @Override
    protected void vykonejPrikaz() {

    }
}
