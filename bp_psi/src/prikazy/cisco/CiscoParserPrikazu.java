package prikazy.cisco;

import datoveStruktury.CiscoStavy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static datoveStruktury.CiscoStavy.*;
import static Main.Main.*;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.SitoveRozhrani;
import prikazy.AbstraktniPrikaz;
import prikazy.AbstraktniPrikaz.*;
import prikazy.ParserPrikazu;
import prikazy.linux.LinuxIfconfig;
import prikazy.linux.LinuxRoute;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Parser prikazu pro cisco, zde se volaji prikazy dle toho, co poslal uzivatel.
 * @author haldyr
 */
public class CiscoParserPrikazu extends ParserPrikazu {

    public CiscoParserPrikazu(AbstraktniPocitac pc, Konsole kon) {
        super(pc, kon);
    }
    /**
     * Stav, ve kterem se aktualne nachazi cisco.
     */
    CiscoStavy stav = USER;
    /**
     * Specialni formatovac casu pro vypis servisnich informaci z cisca.
     */
    DateFormat formator = new SimpleDateFormat("MMM  d HH:mm:ss.SSS");
    /**
     * Indikuje stav, kdy uzivatel napise jen configure. Pak se mu nevypise promt, ale staci zmacknout Enter
     * pro potvrzeni prikazu 'configure terminal'
     */
    boolean configure1 = false;
    /**
     * Rozhrani, ktere se bude upravovat ve stavu IFACE
     */
    SitoveRozhrani aktualni = null;
    /**
     * Pomocna promenna pro zachazeni s '% Ambiguous command: '
     */
    boolean nepokracovat = false;
    /**
     * Chyba, ktera se vypise pri '% Ambiguous command: '
     */
    String chybovyVypis = "";
    /**
     * Specialni rezim. Dovoluje pouziti prikazu 'ip route' v ROOT rezimu + dalsi vypisy.
     */
    boolean debug = true;
    AbstraktniPrikaz prikaz;

    private void ladici(String s) {
        if (debug) {
            System.out.println("ciscoParserPrikazu: " + s);
        }
    }

    /**
     * Tato metoda simuluje zkracovani prikazu tak, jak cini cisco.
     * @param command prikaz, na ktery se zjistuje, zda lze na nej doplnit.
     * @param cmd prikaz, ktery zadal uzivatel
     * @return Vrati true, pokud retezec cmd je jedinym moznym prikazem, na ktery ho lze doplnit.
     */
    private boolean kontrola(String command, String cmd) {

        int i = 10;

        // Zde jsou zadefinovany vsechny prikazy. Jsou rozdeleny do poli podle poctu znaku,
        // ktere je potreba k jejich bezpecne identifikaci. Cisla byla ziskana z praveho cisca IOS version 12.4
        String[] jedna = {"terminal", "inside", "outside", "source", "static", "pool", "netmask", "permit"};
        // + ip, exit
        String[] dva = {"show", "interface", "no", "shutdown", "enable", "classless",
            "access-list", "ping", "logout", "nat", "traceroute"};
        // + ip, exit
        String[] tri = {"running-config", "name-server", "nat", "address"};
        // + exit
        String[] ctyri = {"configure", "disable"};
        //String[] pet = {"route"};

        List<String[]> seznam = new ArrayList<String[]>();
        seznam.add(jedna);
        seznam.add(dva);
        seznam.add(tri);
        seznam.add(ctyri);
        //seznam.add(pet);

        int n = 0;
        for (String[] pole : seznam) { // nastaveni spravne delky dle zarazeni do seznamu
            n++;
            for (String s : pole) {
                if (s.equals(command)) {
                    i = n;
                }
            }
        }

        if (command.equals("exit")) { // specialni chovani prikazu exit v ruznych stavech
            switch (stav) {
                case USER:
                case ROOT:
                    i = 2;
                    break;
                case CONFIG:
                    i = 3;
                    break;
                case IFACE:
                    i = 1;
            }
        }

        if (command.equals("ip")) { // specialni chovani prikazu ip v ruznych stavech
            switch (stav) {
                case CONFIG:
                case ROOT:
                    i = 2;
                    break;
                case IFACE:
                    i = 1;
            }
        }

        if (command.equals("route")) { // specialni chovani prikazu route v ruznych stavech
            switch (stav) {
                case ROOT:
                    i = 2;
                    break;
                case CONFIG:
                    i = 5;
            }
        }

        if (cmd.length() >= i && command.startsWith(cmd)) { // lze doplnit na jeden jedinecny prikaz
            return true;
        }

        if (command.startsWith(cmd)) {
            chybovyVypis = "% Ambiguous command:  \"" + radek + "\"\n";
            nepokracovat = true;
        }

        return false;
    }

