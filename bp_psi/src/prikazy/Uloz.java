package prikazy;

import Main.SAXHandler.*;
import datoveStruktury.CiscoWrapper.CiscoZaznam;
import datoveStruktury.NATAccessList.AccessList;
import datoveStruktury.NATPool.Pool;
import datoveStruktury.NATPoolAccess.PoolAccess;
import datoveStruktury.NATtabulka.NATzaznam;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.LinuxPocitac;
import pocitac.SitoveRozhrani;
import vyjimky.NeznamyTypPcException;
import static Main.Main.*;

/**
 * Prikaz ukladani do XML souboru vsechny pocitace dle aktualnich nastaveni.
 * @author haldyr
 */
public class Uloz extends AbstraktniPrikaz {

    List<AbstraktniPocitac> pocitace;
    BufferedWriter out;
    String tabs = "";
    String soubor = konfigurak;
    List<PCJmeno> pripojenoKnove;

    public Uloz(AbstraktniPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        pocitace = (List<AbstraktniPocitac>) Main.Main.vsechno;
        pripojenoKnove = new ArrayList();
        if (slova.size() >= 2) {
            soubor = slova.get(1);
        }
        vykonejPrikaz();
    }

    private class PCJmeno {

        private PCJmeno(String pc_jmeno, String rozh_jmeno) {
            this.pc_jmeno = pc_jmeno;
            this.rozh_jmeno = rozh_jmeno;
        }
        String pc_jmeno;
        String rozh_jmeno;
    }

    /**
     * Zapise do souboru dany pocitac v XML formatu.
     * @param pocitac pocitac, ktery chceme zapsat
     * @throws IOException
     */
    private void ulozPC(AbstraktniPocitac pocitac) throws IOException {
        out.write("<pocitac jmeno=\"" + pocitac.jmeno + "\" ");

        if (pocitac instanceof CiscoPocitac) {
            out.write("typ=\"cisco\">\n");
        } else if (pocitac instanceof LinuxPocitac) {
            out.write("typ=\"linux\">\n");
        } else {
            throw new NeznamyTypPcException();
        }

        tabs += "\t";

        for (SitoveRozhrani iface : pocitac.rozhrani) {
            ulozRozhrani(iface);
        }

        ulozRoutovaciTabulku(pocitac);

        if (pocitac instanceof LinuxPocitac) {
            if (pocitac.ip_forward) {
                zapis(vratElement("ip_forward", "1"));
            } else {
                zapis(vratElement("ip_forward", "0"));
            }
        }

        ulozNATtabulku(pocitac);

        tabs = tabs.substring(1);
        zapis("</pocitac>\n\n");
    }

    /**
     * Vrati element, ktery vyrobi na zaklade parametru.
     * @param jmeno jmeno elementu
     * @param obsah obsah elementu, kdyz je null, tak tam bude prazdnej ""
     * @return
     */
    private String vratElement(String jmeno, String obsah) {
        if (obsah == null) {
            obsah = "";
        }

        return "<" + jmeno + ">" + obsah + "</" + jmeno + ">\n";
    }

    /**
     * Zapise do souboru dane rozhrani v XML formatu.
     * @param rozhrani rozhrani, ktere chceme zapsat
     * @throws IOException
     */
    private void ulozRozhrani(SitoveRozhrani rozhrani) throws IOException {
        zapis("<rozhrani>\n");
        tabs += "\t";
        zapis(vratElement("jmeno", rozhrani.jmeno));
        if (rozhrani.vratPrvni() == null) {
            zapis(vratElement("ip", ""));
            zapis(vratElement("maska", ""));
        } else {
            zapis(vratElement("ip", rozhrani.vratPrvni().vypisAdresu()));
            zapis(vratElement("maska", rozhrani.vratPrvni().vypisMasku()));
        }
        zapis(vratElement("mac", rozhrani.macAdresa));
        zapis(vratElement("nahozene", rozhrani.jeNahozene() ? "true" : "false"));

        if (rozhrani.getPc().natTabulka.vratInside().contains(rozhrani)) {
            zapis(vratElement("nat", "soukrome"));
        }
        if (rozhrani.getPc().natTabulka.vratVerejne() != null) {
            if (rozhrani.getPc().natTabulka.vratVerejne().jmeno.equals(rozhrani.jmeno)) {
                zapis(vratElement("nat", "verejne"));
            }
        }


        tabs = tabs.substring(1);
        zapis("</rozhrani>\n");
    }

