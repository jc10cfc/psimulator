/**
 * Projekt založen v pondělí 4.1.2010
 */

package Main;
import java.util.List;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import pocitac.*;

/**
 *
 * @author haldyr
 */
public class Main {

    static private Object vratNastaveni(){

        // Cesta ke zdrojovému XML dokumentu
        final String sourcePath = "psi.xml";

        Object o = null;

        try {

            // Vytvoříme instanci parseru.
            XMLReader parser = XMLReaderFactory.createXMLReader();

            SAXHandler sax = new SAXHandler();

            // Vytvoříme vstupní proud XML dat.
            InputSource source = new InputSource(sourcePath);

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

        


        AbstractPocitac pc=new AbstractPocitac();
//        AbstractPocitac pc2= new AbstractPocitac(3566);

        Object nastaveni = vratNastaveni();

        if ( nastaveni == null) {
            System.err.println("Nepodarilo se nic nacist z konfiguraku.\nUkoncuji..");
            System.exit(131);
        }

        // --------vypis---------
//        System.out.println(nastaveni);

        // --------vypis---------
        int cisloPC = 1;
        int cisloRozhrani = 1;
        for (Object computer : (List)nastaveni) {

            cisloRozhrani = 1;
            System.out.println("PC"+cisloPC);
            for (Object rozh : (List)computer) {
                System.out.println(" rozhrani c."+cisloRozhrani);
//                System.out.println("  "+rozh);
                System.out.println("   jmeno:      "+((List)rozh).get(0));
                System.out.println("   ip:         "+((List)rozh).get(1));
                System.out.println("   mac:        "+((List)rozh).get(2));
                System.out.println("   pripojenoK: "+((List)rozh).get(3));

                System.out.println("");
                cisloRozhrani++;
            }
            cisloPC++;
        }

        // pro testovani, abych to nemusel furt vypinat rucne
//        System.exit(0);
    }

}
