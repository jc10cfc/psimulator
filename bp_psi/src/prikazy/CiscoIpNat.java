/*
 * Dodelat:
 * prikaz no
 */
package prikazy;

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

    int poolPrefix = -1;
    IpAdresa start = null;
    IpAdresa konec = null;
    String poolJmeno = null;
    int accesslist = -1;
    boolean overload = false;

    public CiscoIpNat(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    @Override
    protected boolean zpracujRadek() {

        // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
        // ip nat inside source list 7 pool ovrld overload

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
        if (poolPrefix != -1) { // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24
            int ret = pc.NATtabulka.pridejPool(start, konec, poolPrefix, poolJmeno);
            switch (ret) {
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
            pc.NATtabulka.nastavAktivniPool(poolJmeno);
            pc.NATtabulka.cisloAccess = accesslist;
            pc.NATtabulka.overload = overload;
        }
    }

    /**
     * Vrati true, pokud parsovani dobre dopadlo.
     * @return
     */
    private boolean zpracujPool() {
        // ip nat pool ovrld 172.16.10.1 172.16.10.1 prefix 24

        String dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }

        if (!kontrola("pool", dalsi, 1)) {
            return false;
        }

        dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }
        poolJmeno = dalsi;

        try {
            start = new IpAdresa(dalsiSlovo());
            konec = new IpAdresa(dalsiSlovo());
        } catch (Exception e) {
            invalidInputDetected();
            return false;
        }

        dalsi = dalsiSlovo();
        if (jePrazdny(dalsi)) {
            return false;
        }
        if (!kontrola("prefix-length", dalsi, 1)) {
            return false;
        }

        try {
            poolPrefix = Integer.parseInt(dalsiSlovo());
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
            incompleteCommand();
            return false;
        }
        poolJmeno = dalsi;

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