    @Override
    public void zpracujRadek(String s) {

        radek = s;
        nepokracovat = false;
        chybovyVypis = "";

        if (debug && stav == USER) {
            stav = ROOT;
            kon.prompt = pc.jmeno + "#";
        }

        rozsekej();

        if (slova.size() < 1) {
            return; // jen mezera
        }

        String prvniSlovo = slova.get(0);

        if (configure1) {
            if (kontrola("terminal", prvniSlovo) || prvniSlovo.equals("")) {
                configure();
                return;
            } else {
                kon.posliRadek("?Must be \"terminal\"");
                return;
            }
        }

        if (prvniSlovo.equals("")) {
            return; // prazdny Enter
        }

        boolean nepokracuj = spolecnePrikazy(debug);
        if (nepokracuj) {
            return;
        }

        if (prvniSlovo.equals("kill")) {
            kon.posliRadek(jmenoProgramu + ": servisni ukonceni");
            kon.ukonciSpojeni();
            return;
        }

        if (prvniSlovo.equals("help")) {
            prikaz = new CiscoHelp(pc, kon, slova, false);
            return;
        }

        if (prvniSlovo.equals("help_en")) {
            prikaz = new CiscoHelp(pc, kon, slova, true);
            return;
        }

        if (prvniSlovo.equals("?")) {
            prikaz = new CiscoOtaznik(pc, kon, slova, stav);
            return;
        }

        switch (stav) {
            case USER:
                if (kontrola("enable", prvniSlovo)) {
                    stav = ROOT;
                    kon.prompt = pc.jmeno + "#";
                    return;
                }
                if (kontrola("ping", prvniSlovo)) {
                    prikaz = new CiscoPing(pc, kon, slova);
                    return;
                }
                if (kontrola("traceroute", prvniSlovo)) {
                    prikaz = new CiscoTraceroute(pc, kon, slova);
                    return;
                }
                if (kontrola("show", prvniSlovo)) {
                    prikaz = new CiscoShow(pc, kon, slova, stav);
                    return;
                }
                if (kontrola("exit", prvniSlovo) || kontrola("logout", prvniSlovo)) {
                    kon.ukonciSpojeni();
                    return;
                }
                break;

            case ROOT:
                if (kontrola("enable", prvniSlovo)) { // funguje u cisco taky, ale nic nedela
                    return;
                }
                if (kontrola("disable", prvniSlovo)) {
                    stav = USER;
                    kon.prompt = pc.jmeno + ">";
                    return;
                }
                if (kontrola("ping", prvniSlovo)) {
                    prikaz = new CiscoPing(pc, kon, slova);
                    return;
                }
                if (kontrola("traceroute", prvniSlovo)) {
                    prikaz = new CiscoTraceroute(pc, kon, slova);
                    return;
                }
                if (kontrola("configure", prvniSlovo)) {
                    configure();
                    return;
                }
                if (kontrola("show", prvniSlovo)) {
                    prikaz = new CiscoShow(pc, kon, slova, stav);
                    return;
                }
                if (kontrola("exit", prvniSlovo) || kontrola("logout", prvniSlovo)) {
                    kon.ukonciSpojeni();
                    return;
                }

                if (debug) {
                    if (kontrola("ip", prvniSlovo)) {
                        prikaz = new CiscoIp(pc, kon, slova, false, stav);
                        return;
                    }
                    if (kontrola("access-list", prvniSlovo)) {
                        prikaz = new CiscoAccessList(pc, kon, slova, false);
                        return;
                    }
                    if (kontrola("no", prvniSlovo)) {
                        no();
                        return;
                    }
                }
                break;

            case CONFIG:
                if (kontrola("exit", prvniSlovo) || prvniSlovo.equals("end")) {
                    stav = ROOT;
                    kon.prompt = pc.jmeno + "#";
                    Date d = new Date();
                    kon.posliRadek(formator.format(d) + ": %SYS-5-CONFIG_I: Configured from console by console");
                    return;
                }
                if (kontrola("ip", prvniSlovo)) {
                    prikaz = new CiscoIp(pc, kon, slova, false, stav);
                    return;
                }
                if (kontrola("interface", prvniSlovo)) {
                    iface();
                    return;
                }
                if (kontrola("access-list", prvniSlovo)) {
                    prikaz = new CiscoAccessList(pc, kon, slova, false);
                    return;
                }
                if (kontrola("no", prvniSlovo)) {
                    no();
                    return;
                }
                break;

            case IFACE:
                if (kontrola("exit", prvniSlovo)) {
                    stav = CONFIG;
                    kon.prompt = pc.jmeno + "(config)#";
                    aktualni = null; // zrusime odkaz na menene rozhrani
                    return;
                }
                if (prvniSlovo.equals("end")) {
                    stav = ROOT;
                    kon.prompt = pc.jmeno + "#";
                    Date d = new Date();
                    kon.posliRadek(formator.format(d) + ": %SYS-5-CONFIG_I: Configured from console by console");
                    return;
                }
                if (kontrola("ip", prvniSlovo)) {
                    prikaz = new CiscoIp(pc, kon, slova, false, stav, aktualni);
                    return;
                }
                if (kontrola("no", prvniSlovo)) {
                    no();
                    return;
                }
                if (kontrola("shutdown", prvniSlovo)) {
                    shutdown();
                    return;
                }
        }

        if (debug) {
            if (slova.get(0).equals("ifconfig")) { // pak smazat
                prikaz = new LinuxIfconfig(pc, kon, slova);
            } else if (slova.get(0).equals("route")) {
                prikaz = new LinuxRoute(pc, kon, slova);
            }
        }

        if (nepokracovat) {
            kon.posli(chybovyVypis);
            nepokracovat = false;
            chybovyVypis = "";
            return;
        }

        switch (stav) {
            case CONFIG:
            case IFACE:
                invalidInputDetected();
                break;

            default:
                kon.posliRadek("% Unknown command or computer name, or unable to find computer address");
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
        if (kontrola("terminal", slova.get(cis)) || configure1) {
            //if (slova.get(cis).equals("terminal") || configure1) {
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

    /**
     * Prepne cisco do stavu config-if (IFACE).
     * Kdyz ma prikaz interface 2 argumenty, tak se sloucej do jednoho (pripad: interface fastEthernet 0/0).
     * 0 nebo vice nez argumenty znamena chybovou hlasku.
     * Do globalni promenne 'aktualni' uklada referenci na rozhrani, ktere chce uzivatel konfigurovat.
     * prikaz 'interface fastEthernet0/1'
     */
    private void iface() {

        String rozhrani = "";
        switch (slova.size()) {
            case 1:
                incompleteCommand();
                return;

            case 2:
                rozhrani = slova.get(1);
                break;

            case 3:
                rozhrani = slova.get(1) + slova.get(2);
                break;

            default:
                invalidInputDetected();
                return;
        }

        boolean nalezeno = false;
        for (SitoveRozhrani iface : pc.rozhrani) {
            if (iface.jmeno.equalsIgnoreCase(rozhrani)) {
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

    /**
     * Shodi rozhrani a zaktualizuje routovaci tabulku.
     */
    private void shutdown() {
        if (aktualni.jeNahozene()) {
            aktualni.nastavRozhrani(false);

            AbstraktniPrikaz.cekej(250);
            Date d = new Date();
            kon.posliRadek(formator.format(d) + ": %LINK-5-UPDOWN: Interface " + aktualni.jmeno + ", changed state to down");
            kon.posliRadek(formator.format(d) + ": %LINEPROTO-5-UPDOWN: Line protocol on Interface " + aktualni.jmeno + ", changed state to down");
            ((CiscoPocitac) pc).getWrapper().update();
        }
    }

    /**
     * Tento prikaz zapne rozhrani, ktere je definovano v aktualnim nastovacim rezimu (napr.: interface fastEthernet0/0)
     * a aktualizuje routovaci tabulku.
     */
    private void noshutdown() {
        if (slova.size() != 2) {
            incompleteCommand();
            return;
        }
        if (kontrola("shutdown", slova.get(1))) {
            //if (slova.get(1).equals("shutdown")) {
            if (aktualni.jeNahozene() == false) { // kdyz nahazuju rozhrani
                AbstraktniPrikaz.cekej(500);
                Date d = new Date();
                kon.posliRadek(formator.format(d) + ": %LINK-3-UPDOWN: Interface " + aktualni.jmeno + ", changed state to up");
                kon.posliRadek(formator.format(d) + ": %LINEPROTO-5-UPDOWN: Line protocol on Interface " + aktualni.jmeno + ", changed state to up");
            }

            aktualni.nastavRozhrani(true);
            ((CiscoPocitac) pc).getWrapper().update();
        }
        if (nepokracovat) {
            kon.posliRadek("% Ambiguous command:  \"" + radek + "\"");
        }
    }

    /**
     * Jednoduchy parser pro prikazy: <br />
     * no ip nat inside<br />
     * no ip route <br />
     * no access-list <br />
     * 
     * no ip address <br />
     * no ip nat inside/outside <br />
     * no shutdown
     */
    private void no() {

        String dalsi = slova.size() == 1 ? "" : slova.get(1);
        if (dalsi.isEmpty()) {
            incompleteCommand();
            return;
        }
        if (stav == CONFIG || (debug && stav == ROOT)) {
            if (kontrola("access-list", dalsi)) {
                prikaz = new CiscoAccessList(pc, kon, slova, true);
                return;
            }
            if (kontrola("ip", dalsi)) {
                System.out.println("prikaz ip");
                prikaz = new CiscoIp(pc, kon, slova, true, stav, aktualni);
                return;
            }
        }
        if (stav == IFACE) {
            if (kontrola("shutdown", dalsi)) {
                noshutdown();
                return;
            }
            if (kontrola("ip", dalsi)) {
                prikaz = new CiscoIp(pc, kon, slova, true, stav, aktualni);
                return;
            }
        }
        invalidInputDetected();
    }
}
