/*
 * Gegründet am Dienstag 5.1.2010 Abend.
 */

package pocitac;

import datoveStruktury.AlternativniIpAdresa;
import datoveStruktury.IpAdresa;
import java.util.LinkedList;
import java.util.List;

/**
 * Tohleto by měla bejt třída pro jedno síťové rozhraní.
 * @author neiss
 */
public class SitoveRozhrani {

    public AlternativniIpAdresa ip;
    public String jmeno;
    public String macAdresa;
    public SitoveRozhrani pripojenoK; //sitove rozhrani, se kterym je toto rozhrani spojeno
    private AbstractPocitac pc; //pocitac, kteremu toto rozhrani patri
    private List<SitoveRozhrani> seznamPodrazenych; //seznam podrazenych rozhrani
    SitoveRozhrani nadrizene;


    public SitoveRozhrani(String jmeno, AbstractPocitac pc, String macAdresa) {
        this.pc=pc;
        this.jmeno=jmeno;
        this.macAdresa=macAdresa;
        seznamPodrazenych=new LinkedList<SitoveRozhrani>();
    }


}
