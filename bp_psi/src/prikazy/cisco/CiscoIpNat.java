/*
 * Hotovo:
 * prikaz no
 */
package prikazy.cisco;

import datoveStruktury.IpAdresa;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.Konsole;

/**
 * Trida pro zpracovani prikazu: <br />
 * ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24 <br />
 * ip nat inside source list 7 pool ovrld overload
 * @author haldyr
 */
public class CiscoIpNat extends CiscoPrikaz {

    /**
     * Rika, ze je to prikaz negovany - "no ..."
     */
    boolean no;
    int poolPrefix = -1;
    IpAdresa start = null;
    IpAdresa konec = null;
    String poolJmeno = null;
    int accesslist = -1;
    boolean overload = false;

    public CiscoIpNat(AbstraktniPocitac pc, Konsole kon, List<String> slova, boolean no) {
        super(pc, kon, slova);
        this.no = no;

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    @Override
    protected boolean zpracujRadek() {

        // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
        // ip nat inside source list 7 pool ovrld overload?

        if (no) {
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
            return false;
        }
    }

    @Override
    protected void vykonejPrikaz() {
        
        if (no) {
            int n;
            if (accesslist != -1) { // no ip nat inside source list 7 pool ovrld overload?

                n = pc.natTabulka.natSeznamPoolAccess.smazPoolAccess(accesslist);
                if (n == 1) {
                    kon.posliRadek("%Dynamic mapping not found");
                }
                return;
            }
            if (poolJmeno != null) { // no ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24

                n = pc.natTabulka.natSeznamPoolu.smazPool(poolJmeno);
                if (n == 1) {
                    kon.posliRadek("%Pool " + poolJmeno + " not found");
                }
            }
            return;
        }

        if (poolPrefix != -1) { // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
            int ret = pc.natTabulka.natSeznamPoolu.pridejPool(start, konec, poolPrefix, poolJmeno);
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

        if (accesslist != -1) { // ip nat inside source list 7 pool ovrld overload
            pc.natTabulka.natSeznamPoolAccess.pridejPoolAccess(accesslist, poolJmeno, overload);
        }
    }

    /**
     * Vrati true, pokud parsovani dobre dopadlo.
     * @return
     */
    private boolean zpracujPool() {
        // ip nat pool | ovrld 172.16.10.1 172.16.10.1 prefix 24

        String dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }
        poolJmeno = dalsi;

        if (no) { // staci po jmeno, dal me to nezajima
            return true;
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
        
        if (!kontrola("prefix-length", dalsiSlovo(), 1)) {
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

        String dalsi = "";

        if (!kontrola("source", dalsiSlovo(), 1)) {
            return false;
        }

        if (!kontrola("list", dalsiSlovo(), 1)) {
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

    /**
     * Zjisti, zda je rezetec prazdny.
     * Kdyz ano, tak to jeste vypise hlasku incompleteCommand.
     * @param s
     * @return
     */
    private boolean jePrazdny(String s) {
        if (s.equals("")) {
            incompleteCommand();
            return true;
        }
        return false;
    }
}
