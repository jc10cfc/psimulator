package Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Pomocna trida pro ukladani pocitacu pri nacitani z konfiguraku.
 * @author haldyr
 */
public class PocitacBuilder {

    String jmeno = "";
    String typ = "";
    List<String[]> rozhrani;
    List<String[]> routovaciTabulka;
    boolean ip_forward = false;
    List<String[]> pool;
    List<String[]> accessList;
    List<String[]> poolAccess;
    List<String[]> staticke;

    public PocitacBuilder() {
        rozhrani = new ArrayList<String[]>();
        routovaciTabulka = new ArrayList<String[]>();
        pool = new ArrayList<String[]>();
        accessList = new ArrayList<String[]>();
        poolAccess = new ArrayList<String[]>();
        staticke = new ArrayList<String[]>();
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "PC:  " + jmeno + "\n";
        ret += "typ: " + typ + "\n";
        vypisPole(rozhrani, "rozhrani");
        vypisPole(routovaciTabulka, "routovaci tabulka");
        vypisPole(pool, "pool");
        vypisPole(poolAccess, "poolAccess");
        vypisPole(accessList, "access-list");
        vypisPole(staticke, "staticke");

        return ret;
    }

    /**
     * Pomocna metoda pro vypis nactenych veci pro vytvoreni pocitace.
     * @param seznam seznam retezcu urcenych pro vypis
     * @param jmeno, ktere identifikuje, co je to za seznam
     * @return
     */
    private String vypisPole(List<String[]> seznam, String jmeno) {
        String s = "";
        int i;

        s += jmeno + ":\n";
        for (String[] pole : seznam) {
            i = 0;
            for (String nove : pole) {
                i++;
                if (i==0) s += nove;
                else s += "," + nove;
            }
            s += "\n";
        }
        s += "\n";
        return s;
    }
}
