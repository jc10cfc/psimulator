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
        nastavPrikaz();
        vykonejPrikaz();
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
}
