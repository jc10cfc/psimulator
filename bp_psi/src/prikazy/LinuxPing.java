/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import datoveStruktury.*;
import java.util.List;
import pocitac.*;
import vyjimky.SpatnaAdresaException;

/**
 *
 * @author neiss
 */
public class LinuxPing extends AbstraktniPing{
    
    //parametry prikazu:
    IpAdresa cil; //adresa, na kterou ping posilam
    int count=1; //pocet paketu k poslani, zadava se prepinacem -c
    int size=64; //velikost paketu k poslani, zadava se -s
    int interval; //interval mezi odesilanim paketu, zadava se -i, narozdil od vrchnich je dulezitej
    int ttl=64; //zadava se prepinacem -t
    boolean minus_q=false; //tichy vystup, vypisujou se jen statistiky, ale ne jednotlivy pakety
    boolean minus_b=false; //dovoluje pingat na broadcastovou adresu
    //dalsi prepinace, ktery bych mel minimalne akceptovat: -a, -v

    //parametry parseru:
    private String slovo; //slovo parseru, se kterym se zrovna pracuje
    /**
     * 0 - v poradku
     * 1 - nezadano nic krome slova ping
     * 2 - spatna adresa
     */
    private int navratovyKod=0;

    public LinuxPing(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
    }

    /**
     * Cte prikaz, zatim cte jenom IP adresu a nic nekontroluje.
     */
    private void parsujPrikaz(){
        slovo=dalsiSlovo();
        if(slovo.equals("")){
            navratovyKod |=1;
            vypisNapovedu(); //vypise napovedu a skonci
        }else{
            while( slovo.charAt(0)=='-' && slovo.length()>1 ){ //cteni prepinacu
                zpracujPrepinace(slovo);
                slovo=dalsiSlovo();
            }
            try{ //cteni ip adresy
                cil=new IpAdresa(slovo);
            }catch(SpatnaAdresaException ex){
                navratovyKod |=2;
                kon.posliRadek("ping: unknown host "+slovo);
            }
        }

    }

    /**
     * Zpracovava prepinace. Predpoklada, ze krome minusu bude mit jeste aspon jeden dalsi znak.
     */
    private void zpracujPrepinace(String ret) {
        int uk=1; //ukazatel na znak v tom Stringu
        while (uk<ret.length()){
            //if()
        }
    }

    private void vypisNapovedu() {
        kon.posliRadek("Usage: ping [-LRUbdfnqrvVaA] [-c count] [-i interval] [-w deadline]");
        kon.posliRadek("            [-p pattern] [-s packetsize] [-t ttl] [-I interface or address]");
        kon.posliRadek("            [-M mtu discovery hint] [-S sndbuf]");
        kon.posliRadek("            [ -T timestamp option ] [ -Q tos ] [hop1 ...] destination");
    }

    @Override
    protected void vykonejPrikaz() {
        if(pc.posliNovejPaket(cil, 8, 0, 0, 0,ttl, this)){
            for (int i=1;i<1;i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                pc.posliNovejPaket(cil, 8, 0, 0, i,ttl,  this);
            }
        }else{
            kon.posliRadek("connect: Network is unreachable");
        }
        
    }

    /**
     * Slouzi ke zpracovani prichoziho paketu (icmp reply, paket nemohl byt dorucen)
     */
    @Override
    public void zpracujPaket(Paket p) {
        if (p.typ == 0) {
            kon.posliRadek("64 bytes from " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                    p.icmp_seq + " ttl=" + p.ttl + " time=" + ((double) Math.round(p.cas * 1000)) / 1000 + " ms");
        } else if (p.typ == 3) {
            if (p.kod == 0) {
                kon.posliRadek("From " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                        p.icmp_seq + " Destination Net Unreachable");
            } else if (p.kod == 1) {
                kon.posliRadek("From " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                        p.icmp_seq + " Destination Host Unreachable");
            }
        } else if (p.typ == 11) {
            kon.posliRadek("From " + p.zdroj.vypisAdresu() + " icmp_seq=" + p.icmp_seq + "Time to live exceeded");
        }
    }

}
