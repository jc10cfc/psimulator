package prikazy;

import Main.SAXHandler.*;
import datoveStruktury.CiscoWrapper.CiscoZaznam;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import pocitac.AbstractPocitac;
import pocitac.CiscoPocitac;
import pocitac.Konsole;
import pocitac.LinuxPocitac;
import pocitac.SitoveRozhrani;
import vyjimky.NeznamyTypPcException;

/**
 * Prikaz ukladani do XML souboru vsechny pocitace dle aktualnich nastaveni.
 * @author haldyr
 */
public class Uloz extends AbstraktniPrikaz {

    List<AbstractPocitac> pocitace;
    BufferedWriter out;
    String tabs = "";
    String soubor = "psi2.xml";

    public Uloz(AbstractPocitac pc, Konsole kon, List<String> slova) {
        super(pc, kon, slova);
        pocitace = (List<AbstractPocitac>) Main.Main.vsechno;
        if (slova.size() >= 2) {
            soubor = slova.get(1);
        }
        vykonejPrikaz();
    }

    /**
     * Zapise do souboru dany pocitac v XML formatu.
     * @param pocitac pocitac, ktery chceme zapsat
     * @throws IOException
     */
    private void ulozPC(AbstractPocitac pocitac) throws IOException {
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
        if (rozhrani.pripojenoK == null) {
            zapis(vratElement("pripojenoK", ""));
        } else {
            zapis(vratElement("pripojenoK", rozhrani.pripojenoK.getPc().jmeno + ":" + rozhrani.pripojenoK.jmeno));
        }
        zapis(vratElement("nahozene", rozhrani.jeNahozene() ? "true" : "false"));

        tabs = tabs.substring(1);
        zapis("</rozhrani>\n");
    }

    /**
     * Zapise do souboru routovaci tabulku pro dany pocitac v XML formatu.
     * @param pc pocitac u ktereho chceme zapsat routovaci tabulku
     * @throws IOException
     */
    private void ulozRoutovaciTabulku(AbstractPocitac pc) throws IOException {

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

            CiscoPocitac poc = (CiscoPocitac)pc;
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
     * Pomocna metoda na zapis do souboru. Pridava odsazeni, ktere je udrzovano pomoci jinych metod (a podle umisteni v souboru).
     * @param s text, ktery chceme zapsat.
     * @throws IOException
     */
    private void zapis(String s) throws IOException {
        out.write(tabs + s);
    }

    @Override
    public void vykonejPrikaz() {

        kon.posliRadek("Saving to file " + soubor + "..");

        try {
            out = new BufferedWriter(new FileWriter(soubor));
            zapis("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            zapis("<!-- DTD tady musi byt!!! -->\n"
                    + "<!DOCTYPE konfigurak SYSTEM \"psi.dtd\">\n\n"
                    + "<konfigurak>\n");

            zapis(vratElement("port", pocitace.get(0).komunikace.getPort() + "") + "\n");

            for (AbstractPocitac poc : pocitace) {
                ulozPC(poc);
            }

            out.write("</konfigurak>\n");
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Chyba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
