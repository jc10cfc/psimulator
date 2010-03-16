/*
 * Gegründet am Dienstag 5.1.2010 Abend.
 */

package pocitac;

import datoveStruktury.IpAdresa;
import java.util.LinkedList;
import java.util.List;

/**
 * Tohleto by měla bejt třída pro jedno síťové rozhraní.
 * @author neiss
 */
public class SitoveRozhrani {
    
    public IpAdresa ip;
    public String jmeno;
    public String macAdresa;
    public SitoveRozhrani pripojenoK; //sitove rozhrani, se kterym je toto rozhrani spojeno
    private AbstractPocitac pc; //pocitac, kteremu toto rozhrani patri
    private List<SitoveRozhrani> seznamPodrazenych; //seznam podrazenych rozhrani
    SitoveRozhrani nadrizene;
    private boolean zapnute;

    /**
     * Vrati stav rozhrani - zapnuto/vypnuto
     * @return
     */
    public boolean vratStavRozhrani() {
        return zapnute;
    }

    /**
     * Nastavi stav rozhrani
     * @param zapnute stav, ktery chceme nastavit
     */
    public void nastavRozhrani(boolean zapnute) {
        this.zapnute = zapnute;
    }

    /**
     * Getter pro pocitac, ktery drzi toto rozhrani.
     * @return
     */
    public AbstractPocitac getPc(){
        return pc;
    }

    public SitoveRozhrani(String jmeno, AbstractPocitac pc, String macAdresa) {
        this.pc=pc;
        this.jmeno=jmeno;
        this.macAdresa=macAdresa;

        if (pc instanceof LinuxPocitac) {
            this.zapnute = true;
        } else if (pc instanceof CiscoPocitac) {
            this.zapnute = false;
        }

        
        seznamPodrazenych=new LinkedList<SitoveRozhrani>();
    }


}
