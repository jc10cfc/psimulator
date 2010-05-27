/*
 * Materiály:
 * http://www.linpro.cz/zacatecnici/prikazovy-radek/nastaveni-site.html
 * http://www.damika.cz/linux/navody/ip.php
 * http://www.abclinuxu.cz/blog/escaped/2006/2/11/120814
 * http://www.abclinuxu.cz/faq/site/nastaveni-pripojeni-k-siti-v-prikazove-radce
 * http://www.linuxsoft.cz/article.php?id_article=302
 * 
 */

package prikazy.linux;
import java.util.List;
import pocitac.*;
import prikazy.*;
//import static prikazy.linux.LinuxIp.Terminal.*; //tohle tady musi bejt, takhle divne

/**
 *
 * @author neiss
 */
public class LinuxIp extends AbstraktniPrikaz {
    public LinuxIp(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
    }

    private boolean ladeni=false;

    private final int necoSpatne=1; //neco je spatne, vypise se help
    private final int spatnaFamily=2;
    private final int neexistujiciPrepinac=4;
    private final int neexistujiciPrikaz=8;
    private final int nepodporovanyPrikaz=16;
    int navrKod=0;

    String slovo;

    final int fam_ipv4=1;
    final int fam_ipv6=2;
    final int fam_ethernet=3;
    int family=0;

    //prepinace:
    public boolean minus_o=false;
    public boolean minus_V=false;
    public boolean minus_s=false;
    public boolean minus_r=false;
    public boolean minus_h=false;



    /**
     * 1 - link <br />
     * 2 - addr<br />
     * 3 - route<br />
     * 4 - help<br />
     * 5 - nepodporovany<br />
     */
    public int cisloPrikazu=0;
    private String prikaz;

    private List<String>podSlova; //slova, ktery se posilaj podprikazu
    private AbstraktniPrikaz pr;


    private void parsujPrikaz() {
        slovo=dalsiSlovo();
        if(prectiPrepinace()){ //po prepinacich se ma pokracovat...
            prectiPrikaz();
        }
        if((navrKod & necoSpatne)!=0){
            vypisHelp();
        }
        
    }
    
    /**
     * Cte a kontroluje prepinace.
     * @return true, kdyz se po prepinacich ma jeste pokracovat v parsovani
     */
    private boolean prectiPrepinace(){
        boolean ukoncit=false;
        while(slovo.length()>=1 && slovo.charAt(0)=='-' && !ukoncit){ //posunuje se, jen kdyz slovo zacina "-"
            if(slovo.equals("-V")||slovo.equals("-Version")){
                minus_V=true;
                ukoncit=true;
            }else if(slovo.equals("-s")||slovo.equals("-stats")||slovo.equals("-statistics")){
                minus_s=true;
            }else if(slovo.equals("-4")){
                family=fam_ipv4;
            }else if(slovo.equals("-6")){
                family=fam_ipv6;
            }else if(slovo.equals("-0")){
                family=fam_ethernet;
            }else if(slovo.equals("-f")||slovo.equals("-family")){
                slovo=dalsiSlovo();
                if(slovo.equals("inet")){
                    family=fam_ipv4;
                }else if(slovo.equals("inet6")){
                    family=fam_ipv6;
                }else if(slovo.equals("link")){
                    family=fam_ethernet;
                }else if(slovo.equals("")){
                    navrKod|=necoSpatne;
                    ukoncit=true;
                }else{
                    navrKod|=spatnaFamily;
                    kon.posliRadek("Error: argument \"invalid protocol family\" is wrong: "+slovo);
                    ukoncit=true;
                }
            }else if(slovo.equals("-o")||slovo.equals("-oneline")){
                minus_o=true;
            }else if(slovo.equals("-r")||slovo.equals("-resolve")){
                minus_r=true;
            }else if(slovo.equals("-h")||slovo.equals("-help")){
                minus_h=true;
                ukoncit=true;
            }else{
                navrKod|=neexistujiciPrepinac;
                kon.posliRadek("Option \""+slovo+"\" is unknown, try \"ip -help\".");
                ukoncit=true;
            }
            slovo=dalsiSlovo();
        }
        return ! ukoncit;
    }
    
