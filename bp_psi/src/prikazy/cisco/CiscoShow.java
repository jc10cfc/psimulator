/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy.cisco;

import datoveStruktury.CiscoStavy;
import datoveStruktury.NATAccessList.AccessList;
import datoveStruktury.NATPool.Pool;
import datoveStruktury.NATPoolAccess.PoolAccess;
import datoveStruktury.NATtabulka.NATzaznam;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;
import static prikazy.cisco.CiscoShow.Stav.*;

/**
 * Trida pro zpracovani a obsluhu prikazu 'show'.
 * @author haldyr
 */
public class CiscoShow extends CiscoPrikaz {

    Stav stavShow = null;
    CiscoStavy stavCisco;
    boolean debug = true;

    public CiscoShow(AbstraktniPocitac pc, Konsole kon, List<String> slova, CiscoStavy stavCisco) {
        super(pc, kon, slova);
        this.stavCisco = stavCisco;

        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    enum Stav {
        RUN,
        ROUTE,
        NAT,
        OTAZNIK
    };

    @Override
    protected boolean zpracujRadek() {
        // show ip route
        // show running-config      - jen v ROOT rezimu
        // show ip nat translations
        
        String dalsi = dalsiSlovo(); // druhe slovo
        if (dalsi.length() == 0) {
            kon.posliRadek("% Type \"show ?\" for a list of subcommands\n");
            return false;
        }

        if (dalsi.equals("?")) {
            String s = "";
            s += Main.Main.jmenoProgramu+": v opravdovem ciscu je tady holy seznam parametru " +
                    "(bez celeho prikazu jako zde!)\n\n";
            s += "  show ip route                   IP routing table\n";
            s += "  show ip nat translations        Translation entries\n";
            if (stavCisco == CiscoStavy.ROOT) {
                s += "  show running-config             Current operating configuration\n";
            }
            s += "\n";
            kon.posliPoRadcich(s, 150);
            return false;
        }

        if (dalsi.startsWith("r")) {
            if (stavCisco == CiscoStavy.USER) {
                invalidInputDetected();
                return false;
            }

            if(!kontrola("running-config", dalsi, 3)) {
                return false;
            }
            stavShow = RUN;
            return true;
        } else {
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
                showIpRoute();
                break;
            case NAT:
                showIpNatTranslations();
                break;
        }
    }

    /**
     * Posle vypis pro prikaz 'show ip nat translations.
     */
    private void showIpNatTranslations() {
        String s = "";
        s += pc.natTabulka.vypisZaznamyCisco();
        kon.posliPoRadcich(s, 50);
    }

    /**
     * Posle vypis pro prikaz 'show ip route'
     */
    private void showIpRoute() {
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
        for (Object o : pc.rozhrani) {
            SitoveRozhrani sr = (SitoveRozhrani) o;


            s += "interface " + sr.jmeno + "\n";
            if (sr.vratPrvni() != null) {
                s += " ip address " + sr.vratPrvni().vypisAdresu() + " " + sr.vratPrvni().vypisMasku() + "\n";
            }

            if (pc.natTabulka.vratVerejne() != null) {
                if (sr.jmeno.equals(pc.natTabulka.vratVerejne().jmeno)) {
                    s += " ip nat outside" + "\n";
                }
            }

            if (pc.natTabulka.vratInside() != null) {
                for (SitoveRozhrani iface : pc.natTabulka.vratInside()) {
                    if (iface.jmeno.equals(sr.jmeno)) {
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
                s += "ip nat inside source static "+zaznam.vratIn().vypisAdresu()+" "+zaznam.vratOut().vypisAdresu()+"\n";
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
