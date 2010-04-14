/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import datoveStruktury.IpAdresa;
import datoveStruktury.Paket;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.Konsole;

/**
 *
 * @author haldyr
 */
public class CiscoPing extends AbstraktniPing {

    /**
     * Adresa, na kterou posilame ping.
     */
    IpAdresa cil;
    /**
     * Pocet posilanych icmp_req.
     */
    int pocet = 5;
    /**
     * Velikost paketu v bytech (pro okrasu).
     */
    int velikost = 100;
    /**
     * Timeout v ms
     */
    int timeout = 1000; // default je 2000

    public CiscoPing(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        boolean pokracovat = zpracujRadek();
        if (pokracovat) {
            vykonejPrikaz();
        }
    }

    /**
     * Tato metoda simuluje zkracovani prikazu tak, jak cini cisco.
     * @param command prikaz, na ktery se zjistuje, zda lze na nej doplnit.
     * @param cmd prikaz, ktery zadal uzivatel
     * @return Vrati true, pokud retezec cmd je jedinym moznym prikazem, na ktery ho lze doplnit.
     */
    private boolean kontrola(String command, String cmd) {

        int n = 1;
        if (command.equals("size")) n = 2;

        if (cmd.length() >= n && command.startsWith(cmd)) { // lze doplnit na jeden jedinecny prikaz
            return true;
        }
        return false;
    }


    /**
     * Ulozi vsechny parametry do tridnich promennych nebo vypise chybovou hlasku.
     * @param typVolby, ktery ma zpracovavat
     * @return true pokud se ma pokracovat v posilani pingu
     *         false pokud to vypsalo chybu a tedy uz nic nedelat
     */
    private boolean zpracujParametry(String typVolby) {
        if (!kontrola("timeout", typVolby) && !kontrola("repeat", typVolby) && !kontrola("size", typVolby)) {
            kon.posliRadek("% Invalid input detected.");
            return false;
        }

        String volba = dalsiSlovo();

        if (volba.equals("")) {
            kon.posliRadek("% Incomplete command.");
            return false;
        }

        if (kontrola("timeout", typVolby)) {
            try {
                timeout = Integer.valueOf(volba) * 1000;
            } catch (NumberFormatException e) {
                kon.posliRadek("% Invalid input detected.");
                return false;
            }
        }

        if (kontrola("repeat", typVolby)) {
            try {
                pocet = Integer.valueOf(volba);
            } catch (NumberFormatException e) {
                kon.posliRadek("% Invalid input detected.");
                return false;
            }
        }
        if (kontrola("size", typVolby)) {
            int n;
            try {
                n = Integer.valueOf(volba);
            } catch (NumberFormatException e) {
                kon.posliRadek("% Invalid input detected.");
                kon.posliRadek("tadddd");
                return false;
            }
            if (n < 36 || n > 18024) {
                kon.posliRadek("% Invalid input detected.");
                return false;
            }
            velikost = n;
        }

        typVolby = dalsiSlovo();
        if (! typVolby.equals("")) {
            return zpracujParametry(typVolby);
        }

        return true;
    }

    /**
     * Parsuje prikaz ping.
     * @return
     */
    private boolean zpracujRadek() {
        if (slova.size() < 2) {
            return false;
        }
        String ip = dalsiSlovo();
        try {
            cil = new IpAdresa(ip);
        } catch (Exception e) {
            kon.posliRadek("Translating \"" + ip + "\"" + "...domain server (255.255.255.255)");
            cekej(500);
            kon.posliRadek("% Unrecognized host or address, or protocol not running.");
            return false;
        }

        String typVolby = dalsiSlovo();
        if (typVolby.equals("")) {
            return true;
        }

        boolean pokracovat = true;

        pokracovat = zpracujParametry(typVolby);

        if (pokracovat == false) {
            return false;
        }

        return true;
    }

    @Override
    protected void vykonejPrikaz() {
        String s = "";
        s += "\nType escape sequence to abort.\n"
                + "Sending " + pocet + ", " + velikost + "-byte ICMP Echos to " + cil.vypisAdresu() + ", timeout is " + timeout / 1000 + " seconds:";

        kon.posliPoRadcich(s, 20);
        for (int i = 0; i < pocet; i++) {
            boolean doslo = pc.posliIcmpRequest(cil, i, 255, this); // skolni cisca odpovidaj s TTL=255
            odeslane++;

            if (!doslo) {
                cekej(timeout);
                kon.posli(".");
            }
        }

        aktualizujStatistiky();
        s = "\nSuccess rate is " + ztrata + " percent (" + prijate + "/" + odeslane + ")";
        if (prijate > 0) {
            s += ", round-trip min/avg/max = " + Math.round(min) + "/" + Math.round(avg) + "/" + Math.round(max) + " ms";
        }
        kon.posliPoRadcich(s, 10);
    }

    @Override
    public void zpracujPaket(Paket p) {
        if (p.typ == 0) {
            odezvy.add(p.cas);
            kon.posli("!");
        } else if (p.typ == 3) {
            if (p.kod == 0) {
                // cisco posila 'U' a '.', jak se mu chce
                if (Math.round(Math.random()) % 2 == 0) {
                    kon.posli("U");
                    pc.vypis("posilam random 'U'");
                } else {
                    kon.posli(".");
                    pc.vypis("posilam random '.'");
                }
            } else if (p.kod == 1) {
                kon.posli(".");
            }
        } else {
            cekej(timeout);
            kon.posli(".");
        }
    }
}
