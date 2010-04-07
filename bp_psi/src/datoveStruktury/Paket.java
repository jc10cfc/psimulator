/*
 * Typy a kódy icmp paketů viz:
 *      http://www.root.cz/clanky/sokety-a-c-icmp-protokol/
 *      http://www.svetsiti.cz/view.asp?rubrika=Technologie&clanekID=36
 */

package datoveStruktury;

import prikazy.*;

/**
 * Třída representující paket pro ping, kterej si budou počítače mezi sebou předávat.
 * @author neiss
 */
public class Paket {
    /**
     * Typy icmp paketu (ty, ktery pouzivam):<br />
     * 0  	Ozvěna (icmp reply)<br />
     * 3 	Signalizace nedoručení IP paketu. <br />
     * 8 	Žádost o ozvěnu (icmp request) <br />
     * 11 	Čas (ttl) vypršel<br />
     */
    public int typ;

    /**
     * Podtypy icmp paketu, pro kazdej typ jinej vyznam, u nas to ma vyznam jen pro typ 3. Jinak defaultne vetsinou
     * nula.<br />
     * 0 – nedosažitelná síť (network unreachable)<br />
     * 1 - nedosažitelný uzel (host unreachable)<br />
     * 2 - nedosažitelný protokol (protocol unreachable)<br />
     * 3 – nedosažitelný port (port unreachable)<br />
     * 4 - nedosažitelná síť (network unreachable)<br />
     * 5 – nutná fragmentace, ale není povolena<br />
     * 6 – neznámá cílová síť (destination network unknown) <br />
     */
    public int kod;

    public IpAdresa zdroj;
    public IpAdresa cil;
    public int ttl;
    public int icmp_seq;
    public double cas; //cas, kterej jakoby paket bezi
    public AbstraktniPing prikaz; //paket si nese odkaz na ping, kterej ho poslal, aby po stastnym navratu
                                        //moh o sobe vypsat informace


    /**
     *
     * @param zdroj
     * @param cil
     * @param typ <br /> 0 - reply<br />3 - nedorucen<br />8 - request <br /> 11 - ttl vyprsel<br />
     * @param kod <br /> 0 - network unreachable <br /> 1 - host unreachable <br />
     * @param cas - zadava se v miliseknudach
     * @param icmp_seq
     * @param ttl
     * @param prikaz
     */
    public Paket(IpAdresa zdroj, IpAdresa cil, int typ, int kod, double cas, int icmp_seq,
            int ttl, AbstraktniPing prikaz){
        this.cil=cil;
        this.zdroj=zdroj;
        this.typ=typ;
        this.ttl=ttl;
        this.icmp_seq=icmp_seq;
        this.kod=kod; //defaultni
        this.prikaz=prikaz;
        this.cas=cas;
    }

    @Override
    public String toString(){
        return "z: "+zdroj.vypisAdresu()+"; c: "+cil.vypisAdresu()+"; typ: "+typ+"; ttl: "+ttl;
    }



}
