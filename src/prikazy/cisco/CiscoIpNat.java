/*
 * Hotovo:
 * prikaz no
 * ip nat pool ovrld 172.16.0.1 172.16.0.1 netmask 255.255.255.252  - nebudu implemetovat
 */
package prikazy.cisco;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.apps.CommandShell.CommandShell;
import static prikazy.cisco.CiscoIpNat.Stav.*;

/**
 * Trida pro zpracovani prikazu: <br />
 * ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24 <br />
 * ip nat inside source list 7 pool ovrld overload
 * ip nat inside source static 10.10.10.2 171.16.68.5
 * @author Stanislav Řehák
 */
public class CiscoIpNat extends CiscoPrikaz {
    
    int poolPrefix = -1;
    IpAdresa start = null;
    IpAdresa konec = null;
    String poolJmeno = null;
    int accesslist = -1;
    boolean overload = false;
    Stav stav = null;

    enum Stav {

        POOL, // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
        INSIDE, // ip nat inside source list 7 pool ovrld overload?
        STATIC // ip nat inside source static 10.10.10.1 171.16.68.5 
    }

    public CiscoIpNat(AbstraktniPocitac pc, CommandShell kon, List<String> slova, boolean no) {
        super(pc, kon, slova, no);

        debug = false;
        ladici("vytvoren prikaz CiscoIpNat s no="+no);

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    @Override
    protected boolean zpracujRadek() {

        // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
        // ip nat inside source list 7 pool ovrld overload?
        // ip nat inside source static 10.10.10.1 171.16.68.5 

        if (no) {
            ladici("prikaz no");
            if (!dalsiSlovo().equals("ip")) {
                invalidInputDetected();
                return false;
            }
        }
        
        if (!kontrola("nat", dalsiSlovo(), 3)) {
            return false;
        }
        
        String dalsi = dalsiSlovo();
        if (dalsi.startsWith("p")) {
            if (kontrola("pool", dalsi, 1)) {
                return zpracujPool();
            }
            return false;
        } else {
            if (kontrola("inside", dalsi, 1)) {
                return zpracujInside();
            }
            if (dalsi.startsWith("outside")) {
                kon.posliServisne("Tato funkcionalita neni implementovana.");
            }
            return false;
        }
    }

    @Override
    protected void vykonejPrikaz() {

        int n;
        if (no) {
            if (stav == INSIDE) { // no ip nat inside source list 7 pool ovrld overload?
                n = pc.natTabulka.lPoolAccess.smazPoolAccess(accesslist);
                if (n == 1) {
                    kon.posliRadek("%Dynamic mapping not found");
                }
                return;
            }
            if (stav == POOL) { // no ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
                n = pc.natTabulka.lPool.smazPool(poolJmeno);
                if (n == 1) {
                    kon.posliRadek("%Pool " + poolJmeno + " not found");
                }
                if (n == 2) {
                    kon.posliRadek("%Pool " + poolJmeno + " in use, cannot redefine");
                }
            }

            if (stav == STATIC) {
                n = pc.natTabulka.smazStatickyZaznam(start, konec);
                if (n == 1) {
                    kon.posliRadek("% Translation not found");
                }
            }

            return;
        }

        if (stav == POOL) { // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
            int ret = pc.natTabulka.lPool.pridejPool(start, konec, poolPrefix, poolJmeno);
            switch (ret) {
                case 0:
                    // ok
                    break;
                case 1:
                    kon.posliRadek("%End address less than start address");
                    break;
                case 2:
                    kon.posliRadek("%Pool ovrld in use, cannot redefine");
                    break;
                case 3:
                    invalidInputDetected();
                    break;
                case 4:
                    kon.posliRadek("%Start and end addresses on different subnets");
                    break;
                default:
                    invalidInputDetected();
            }
            return;
        }

        if (stav == INSIDE) { // ip nat inside source list 7 pool ovrld overload
            pc.natTabulka.lPoolAccess.pridejPoolAccess(accesslist, poolJmeno, overload);
        }

        if (stav == STATIC) { // ip nat inside source static 10.10.10.2 171.16.68.5
            n = pc.natTabulka.pridejStatickePravidloCisco(start, konec);
            if (n == 1) {
                kon.posliRadek("% " + start.vypisAdresu() + " already mapped (" + start.vypisAdresu() + " -> " 
                        + konec.vypisAdresu() + ")");
            }
            if (n == 2) {
                kon.posliRadek("% similar static entry (" + start.vypisAdresu() + " -> " + konec.vypisAdresu() + ") "
                        + "already exists");
            }
        }
    }

    /**
     * Vrati true, pokud parsovani dobre dopadlo.
     * @return
     */
    private boolean zpracujPool() {
        // ip nat pool ovrld | 172.16.10.1 172.16.10.1 prefix 24

        String dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }
        poolJmeno = dalsi;

        stav = POOL;

        ladici("tady10");
        if (no) { // staci po jmeno, dal me to nezajima
            return true;
        }
        ladici("tady11");
        try {
            dalsi = dalsiSlovo();
            if (jePrazdny(dalsi)) {
                return false;
            }
            start = new IpAdresa(dalsi);

            dalsi = dalsiSlovo();
            if (jePrazdny(dalsi)) {
                return false;
            }
            konec = new IpAdresa(dalsi);
        } catch (Exception e) {
            invalidInputDetected();
            return false;
        }

        dalsi = dalsiSlovo();

        if(dalsi.startsWith("n")) {
            if (!kontrola("netmask", dalsi, 1)) {
                return false;
            }
            kon.posliServisne("netmask neni implementovan; pouzijte volbu prefix-length");
            return false;
        }

        if (!kontrola("prefix-length", dalsi, 1)) {
            return false;
        }

        dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }
        try {
            poolPrefix = Integer.parseInt(dalsi);
        } catch (NumberFormatException e) {
            invalidInputDetected();
            return false;
        }
        if (poolPrefix > 30) {
            kon.posliRadek("%Pool " + poolJmeno + " prefix length " + poolPrefix + " too large; should be no more than 30");
            return false;
        } else if (poolPrefix < 1) {
            invalidInputDetected();
            return false;
        }

