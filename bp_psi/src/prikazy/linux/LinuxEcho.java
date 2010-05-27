/*
 * Montag 19.4.2010
 */

package prikazy.linux;

import Main.Main;
import prikazy.*;
import java.util.List;
import pocitac.*;

/**
 * Tahleta třída slouží k zapisování do souboru /proc/sys/net/ipv4/ip_forward.
 * Je to takovej kanón na vrabce, ale snažil jsem se zachovat strukturu ostatních linuxovejch příkazů
 * @author neiss
 */
public class LinuxEcho extends AbstraktniPrikaz{

    public LinuxEcho(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
    }

    /**
     * 0 - v poradku
     * 1 - chyba
     */
    private int navrKod=0;
    int h;



    private void parsujPrikaz() {
        if(slova.size()!=4){
            kon.posliRadek(Main.jmenoProgramu+": Spravna syntaxe napr: \"echo 1 > jmenoSouboru\".");
            kon.posliRadek(Main.jmenoProgramu+": Podporuji jen tuto variantu zadani prikazu, " +
                    "ty mezery jsou povinny");
        }
        String hodnota=dalsiSlovo();

        try{
            h = Integer.parseInt(hodnota);
        }catch(NumberFormatException ex){
            kon.posliRadek("bash: echo: write error: Invalid argument");
            navrKod=1;
            return;
        }
        if(! dalsiSlovo().equals(">") ){
            kon.posliRadek("echo: spatna syntaxe");
            navrKod=1;
            return;
        }
        String soubor=dalsiSlovo();
        if(soubor.equals("/proc/sys/net/ipv4/ip_forward") || soubor.equals("/proc/sys/net/ipv4/ip_forward")){
            //v poradku
        }else{
            kon.posliRadek(Main.jmenoProgramu+": echo: neznamy soubor");
            navrKod=1;
        }


    }

    @Override
    protected void vykonejPrikaz() {
        if(navrKod==0){
            if(h==0){
                pc.ip_forward=false;
            }else{
                pc.ip_forward=true; //opravdu, na vsechny jiny hodnoty nez nula to preposila
            }
        }
    }


}
