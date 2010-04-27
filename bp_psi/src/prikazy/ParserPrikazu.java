/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import java.util.List;
import pocitac.*;

/**
 * Metoda zpracujRadek(String s) prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej()
 * rozseka na jednotlivy slova. Pak se testuje,
 * @author neiss & haldyr
 */
public abstract class ParserPrikazu {

    protected String radek;
    protected List<String> slova; //seznam jednotlivejch slov ze vstupniho stringu
    protected AbstraktniPocitac pc;
    protected Konsole kon;

    /**
     * Konstruktor. Kazda konsole si uchovava prave jeden parser. Tenhle konstruktor se tedy vola
     * v konstruktoru konsole.
     * @param pc
     * @param kon
     */
    public ParserPrikazu(AbstraktniPocitac pc, Konsole kon) {
        this.pc = pc;
        this.kon = kon;
    }

    /**
     * Prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej() rozseka na jednotlivy slova.
     * Pak se testuje, jestli prvni slovo je nazev nejakyho podporovanyho prikazu, jestlize ne, tak se vypise
     * "command not found", jinak se preda rizeni tomu spravnymu prikazu.
     * @param s
     */
    public abstract void zpracujRadek(String s);

    /**
     * Tahlecta metoda rozseka vstupni string na jednotlivy slova (jako jejich oddelovac se bere mezera)
     * @autor neiss
     */
    @Deprecated
    protected void rozsekej() {
        int i = 0;
        int j = 0;
        while (j < radek.length()) {
            j = radek.indexOf(' ', i);
            if (j == -1) {
                j = radek.length();
            }
            if (i != j) {
                slova.add(radek.substring(i, j)); //pri pridavani nepridavam vic mezer
            }
            i = j + 1;
        }
    }

    /**
     * Tahlecta metoda rozseka vstupni string na jednotlivy slova (jako jejich oddelovac se bere mezera)
     * @autor haldyr
     */
    protected void rozsekejLepe() {

        radek = radek.trim(); // rusim bile znaky na zacatku a na konci

        String[] bileZnaky = {" ", "\t"};
        for (int i = 0; i < bileZnaky.length; i++) { // odstraneni bylych znaku
            while (radek.contains(bileZnaky[i] + bileZnaky[i])) {
                radek = radek.replace(bileZnaky[i] + bileZnaky[i], bileZnaky[i]);
            }
        }

        String[] pole = radek.split(" ");
        for (String s : pole) {
            slova.add(s);
        }
    }

    /**
     * V teto metode je se kontroluje, zda neprisel nejaky spolecny prikaz, jako napr. save ci v budoucnu jeste jine.
     * @return vrati true, kdyz konkretni parser uz nema pokracovat dal v parsovani (tj. jednalo se o spolecny prikaz)
     * @autor haldyr
     */
    protected boolean spolecnePrikazy(boolean debug) {
        AbstraktniPrikaz prikaz;

        if (slova.get(0).equals("uloz") || slova.get(0).equals("save")) {
            prikaz = new Uloz(pc, kon, slova);
            return true;
        }
        if (debug) {
            if (slova.get(0).equals("nat")) {
                kon.posliPoRadcich(pc.natTabulka.vypisZaznamyDynamicky(), 10);
                return true;
            }
        }
        return false;
    }
}
