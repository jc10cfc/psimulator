package prikazy;

import datoveStruktury.CiscoStavy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import static datoveStruktury.CiscoStavy.*;
import java.util.LinkedList;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;

/**
 * Parser prikazu pro cisco, zde se volaji prikazy dle toho, co poslal uzivatel.
 * @author haldyr
 */
public class CiscoParserPrikazu extends ParserPrikazu {

    public CiscoParserPrikazu(AbstractPocitac pc, Konsole kon) {
        super(pc, kon);
        slova = new LinkedList<String>();
    }
    CiscoStavy stav = USER;
    boolean configure1 = false;

    private void ping() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void show() {

        switch (stav) {
            case USER:
                break;

            case ROOT:
                if (slova.size() == 2) {
                    if (slova.get(1).equals("running-config")) {
                        runningconfig();
                        return;
                    }
                }
                break;
        }
    }

    // hotov
    private void configure() {

        if (slova.size() == 1 && !configure1) {
            kon.posli("Configuring from terminal, memory, or network [terminal]? ");
            kon.vypisPrompt = false;
            configure1 = true;
            return;
        }

        int cis = 1;
        if (configure1) {
            cis = 0;
        }
        if (slova.get(cis).equals("terminal") || configure1) {
            stav = CONFIG;
            kon.prompt = pc.jmeno + "(config)#";
            kon.posliRadek("Enter configuration commands, one per line.  End with 'exit'."); // zmena oproti ciscu: End with CNTL/Z.
            configure1 = false;
            kon.vypisPrompt = true;
            return;
        }

        int pocet = pc.jmeno.length() + 1 + slova.get(0).length() + 1;
        String ret = "";

        for (int i = 0; i
                < pocet; i++) {
            ret += " ";
        }
        ret += "^";
        kon.posliRadek(ret);
        kon.posliRadek("% Invalid input detected at '^' marker.");
        kon.posliRadek("");
    }

    private void iproute() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void iface() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void accesslist() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void noshutdown() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * pomocna metoda pro vypis povolenych prikazu
     * @param n seznam, ktery se bude prochazet po prvcich a posilat uzivateli
     */
    private void posliList(List n) {
        Collections.sort(n);
        for (Object s : n) {
            kon.posliRadek((String) s);
        }
    }

