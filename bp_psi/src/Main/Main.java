/**
 * Projekt založen v pondělí 4.1.2010
 */

package Main;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author haldyr
 */
public class Main {

    /**
     * Vrati List vytvorenych pocitacu
     * @return
     */
    static private Object nacti(){

        // Cesta ke zdrojovému XML dokumentu
        final String sourcePath = "psi.xml";

        Object o = null;

        try {

            // Vytvoříme instanci parseru.
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // Vytvoříme vlastní content handler.
            SAXHandler sax = new SAXHandler();

            // Vytvoříme vstupní proud XML dat.
            InputSource source = new InputSource(sourcePath);

            // Nastavíme náš vlastní content handler pro obsluhu SAX událostí.
            parser.setContentHandler(sax);

            // Zpracujeme vstupní proud XML dat.
            parser.parse(source);
            o = sax.vratNastaveni();

        } catch (Exception e) {

            // TODO: zakomentovat, az bude vse hotovo
            e.printStackTrace();

        }
        
        return o;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        
        // 
        Object pocitace = nacti();

        if ( pocitace == null) {
            System.err.println("Nepodarilo se nic nacist z konfiguraku.\nUkoncuji..");
            System.exit(131);
        }




        // pro testovani, abych to nemusel furt vypinat rucne
//        System.exit(0);
    }

}
