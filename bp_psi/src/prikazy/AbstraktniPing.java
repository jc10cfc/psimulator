/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

import datoveStruktury.*;
import java.util.List;
import pocitac.*;

/**
 * Tuhle tridu jsem delal jen kvuli abstraktni metode zpracujPaket(), ktera zpracovava prichozi icmp pakety,
 * ktery je treba nejak vypsat.
 * @author neiss
 */
public abstract class AbstraktniPing extends AbstraktniPrikaz{

    /**
     * pocet odeslanych paketu
     */
    protected int odeslane=0;
    /**
     * pocet prijatych paketu
     */
    protected int prijate=0;

    public AbstraktniPing(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
    }

    public abstract void zpracujPaket(Paket p);

}
