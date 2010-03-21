/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Pomocna trida pro vytvareni uz vlastnich pocitacu.
 * @author haldyr
 */
public class PocitacBuilder {

    String jmeno = "";
    String typ = "";
    List<String[]> rozhrani;
    List<String[]> routovaciTabulka;

    public PocitacBuilder() {
        rozhrani = new ArrayList<String[]>();
        routovaciTabulka = new ArrayList<String[]>();
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "PC:  "+jmeno +"\n";
        ret += "typ: "+typ +"\n";
        for (String[] pole : rozhrani) {
            for (String s : pole) {
                ret += s +"\n";
            }
        }

        if (routovaciTabulka.size() == 0) return ret;
        ret += "\n";
        for (String[] pole : routovaciTabulka) {
            for (String s : pole) {
                ret += s + "\n";
            }
        }
        ret += "\n";

        return ret;
    }
    
    
}
