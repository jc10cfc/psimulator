/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import datoveStruktury.*;
import java.util.List;
import pocitac.*;

/**
 *
 * @author neiss
 */
public class LinuxPing extends AbstraktniPrikaz{
    
    IpAdresa cil; //adresa, na kterou ping posilam

    public LinuxPing(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
    }

    /**
     * Cte prikaz, zatim cte jenom IP adresu a nic nekontroluje.
     */
    private void parsujPrikaz(){
        if (slova.size()<2){
            vypisNapovedu();
        }else{
            cil=new IpAdresa(slova.get(1));
        }
    }

    private void vypisNapovedu(){
        kon.posliRadek("Tohle je napoveda.");
    }

    @Override
    protected void vykonejPrikaz() {
        //nejdriv je potreba zjistit, jakym rozhranim se bude paket posilat, abych vedel, jakou IP adresu
            //zdroje mam do toho paketu dat
        pc.posliPing(cil, 8, 0);
    }

}
