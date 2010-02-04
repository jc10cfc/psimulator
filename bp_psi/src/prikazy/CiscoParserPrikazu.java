/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import datoveStruktury.CiscoStavy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import static datoveStruktury.CiscoStavy.*;
import java.util.LinkedList;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

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

    private void ping() {
    }

    private void show() {
    }

    private void configure() {
        if (slova.get(1).equals("terminal")){
            stav = CONFIG;
            kon.prompt = pc.jmeno + "(config)#";
            kon.posliRadek("Enter configuration commands, one per line.  End with exit."); // zmena oproti ciscu: End with CNTL/Z.
        }


        //
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

    private void posli(List n) {
        Collections.sort(n);

        for (Object s : n) {
            kon.posliRadek("  " + (String) s);
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
            return;
        }

        if (slova.get(0).equals("")) {
            return; // prazdny Enter
        }


        if (slova.get(0).equals("?")) {
            switch (stav) {
                case USER:
                    kon.posliRadek("Exec commands:");
                    napoveda.add("enable");
                    napoveda.add("exit");
                    napoveda.add("ping");
                    napoveda.add("show");
                    break;

                case ROOT:
                    kon.posliRadek("Exec commands:");
                    napoveda.add("configure");
                    napoveda.add("disable");
                    napoveda.add("enable");
                    napoveda.add("ping");
                    napoveda.add("show");
                    break;

                case CONFIG:
                    kon.posliRadek("Configure commands:");
                    napoveda.add("interface");
                    napoveda.add("ip");
                    napoveda.add("exit");
                    napoveda.add("access-list");
                    break;

                case IFACE:
                    kon.posliRadek("Interface configuration commands:");
                    napoveda.add("exit");
                    napoveda.add("ip");
                    napoveda.add("no");
            }
            posli(napoveda);
            return;
        }

// == stavy ==
// user -                           ping, enable
// root - enable/disable            ping, enable, configure, reload, show    (jmeno#)
// konfiguracni rezim - configure terminal/exit             ip route, interface, access-list, exit   (jmeno(config)#)
// konfigurace interface - interface/exit                   ip address, no shutdown, exit   (jmeno(config-if)#)


        switch (stav) {
            case USER:
                if (slova.get(0).equals("enable")) {
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




        if (slova.get(0).equals("exit")) {
            prikaz = new Exit(pc, kon, slova);
        } else if (slova.get(0).equals("ifconfig")) {
            prikaz = new Ifconfig(pc, kon, slova);
        } else {
            kon.posliRadek("% Unknown command or computer name, or unable to find computer address");
        }
    }
}

