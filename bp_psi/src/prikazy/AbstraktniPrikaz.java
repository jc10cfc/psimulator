/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import java.util.List;
import pocitac.*;

/**
 * Predek vsech, linuxovejch i cicsovejch prikazu. Dulezity jsou jen promenny a abstraktni metoda vykonejPrikaz().
 * Ostatni jsou jen nejaky uzitecny staticky metody.
 * @author neiss
 */
public abstract class AbstraktniPrikaz extends Abstraktni {

    protected AbstraktniPocitac pc;
    protected Konsole kon;
//    protected List<String> slova;
//    private int uk; //ukazatel do seznamu slov

    public AbstraktniPrikaz(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(slova);
        this.pc = pc;
        this.kon = kon;
//        this.slova = slova;
//        uk=1; //ukazatel do seznamu slov, prvni slovo je nazev prikazu, ukazuje se az za nej
    }

    
    /**
     * Tato metoda vykona vlastni prikaz.
     */
    protected abstract void vykonejPrikaz();
}
