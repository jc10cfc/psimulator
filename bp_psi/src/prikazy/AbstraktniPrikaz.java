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
public abstract class AbstraktniPrikaz {

    protected AbstractPocitac pc;
    protected Konsole kon;
    protected List<String> slova;

    public AbstraktniPrikaz(AbstractPocitac pc, Konsole kon, List<String> slova) {
        this.pc = pc;
        this.kon = kon;
        this.slova = slova;
    }

    protected abstract void nastavPrikaz();
    protected abstract void vykonejPrikaz();

    boolean jeInteger(String ret) {
        try {
            int a = Integer.parseInt(ret);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Vraci true, kdyz string obsahuje 4 maximalne trojciferny cisla oddeleny teckou. Neresi se tedy spravnej
     * rozsah cisel.
     * @param ret
     * @return
     */
    boolean moznaIP(String ret){
        if (!ret.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            return false;
        }
        return true;
    }

    boolean moznaIPsMaskou(String ret){
        if (!ret.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}/[0-9]++")) {
            return false;
        }
        return true;
    }
}
