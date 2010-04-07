/*
 * Typy a kódy icmp paketů viz:
 *      http://www.root.cz/clanky/sokety-a-c-icmp-protokol/
 *      http://www.svetsiti.cz/view.asp?rubrika=Technologie&clanekID=36
 */

package datoveStruktury;

import prikazy.AbstraktniPrikaz;

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
    public int cas; //cas, kterej jakoby paket bezi
    public AbstraktniPrikaz prikaz; //paket si nese odkaz na ping, kterej ho poslal, aby po stastnym navratu
                                        //moh o sobe vypsat informace
    
    public Paket(IpAdresa zdroj, IpAdresa cil, int typ, int kod, int ttl, int icmp_seq){
        this.cil=cil;
        this.zdroj=zdroj;
        this.typ=typ;
        this.ttl=ttl;
        this.icmp_seq=icmp_seq;
        this.kod=kod; //defaultni
    }



}
