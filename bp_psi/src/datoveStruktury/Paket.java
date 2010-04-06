/*
 * Typy a kódy icmp paketů viz:
 *      http://www.root.cz/clanky/sokety-a-c-icmp-protokol/
 *      http://www.svetsiti.cz/view.asp?rubrika=Technologie&clanekID=36
 */

package datoveStruktury;

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
    int typ;

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
    int kod;

    IpAdresa zdroj;
    IpAdresa cil;

}
