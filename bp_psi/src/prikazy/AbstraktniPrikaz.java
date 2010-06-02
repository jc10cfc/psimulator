/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import java.util.List;
import pocitac.*;

/**
 * Predek vsech, linuxových i ciscových prikazu. Dulezité jsou jen promenné a abstraktni metoda vykonejPrikaz().
 * Ostatni jsou nejaké uzitecné statické metody.
 * @author Tomáš Pitřinec
 */
public abstract class AbstraktniPrikaz extends Abstraktni {

    protected AbstraktniPocitac pc;
    protected Konsole kon;

    public AbstraktniPrikaz(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(slova);
        this.pc = pc;
        this.kon = kon;
    }

    
    /**
     * Tato metoda vykona vlastni prikaz.
     */
    protected abstract void vykonejPrikaz();
}
