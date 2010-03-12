/*
 * Gegründet am Mittwoch 10.3.2010
 */


package datoveStruktury;

import java.util.AbstractList;
import java.util.LinkedList;
import pocitac.AbstractPocitac;
import pocitac.SitoveRozhrani;

/**
 * Trida, ktera representuje routovaci tabulku pocitace, jak linuxoveho, tak ciscoveho.
 * @author neiss
 */
public class RoutovaciTabulka {
    
    /**
     * Representuje jeden radek v routovaci tabulce
     */
    private class Zaznam{
        IpAdresa adresat;
        IpAdresa brana;
        SitoveRozhrani rozhrani;

        public Zaznam(IpAdresa adresat, SitoveRozhrani rozhrani){
            this.adresat=adresat;
            this.rozhrani=rozhrani;
        }
        public Zaznam(IpAdresa adresat, IpAdresa brana){
            this.adresat=adresat;
            this.brana=brana;
        }
    }

    private AbstractList<Zaznam>radky; //jednotlive radky routovaci tabulky
    private AbstractPocitac pc; //odkaz na pocitac, mozna nebude potreba

    /**
     * V konstruktoru se hazi odkaz na pocitac, aby byl prostup k jeho rozhranim.
     * @param pc
     */
    public RoutovaciTabulka(AbstractPocitac pc){
        radky=new LinkedList<Zaznam>();
        this.pc=pc;
    }

    /**
     * Tahleta metoda hleda zaznam v routovaci tabulce, ktery odpovida zadane IP adrese. Slouzi predevsim pro
     * samotne routovani.
     * @param cil - IP, na kterou je paket posilan
     * @return null - nenasel se zadnej zaznam, kterej by se pro tuhle adresu hodil
     */
    public SitoveRozhrani najdiRozhrani(IpAdresa cil){
        int r=najdiOdpovidajiciRadek(cil);
        if (r >= 0) return radky.get(r).rozhrani;
        return null;
    }
    /**
     * Tahleta metoda hleda zaznam v routovaci tabulce, ktery odpovida zadane IP adrese.
     * @param vstupni
     * @return cislo radku odpovidajici zadane IP nebo -1, kdyz zadnej radek neodpovida
     */
    public int najdiOdpovidajiciRadek(IpAdresa vstupni){
        for (int i=0;i<radky.size();i++){
            if (radky.get(i).adresat.jeStejnyCisloSite(vstupni)){
                return i;
            }
        }
        return -1;
    }

    /**
     * prida novej zaznam na urcenou posici
     * @param adresat
     * @param rozhr
     * @param posice
     */
    public void pridejZaznam(IpAdresa adresat, SitoveRozhrani rozhr, int posice){
        radky.add(posice,new Zaznam(adresat, rozhr));
    }

    /**
     * prida novej zaznam na urcenou posici
     * @param adresat
     * @param brana
     * @param posice
     */
    public void pridejZaznam(IpAdresa adresat, IpAdresa brana, int posice){
        radky.add(posice,new Zaznam(adresat, brana));
    }

}
