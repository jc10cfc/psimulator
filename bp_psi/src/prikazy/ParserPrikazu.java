/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import java.util.LinkedList;
import java.util.List;
import pocitac.*;

/**
 * Metoda zpracujRadek(String s) prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej() rozseka na jednotlivy
 * slova. Pak se testuje,
 * @author neiss
 */
public class ParserPrikazu {
    
    private String radek;
    private List<String> slova; //seznam jednotlivejch slov ze vstupniho stringu
    private AbstractPocitac pc;
    private Konsole kon;

    public ParserPrikazu(AbstractPocitac pc,Konsole kon){
        this.pc=pc;
        this.kon=kon;
    }

    /**
     * Prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej() rozseka na jednotlivy slova.
     * Pak se testuje, jestli prvni slovo je nazev nejakyho podporovanyho prikazu, jestlize ne, tak se vypise
     * "command not found", jinak se preda rizeni tomu spravnymu prikazu.
     * @param s
     */
    public void zpracujRadek(String s){
        AbstraktniPrikaz pr;
        radek=s;
        slova=new LinkedList<String>();        

        //rozsekej();
        rozsekejLepe();

        if(slova.size()<1)return;

        if (slova.get(0).equals("")) {
            return; // prazdny Enter
        }

        if(slova.get(0).equals("exit")){
            pr=new Exit(pc, kon, slova);
        }
        else if(slova.get(0).equals("ifconfig")){
            pr=new Ifconfig(pc, kon, slova);
        }
        else{
            kon.posliRadek("bash: "+slova.get(0)+": command not found");
        }



    }

    /**
     * Tahlecta metoda rozseka vstupni string na jednotlivy slova (jako jejich oddelovac se bere mezera)
     */
    private void rozsekej(){
        int i=0;
        int j=0;
        while(j<radek.length()){
            j=radek.indexOf(' ',i);
            if(j==-1)j=radek.length();
            if(i!=j) slova.add(radek.substring(i,j)); //pri pridavani nepridavam vic mezer
            i=j+1;
        }
    }

    private void rozsekejLepe() {
        String [] pole = radek.split(" ");
        for (String s : pole) {
            slova.add(s);
        }
    }

    /*
    public void setRadek(String s) {
        radek = s;
    }

    public void getRadek() {
        System.out.println(radek);
    }
     */
}