    /**
     * Zapise do souboru routovaci tabulku pro dany pocitac v XML formatu.
     * @param pc pocitac u ktereho chceme zapsat routovaci tabulku
     * @throws IOException
     */
    private void ulozRoutovaciTabulku(AbstraktniPocitac pc) throws IOException {

        if (pc.routovaciTabulka.pocetZaznamu() == 0) {
            return;
        }

        zapis("<routy>\n");
        tabs += "\t";

        if (pc instanceof LinuxPocitac) {

            for (int i = 0; i < pc.routovaciTabulka.pocetZaznamu(); i++) {

                zapis("<zaznam>\n");
                tabs += "\t";

                zapis(vratElement("adresat", pc.routovaciTabulka.vratZaznam(i).getAdresat().vypisAdresu()));
                zapis(vratElement("maskaAdresata", pc.routovaciTabulka.vratZaznam(i).getAdresat().vypisMasku()));

                if (pc.routovaciTabulka.vratZaznam(i).getBrana() != null) {
                    zapis(vratElement("brana", pc.routovaciTabulka.vratZaznam(i).getBrana().vypisAdresu()));
                } else {
//                    zapis(vratElement("brana", "null")); // nakonec to tam nechci
                }
                zapis(vratElement("rozhraniKam", pc.routovaciTabulka.vratZaznam(i).getRozhrani().jmeno));

                tabs = tabs.substring(1);
                zapis("</zaznam>\n");
            }
        }

        if (pc instanceof CiscoPocitac) {
            // u cisca ukladam jen zadane prikazy, protoze se routy z rozhrani generuji automaticky

            CiscoPocitac poc = (CiscoPocitac) pc;
            for (int i = 0; i < poc.getWrapper().size(); i++) {
                CiscoZaznam zaznam = poc.getWrapper().vratZaznam(i);

                zapis("<zaznam>\n");
                tabs += "\t";

                zapis(vratElement("adresat", zaznam.getAdresat().vypisAdresu()));
                zapis(vratElement("maskaAdresata", zaznam.getAdresat().vypisMasku()));

                if (zaznam.getBrana() != null) {
                    zapis(vratElement("brana", zaznam.getBrana().vypisAdresu()));
                }

                if (zaznam.getRozhrani() != null) {
                    zapis(vratElement("rozhraniKam", zaznam.getRozhrani().jmeno));
                }

                tabs = tabs.substring(1);
                zapis("</zaznam>\n");
            }
        }

        tabs = tabs.substring(1);
        zapis("</routy>\n");
    }

    /**
     * Zapise do souboru vsechno ohledne nastaveni DNATu.
     * @throws IOException
     */
    private void ulozNATtabulku(AbstraktniPocitac pocitac) throws IOException {

        zapis("<natovani>\n");
        tabs += "\t";

        ulozNATPooly(pocitac);
        ulozNATPoolAccess(pocitac);
        ulozNATAccessList(pocitac);
        ulozNATStaticky(pocitac);

        tabs = tabs.substring(1);
        zapis("</natovani>\n");

    }

