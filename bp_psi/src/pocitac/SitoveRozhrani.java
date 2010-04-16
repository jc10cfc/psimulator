/*
 * Gegründet am Dienstag 5.1.2010 Abend.
 */

package pocitac;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Tohleto by měla bejt třída pro jedno síťové rozhraní.
 * @author neiss
 */
public class SitoveRozhrani {
    
//    public int ip;
    public List<IpAdresa> seznamAdres = new ArrayList<IpAdresa>();
    public String jmeno;
    public String macAdresa;
    public SitoveRozhrani pripojenoK; //sitove rozhrani, se kterym je toto rozhrani spojeno
    private AbstractPocitac pc; //pocitac, kteremu toto rozhrani patri
//    private List<SitoveRozhrani> seznamPodrazenych; //seznam podrazenych rozhrani
//    SitoveRozhrani nadrizene;
    
    /**
     * Stav rozhrani. True..zapnuto, false..vypnuto. <br />
     * Cisco je defaultne vypnute, linux zapnuty.
     */
    private boolean nahozene;

    /**
     * Vrati stav rozhrani - zapnuto/vypnuto. True..zapnuto, false..vypnuto
     * @return
     */
    public boolean jeNahozene() {
        return nahozene;
    }

    /**
     * Nastavi stav rozhrani
     * @param stav stav, ktery chceme nastavit
     */
    public void nastavRozhrani(boolean stav) {
        this.nahozene = stav;
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
            this.nahozene = true;
        } else if (pc instanceof CiscoPocitac) {
            this.nahozene = false;
        }

        
//        seznamPodrazenych=new LinkedList<SitoveRozhrani>();
    }

    /**
     * Vrati ip adresu na pozici 0 nebo null, pokud tam zadna IP neni.
     * @return
     */
    public IpAdresa vratPrvni() {
        if (seznamAdres.size() == 0) return null;
        return seznamAdres.get(0);
    }

    /**
     * Vrati true, pokud najde ip adresu shodnou jen v adrese. <br />
     * Hleda pomoci jeStejnaAdresa().
     * @param hledana
     * @return
     */
    public boolean obsahujeStejnouAdresu(IpAdresa hledana) {
        for (IpAdresa ip : seznamAdres) {
            if (ip.jeStejnaAdresa(hledana)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vrati true, pokud najde ip adresu shodnou v adrese + masce. <br />
     * Hleda pomoci equals().
     * @param hledana
     * @return
     */
    public boolean obsahujeStejnouAdresuEq(IpAdresa hledana) {
        for (IpAdresa ip : seznamAdres) {
            if (ip.equals(hledana)) {
                return true;
            }
        }
        return false;
    }

    public void pridejNaPrvniPozici(IpAdresa adr) {
        if (seznamAdres.size() > 0) {
            seznamAdres.remove(0);
        } 
        seznamAdres.add(0, adr);
        
    }
}