        if (!dalsiSlovo().equals("")) { // kdyz je jeste neco za tim
            invalidInputDetected();
            return false;
        }

        return true;
    }

    /**
     * Vrati true, pokud parsovani dobre dopadlo.
     * @return
     */
    private boolean zpracujInside() {
        // ip nat inside source list 7 pool ovrld overload
        // ip nat inside source static 10.10.10.1 171.16.68.5 

        String dalsi = "";

        if (!kontrola("source", dalsiSlovo(), 1)) {
            return false;
        }

        dalsi = dalsiSlovo();
        if (dalsi.startsWith("s")) {
            return zpracujStatic(dalsi);
        }

        if (!kontrola("list", dalsi, 1)) {
            return false;
        }

        dalsi = dalsiSlovo();
        try {
            if (jePrazdny(dalsi)) {
                return false;
            }
            accesslist = Integer.parseInt(dalsi);
        } catch (NumberFormatException e) {
            invalidInputDetected();
            return false;
        }

        if (!kontrola("pool", dalsiSlovo(), 1)) {
            return false;
        }

        dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }
        poolJmeno = dalsi;

        stav = INSIDE;

        if (no) { // pokud mazu, tak pocamcad mi to staci, ale mazu stejnak jen podle cisla:-)
            return true;
        }

        dalsi = dalsiSlovo();
        if (dalsi.equals("overload")) {
            overload = true;
        } else if (dalsi.equals("")) {
            // nic, parametr overload je volitelny
            return true;
        } else {
            invalidInputDetected();
            return false;
        }

        dalsi = dalsiSlovo();
        if (!dalsi.equals("")) {
            invalidInputDetected();
            return false;
        }

        return true;
    }

    private boolean zpracujStatic(String s) {
        // ip nat inside source static 10.10.10.2 171.16.68.5
        String dalsi = s;
        if (!kontrola("static", dalsi, 1)) {
            return false;
        }

        try {
            dalsi = dalsiSlovo();
            if (jePrazdny(dalsi)) {
                return false;
            }
            start = new IpAdresa(dalsi);

            dalsi = dalsiSlovo();
            if (jePrazdny(dalsi)) {
                return false;
            }
            konec = new IpAdresa(dalsi);
        } catch (Exception e) {
            invalidInputDetected();
            return false;
        }
        stav = STATIC;

        return true;
    }
}
