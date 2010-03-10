package prikazy;

import datoveStruktury.CiscoStavy;
import datoveStruktury.IpAdresa;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static datoveStruktury.CiscoStavy.*;
import java.util.LinkedList;
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
    SitoveRozhrani aktualni = null;

    private void ping() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * V teto metode se vola runningconfig(). Kdyz je spatny vstup (tj. jiny nez 'show running-config' ve stavu ROOT),
     * tak to vyvola chybovou hlasku.
     */
    private void show() {

        switch (stav) {

            case ROOT:
                if (slova.size() == 2) {
                    if (slova.get(1).equals("running-config")) {
                        runningconfig();
                        return;
                    }
                }
                break;

            case USER:

            default:
                invalidInputDetected();
        }
        invalidInputDetected();
    }

    /**
     * Prepina cisco do stavu config (CONFIG).
     */
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

        for (int i = 0; i < pocet; i++) {
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

    /**
     * interface fastEthernet0/1
     * Prepne cisco do stavu config-if (IFACE).
     * Kdyz ma prikaz interface 2 argumenty, tak se sloucej do jednoho (pripad: interface fastEthernet 0/0).
     * 0 nebo vice nez argumenty znamena chybovou hlasku.
     * Do globalni promenne 'aktualni' uklada referenci na rozhrani, ktere chce uzivatel konfigurovat.
     */
    private void iface() {

        String rozh = "";
        switch (slova.size()) {
            case 1:
                incompleteCommand();
                return;

            case 2:
                rozh = slova.get(1);
                break;

            case 3:
                rozh = slova.get(1) + slova.get(2);
                break;

            default:
                invalidInputDetected();
                return;
        }

        boolean nalezeno = false;
        for (SitoveRozhrani iface : pc.rozhrani) {
            if (iface.jmeno.equalsIgnoreCase(rozh)) {
                aktualni = iface;
                nalezeno = true;
            }
        }

        if (nalezeno == false) {
            invalidInputDetected();
            return;
        }

        stav = IFACE;
        kon.prompt = pc.jmeno + "(config-if)#";
    }

    private void incompleteCommand() {
        kon.posliRadek("% Incomplete command.");
    }

    private void invalidInputDetected() {
        kon.posliRadek("% Invalid input detected.");
    }

    private void accesslist() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void noshutdown() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void zpracujRadek(String s) {

        AbstraktniPrikaz prikaz;
        radek = s;
        slova.clear();

        if (kon.doplnovani) {
            System.out.println("chci napovedet co dal napsat: '" + radek + "'");
            kon.doplnovani = false;
            return;
        }


        rozsekejLepe();

        if (slova.size() < 1) {
            return; // jen mezera
        }

//        kon.posliRadek("Doplnovani: '"+radek+"'");

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

        boolean nepokracuj = spolecnePrikazy();
        if (nepokracuj) {
            return;
        }

        if (slova.get(0).equals("?")) {
            prikaz = new Otaznik(pc, kon, slova, stav);
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
                    ipaddress();
                    return;
                }
                if (slova.get(0).equals("no")) {
                    noshutdown();
                    return;
                }
        }

        if (slova.get(0).equals("ifconfig")) { // pak smazat
            prikaz = new Ifconfig(pc, kon, slova);
        } else {
            kon.posliRadek("% Unknown command or computer name, or unable to find computer address");
        }

    }

    /**
     * Prikaz 'show running-config' ve stavu # (ROOT).
     * Aneb vypis rozhrani v uplne silenem formatu.
     */
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

    /**
     * Nastavi ip adresu na rozhrani specifikovane v predchozim stavu cisco (promenna aktualni)
     * prikaz ip musi mit 3 argumenty, jina chybova hlaska.
     */
    private void ipaddress() {
        //ip address 192.168.2.129 255.255.255.128

        if ((slova.size() != 4) || (!slova.get(1).equals("address"))) {
            incompleteCommand();
            return;
        }

        aktualni.ip = new IpAdresa(slova.get(2), slova.get(3));
    }
}

