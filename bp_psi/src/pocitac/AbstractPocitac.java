/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import java.util.ArrayList;
import java.util.List;


/**
 * Virtualni pocitac, predek Linuxu a Cisca
 * @author neiss
 */

public class AbstractPocitac {
    public Komunikace komunikace;
    public List <SitoveRozhrani>rozhrani;

    public AbstractPocitac(){
        komunikace=new Komunikace(3567, this);
        rozhrani=new ArrayList<SitoveRozhrani>();

    }

    public AbstractPocitac(int port) {
        komunikace = new Komunikace(port, this);
    }

}
