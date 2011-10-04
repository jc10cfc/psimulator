/*
 * Montag 19.4.2010
 */

package pocitac.apps.CommandShell.prikazy.linux;

import pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz;
import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;

/**
 * Tato třída slouží k čtení souborů, jako třeba /proc/sys/net/ipv4/ip_forward
 * @author Tomáš Pitřinec
 */
public class LinuxCat extends AbstraktniPrikaz{

    public LinuxCat(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(pc, kon, slova);
        parsujPrikaz();
        vykonejPrikaz();
    }
    
    /**
     * 0 - nic
     * 1 - /proc/sys/net/ipv4/ip_forward
     */
    private int soubory=0;



    private void parsujPrikaz() {
        String slovo=dalsiSlovo();
        while(! slovo.equals("")){
            if(slovo.equals("/proc/sys/net/ipv4/ip_forward") ||
                    slovo.equals("/proc/sys/net/ipv4/ip_forward")){//zavedl jsem si alias
                soubory |= 1;
            } else {
                kon.posliRadek("cat: "+slovo+": No such file or directory");
            }
            slovo=dalsiSlovo();
        }

    }

    @Override
    protected void vykonejPrikaz() {
        if((soubory&1)==1){
            if(pc.ip_forward==true){
                kon.posliRadek("1");
            }else{
                kon.posliRadek("0");
            }
        }
    }


}
