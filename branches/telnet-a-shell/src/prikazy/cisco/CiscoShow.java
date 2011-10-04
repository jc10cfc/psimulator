package prikazy.cisco;

import datoveStruktury.CiscoStavy;
import datoveStruktury.IpAdresa;
import datoveStruktury.NATAccessList.AccessList;
import datoveStruktury.NATPool.Pool;
import datoveStruktury.NATPoolAccess.PoolAccess;
import datoveStruktury.NATtabulka.NATzaznam;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.apps.CommandShell.CommandShell;
import pocitac.SitoveRozhrani;
import static prikazy.cisco.CiscoShow.Stav.*;

/**
 * Trida pro zpracovani a obsluhu prikazu 'show'.
 * @author Stanislav Řehák
 */
public class CiscoShow extends CiscoPrikaz {

    Stav stavShow = null;
    CiscoStavy stavCisco;
    /**
     * Pomocne rozhrani pro prikaz 'show interfaces FastEthernet0/0'
     */
    SitoveRozhrani iface;

    public CiscoShow(AbstraktniPocitac pc, CommandShell kon, List<String> slova, CiscoStavy stavCisco) {
        super(pc, kon, slova);
        this.stavCisco = stavCisco;
        iface = null;

        debug = false;
        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    // show interfaces (rozhrani 0/0)?
    private boolean zpracujInterfaces() {
        stavShow = INTERFACES;

        String rozh = dalsiSlovo();
        if (rozh.isEmpty()) {
            return true;
        }
        rozh += dalsiSlovo();

        iface = null;
        iface = pc.najdiRozhrani(rozh);
        if (iface == null) {
            invalidInputDetected();
            return false;
        }
        return true;
    }

    enum Stav {

        RUN,
        ROUTE,
        NAT,
        OTAZNIK,
        INTERFACES
    };

    @Override
    protected boolean zpracujRadek() {
        // show ip route
        // show running-config      - jen v ROOT rezimu
        // show interfaces          - jen v ROOT rezimu
        // show ip nat translations

        String dalsi = dalsiSlovo(); // druhe slovo
        if (dalsi.length() == 0) {
            kon.posliRadek("% Type \"show ?\" for a list of subcommands\n");
            return false;
        }

        if (dalsi.equals("?")) {
            String s = "";
            s += Main.Main.jmenoProgramu + ": v opravdovem ciscu je tady holy seznam parametru "
                    + "(bez celeho prikazu jako zde!)\n\n";
            s += "  show ip route                   IP routing table\n";
            s += "  show ip nat translations        Translation entries\n";
            if (stavCisco == CiscoStavy.ROOT) {
                s += "  show running-config             Current operating configuration\n";
            }
            s += "\n";
            kon.posliPoRadcich(s, 50);
            return false;
        }

        if (dalsi.startsWith("r")) {
            if (stavCisco == CiscoStavy.USER) {
                invalidInputDetected();
                return false;
            }

            if (!kontrola("running-config", dalsi, 3)) {
                return false;
            }
            stavShow = RUN;
            return true;
        } else {
            if (dalsi.startsWith("in")) {
                if (stavCisco != CiscoStavy.ROOT) {
                    invalidInputDetected();
                    return false;
                }
                if (!kontrola("interfaces", dalsi, 3)) {
                    return false;
                }
                return zpracujInterfaces();
            }

            if (!kontrola("ip", dalsi, 2)) {
                return false;
            }

            dalsi = dalsiSlovo();
            if (dalsi.startsWith("r")) {
                if (!kontrola("route", dalsi, 2)) {
                    return false;
                }
                stavShow = ROUTE;
                return true;
            } else {
                if (!kontrola("nat", dalsi, 2)) {
                    return false;
                }
                if (stavCisco == CiscoStavy.USER) {
                    invalidInputDetected();
                    return false;
                }
                if (!kontrola("translations", dalsiSlovo(), 1)) {
                    return false;
                }
                stavShow = NAT;
                return true;
            }
        }
    }

    @Override
    protected void vykonejPrikaz() {
        switch (stavShow) {
            case RUN:
                runningConfig();
                break;
            case ROUTE:
                ipRoute();
                break;
            case NAT:
                ipNatTranslations();
                break;
            case INTERFACES:
                interfaces();
                break;
        }
    }

    /**
     * Posle vypis pro prikaz 'show interfaces.
     */
    private void interfaces() {
        if (iface == null) {
            for (SitoveRozhrani sr : pc.rozhrani) {
                kon.posliPoRadcich(sr.vratCiscoVypis(), 30);
            }
            kon.posliRadek("");
            return;
        }
        kon.posliPoRadcich(iface.vratCiscoVypis(), 30);
    }

    /**
     * Posle vypis pro prikaz 'show ip nat translations.
     */
    private void ipNatTranslations() {
        String s = "";
        s += pc.natTabulka.vypisZaznamyCisco();
        kon.posliPoRadcich(s, 50);
    }

    /**
     * Posle vypis pro prikaz 'show ip route'
     */
    private void ipRoute() {
        String s = "";
        s += ((CiscoPocitac) pc).getWrapper().vypisRT();
        kon.posliPoRadcich(s, 80);
    }

    /**
     * Prikaz 'show running-config' ve stavu # (ROOT).
     * Aneb vypis rozhrani v uplne silenem formatu.
     */
    private void runningConfig() {
        String s = "";

        s += "Building configuration...\n"
                + "\n"
                + "Current configuration : 827 bytes\n"
                + "!\n"
                + "version 12.4\n"
                + "service timestamps debug datetime msec\n"
                + "service timestamps log datetime msec\n"
                + "no service password-encryption\n"
                + "!\n"
                + "hostname " + pc.jmeno + "\n"
                + "!\n"
                + "boot-start-marker\n"
                + "boot-end-marker\n"
                + "!\n"
                + "!\n"
                + "no aaa new-model\n"
                + "!\n"
                + "resource policy\n"
                + "!\n"
                + "mmi polling-interval 60\n"
                + "no mmi auto-configure\n"
                + "no mmi pvc\n"
                + "mmi snmp-timeout 180\n"
                + "ip subnet-zero\n"
                + "ip cef\n"
                + "!\n"
                + "!\n"
                + "no ip dhcp use vrf connected\n"
                + "!\n"
                + "!\n"
                + "!\n";
        for (SitoveRozhrani sr : pc.rozhrani) {

            s += "interface " + sr.jmeno + "\n";
            for (IpAdresa adr : sr.seznamAdres) {
                if (adr == null) {
                    s += " no ip address\n";
                } else {
                    s += " ip address " + adr.vypisAdresu() + " " + adr.vypisMasku() + "\n";
                }
                if (debug == false) { // kdyz neni debug rezim, tak vypis jen prvni IP.
                    break;
                }
            }

            if (pc.natTabulka.vratVerejne() != null) {
                if (sr.jmeno.equals(pc.natTabulka.vratVerejne().jmeno)) {
                    s += " ip nat outside" + "\n";
                }
            }

            if (pc.natTabulka.vratInside() != null) {
                for (SitoveRozhrani iface0 : pc.natTabulka.vratInside()) {
                    if (iface0.jmeno.equals(sr.jmeno)) {
                        s += " ip nat inside" + "\n";
                        break;
                    }
                }
            }

            if (sr.jeNahozene() == false) {
                s += " shutdown" + "\n";
            }
            s += " duplex auto\n speed auto\n!\n";
        }

        if (pc.routovaciTabulka.classless) {
            s += "ip classless\n";
        }
        s += ((CiscoPocitac) pc).getWrapper().vypisRunningConfig();

        s += "!\n";
        s += "ip http server\n";

        for (Pool pool : pc.natTabulka.lPool.seznam) {
            s += "ip nat pool " + pool.jmeno + " " + pool.prvni().vypisAdresu() + " " + pool.posledni().vypisAdresu()
                    + " prefix-length " + pool.prvni().pocetBituMasky() + "\n";
        }

        for (PoolAccess pa : pc.natTabulka.lPoolAccess.seznam) {
            s += "ip nat inside source list " + pa.access + " pool " + pa.pool;
            if (pa.overload) {
                s += " overload";
            }
            s += "\n";
        }

        for (NATzaznam zaznam : pc.natTabulka.vratTabulku()) {
            if (zaznam.jeStaticke()) {
                s += "ip nat inside source static " + zaznam.vratIn().vypisAdresu() + " " + zaznam.vratOut().vypisAdresu() + "\n";
            }
        }

        s += "!\n";

        for (AccessList access : pc.natTabulka.lAccess.seznam) {
            s += "access-list " + access.cislo + " permit " + access.ip.vypisAdresu() + " " + access.ip.vypisWildcard() + "\n";
        }

        if (!debug) {
            s += "!\n" + "!\n" + "control-plane\n"
                    + "!\n" + "!\n" + "line con 0\n"
                    + "line aux 0\n" + "line vty 0 4\n" + " login\n" + "!\n" + "end\n\n";

        }
        kon.posliPoRadcich(s, 10);
    }
}
