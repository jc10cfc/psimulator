/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import java.util.LinkedList;
import java.util.List;
import pocitac.*;

/**
 *
 * @author neiss
 */
public class ParserPrikazu {
    
    private String radek;
    private List<String> slova;
    private AbstractPocitac pc;
    private Konsole kon;

    public ParserPrikazu(AbstractPocitac pc,Konsole kon){
        this.pc=pc;
        this.kon=kon;
    }

    public void zpracujRadek(String s){
        AbstraktniPrikaz pr;
        radek=s;
        slova=new LinkedList<String>();
        rozsekej();
        if(slova.size()<1)return;

        if(slova.get(0).equals("exit")){
            pr=new Exit(pc, kon, slova);
        }
        else if(slova.get(0).equals("ifconfig")){
            pr=new Ifconfig(pc, kon, slova);
        }
        else{
            kon.posli("bash: "+slova.get(0)+": command not found");
        }



    }

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

}
