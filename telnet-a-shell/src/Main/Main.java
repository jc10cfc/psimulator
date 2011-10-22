/**
 * Project was created on Monday 4.1.2010
 */
package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import telnetd.BootException;
import telnetd.TelnetD;
import telnetd.pridaneTridy.TelnetProperties;

/**
 * Trida main zpracuje parametry a podle nich pozada SAXHandler o data z konfiguraku.
 * @author Stanislav Řehák
 */
public class Main {

    public static String jmenoProgramu = "psi simulator";
    static int port = 4001;
    static boolean bezNastaveni = false;
    public static boolean chyba_spusteni = false;
    /**
     * Object vsechno je refence na seznam vsech pocitacu, ktere se nacetly z konfiguraku.
     */
    public static Object vsechno;
    /**
     * Konfigurak se kterym to bylo spusteny.
     */
    public static String konfigurak;
    public static boolean debug = true;

    /**
     * metoda pro výpis ladících informací programu
     * @param text
     */
    public static void debug(String text) {
        if (debug) {
            System.out.println(text);
        }
    }

    /**
     * Vrati List vytvorenych pocitacu.
     * @return
     */
    public Object nacti() {

        Object o = null;

        try {

            // Vytvoříme instanci parseru.
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // Vytvoříme vlastní content handler.
            SAXHandler sax = new SAXHandler(port, bezNastaveni);

            // Vytvoříme vstupní proud XML dat.
            InputSource source = new InputSource(konfigurak);

            // Nastavíme náš vlastní content handler pro obsluhu SAX událostí.
            parser.setContentHandler(sax);

            // Zpracujeme vstupní proud XML dat.
            parser.parse(source);
            o = sax.vratNastaveni();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return o;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        parsujParametry(args);
        vsechno = new Main().nacti();

        Properties prop = TelnetProperties.getProperties();
        prop.list(System.out);

        TelnetD daemon;
        try {
            System.out.println("Startuji telnet listenery pro vsechny nakonfigurovane pocitace");
            //1. create singleton instance
            daemon = TelnetD.createTelnetD(TelnetProperties.getProperties());
            //2.start serving
            daemon.start();
            System.out.println("Listenery běží");
        } catch (BootException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }



        if (vsechno == null) {
            if (!konfigurak.endsWith(".xml")) {
                System.err.println("\nNepodarilo se nic nacist z konfiguracniho souboru " + konfigurak + ".");
                System.out.println("Zkousim pridat koncovku .xml:\n");
                konfigurak = konfigurak + ".xml";
                vsechno = new Main().nacti();
            }
            if (vsechno == null) {
                System.err.println("Nepodarilo se nic nacist z konfiguracniho souboru " + konfigurak + ".\nUkoncuji..");
                System.exit(131);
            }
        }
    }

    /**
     * Zpracuje parametry pri spusteni serveru.
     * Parametr -n se muze vyskytovat kdekoliv,
     * jinak plati, ze prvni je konfiguracni soubor a pak je port.
     * @param args
     */
    private static void parsujParametry(String[] args) {

        List<String> param = new ArrayList<String>();
        for (String s : args) {
            if (s.equals("-n")) {
                bezNastaveni = true;
                continue;
            }
            param.add(s);
        }

        if (param.size() >= 1) {
            konfigurak = param.get(0);
            if (param.size() >= 2) {
                try {
                    port = Integer.parseInt(param.get(1));
                } catch (NumberFormatException e) {
                    System.err.println(param.get(1) + " neni platne cislo portu.\nUkoncuji..");
                    System.exit(2);
                }
                if (port < 0) {
                    chyba_spusteni = true;
                    System.err.println(param.get(1) + " neni platne cislo portu.\nUkoncuji..");
                }
            }
        } else {
            System.err.println("Parametrem urcete konfiguracni soubor.\nUkoncuji..");
            System.exit(3);
        }
    }
}
