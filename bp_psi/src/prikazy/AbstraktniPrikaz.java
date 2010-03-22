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
public abstract class AbstraktniPrikaz {

    protected AbstractPocitac pc;
    protected Konsole kon;
    protected List<String> slova;

    public AbstraktniPrikaz(AbstractPocitac pc, Konsole kon, List<String> slova) {
        this.pc = pc;
        this.kon = kon;
        this.slova = slova;
    }

    
    /**
     * Tato metoda vykona vlastni prikaz.
     */
    protected abstract void vykonejPrikaz();

//*****************************************************************************************************************
//staticky metody:

    public static boolean jeInteger(String ret) {
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
    public static boolean moznaIP(String ret){
        if (!ret.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            return false;
        }
        return true;
    }

    public static boolean moznaIPsMaskou(String ret){
        if (!ret.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}/[0-9]++")) {
            return false;
        }
        return true;
    }

    /**
     * Dorovna zadanej String mezerama na zadanou dylku. Kdyz je String delsi nez zadana dylka, tak nic neudela
     * a String vrati nezmenenej. Protoze String se nikdy nemeni, ale vzdy se vytvori novej, se zadavany, Stringem
     * se nic nestane.
     * @param ret
     * @param dylka
     * @return
     */
    public static String zarovnej(String ret, int dylka){
        int dorovnat = dylka-ret.length();
        for(int i=0;i<dorovnat;i++){
            ret=ret+" ";
        }
        return ret;
    }
}
