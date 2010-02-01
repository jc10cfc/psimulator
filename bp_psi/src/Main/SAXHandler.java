package Main;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.Attributes;
import pocitac.AbstractPocitac;
import pocitac.SitoveRozhrani;

/**
 * Na základě SAX událostí rekonstruujte elementy a atributy původního
 * XML dokumentu (včetně původního vnoření elementu a jmenných prostorů).
 * Znaková data ignorujte. Zanorené elementy formátujte odstavením pomocí tabulátoru.
 * pouzito http://www.ksi.mff.cuni.cz/~mlynkova/Y36XML/indexCV.html
 * 
 * @author haldyr
 */


/**
 * Náš vlastní content handler pro obsluhu SAX událostí.
 * Implementuje metody interface ContentHandler.
 */
public class SAXHandler implements ContentHandler {

    // Umožnuje zacílit místo v dokumentu, kde vznikla aktualní událost
    Locator locator;
    String tabs = "";
    String namespaces = "";
    String jmenoElementu = "";
    List vsechno = new ArrayList<List>();
    List pocitac = new ArrayList<List>();
    List rozhrani = new ArrayList<String>();
    // pro vypis kostry xml dokumentu
    boolean vypis = true;
    public int port = -1;

    List hotovePocitace = new ArrayList<AbstractPocitac>();

    /**
     * Nastaví locator
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    private boolean patriDoRozhrani(String localName) {
        if (localName.equals("jmeno") || localName.equals("ip") || localName.equals("mac") || localName.equals("pripojenoK")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * pomocna metoda
     * @param l
     * @return
     */
    private List zkopirujListStringu(List l) {
        List novy = new ArrayList<String>();
        for (Object o : l) {
            novy.add(o);
        }
        return novy;
    }

    /**
     * Obsluha události "zacátek elementu".
     * @param uri URI jmenného prostoru elementu (prázdné, pokud element není v žádném jmenném prostoru)
     * @param localName Lokální jméno elementu (vždy neprázdné)
     * @param qName Kvalifikované jméno (tj. prefix-uri + ':' + localName, pokud je element v nejakém jmenném prostoru, nebo localName, pokud element není v žádném jmenném prostoru)
     * @param atts Atributy elementu
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        
        String attsStr = "";
        for (int i = 0; i < atts.getLength(); i++) {
            attsStr += (" " + atts.getQName(i) + "=\"" + atts.getValue(i) + "\"");
        }

        if (localName.equals("pocitac")) {
            pocitac.clear();
        }

        if (localName.equals("rozhrani")) {
            rozhrani.clear();
        }

        jmenoElementu = localName;

        if (vypis) {
            System.out.printf("%s<%s%s%s>\n", tabs, qName, namespaces, attsStr);
        }

        tabs += "\t";
    }

    /**
     * Obsluha události "konec elementu"
     * Parametry mají stejný význam jako u @see startElement
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        tabs = tabs.substring(1);

        if (localName.equals("rozhrani")) {

            List rozhraniNove = new ArrayList<String>();
            for (Object o : rozhrani) {
                rozhraniNove.add(o);
            }

            pocitac.add(rozhraniNove);
        }


        if (localName.equals("pocitac")) {

            List pocitacNovy = new ArrayList<List>();
            for (Object o : pocitac) {
//                String jm = (String)(((List)o).get(0));
//                ((List)o).remove(0);
//                List l = zkopirujListStringu((List) o);
//                l.add(0, jm);
                pocitacNovy.add(zkopirujListStringu((List) o));
//                pocitacNovy.add(l);
            }
//            System.out.println("pocitacNovy: " + pocitacNovy);
            vsechno.add(pocitacNovy);
        }

        if (vypis) {
            System.out.printf("%s</%s>\n", tabs, qName);
        }

    }

    /**
     * Obsluha události "znaková data".
     * SAX parser muže znaková data dávkovat jak chce. Nelze tedy pocítat s tím, že je celý text dorucen v rámci jednoho volání.
     * Text je v poli (ch) na pozicich (start) az (start+length-1).
     * @param ch Pole se znakovými daty
     * @param start Index zacátku úseku platných znakových dat v poli.
     * @param length Délka úseku platných znakových dat v poli.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {


        String s = new String(ch, start, length);

        if (vypis) { // tisk
            System.out.println(tabs + s);
        }

        if (jmenoElementu.equals("port") && s.length() > 2) {
//            System.out.println("jsem u portu: "+s+" "+s.length());
            try {
                port = Integer.valueOf(s);
            } catch (Exception e) {
                System.out.println("Specifikace portu musi byt ciselna.\nChyba: "+s);
                System.exit(1);
            }

            return;
        }

        if (jmenoElementu.equals("typ")) {
            pocitac.add(s);
        }

//        System.out.println("jmenoElementu: "+jmenoElementu);

        if (patriDoRozhrani(jmenoElementu)) {
            rozhrani.add(s);
        }

    }

    /**
     * Obsluha události "deklarace jmenného prostoru".
     * @param prefix Prefix prirazený jmennému prostoru.
     * @param uri URI jmenného prostoru.
     */
    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    /**
     * Obsluha události "konec platnosti deklarace jmenného prostoru".
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /**
     * Obsluha události "ignorované bílé znaky".
     * Stejné chování a parametry jako @see characters
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    /**
     * Obsluha události "instrukce pro zpracování".
     */
    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    /**
     * Obsluha události "nezpracovaná entita"
     */
    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    /**
     * Obsluha události "zacátek dokumentu"
     */
    @Override
    public void startDocument() throws SAXException {
        // ...
    }

    /**
     * Obsluha události "konec dokumentu"
     */
    @Override
    public void endDocument() throws SAXException {
        
//        System.out.println("vsechno:  " + vsechno);




        String jmenoPC = "";

        for (Object pc : (List)vsechno) {
            List pcList = (List)pc;

            AbstractPocitac absPocitac = new AbstractPocitac(port++);
            
//            jmenoPC = (String)((pcList).get(0));
//            pcList.remove(0); // mazu typ PC, abych to mohl prochazet jako seznamy
            for (Object rozh : pcList) {
//                System.out.println("   jmeno:      "+((List)rozh).get(0));
//                System.out.println("   ip:         "+((List)rozh).get(1));
//                System.out.println("   mac:        "+((List)rozh).get(2));
//                System.out.println("   pripojenoK: "+((List)rozh).get(3));
                SitoveRozhrani sr = new SitoveRozhrani(jmenoPC, absPocitac, (String)((List)rozh).get(2));
                sr.jmeno = (String)((List)rozh).get(0);
                IpAdresa ip = new IpAdresa((String)((List)rozh).get(1), "255.255.255.0");


            }


            
        }

    }

    public Object vratNastaveni(){
        return vsechno;
    }
}
