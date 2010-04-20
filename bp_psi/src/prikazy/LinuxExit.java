/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import java.util.List;
import pocitac.*;

/**
 *
 * @author neiss
 */
public class LinuxExit extends AbstraktniPrikaz{

    public LinuxExit(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc,kon,slova);
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        kon.posliRadek("logout");
        if(slova.size()==2 ){
            if (! jeInteger(slova.get(1))) kon.posliRadek("-bash: exit: "+slova.get(1)+": numeric argument required");
        }
        if(slova.size()>2 ){
            kon.posliRadek("-bash: exit: too many arguments");
            return;
        }
        kon.ukonciSpojeni();
    }
}
