package prikazy;

import Main.SAXHandler.*;
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
 * Prikaz uloz, ktery ulozi do souboru XML vsechny pocitace dle aktualnich nastaveni.
 * @author haldyr
 */
public class Uloz extends AbstraktniPrikaz {

    List<AbstractPocitac> pocitace;
    BufferedWriter out;
    String tabs = "";
    String soubor = "psi2.xml";

    public Uloz(AbstractPocitac pc, Konsole kon, List<String> slova, Object o) {
        super(pc, kon, slova);
        pocitace = (List<AbstractPocitac>) o;
        if (slova.size() >= 2) {
            soubor = slova.get(1);
        }
        vykonejPrikaz();
    }

    /**
     * Zapise do souboru dany pocitac v XML formatu.
     * @param pocitac, ktery chceme zapsat
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

        tabs = tabs.substring(1);
        zapis("</pocitac>\n\n");
    }

    /**
     * Vrati element, ktery vyrobi na zaklade parametru.
     * @param jmeno - jmeno elementu
     * @param obsah - obsah elementu
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
     * @param rozhrani, ktere chceme zapsat
     * @throws IOException
     */
    private void ulozRozhrani(SitoveRozhrani rozhrani) throws IOException {
        zapis("<rozhrani>\n");
        tabs += "\t";

        zapis(vratElement("jmeno", rozhrani.jmeno));
        if (rozhrani.ip == null) {
            zapis(vratElement("ip", ""));
            zapis(vratElement("maska", ""));
        } else {
            zapis(vratElement("ip", rozhrani.ip.vypisIP()));
            zapis(vratElement("maska", rozhrani.ip.vypisMasku()));
        }
        zapis(vratElement("mac", rozhrani.macAdresa));
        if (rozhrani.pripojenoK == null) {
            zapis(vratElement("pripojenoK", ""));
        } else {
            zapis(vratElement("pripojenoK", rozhrani.pripojenoK.getPc().jmeno + ":" + rozhrani.pripojenoK.jmeno));
        }

        tabs = tabs.substring(1);
        zapis("</rozhrani>\n");
    }

    /**
     * Pomocna metoda na zapis do souboru. Pridava odsazeni, ktere je udrzovano pomoci jinych metod (a podle umisteni v souboru).
     * @param s - text, ktery chceme zapsat.
     * @throws IOException
     */
    private void zapis(String s) throws IOException {
        out.write(tabs + s);
    }

    @Override
    public void vykonejPrikaz() {

        kon.posliRadek("Ukladam do souboru " + soubor + "..");

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

    /*
    public static void main(String[] args) {

    Object o = Main.Main.nacti();

    //Uloz ukladac = new Uloz(Main.Main.vsechno);
    Uloz ukladac = new Uloz(o);
    ukladac.vykonejPrikaz(null);
    }
     */
}