    /**
     * Pomocna metoda pro ulozeni NAT poolu.
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATPooly(AbstraktniPocitac pocitac) throws IOException {
        zapis("<pooly>\n");
        tabs += "\t";

        for (Pool pool : pocitac.natTabulka.lPool.seznam) {
            zapis("<pool>\n");
            tabs += "\t";
            zapis(vratElement("pJmeno", pool.jmeno));
            zapis(vratElement("ip_start", pool.prvni().vypisAdresu()));
            zapis(vratElement("ip_konec", pool.posledni().vypisAdresu()));
            zapis(vratElement("prefix", "" + pool.prvni().pocetBituMasky()));
            tabs = tabs.substring(1);
            zapis("</pool>\n");
        }

        tabs = tabs.substring(1);
        zapis("</pooly>\n");
    }

    /**
     * Pomocna metoda pro ulozeni NAT PoolAcessu.
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATPoolAccess(AbstraktniPocitac pocitac) throws IOException {
        zapis("<prirazeniVice>\n");
        tabs += "\t";

        for (PoolAccess pa : pocitac.natTabulka.lPoolAccess.seznam) {
            zapis("<prirazeni>\n");
            tabs += "\t";

            zapis(vratElement("accessCislo", "" + pa.access));
            zapis(vratElement("poolJmeno", "" + pa.pool));
            zapis(vratElement("overload", pa.overload ? "true" : "false"));

            tabs = tabs.substring(1);
            zapis("</prirazeni>\n");

        }

        tabs = tabs.substring(1);
        zapis("</prirazeniVice>\n");
    }

    /**
     * Pomocna metoda pro ulozeni NAT access-listu
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATAccessList(AbstraktniPocitac pocitac) throws IOException {
        zapis("<access-listy>\n");
        tabs += "\t";

        for (AccessList access : pocitac.natTabulka.lAccess.seznam) {
            zapis("<access-list>\n");
            tabs += "\t";

            zapis(vratElement("cislo", "" + access.cislo));
            zapis(vratElement("ipA", access.ip.vypisAdresu()));
            zapis(vratElement("ipAWildcard", access.ip.vypisWildcard()));

            tabs = tabs.substring(1);
            zapis("</access-list>\n");
        }

        tabs = tabs.substring(1);
        zapis("</access-listy>\n");
    }

    /**
     * Ulozi staticka pravidla do konfiguraku.
     * @param pocitac
     * @throws IOException
     */
    private void ulozNATStaticky(AbstraktniPocitac pocitac) throws IOException {

        for (NATzaznam zaznam : pocitac.natTabulka.vratTabulku()) {
            if (zaznam.jeStaticke()) {
                zapis("<staticke>\n");
                tabs += "\t";

                zapis(vratElement("in", zaznam.vratIn().vypisAdresu()));
                zapis(vratElement("out", zaznam.vratOut().vypisAdresu()));

                tabs = tabs.substring(1);
                zapis("</staticke>\n");
            }
        }
    }

    /**
     * Ulozi propojeni pocitacu pomoci kabelu.
     * @throws IOException
     */
    private void ulozPripojeni() throws IOException {

        zapis("<kabelaz>\n");
        tabs += "\t";

        for (AbstraktniPocitac pocitac : pocitace) {
            for (SitoveRozhrani iface : pocitac.rozhrani) {
                if (jeRozhraniPripojenoAUlozeno(pocitac.jmeno, iface.jmeno) || iface.pripojenoK == null) {
                    continue;
                }

                zapis("<kabel>\n");
                tabs += "\t";
                zapis(vratElement("prvni", pocitac.jmeno+":"+iface.jmeno));
                zapis(vratElement("druhy", iface.pripojenoK.getPc().jmeno+":"+iface.pripojenoK.jmeno));

                tabs = tabs.substring(1);
                zapis("</kabel>\n");
                pripojenoKnove.add(new PCJmeno(pocitac.jmeno, iface.jmeno));
                pripojenoKnove.add(new PCJmeno(iface.pripojenoK.getPc().jmeno, iface.pripojenoK.jmeno));
            }
        }
        
        tabs = tabs.substring(1);
        zapis("</kabelaz>\n");
    }

    /**
     * Vrati true, pokud je uz takove rozhrani zapsane do konfiguraku nebo pokud k rozhrani nevede kabel.
     * @param pc jmeno pocitace
     * @param jm jmeno rozhrani
     * @return
     */
    private boolean jeRozhraniPripojenoAUlozeno(String pc, String jm) {
        for (PCJmeno zaznam : pripojenoKnove) {
            if (zaznam.pc_jmeno.equals(pc) && zaznam.rozh_jmeno.equals(jm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pomocna metoda na zapis do souboru. Pridava odsazeni, ktere je udrzovano pomoci jinych metod (a podle umisteni v souboru).
     * @param s text, ktery chceme zapsat.
     * @throws IOException
     */
    private void zapis(String s) throws IOException {
        out.write(tabs + s);
    }

    @Override
    public void vykonejPrikaz() {

        kon.posliRadek("Ukladam do " + soubor + "..");

        try {
            out = new BufferedWriter(new FileWriter(soubor));
            zapis("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            zapis("<!-- DTD tady musi byt!!! -->\n"
                    + "<!DOCTYPE konfigurak SYSTEM \"psi.dtd\">\n\n"
                    + "<konfigurak>\n");

            for (AbstraktniPocitac pocitac : pocitace) {
                ulozPC(pocitac);
            }

            ulozPripojeni();

            out.write("</konfigurak>\n");
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Chyba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
