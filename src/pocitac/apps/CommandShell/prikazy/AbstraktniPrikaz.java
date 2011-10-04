/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac.apps.CommandShell.prikazy;

import pocitac.apps.CommandShell.prikazy.Abstraktni;
import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;

/**
 * Predek vsech, linuxových i ciscových prikazu. Dulezité jsou jen promenné a abstraktni metoda vykonejPrikaz().
 * Ostatni jsou nejaké uzitecné statické metody.
 * @author Tomáš Pitřinec
 */
public abstract class AbstraktniPrikaz extends Abstraktni {

    protected AbstraktniPocitac pc;
    protected CommandShell kon;

    public AbstraktniPrikaz(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(slova);
        this.pc = pc;
        this.kon = kon;
    }

    
    /**
     * Tato metoda vykona vlastni prikaz.
     */
    protected abstract void vykonejPrikaz();
}
