package prikazy;

import java.util.List;

/**
 * Predek uplne vseho, dedi od nej AbstraktniPrikaz a ParserPrikazu (cili temer vsechny tridy).
 * @author Stanislav Řehák
 */
public abstract class Abstraktni {

    protected List<String> slova;
    private int uk; //ukazatel do seznamu slov

    public Abstraktni(List<String> slova) {
        this.slova = slova;
        uk = 1;//ukazatel do seznamu slov, prvni slovo je nazev prikazu, ukazuje se az za nej
    }

    /**
     * Tahle metoda postupne vraci slova, podle vnitrni promenny uk. Pocita s tim, ze prazdny
     * retezec ji nemuze prijit.
     * @return prazdny retezec, kdyz je na konci seznamu
     */
    protected String dalsiSlovo() {
        String vratit;
        if (uk < slova.size()) {
            vratit = slova.get(uk);
            uk++;
        } else {
            vratit = "";
        }
        return vratit;
    }

    /**
     * Tahle metoda postupne dela to samy, co horni, ale nezvysuje citac.
     * Slouzi, kdyz je potreba zjistit, co je dal za slovo, ale zatim jenom zjistit.
     * @return prazdny retezec, kdyz je na konci seznamu
     */
    protected String dalsiSlovoAleNezvetsujCitac() {
        String vratit;
        if (uk < slova.size()) {
            vratit = slova.get(uk);
        } else {
            vratit = "";
        }
        return vratit;
    }

    protected int getUk() {
        return uk;
    }

    //*****************************************************************************************************************
    //staticky metody:
    public static boolean jeInteger(String ret) {
        try {
            int a = Integer.parseInt(ret);
        } catch (Exception e) {
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
    public static boolean moznaIP(String ret) {
        if (!ret.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            return false;
        }
        return true;
    }

    public static boolean moznaIPsMaskou(String ret) {
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
    public static String zarovnej(String ret, int dylka) {
        int dorovnat = dylka - ret.length();
        for (int i = 0; i < dorovnat; i++) {
            ret = ret + " ";
        }
        return ret;
    }

    /**
     * Ceka x milisekund.
     * @param miliseconds
     */
    public static void cekej(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Rozlozi cislo na mocniny dvojky.
     * Pouzivam pri vypisovani navratovyho kodu.
     * @param c
     * @return
     */
    public static String rozlozNaMocniny2(int c) {
        String vratit = "";
        for (int i = 0; i < 31; i++) {
            if ((c & (1 << i)) != 0) {
                if (vratit.equals("")) {
                    vratit += (1 << i);
                } else {
                    vratit += " + " + (1 << i);
                }
            }
        }
        if (vratit.equals("")) {
            vratit = "0";
        }
        return vratit;
    }

    public static String rozlozNaLogaritmy2(int c) {
        String vratit = "";
        for (int i = 0; i < 31; i++) {
            if ((c & (1 << i)) != 0) {
                if (vratit.equals("")) {
                    vratit += (log2(1 << i));
                } else {
                    vratit += ", " + (log2(1 << i));
                }
            }
        }
        if (vratit.equals("")) {
            vratit = "Zadny chybovy kod nebyl zadan.";
        }
        return vratit;
    }

    private static int log2(int num) {
        return (int) (Math.log(num) / Math.log(2));
    }

    public static int md(int c){
        return (1<<c);
    }

    /**
     * Zaokrouhluje na tri desetinna mista.
     * @param d
     * @return
     */
    public static double zaokrouhli(double d) {
        return ((double) Math.round(d * 1000)) / 1000;
    }
}