    @Override
    public void zpracujRadek(String s) {

        List napoveda = new ArrayList<String>();
        AbstraktniPrikaz prikaz;
        radek = s;
        slova.clear();

        rozsekejLepe();

        if (slova.size() < 1) {
            return; // jen mezera
        }

        if (configure1) {
            if (slova.get(0).equals("terminal") || slova.get(0).equals("")) {
                configure();
                return;
            } else {
                kon.posliRadek("?Must be \"terminal\"");
                return;
            }
        }


        if (slova.get(0).equals("")) {
            return; // prazdny Enter
        }

        if (slova.get(0).equals("?")) {
            switch (stav) {
                case USER:
                    kon.posliRadek("Exec commands:");
                    napoveda.add("  enable           Turn on privileged commands");
                    napoveda.add("  exit             Exit from the EXEC");
                    napoveda.add("  ping             Send echo messages");
                    napoveda.add("  show             Show running system information");
                    break;

                case ROOT:
                    kon.posliRadek("Exec commands:");
                    napoveda.add("  configure        Enter configuration mode");
                    napoveda.add("  disable          Turn off privileged commands");
                    napoveda.add("  enable           Turn on privileged commands");
                    napoveda.add("  ping             Send echo messages");
                    napoveda.add("  show             Show running system information");
                    break;

                case CONFIG:
                    kon.posliRadek("Configure commands:");
                    napoveda.add("  interface                   Select an interface to configure");
                    napoveda.add("  ip                          Global IP configuration subcommands");
                    napoveda.add("  exit                        Exit from configure mode");
                    napoveda.add("  access-list                 Add an access list entry");
                    break;

                case IFACE:
                    kon.posliRadek("Interface configuration commands:");
                    napoveda.add("  exit                    Exit from interface configuration mode");
                    napoveda.add("  ip                      Interface Internet Protocol config commands");
                    napoveda.add("  no                      Negate a command or set its defaults");
            }
            posliList(napoveda);
            return;


        }

// == stavy ==
// user -                           ping, enable
// root - enable/disable            ping, enable, configure, reload, show    (jmeno#)
// konfiguracni rezim - configure terminal/exit             ip route, interface, access-list, exit   (jmeno(config)#)
// konfigurace interface - interface/exit                   ip address, no shutdown, exit   (jmeno(config-if)#)


        switch (stav) {
            case USER:
                if (slova.get(0).equals("e")) {
                    stav = ROOT;
                    kon.prompt = pc.jmeno + "#";
                    return;
                }
                if (slova.get(0).equals("ping")) {
                    ping();
                    return;
                }
                if (slova.get(0).equals("show")) {
                    show();
                    return;
                }
                if (slova.get(0).equals("exit")) {
                    kon.ukonciSpojeni(); //TODO: cisco neukonci, co s tim?
                    return;
                }
                break;

            case ROOT:
                if (slova.get(0).equals("enable")) {
                    return;
                }
                if (slova.get(0).equals("disable")) {
                    stav = USER;
                    kon.prompt = pc.jmeno + ">";
                    return;
                }
                if (slova.get(0).equals("ping")) {
                    ping();
                    return;
                }
                if (slova.get(0).equals("configure")) {
                    configure();
                    return;
                }
                if (slova.get(0).equals("show")) {
                    show();
                    return;
                }
                break;

            //ip route, interface, access-list, exit
            case CONFIG:
                if (slova.get(0).equals("exit")) {
                    stav = ROOT;
                    kon.prompt = pc.jmeno + "#";
                    Date d = new Date();
                    DateFormat formator = new SimpleDateFormat("MMM  d HH:mm:ss.SSS");
                    kon.posliRadek(formator.format(d) + ": %SYS-5-CONFIG_I: Configured from console by console");
                    return;
                }
                if (slova.get(0).equals("ip")) {
                    iproute();
                    return;
                }
                if (slova.get(0).equals("interface")) {
                    iface();
                    return;
                }
                if (slova.get(0).equals("access-list")) {
                    accesslist();
                    return;
                }
                break;

            //ip address, no shutdown, exit   (jmeno(config-if)#)
            case IFACE:
                if (slova.get(0).equals("exit")) {
                    stav = CONFIG;
                    kon.prompt = pc.jmeno + "(config)#";
                    return;
                }
                if (slova.get(0).equals("ip")) {
                    iproute();
                    return;
                }
                if (slova.get(0).equals("no")) {
                    noshutdown();
                    return;
                }
        }

        // pak zrusit ifconfig
        if (slova.get(0).equals("ifconfig")) {
            prikaz = new Ifconfig(pc, kon, slova);
        } else { // pak se budou resit asi jmena pocitacu
            kon.posliRadek("% Unknown command or computer name, or unable to find computer address");
        }
    }

    private void runningconfig() {
        kon.posliRadek("Building configuration...\n"
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
                + "ip name-server 147.32.80.9\n"
                + "ip name-server 147.32.80.105\n"
                + "!\n"
                + "!\n"
                + "!\n"
                + "!");
        for (Object o : pc.rozhrani) {
            SitoveRozhrani sr = (SitoveRozhrani) o;
            kon.posliRadek("interface " + sr.jmeno + "\n"
                    + " ip address " + sr.ip.vypisIP() + " " + sr.ip.vypisMasku() + "\n"
                    + " duplex auto\n"
                    + " speed auto\n!"
                    + "ip classless\n"
                    + "!\n");
        }

        kon.posliRadek("ip http server\n"
                + "!\n"
                + "!\n"
                + "control-plane\n"
                + "!\n"
                + "!\n"
                + "line con 0\n"
                + "line aux 0\n"
                + "line vty 0 4\n"
                + " login\n"
                + "!\n"
                + "end\n");
    }
}

