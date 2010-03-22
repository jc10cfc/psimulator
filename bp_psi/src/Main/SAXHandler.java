package Main;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import pocitac.AbstractPocitac;
import pocitac.CiscoPocitac;
import pocitac.LinuxPocitac;
import pocitac.SitoveRozhrani;
import vyjimky.ChybaKonfigurakuException;

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
    final int velikostPoleRozhrani = 6;
    List<AbstractPocitac> hotovePocitace = new ArrayList<AbstractPocitac>(); // tady drzim seznam vytvorenych objektu tridy AbstraktPocitac
    String[] rozhrani = new String[velikostPoleRozhrani]; //naddimenzovano do budoucna
    String[] zaznam = new String[4]; //adresat, maskaAdresata, brana, rozhrani
    boolean vypis = false; // pro vypis kostry xml dokumentu
    boolean vypis2 = false; // vypis pocitacu
    static public int port = -1;
    List<List> pripojeno = new ArrayList<List>();
    List<PocitacBuilder> seznamPocitacBuilder = new ArrayList<PocitacBuilder>();
    private int aktualniPC = -1;

    /**
     * Nastaví locator
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * Vrati odkaz na aktualne zpracovavane PC.
     * @return
     */
    private PocitacBuilder dejOdkazNaAktualniPC() {
        return seznamPocitacBuilder.get(aktualniPC);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do rozhrani.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoRozhrani(String localName) {
        String[] pole = {"jmeno", "ip", "mac", "pripojenoK", "maska", "nahozene"};

        for (String s : pole) {
            if (localName.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do zaznamu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoZaznamu(String localName) {
        String[] pole = {"adresat", "maskaAdresata", "brana", "rozhraniKam"};

        for (String s : pole) {
            if (localName.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pomocna metoda pro pristup k poli (prvky pole jsou casti zaznamu)
     * @param s   - co chceme z pole
     * @return  index v poli, kde se hledana hodnota naleza
     */
    private int dejIndexVZaznamu(String s) {
        int i = 9;
        if (s.equals("adresat")) {
            i = 0;
        } else if (s.equals("maskaAdresata")) {
            i = 1;
        } else if (s.equals("brana")) {
            i = 2;
        } else if (s.equals("rozhraniKam")) {
            i = 3;
        }

        return i;
    }

    /**
     * Pomocna metoda pro pristup k poli (prvky pole jsou casti rozhrani)
     * @param s   - co chceme z pole
     * @return  index v poli, kde se hledana hodnota naleza
     */
    private int dejIndexVRozhrani(String s) {

        int i = 9;
        if (s.equals("jmeno")) {
            i = 0;
        } else if (s.equals("ip")) {
            i = 1;
        } else if (s.equals("maska")) {
            i = 2;
        } else if (s.equals("mac")) {
            i = 3;
        } else if (s.equals("pripojenoK")) {
            i = 4;
        } else if (s.equals("nahozene")) {
            i = 5;
        }

        return i;
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

        jmenoElementu = localName;
        String attsStr = "";

        for (int i = 0; i < atts.getLength(); i++) { // pro vypis
            attsStr += (" " + atts.getQName(i) + "=\"" + atts.getValue(i) + "\"");
        }

        if (localName.equals("pocitac")) {

            aktualniPC++;
            PocitacBuilder pcbuilder = new PocitacBuilder();
            seznamPocitacBuilder.add(pcbuilder);

            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("jmeno")) {
                    dejOdkazNaAktualniPC().jmeno = atts.getValue(i);
                }
                if (atts.getQName(i).equals("typ")) {
                    dejOdkazNaAktualniPC().typ = atts.getValue(i);
                }
            }
        }

        if (localName.equals("rozhrani")) {
            for (int i = 0; i < rozhrani.length; i++) {
                rozhrani[i] = "";
            }
        }

        if (localName.equals("zaznam")) {
            for (int i = 0; i < zaznam.length; i++) {
                zaznam[i] = "";
            }
        }

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

            String[] pole = new String[rozhrani.length];
            for (int i = 0; i < rozhrani.length; i++) {
                pole[i] = rozhrani[i];
            }

            dejOdkazNaAktualniPC().rozhrani.add(pole);
        }

        if (localName.equals("zaznam")) {

            String[] pole = new String[zaznam.length];
            for (int i = 0; i < zaznam.length; i++) {
                pole[i] = zaznam[i];
            }

            dejOdkazNaAktualniPC().routovaciTabulka.add(pole);
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

        if (jmenoElementu.equals("port")) {
            try {
                port = Integer.valueOf(s);
            } catch (Exception e) {
                System.out.println("Specifikace portu musi byt ciselna.\nChyba: " + s);
                System.exit(1);
            }
            return;
        }

        if (patriDoZaznamu(jmenoElementu)) {
            zaznam[dejIndexVZaznamu(jmenoElementu)] = s;
        }

        if (patriDoRozhrani(jmenoElementu)) {
            rozhrani[dejIndexVRozhrani(jmenoElementu)] = s;
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
     * Obsluha události "konec dokumentu".
     * V teto metode se vyrabeji vsechny pocitace, natahujou draty mezi "fyzicky" spojenymi rozhranimi.
     */
    @Override
    public void endDocument() throws SAXException {


        for (PocitacBuilder pcbuilder : seznamPocitacBuilder) {
            AbstractPocitac pocitac;

            if (pcbuilder.typ.equals("cisco")) {
                pocitac = new CiscoPocitac(pcbuilder.jmeno, port++);
            } else if (pcbuilder.typ.equals("linux")) {
                pocitac = new LinuxPocitac(pcbuilder.jmeno, port++);
            } else {
                throw new ChybaKonfigurakuException("Pocitac nema nastaven typ - linux | cisco.");
            }

            if (vypis2) {
                System.out.println("\nPC");
                System.out.println(" jmeno: " + pcbuilder.jmeno);
                System.out.println(" typ:   " + pcbuilder.typ + "\n");
            }

            for (String[] iface : pcbuilder.rozhrani) { // prochazim a pridavam rozhrani k PC

                if (vypis2) {
                    System.out.println("  jmeno: " + iface[dejIndexVRozhrani("jmeno")]);
                    System.out.println("  ip:    " + iface[dejIndexVRozhrani("ip")]);
                    System.out.println("  maska: " + iface[dejIndexVRozhrani("maska")]);
                    System.out.println("  mac:   " + iface[dejIndexVRozhrani("mac")]);
                    System.out.println("  conn:  " + iface[dejIndexVRozhrani("pripojenoK")]);
                    System.out.println("  nahoze:" + iface[dejIndexVRozhrani("nahozene")]);
                    System.out.println("");
                }

                SitoveRozhrani sr = new SitoveRozhrani(iface[dejIndexVRozhrani("jmeno")], pocitac, iface[dejIndexVRozhrani("mac")]);

                // osetreni prazdne IP nebo masky
                // kdyz chybi maska, tak se dopocita v kontruktou IpAdresy, kdyz chybi IP, tak se maska neresi
                if (iface[dejIndexVRozhrani("maska")].equals("") && !iface[dejIndexVRozhrani("ip")].equals("")) { // chybi maska, ale IP je, pak se maska dopocita
                    IpAdresa ip = new IpAdresa(iface[dejIndexVRozhrani("ip")]);
                    sr.ip = ip;
                } else if (!iface[dejIndexVRozhrani("ip")].equals("") && !iface[dejIndexVRozhrani("maska")].equals("")) { // kdyz je tu oboje
                    IpAdresa ip = new IpAdresa(iface[dejIndexVRozhrani("ip")], iface[dejIndexVRozhrani("maska")]);
                    sr.ip = ip;
                } else if (!iface[dejIndexVRozhrani("maska")].equals("") && iface[dejIndexVRozhrani("ip")].equals("")) { // vypisem, ze preskakujem
                    System.err.println("Preskakuji masku z duvodu nepritomnosti IP adresy..");
                }

                if (iface[dejIndexVRozhrani("nahozene")].equals("1") || iface[dejIndexVRozhrani("nahozene")].equals("true")) {
                    sr.nastavRozhrani(true);
                } else if (iface[dejIndexVRozhrani("nahozene")].equals("0") || iface[dejIndexVRozhrani("nahozene")].equals("false")) {
                    sr.nastavRozhrani(false);
                }

                pocitac.pridejRozhrani(sr);

                if (iface[dejIndexVRozhrani("pripojenoK")].contains(":")) {
                    List prip = new ArrayList<String>();
                    prip.add(pocitac.jmeno);
                    prip.add(sr.jmeno);

                    String[] pole = iface[dejIndexVRozhrani("pripojenoK")].split(":");
                    if (pole.length == 2) {
                        prip.add(pole[0]);
                        prip.add(pole[1]);
                        pripojeno.add(prip);
                    } else {
                        vypisChybuPriZpracovaniPripojenoKXML(iface);
                    }
                } else if (!iface[dejIndexVRozhrani("pripojenoK")].equals("")) {
                    vypisChybuPriZpracovaniPripojenoKXML(iface);
                }
            }

            for (String[] mujzaznam : pcbuilder.routovaciTabulka) { // tady resim routovaci tabulku

                if (vypis2) {
                    System.out.print(" ");
                    for (String s : mujzaznam) {
                        System.out.print(s + "\t");
                    }
                    System.out.println("");
                }

                IpAdresa adresat = new IpAdresa(mujzaznam[dejIndexVZaznamu("adresat")], mujzaznam[dejIndexVZaznamu("maskaAdresata")]);
                SitoveRozhrani iface = null;
                String jmeno = mujzaznam[dejIndexVZaznamu("rozhraniKam")];
                for (SitoveRozhrani sr : pocitac.rozhrani) {
                    if (sr.jmeno.equals(jmeno)) {
                        iface = sr;
                    }
                }
                if (iface == null) {
                    System.err.println("Nepodarilo se najit rozhrani s nazvem: " + jmeno);
                    System.err.println("Preskakuji tento zaznam v routovaci tabulce..");
                    continue;
                }

                if (mujzaznam[dejIndexVZaznamu("brana")].equals("")
                        || mujzaznam[dejIndexVZaznamu("brana")].equals("null")) { // kdyz to je bez brany
                    pocitac.routovaciTabulka.pridejZaznamBezKontrol(adresat, null, iface);

                } else { // vcetne brany
                    IpAdresa brana = new IpAdresa(mujzaznam[dejIndexVZaznamu("brana")]);
                    pocitac.routovaciTabulka.pridejZaznamBezKontrol(adresat, brana, iface);
                }
            }

            hotovePocitace.add(pocitac);
        }

        // tady resim natazeni dratu (odkazu) mezi rozhranimi spojenych pocitacu
        for (List l : pripojeno) {
            SitoveRozhrani najiteRozhrani = najdiDaneRozhrani(l.get(0), l.get(1));
            if (najiteRozhrani == null) {
                vypisChybuPriHledaniRozhrani(l.get(0), l.get(1));
                break;
            }

            SitoveRozhrani najiteRozhrani2 = najdiDaneRozhrani(l.get(2), l.get(3));
            if (najiteRozhrani2 == null) {
                vypisChybuPriHledaniRozhrani(l.get(2), l.get(3));
                break;
            }

            if (najiteRozhrani.equals(najiteRozhrani2)) {
                System.out.println("Nemuze byt pripojeno rozhrani samo na sebe -> break;");
                break;
            }

            najiteRozhrani.pripojenoK = najiteRozhrani2;
        }
    }

    /**
     * Vypise na standartni chybovy vystup hlasku o zpracovani elementu propojenoK.
     * Kdyz je obsah elementu ve spatnem formatu, tak to vypise hlasku a preskoci tento element.
     * @param iface - pole stringu, ve kterem je ulozen obsah elemetu pripojenoK
     */
    private void vypisChybuPriZpracovaniPripojenoKXML(String[] iface) {
        System.err.println("Ignoruji volbu pripojenoK: '" + iface[dejIndexVRozhrani("pripojenoK")] + "'");
        System.err.println("pripojenoK musi byt ve tvaru nazevPC:nazevRozhrani");
    }

    /**
     * Vypise na standartni chybovy vystup hlasku, ze nebyl nalezen zadny pocitac s danym rozhranim.
     * @param o1, reference na jmeno pocitace
     * @param o2, reference na jmeno rozhrani
     */
    private void vypisChybuPriHledaniRozhrani(Object o1, Object o2) {
        System.err.println("Nepodarilo se najit pocitac " + o1 + " s rozhranim " + o2 + ". Preskakuji..");
    }

    /**
     * Vraci rozhrani, ktere odpovida jmenu pocitaci + jmenu rozhrani dle parametru.
     * @param pc0, reference na jmeno pocitace
     * @param rozhrani0, reference na jmeno rozhrani
     * @return
     */
    private SitoveRozhrani najdiDaneRozhrani(Object pc0, Object rozhrani0) {
        String pc = (String) pc0;
        String rozhr = (String) rozhrani0;
        for (AbstractPocitac apc : hotovePocitace) {
            if (apc.jmeno.equals(pc)) {
                for (SitoveRozhrani iface : apc.rozhrani) {
                    if (iface.jmeno.equals(rozhr)) {
                        return iface;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Jednoduchy getter pro Main. 
     * @return
     */
    public Object vratNastaveni() {

//        for (PocitacBuilder pcbuilder : seznamPocitacBuilder) {
//            System.out.println(pcbuilder);
//        }

        return hotovePocitace;
    }
}
