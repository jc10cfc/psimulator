/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.linux;

import prikazy.*;
import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;

/**
 *
 * @author Tomáš Pitřinec
 */
public class LinuxExit extends AbstraktniPrikaz{

    public LinuxExit(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
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
