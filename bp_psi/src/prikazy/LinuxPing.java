/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import datoveStruktury.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pocitac.*;

/**
 *
 * @author neiss
 */
public class LinuxPing extends AbstraktniPing{
    
    IpAdresa cil; //adresa, na kterou ping posilam

    public LinuxPing(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
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
        if(pc.posliNovejPaket(cil, 8, 0, 0, 0, this)){
            for (int i=1;i<1;i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                pc.posliNovejPaket(cil, 8, 0, 0, i, this);
            }
        }else{
            kon.posliRadek("connect: Network is unreachable");
        }
        
    }

    /**
     * Slouzi ke zpracovani prichoziho paketu (icmp reply, paket nemohl byt dorucen)
     */
    public void zpracujPaket(Paket p){
        if(p.typ==0){
            kon.posliRadek("64 bytes from "+p.zdroj.vypisAdresu()+": icmp_seq="+
                    p.icmp_seq+" ttl="+p.ttl+" time="+((double)Math.round(p.cas*1000))/1000+" ms");
        }else if(p.typ==3){
            if(p.kod==0){
                kon.posliRadek("From "+p.zdroj.vypisAdresu()+": icmp_seq="+
                    p.icmp_seq+" Destination Net Unreachable");
            }
        }else if(p.typ==11){
            kon.posliRadek("From "+p.zdroj.vypisAdresu()+" icmp_seq="+p.icmp_seq +"Time to live exceeded");
        }
    }

}
