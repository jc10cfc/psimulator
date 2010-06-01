/*
 * http://jiri.patera.name/!old-data/ppi/ping-linux.html - pekny
 * http://cs.wikipedia.org/wiki/Ping - jen wikipedie
 */

package prikazy.linux;

import prikazy.*;
import datoveStruktury.*;
import java.util.List;
import pocitac.*;
import vyjimky.SpatnaAdresaException;

/**
 *
 * @author Tomáš Pitřinec
 */
public class LinuxPing extends AbstraktniPing{

    private boolean ladeni = false;
    
    //parametry prikazu:
    IpAdresa cil; //adresa, na kterou ping posilam
    int count=4; //pocet paketu k poslani, zadava se prepinacem -c
    int size=56; //velikost paketu k poslani, zadava se -s
    double interval=1; //interval mezi odesilanim paketu v sekundach, zadava se -i, narozdil od vrchnich je dulezitej
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
     * 4 - chyba v ciselny hodnote prepinace
     * 8 - neznamy prepinac
     * 16 - adresa nebyla zadana
     */
    private int navratovyKod=0;

    public LinuxPing(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
    }

    @Override
    protected void vykonejPrikaz() {
        if (ladeni){
            kon.posliRadek(toString());
            kon.posliRadek("--------------------");
        }

        if(navratovyKod != 0) return; //neni-li vsechno v poradku, nic se nekona

        /*
         * Neodesilani zkusebniho paketu s icmp_seq -1. Je jen zkusebni, metoda odesliNovejPaket(...)
         * ho zablokuje a nikam se NEPOSILA ani nepocita.
         */
        if (pc.posliIcmpRequest(cil, -1, ttl, this)) {
            kon.posliRadek("PING " + cil.vypisAdresu() + " (" + cil.vypisAdresu() + ") " //vypsani 1. radku
                    + size + "(" + (size + 28) + ") bytes of data.");
        } else {
            kon.posliRadek("connect: Network is unreachable");
            return; //dalsi pakety se neposilaj, prvni radek se nevypisuje
        }
        
        /*
         * posilani dalsich pingu:
         */
        for (int i = 0; i < count; i++) {
            int icmp_seq=(i+1) % 65536; //zacina to od jednicky a po 65535 se jede znova od nuly
            if (pc.posliIcmpRequest(cil, icmp_seq, ttl, this)) {
                //paket se odeslal
            } else {
                kon.posliRadek("ping: sendmsg: Network is unreachable");
            }
            odeslane++;
            if(i!=count-1) //cekani po zadany interval - naposled se neceka
                AbstraktniPrikaz.cekej((int) (interval * 1000));
        }

        /*
         * vypisovani konecnej statistik:
         */
        aktualizujStatistiky();
        if(ladeni){
            kon.posliRadek("ztrata: "+ztrata+", odeslane: "+odeslane+", prijate: "+prijate);
        }
        int cas = (int) (odeslane * interval * 1000 + Math.random() * 10); //na okrasu musim vymyslet nejakej cas
        kon.posliRadek("");
        kon.posliRadek("--- " + cil.vypisAdresu() + " ping statistics ---");
        if (errors == 0) {//errory nebyly - tak se nevypisujou
            kon.posliRadek(odeslane + " packets transmitted, " + prijate + " received, " +
                    ztrata + "% packet loss, time " + cas + "ms");
        } else { //vypis i s errorama
            kon.posliRadek(odeslane + " packets transmitted, " + prijate + " received, +" + errors + " errors, " +
                    ztrata + "% packet loss, time " + cas + "ms");
        }
        if (prijate > 0) { //aspon jeden prijaty paket - vypisuji se statistiky
            double mdev = zaokrouhli((((avg - min) + (max - avg)) / 2) * 0.666); //ma to bejt stredni odchylka,
            //je tam jen na okrasu, tak si ji pocitam po svym =)
            kon.posliRadek("rtt min/avg/max/mdev = " + zaokrouhli(min) + "/" + zaokrouhli(max) + "/" +
                    zaokrouhli(avg) + "/" + mdev + " ms");
        } else { // neprijat zadny paket, statistiky se nevypisuji
            kon.posliRadek(", pipe 3");
        }
        
    }

