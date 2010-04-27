/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    private final int necoSpatne=1; //neco je spatne, vypise se help
    private final int spatnaFamily=2;
    private final int neznamyPrepinac=4;
    int navrKod=0;

    String slovo;
    final int fam_ipv4=1;
    final int fam_ipv6=2;
    final int fam_ethernet=3;
    int family=0;
    boolean minus_o=false;
    boolean minus_V=false;
    boolean minus_s=false;
    boolean minus_r=false;
    boolean minus_h=false;


    @Override
    protected void vykonejPrikaz() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void parsujPrikaz() {
        slovo=dalsiSlovo();
        if(prectiPrepinace()){ //po prepinacich se ma pokracovat...

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
                navrKod|=neznamyPrepinac;
                ukoncit=true;
            }
            slovo=dalsiSlovo();
        }
        return ! ukoncit;
    }
    
    /**
     * Cte prikaz, ocekava ho v promenny slovo. Ocekava jeden jedinej prikaz.
     */
    private void prectiPrikaz(){
        
    }



//    /**
//     * Representuje jednotlivy terminaly, jmenuji se stejne, jako by byly ve stringu
//     */
//    public enum Terminal{
//        link, set, up, down, show
//    }

//    private Terminal vratTerm(String s){
//        if(s.equals("l")||s.equals("li")||s.equals("lin")||s.equals("link")) return link;
//        if(s.equals("s")||s.equals("se")||s.equals("set"))return set;
//        if(s.equals("up"))return up;
//        if(s.equals("down"))return down;
//        if(s.equals("sh")||s.equals("sho")||s.equals("show"))return show;
//    }

}
