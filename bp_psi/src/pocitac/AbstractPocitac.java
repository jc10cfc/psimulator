/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import java.util.ArrayList;
import java.util.List;


/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */

public abstract class AbstractPocitac {
    public Komunikace komunikace;
    public List <SitoveRozhrani>rozhrani;
    public String jmeno; //jmeno pocitace


    @Deprecated
    public AbstractPocitac(String jmeno){
        komunikace=new Komunikace(3567, this);
        rozhrani=new ArrayList<SitoveRozhrani>();
        this.jmeno=jmeno;
    }

    public AbstractPocitac(String jmeno, int port) {
        this.jmeno=jmeno;
        rozhrani=new ArrayList<SitoveRozhrani>();
        komunikace = new Komunikace(port, this);
    }

    @Deprecated
    public AbstractPocitac(int port) {
        vypis("Pouziva se deprecated metoda AbstractPocitac(int port)");
        komunikace = new Komunikace(port, this);
        rozhrani=new ArrayList<SitoveRozhrani>();
    }
    
    public void pridejRozhrani(SitoveRozhrani sr){
        rozhrani.add(sr);
    }

    @Deprecated
    public void nastavJmeno(String jm) {
        vypis("Pouziva se deprecated metoda nastavJmeno(String jm)");
        this.jmeno = jm;
    }

    /**
     * Tahle metoda vypisuje na standartni vystup. Pouzivat pro vypisy v Komunikaci, Konsoli i Parseru atd.
     * pro snadnejsi debugovani, aby se vedelo, co kterej pocitac dela.
     * @param ret
     */
    public void vypis(String ret){
        System.out.println("("+jmeno+":) "+ret);
    }

    // zatim pomocna metoda, pak se muze smazat
    public void vypisRozhrani(){

        for (SitoveRozhrani iface : rozhrani) {
            System.out.println("("+jmeno+":) "+iface.jmeno);
            System.out.println("("+jmeno+":) "+iface.ip.vypisIP());
            System.out.println("("+jmeno+":) "+iface.ip.vypisMasku());
            System.out.println("("+jmeno+":) "+iface.macAdresa);
            System.out.println("("+jmeno+":)");
        }
    }
}