    /**
     * Slouzi ke zpracovani prichoziho paketu (icmp reply, paket nemohl byt dorucen)
     */
    @Override
    public void zpracujPaket(Paket p) {
        if (p.typ == 0) {
            if(!minus_q)
                kon.posliRadek((size+8)+" bytes from " + p.zdroj.vypisAdresu() + ": icmp_seq=" +p.icmp_seq +
                    " ttl=" + p.ttl + " time=" + ((double) Math.round(p.cas * 1000)) / 1000 + " ms");
            odezvy.add(p.cas);
        } else if (p.typ == 3) {
            if (p.kod == 0) {
                if(!minus_q)
                    kon.posliRadek("From " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                        p.icmp_seq + " Destination Net Unreachable");
            } else if (p.kod == 1) {
                if(!minus_q)
                    kon.posliRadek("From " + p.zdroj.vypisAdresu() + ": icmp_seq=" +
                        p.icmp_seq + " Destination Host Unreachable");
            }
            errors++;
        } else if (p.typ == 11) {
            if(!minus_q)
                kon.posliRadek("From " + p.zdroj.vypisAdresu() + " icmp_seq=" + p.icmp_seq
                    + " Time to live exceeded");
            errors++;
        }
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
            while( slovo.length()>1 && slovo.charAt(0)=='-'){ //cteni prepinacu
                zpracujPrepinace();
                slovo=dalsiSlovo();
                if(navratovyKod!=0)return; // !!!!!!!! NA CHYBU SE OKAMZITE KONCI !!!!!!!!!
            }
            try{ //cteni ip adresy
                cil=new IpAdresa(slovo);
            }catch(SpatnaAdresaException ex){
                if(slovo.equals("")){ //zadna adresa nebyla zadana
                    navratovyKod |= 16;
                    vypisNapovedu();
                }else{
                    navratovyKod |=2;
                    kon.posliRadek("ping: unknown host "+slovo);
                }
            }
        }

    }

    /**
     * Zpracovava prepinace z jednoho slova. Predpoklada, ze krome minusu bude mit jeste aspon jeden dalsi znak.
     * Ty hlasky, co to vraci, nejsou vzdycky uplne verny
     */
    private void zpracujPrepinace() {
        int uk=1; //ukazatel na znak v tom Stringu, 0 je to minus
        int pom;
        while (uk < slovo.length()) {
            if (slovo.charAt(uk) == 'b') { // -b
                minus_b = true;
            } else if (slovo.charAt(uk) == 'q') { //-q
                minus_q = true;
            } else if (slovo.charAt(uk) == 'c') { //-c
                pom = zpracujCiselnejPrepinac(uk);
                if (pom <= 0){ //povoleny interval je 1 .. nekonecno
                    navratovyKod |= 4;
                    kon.posliRadek("ping: bad number of packets to transmit.");
                } else {
                    count = pom;
                }
                break;
            } else if (slovo.charAt(uk) == 's') { // -s
                pom = zpracujCiselnejPrepinac(uk);
                if (pom < 0 || pom > 65508){//v sesite...
                    navratovyKod |= 4;
                    kon.posliRadek("ping: bad size of packet.");
                } else {
                    size = pom;
                }
                break;
            } else if (slovo.charAt(uk) == 't') { //-t
                pom = zpracujCiselnejPrepinac(uk);
                if (pom <= 0 || pom > 255){ //povoleny interval je 1 .. nekonecno
                    navratovyKod |= 4;
                    if (pom==-1)kon.posliRadek("ping: can't set unicast time-to-live: Invalid argument");
                    else kon.posliRadek("ping: ttl "+pom+" out of range");
                } else {
                    ttl = pom;
                }
                break;
            } else if (slovo.charAt(uk) == 'i') {
                double p=zpracujDoublePrepinac(uk);
                if(p>0){
                    interval=p;
                }else{
                    navratovyKod|=4;
                    kon.posliRadek("ping: bad timing interval.");
                }
                break;
            }else{
                kon.posliRadek("ping: invalid option -- '"+slovo.charAt(uk)+"'");
                vypisNapovedu();
                navratovyKod |= 8;
                break;
            }
            uk++;
        }
    }

    /**
     * Tahleta metoda parsuje ciselne hodnoty prepinace, podle podminek, podle jakych funguje ping
     * (poznamky v mym sesite).
     * @param uk ukazatel na pismeno toho prepinace ve slove
     * @param puvodni hodnota prepinace
     * @return -1 kdyz se zparsovani nepovede
     */
    private int zpracujCiselnejPrepinac(int uk){
        int vratit=0;
        boolean asponJednoCislo=false;
        uk++; //aby ukazoval az za to pismeno
        if(uk>=slovo.length()){ //pismeno toho prepinace bylo poslednim znakem slova, mezi pismenem a
                                    // hodnotou je mezera
            slovo=dalsiSlovo(); //nacitani dalsiho slova
            uk=0;
        }
        while (uk<slovo.length() && Character.isDigit(slovo.charAt(uk))){ //ten cyklus bere jen cislice, to za
            vratit=vratit*10+Character.getNumericValue(slovo.charAt(uk)); //nima ignoruje ( -c12vnf^^$ -> -c12)
            asponJednoCislo=true;
            uk++;
        }
        if(asponJednoCislo){
            return vratit;
        }else{
            return -1;
        }
    }

    private double zpracujDoublePrepinac(int uk){
        slovo=dalsiSlovo();
        try{
            return Double.parseDouble(slovo);
        }catch (NumberFormatException ex){
            return -1;
        }
    }

    


    private void vypisNapovedu() {
        kon.posliRadek("Usage: ping [-LRUbdfnqrvVaA] [-c count] [-i interval] [-w deadline]");
        kon.posliRadek("            [-p pattern] [-s packetsize] [-t ttl] [-I interface or address]");
        kon.posliRadek("            [-M mtu discovery hint] [-S sndbuf]");
        kon.posliRadek("            [ -T timestamp option ] [ -Q tos ] [hop1 ...] destination");
    }

    /**
     * Jen pro ladeni.
     * @return
     */
    @Override
    public String toString(){
        String vratit = "   Parametry prikazu ping:\r\n\tnavratovyKodParseru: " + navratovyKod;
        vratit += "\r\n\tcount: "+count;
        vratit += "\r\n\tsize: "+size;
        vratit += "\r\n\tttl: "+ttl;
        vratit += "\r\n\tinterval v sekundach: "+interval;
        if(minus_q)vratit+="\r\n\tminus_q: zapnuto";
        if(minus_b)vratit+="\r\n\tminus_b: zapnuto";
        if(cil!=null)vratit += "\r\n\tcilova adresa: "+cil.vypisAdresu();
        else vratit += "\r\n\tcilova adresa je null";
        return vratit;
    }

    
}