    /**
     * Cte cisloPrikazu, ocekava ho v promenny slovo. Ocekava jeden jedinej cisloPrikazu.
     */
    private void prectiPrikaz(){
        if(slovo.equals("")){
            navrKod|=necoSpatne;
        }else if("link".startsWith(slovo)){
            cisloPrikazu=1;
        }else if("address".startsWith(slovo)){
            cisloPrikazu=2;
        }else if("route".startsWith(slovo)){
            cisloPrikazu=3;
        }else if("help".startsWith(slovo)){
            cisloPrikazu=4;
        }else if("addrlabel".startsWith(slovo)){ // addr ma vetsi prioritu
            cisloPrikazu=5;
            prikaz= "addrlabel";
        }else if("neighbour".startsWith(slovo)){
            cisloPrikazu=5;
            prikaz= "neighbour";
        }else if("rule".startsWith(slovo)){ // route ma vetsi prioritu
            cisloPrikazu=5;
            prikaz= "rule";
        }else if("maddress".startsWith(slovo)){
            cisloPrikazu=5;
            prikaz= "maddress";
        }else if("mroute".startsWith(slovo)){
            cisloPrikazu=5;
            prikaz= "mroute";
        }else if("tunnel".startsWith(slovo)){
            cisloPrikazu=5;
            prikaz= "tunnel";
        }else if("xfrm".startsWith(slovo)){
            cisloPrikazu=5;
            prikaz= "xfrm";
        }else{
            kon.posliRadek("Object \""+slovo+"\" is unknown, try \"ip help\".");
            navrKod|=neexistujiciPrikaz;
        }
        if(cisloPrikazu==5){
            navrKod |= nepodporovanyPrikaz;
            kon.posliServisne("Prikaz "+prikaz+" neni v simulatoru zatim podporovan.");
//            if(ladeni)kon.posliRadek("Jsem tu.");
        }
    }

    @Override
    protected void vykonejPrikaz() {
        if(minus_V){
            kon.posliRadek("ip utility, iproute2-ss060323");
            return;
        }
        if(minus_h || cisloPrikazu==4){
            vypisHelp();
            return;
        }
        podSlova=slova.subList(getUk()-1, slova.size());

        if(ladeni)kon.posliRadek(toString());
        
        if(cisloPrikazu==1){
            pr=new LinuxIpLink(pc, kon, podSlova, this);
            // -> getUk-1, aby ty slova obsahovaly i nazev prikazu, napr link.
        }
        if(cisloPrikazu==2){
            pr=new LinuxIpAddr(pc, kon, podSlova, this);
        }
        if(cisloPrikazu==3){
            pr=new LinuxIpRoute(pc, kon, podSlova, this);
        }
    }

    private void vypisHelp() {
        kon.posliRadek("Usage: ip [ OPTIONS ] OBJECT { COMMAND | help }");
        kon.posliRadek("       ip [ -force ] [-batch filename");
        kon.posliRadek("where  OBJECT := { link | addr | addrlabel | route | rule | neigh | ntable |");
        kon.posliRadek("                   tunnel | maddr | mroute | monitor | xfrm }");
        kon.posliRadek("       OPTIONS := { -V[ersion] | -s[tatistics] | -d[etails] | -r[esolve] |");
        kon.posliRadek("                    -f[amily] { inet | inet6 | ipx | dnet | link } |");
        kon.posliRadek("                    -o[neline] | -t[imestamp] }");
    }

/**
     * Jen pro ladeni.
     * @return
     */
    @Override
    public String toString(){
        String vratit = "--------------------------\r\n   Parametry prikazu ip:\r\n\tnavratovyKodParseru: "
                + rozlozNaMocniny2(navrKod);
        vratit += "\r\n\tprikaz: " + cisloPrikazu;
        vratit+="\r\n\tprepinace: ";
        if(minus_o)vratit+=" -o";if(minus_r)vratit+=" -r";if(minus_s)vratit+=" -s";
        if(minus_V)vratit+=" -V";if(minus_h)vratit+=" -h";
        if(podSlova!=null)vratit+="\r\n\tpodslova: "+podSlova;
        vratit += "\r\n--------------------------";
        return vratit;
    }

    



//    /**
//     * Representuje jednotlivy terminaly, jmenuji se stejne, jako by byly ve stringu
//     */
//    public enum Terminal{
//        link, set, up, down, show
//    }

//    private Terminal vratTerm(String adrm){
//        if(adrm.equals("l")||adrm.equals("li")||adrm.equals("lin")||adrm.equals("link")) return link;
//        if(adrm.equals("adrm")||adrm.equals("se")||adrm.equals("set"))return set;
//        if(adrm.equals("up"))return up;
//        if(adrm.equals("down"))return down;
//        if(adrm.equals("sh")||adrm.equals("sho")||adrm.equals("show"))return show;
//    }

}
