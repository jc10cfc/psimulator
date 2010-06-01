/*
 * Gegründet am Dienstag 5.1.2010 Abend.
 */
package pocitac;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída pro jedno síťové rozhraní.
 * @author Tomáš Pitřinec & Stanislav Řehák
 *
 * DŮLEŽITÁ POZNÁMKA:
 * V pátek 16.4.2010 jsme zavedli více IpAdres pro jedno rozhraní. Původně jsme všechno
 * dělali jen pro jednu IP na rozhraní, ale pro natování je jich potřeba víc. Protože
 * zjišťování, jak se cisco a linux chová v případě více adres a třeba z jedný nebo z
 * více sítí na jednom rozhraní chová, by bylo strašně složitý, vykašlali jsme se na to.
 * NA VÍCE ADRES NA JEDNOM ROZHRANÍ SE TEDA NEDÁ V ŽÁDNÉM PŘÍPADĚ SPOLÉHAT! Hlavní
 * adresou na rozhraní je vždy ta první, další jsou jen na strojích, kde běží nat a
 * jen pro jeho potřebu. Kdyby snad někdo v budoucnosti chtěl v tomto programu možnost
 * více adres na jednom rozhraní doprogramovat, ať si dává veliký pozor, zvlášť na
 * metody vratPrvni() a pridejNaPrvniPosici(), které počítají pravě jen s tou jednou
 * adresou. Například nové pakety z počítače jsou vždy odesílané s první adresou.
 */
public class SitoveRozhrani {

    /**
     * Seznam adres. Ta prvni je jaksi privilegovana.
     */
    public List<IpAdresa> seznamAdres = new ArrayList<IpAdresa>(); //pozor, neda se na to
    //spolehat - viz. poznamka v javadocu tridy
    public String jmeno;
    public String macAdresa;
    public SitoveRozhrani pripojenoK; //sitove rozhrani, se kterym je toto rozhrani spojeno
    private AbstraktniPocitac pc; //pocitac, kteremu toto rozhrani patri
    /**
     * Stav rozhrani. True..zapnuto, false..vypnuto. <br />
     * Cisco je defaultne vypnute, linux zapnuty.
     */
    private boolean nahozene;

    public SitoveRozhrani(String jmeno, AbstraktniPocitac pc, String macAdresa) {
        this.pc = pc;
        this.jmeno = jmeno;
        this.macAdresa = macAdresa;
        seznamAdres.add(null); //na prvni misto se pridava null, je to jako ta prvni adresa

        if (pc instanceof LinuxPocitac) {
            this.nahozene = true;
        } else if (pc instanceof CiscoPocitac) {
            this.nahozene = false;
        }
    }

    @Override
    public String toString() {
        String s = "jmeno: " + jmeno + "\n";
        s += " mac: " + macAdresa + "\n";
        for (IpAdresa adr : seznamAdres) {
            s += " adr: " + adr.vypisAdresu() + "\n";
        }
        s += " stav: ";
        s += nahozene ? "nahozene" : "zhozene";
        s += "\n";

        if (pripojenoK != null) {
            s += " pripojenoK: " + pripojenoK.getPc().jmeno + ":" + pripojenoK.jmeno + "\n";
        }
        return s;
    }

    /**
     * Vrati stav rozhrani - zapnuto/vypnuto. True..zapnuto, false..vypnuto
     * @return
     */
    public boolean jeNahozene() {
        return nahozene;
    }

    /**
     * Nastavi stav rozhrani
     * @param stav stav, ktery chceme nastavit
     */
    public void nastavRozhrani(boolean stav) {
        this.nahozene = stav;
    }

    /**
     * Getter pro pocitac, ktery drzi toto rozhrani.
     * @return
     */
    public AbstraktniPocitac getPc() {
        return pc;
    }

    /**
     * Vrati ip adresu na pozici 0 nebo null, pokud tam zadna IP neni.
     * @return
     * @author Stanislav Řehák
     */
    public IpAdresa vratPrvni() {
        if (seznamAdres.size() == 0) {
            return null;
        }
        return seznamAdres.get(0);
    }

