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
import vyjimky.NeznamyTypPcException;
import vyjimky.ZakazanaIpAdresaException;

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
    DateFormat formator = new SimpleDateFormat("MMM  d HH:mm:ss.SSS");

    /**
     * True znamena, ze staci psat zkratkovite prikazy (configure - c, interface - i, enable - e)
     */
    boolean usnadneni = true;
    
    /**
     * Indikuje stav, kdy uzivatel napise jen configure. Pak se mu nevypise promt, ale staci zmacknout Enter
     * pro potvrzeni prikazu 'configure terminal'
     */
    boolean configure1 = false;

    /**
     * Rozhrani, ktere se bude upravovat ve stavu IFACE
     */
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
                    if (slova.get(1).equals("running-config") || usnadneniPrace("rc", 1)) {
                        runningconfig();
                        return;
                    }
                } else if (slova.size() == 1) {
                    kon.posliRadek("% Type \"show ?\" for a list of subcommands");
                    return;
                }
                break;

            case USER:
            // TODO: tady asi jeste neco bude?

            default:
                invalidInputDetected();
        }
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

        String cisloRozh = "";
        if (usnadneni) {
            if (rozh.length() >= 3) {
                cisloRozh = rozh.substring(rozh.length() - 3);
            } else {
                cisloRozh = rozh;
            }
        }

        boolean nalezeno = false;
        for (SitoveRozhrani iface : pc.rozhrani) {
            if (usnadneni) {
                if (iface.jmeno.equalsIgnoreCase(rozh) || iface.jmeno.endsWith(cisloRozh)) {
                    aktualni = iface;
                    nalezeno = true;
                }
            } else {
                if (iface.jmeno.equalsIgnoreCase(rozh)) {
                    aktualni = iface;
                    nalezeno = true;
                }
            }
        }

        if (nalezeno == false) {
            invalidInputDetected();
            return;
        }

        stav = IFACE;
        kon.prompt = pc.jmeno + "(config-if)#";
    }

    /**
     * Vypise chybovou hlasku pri zadani nekompletniho prikazu.
     */
    private void incompleteCommand() {
        kon.posliRadek("% Incomplete command.");
    }

    /**
     * Vypise chybovou hlasku pri zadani neplatneho vstupu.
     */
    private void invalidInputDetected() {
        kon.posliRadek("\n% Invalid input detected.\n");
    }

    private void accesslist() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Tento prikaz zapne rozhrani, ktere je definovano v aktualnim nastovacim rezimu (napr.: interface fastEthernet0/0)
     */
    private void noshutdown() {
        if (slova.size() != 2) {
            incompleteCommand();
            return;
        }
        if (slova.get(1).equals("shutdown")) {
            if (aktualni.vratStavRozhrani() == false) { // kdyz nahazuju rozhrani
                Date d = new Date();
                kon.posliRadek(formator.format(d) + ": %LINK-3-UPDOWN: Interface "+aktualni.jmeno+", changed state to up");
                kon.posliRadek(formator.format(d) + ": %LINEPROTO-5-UPDOWN: Line protocol on Interface "+aktualni.jmeno+", changed state to up");
            }

            aktualni.nastavRozhrani(true);
        }
    }
    
    /**
     * Kdyz je nastavena tridni promenna usnadneni na true, tak se pak vraci true, pokud 1. slovo (prikaz) je roven parametru s.
     * @param s s se porovnava s 'slova' na indexu 'index'
     * @param index na kterem indexu v 'slova' to ma porovnavat
     * @return
     */
    private boolean usnadneniPrace(String s, int index) {
        if (usnadneni == false) {
            return false;
        }
        if (slova.get(index).equals(s)) {
            return true;
        }
        return false;
    }

    @Override
    public void zpracujRadek(String s) {

        AbstraktniPrikaz prikaz;
        radek = s;
        slova.clear();

        /*
        if (kon.doplnovani) {
            System.out.println("chci napovedet co dal napsat: '" + radek + "'");
            kon.doplnovani = false;
            return;
        }
         */

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
                if (slova.get(0).equals("enable") || usnadneniPrace("e", 0) ) {
                    stav = ROOT;
                    kon.prompt = pc.jmeno + "#";
                    return;
                }
                if (slova.get(0).equals("ping")) {
                    ping();
                    return;
                }
                if (slova.get(0).equals("show") || usnadneniPrace("s", 0)) {
                    show();
                    return;
                }
                if (slova.get(0).equals("exit")) {
                    kon.ukonciSpojeni();
                    return;
                }
                break;

            case ROOT:
                if (slova.get(0).equals("enable")) { // funguje v cisco taky, ale nic nedela
                    return;
                }
                if (slova.get(0).equals("disable")) {
                    stav = USER;
                    kon.prompt = pc.jmeno + ">";
                    return;
                }
                if (slova.get(0).equals("ping") || usnadneniPrace("p", 0)) {
                    ping();
                    return;
                }
                if (slova.get(0).equals("configure") || usnadneniPrace("c", 0)) {
                    configure();
                    return;
                }
                if (slova.get(0).equals("show") || usnadneniPrace("s", 0)) {
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
                    kon.posliRadek(formator.format(d) + ": %SYS-5-CONFIG_I: Configured from console by console");
                    return;
                }
                if (slova.get(0).equals("ip")) {
                    iproute();
                    return;
                }
                if (slova.get(0).equals("interface") || usnadneniPrace("i", 0)) {
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
                    aktualni = null; // zrusime odkaz na menene rozhrani
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

            switch (stav) {
                case CONFIG:
                case IFACE:
                    invalidInputDetected();
                    break;

                default:
                    kon.posliRadek("% Unknown command or computer name, or unable to find computer address");

            }


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
                    + " ip address " + sr.ip.vypisIP() + " " + sr.ip.vypisMasku());
            if (sr.vratStavRozhrani() == false) {
                kon.posliRadek(" shutdown");
            }
            kon.posliRadek(" duplex auto\n"
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

        try {
            aktualni.ip = new IpAdresa(slova.get(2), slova.get(3));
        } catch (ZakazanaIpAdresaException e) {
            kon.posliRadek("Not a valid host address - "+slova.get(2));
        } catch (Exception e) {
            invalidInputDetected();
        }

    }
}