    /**
     * Vrati true, pokud najde ip adresu shodnou jen v adrese. <br />
     * Hleda pomoci jeStejnaAdresa().
     * @param hledana
     * @return
     * @author Stanislav Řehák
     */
    public boolean obsahujeStejnouAdresu(IpAdresa hledana) {
        for (IpAdresa ip : seznamAdres) {
            if (ip != null && ip.jeStejnaAdresa(hledana)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vrati true, pokud najde ip adresu shodnou v adrese + masce. <br />
     * Hleda pomoci equals().
     * @param hledana
     * @return
     * @author Tomáš Pitřinec
     */
    public boolean obsahujeStejnouAdresuEq(IpAdresa hledana) {
        for (IpAdresa ip : seznamAdres) {
            if (ip != null && ip.equals(hledana)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Zmeni tu privilegovanou prvni adresu. Starou nejdriv smaze a pak tam da novou.
     * Ma smysl, kdyz je ta prvni adresa null.
     * @param adr
     * @author Stanislav Řehák
     */
    public void zmenPrvniAdresu(IpAdresa adr) {
        if (seznamAdres.size() > 0) {
            seznamAdres.remove(0);
        }
        seznamAdres.add(0, adr);
        if (pc instanceof CiscoPocitac) {
            ((CiscoPocitac) pc).getWrapper().update();
        }
    }

    /**
     * Prvni adresu si schovam, vsechny IP smazu a prvni zase pridam.
     * @author Stanislav Řehák
     */
    public void smazVsechnyIpKromPrvni() {
        IpAdresa prvni = null;
        if (vratPrvni() != null) {
            prvni = vratPrvni().vratKopii();
        }
        seznamAdres.clear();
        seznamAdres.add(prvni);
    }

    /**
     * Vrati vypis rozhrani pro cisco prikaz 'show interfaces ..'
     * @return
     */
    public String vratCiscoVypis() {
        String s = jmeno + " is ";
        boolean up = false;
        if (! nahozene) {
            s += "administratively down";
        } else {
            if (pripojenoK == null) { // bez kabelu && nahozene
                s += "down";
                // This indicates a physical problem,
                // either with the interface or the cable attached to it.
                // Or not attached, as the case may be.
            } else { // s kabelem && nahozene
                s += "up";
                up = true;
            }
        }
        s += ", line protocol is ";
        if (up) {
            s += "up";
        } else {
            s += "down";
            /* 1/ encapsulation mismatch, such as when one partner in a point-to-point
             * connection is configured for HDLC and the other for PPP.
             *
             * 2/ Whether the DCE is a CSU/DSU or another Cisco router in a home lab,
             * the DCE must supply a clockrate to the DTE.
             * If that clockrate is not present, the line protocol will come down.
             */
        }
        s += "\n";
        
        s += "  Hardware is Gt96k FE, address is " + macAdresa + " (" + macAdresa + ")\n";
        if (vratPrvni() != null) {
            s += "  Internet address is " + vratPrvni().vypisAdresu() + "\n";
        }
        s += "  MTU 1500 bytes, BW 100000 Kbit/sec, DLY 100 usec, \n"
                + "     reliability 255/255, txload 1/255, rxload 1/255\n"
                + "  Encapsulation ARPA, loopback not set\n"
                + "  Keepalive set (10 sec)\n"
                + "  Auto-duplex, Auto Speed, 100BaseTX/FX\n"
                + "  ARP type: ARPA, ARP Timeout 04:00:00\n"
                + "  Last input never, output never, output hang never\n"
                + "  Last clearing of \"show interface\" counters never\n"
                + "  Input queue: 0/75/0/0 (size/max/drops/flushes); Total output drops: 0\n"
                + "  Queueing strategy: fifo\n"
                + "  Output queue: 0/40 (size/max)\n"
                + "  5 minute input rate 0 bits/sec, 0 packets/sec\n"
                + "  5 minute output rate 0 bits/sec, 0 packets/sec\n"
                + "     0 packets input, 0 bytes\n"
                + "     Received 0 broadcasts, 0 runts, 0 giants, 0 throttles\n"
                + "     0 input errors, 0 CRC, 0 frame, 0 overrun, 0 ignored\n"
                + "     0 watchdog\n"
                + "     0 input packets with dribble condition detected\n"
                + "     115658 packets output, 6971948 bytes, 0 underruns\n"
                + "     0 output errors, 0 collisions, 1 interface resets\n"
                + "     0 unknown protocol drops\n"
                + "     0 babbles, 0 late collision, 0 deferred\n"
                + "     0 lost carrier, 0 no carrier\n"
                + "     0 output buffer failures, 0 output buffers swapped out";
        return s;
    }
}
