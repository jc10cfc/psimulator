package Main;

import datoveStruktury.IpAdresa;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import pocitac.AbstraktniPocitac;
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
 * Nejdrive se vsechno uklada do datove struktury PocitacBuilder,
 * z nech se pat "stavi" uz vlastni pocitac.
 *
 * @author haldyr
 */
/**
 * Náš vlastní content handler pro obsluhu SAX událostí.
 * Implementuje metody interface ContentHandler.
 */
public class SAXHandler implements ContentHandler {

    /**
     *Umožnuje zacílit místo v dokumentu, kde vznikla aktualní událost
     */
    Locator locator;
    String tabs = "";
    String namespaces = "";
    String jmenoElementu = "";
    List<AbstraktniPocitac> hotovePocitace; // tady drzim seznam vytvorenych objektu tridy AbstraktPocitac
    String[] rozhrani; // jmeno, ip, mac, maska, pripojenoK, nahozene, nat
    String[] zaznam; //adresat, maskaAdresata, brana, rozhrani
    String[] accessList; // cislo, ipA, ipAWildcard
    String[] pool; // pJmeno, ip_start, ip_konec, prefix
    String[] poolAccess; // accessCislo, poolJmeno, overload
    String[] staticke; // in, out
    String[] kablik; // prvni, druhy
    /**
     * Datova struktura obsahujici pole stringu.
     * Kazde takove pole obsahuje prave 2 stringy. Kazdy takovy je validni prave tehdy,
     * kdyz je ve tvaru 'jmenoPocitace:jmenoRozhrani'
     */
    List<String[]> kabelaz;
    List<PocitacBuilder> seznamPocitacBuilder;
    private int aktualniPC = -1;
    /**
     * Port, na kterem se ma zacit
     */
    public int port;
    /**
     * True, pokud program byl spusten s parametrem -n.
     * Pak se postavijen konstra site s rozhranimi bez nastaveni.
     */
    boolean bezNastaveni = false;
    /**
     * pro vypis kostry xml dokumentu
     */
    boolean vypis = false;
    /**
     * vypis uz nactenych dat o pocitacich pred vytvoreni vlastnich pocitacu
     */
    boolean vypis2 = false;

    public SAXHandler(int port, boolean bezNastaveni) {
        hotovePocitace = new ArrayList<AbstraktniPocitac>();
        rozhrani = new String[6];
        zaznam = new String[4];
        accessList = new String[3];
        pool = new String[4];
        poolAccess = new String[3];
        staticke = new String[2];
        kablik = new String[2];
        kabelaz = new ArrayList<String[]>();
        seznamPocitacBuilder = new ArrayList<PocitacBuilder>();
        this.port = port;
        this.bezNastaveni = bezNastaveni;
    }
    /**
     * Vypis stacktrace pri vyjimkach.
     */
    boolean debug = true;

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
     * Vrati true, pokud je v poli obsazen retezec s.
     * @param pole
     * @param s
     * @return
     */
    private boolean jeVPoli(String[] pole, String s) {
        for (String p : pole) {
            if (p.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do rozhrani.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoRozhrani(String localName) {
        String[] pole = {"jmeno", "ip", "mac", "maska", "nahozene", "nat"};
        return jeVPoli(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do zaznamu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoZaznamu(String localName) {
        String[] pole = {"adresat", "maskaAdresata", "brana", "rozhraniKam"};
        return jeVPoli(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do poolu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoNatPoolu(String localName) {
        String[] pole = {"pJmeno", "ip_start", "ip_konec", "prefix"};
        return jeVPoli(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do Nat prirazeni.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoNatPrirazeni(String localName) {
        String[] pole = {"accessCislo", "poolJmeno", "overload"};
        return jeVPoli(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do poolu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoNatAccessListu(String localName) {
        String[] pole = {"cislo", "ipA", "ipAWildcard"};
        return jeVPoli(pole, localName);
    }

    /**
     * Pomocna metoda pro pristup k poli (prvky pole jsou casti zaznamu)
     * @param s   - co chceme z pole
     * @return  index v poli, kde se hledana hodnota naleza
     */
    public static int dejIndexVNatAccessListu(String s) {
        int i = 9;
        if (s.equals("cislo")) {
            i = 0;
        } else if (s.equals("ipA")) {
            i = 1;
        } else if (s.equals("ipAWildcard")) {
            i = 2;
        }
        return i;
    }

    /**
     * Pomocna metoda pro pristup k poli (prvky pole jsou casti zaznamu)
     * @param s   - co chceme z pole
     * @return  index v poli, kde se hledana hodnota naleza
     */
    public static int dejIndexVNatPoolu(String s) {
        int i = 9;
        if (s.equals("pJmeno")) {
            i = 0;
        } else if (s.equals("ip_start")) {
            i = 1;
        } else if (s.equals("ip_konec")) {
            i = 2;
        } else if (s.equals("prefix")) {
            i = 3;
        }
        return i;
    }

    /**
     * Pomocna metoda pro pristup k poli (prvky pole jsou casti zaznamu)
     * @param s   - co chceme z pole
     * @return  index v poli, kde se hledana hodnota naleza
     */
    public static int dejIndexVNatPrirazeni(String s) {
        int i = 9;
        if (s.equals("accessCislo")) {
            i = 0;
        } else if (s.equals("poolJmeno")) {
            i = 1;
        } else if (s.equals("overload")) {
            i = 2;
        }
        return i;
    }

    /**
     * Pomocna metoda pro pristup k poli (prvky pole jsou casti zaznamu)
     * @param s   - co chceme z pole
     * @return  index v poli, kde se hledana hodnota naleza
     */
    public static int dejIndexVZaznamu(String s) {
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
    public static int dejIndexVRozhrani(String s) {

        int i = 9;
        if (s.equals("jmeno")) {
            i = 0;
        } else if (s.equals("ip")) {
            i = 1;
        } else if (s.equals("maska")) {
            i = 2;
        } else if (s.equals("mac")) {
            i = 3;
        } else if (s.equals("nahozene")) {
            i = 4;
        } else if (s.equals("nat")) {
            i = 5;
        }
        return i;
    }

    /**
     * Nastavi vsechny prvky na prazdny retezec.
     * @param pole
     */
    private void vymazPole(String[] pole) {
        for (int i = 0; i < pole.length; i++) {
            pole[i] = "";
        }
    }

    /**
     * Vymaze nastaveni poli urcenych pro ulozeni NATu. (nemaze to nasteveni NATu rozhrani)
     */
    private void vymazVsechnyNATPole() {
        vymazPole(accessList);
        vymazPole(pool);
        vymazPole(poolAccess);
    }

    /**
     * Vrati kopii pole.
     * @param pole
     */
    private String[] vratKopiiPole(String[] pole) {
        String[] nove = new String[pole.length];
        for (int i = 0; i < pole.length; i++) {
            nove[i] = pole[i];
        }
        return nove;
    }

    /**
     * Zjistuje, zda je cele pole naplnene.
     * @param pole
     * @return true - pokud zadny prvek neni prazdny retezec
     *         false - aspon 1 prvek neni prazdny retezec nebo pokud je pole null
     */
    public static boolean jePolePlne(String[] pole) {
        if (pole == null) {
            return false;
        }
        for (int i = 0; i < pole.length; i++) {
            if (pole[i].equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Vrati vypis pole.
     * @param pole
     * @return
     */
    private String vypisPole(String[] pole) {
        String s = "[";
        for (int i = 0; i < pole.length; i++) {
            if (i == 0) {
                s += pole[i];
            } else {
                s += "," + pole[i];
            }
        }
        s += "]";
        return s;
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
            PocitacBuilder pcbuilder = new PocitacBuilder(bezNastaveni);
            seznamPocitacBuilder.add(pcbuilder);

            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("jmeno")) {
                    dejOdkazNaAktualniPC().jmeno = atts.getValue(i);
                }
                if (atts.getQName(i).equals("typ")) {
                    dejOdkazNaAktualniPC().typ = atts.getValue(i);
                }
            }
            vymazVsechnyNATPole();
        }

        if (localName.equals("rozhrani")) {
            vymazPole(rozhrani);
        }

        if (localName.equals("zaznam")) {
            vymazPole(zaznam);
        }

        if (localName.equals("access-list")) {
            vymazPole(accessList);
        }

        if (localName.equals("pool")) {
            vymazPole(pool);
        }

        if (localName.equals("staticke")) {
            vymazPole(staticke);
        }

        if (localName.equals("prirazeni")) {
            vymazPole(poolAccess);
        }

        if (localName.equals("kabel")) {
            vymazPole(kablik);
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
            dejOdkazNaAktualniPC().rozhrani.add(vratKopiiPole(rozhrani));
        }

        if (localName.equals("zaznam")) {
            dejOdkazNaAktualniPC().routovaciTabulka.add(vratKopiiPole(zaznam));
        }

        if (localName.equals("staticke")) {
            dejOdkazNaAktualniPC().staticke.add(vratKopiiPole(staticke));
        }

        if (localName.equals("kabel")) {
            if (jePolePlne(kablik)) {
                kabelaz.add(vratKopiiPole(kablik));
            } else {
                throw new ChybaKonfigurakuException("Neni uplny zaznam u kabelu: " + vypisPole(kablik));
            }
        }

        if (localName.equals("pool")) {
            if (jePolePlne(pool)) {
                dejOdkazNaAktualniPC().pool.add(vratKopiiPole(pool));
            } else {
                System.err.println("Neni uplny zaznam, preskakuji: " + vypisPole(pool));
            }

        }

        if (localName.equals("prirazeni")) {
            if (jePolePlne(poolAccess)) {
                dejOdkazNaAktualniPC().poolAccess.add(vratKopiiPole(poolAccess));
            } else {
                System.err.println("Neni uplny zaznam, preskakuji: " + vypisPole(poolAccess));
            }

        }

        if (localName.equals("access-list")) {
            if (jePolePlne(accessList)) {
                dejOdkazNaAktualniPC().accessList.add(vratKopiiPole(accessList));
            } else {
                System.err.println("Neni uplny zaznam, preskakuji: " + vypisPole(accessList));
            }

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

        if (patriDoZaznamu(jmenoElementu)) {
            zaznam[dejIndexVZaznamu(jmenoElementu)] = s;
            return;
        }
        if (patriDoRozhrani(jmenoElementu)) {
            rozhrani[dejIndexVRozhrani(jmenoElementu)] = s;
            return;
        }
        if (patriDoNatAccessListu(jmenoElementu)) {
            accessList[dejIndexVNatAccessListu(jmenoElementu)] = s;
            return;
        }
        if (patriDoNatPoolu(jmenoElementu)) {
            pool[dejIndexVNatPoolu(jmenoElementu)] = s;
            return;
        }
        if (patriDoNatPrirazeni(jmenoElementu)) {
            poolAccess[dejIndexVNatPrirazeni(jmenoElementu)] = s;
            return;
        }

        if (jmenoElementu.equals("in")) {
            staticke[0] = s;
            return;
        }
        if (jmenoElementu.equals("out")) {
            staticke[1] = s;
            return;
        }

        if (jmenoElementu.equals("prvni")) {
            kablik[0] = s;
            return;
        }

        if (jmenoElementu.equals("druhy")) {
            kablik[1] = s;
            return;
        }

        if (jmenoElementu.equals("ip_forward")) {
            if (s.equals("true") || s.equals("1")) {
                dejOdkazNaAktualniPC().ip_forward = true;
            } else if (s.equals("false") || s.equals("0")) {
                dejOdkazNaAktualniPC().ip_forward = false;
            } else {
                System.err.println("ip_forward musi byt bud true/false nebo 1/0, ne todlencto: " + s);

            }
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
    }

    /**
     * Obsluha události "konec dokumentu".
     * V teto metode se vyrabeji vsechny pocitace, natahujou draty mezi "fyzicky" spojenymi rozhranimi.
     */
    @Override
    public void endDocument() throws SAXException {

        if (bezNastaveni) {
            System.out.println("Nacitam bez nastaveni.. (parametr -n)");
        }

        for (PocitacBuilder pcbuilder : seznamPocitacBuilder) {
            if (bezNastaveni) {
                vymazNastaveni(pcbuilder);
            }

            AbstraktniPocitac pocitac;
            if (pcbuilder.typ.equals("cisco")) {
                pocitac = new CiscoPocitac(pcbuilder.jmeno, port++);
            } else if (pcbuilder.typ.equals("linux")) {
                pocitac = new LinuxPocitac(pcbuilder.jmeno, port++);
                pocitac.ip_forward = pcbuilder.ip_forward;
            } else {
                throw new ChybaKonfigurakuException("Pocitac nema nastaven typ - linux | cisco.");
            }

            if (vypis2) {
                System.out.println(pcbuilder);
            }

            pcbuilder.nactiPooly(pocitac);
            pcbuilder.nactiAccessListy(pocitac);
            pcbuilder.nactiPoolAccess(pocitac);
            pcbuilder.nactiRozhrani(pocitac);
            pcbuilder.nactiStatickyNat(pocitac);

            if (pocitac instanceof CiscoPocitac) {
                ((CiscoPocitac) pocitac).getWrapper().update();
            }

            pcbuilder.nactiRoutovaciTabulku(pocitac);

            if (pocitac instanceof LinuxPocitac) {
                if (pocitac.natTabulka.lzePrelozit(new IpAdresa("1.2.3.4"))) {
                    pocitac.natTabulka.nastavZKonfigurakuLinuxBooleanTrue();
                }
            }
            pocitac.natTabulka.lPool.updateIpNaRozhrani();
            pocitac.natTabulka.pridejIpAdresyZeStatickychPravidel(pocitac.natTabulka.vratVerejne());
            hotovePocitace.add(pocitac);
        }

        nactiKabelaz(kabelaz);
    }

    /**
     * Vrati chybovou hlasku pro kabel ve spatnem tvaru.
     * @param s
     * @return
     */
    private String vypisChybuKabelu(String s) {
        return "Element kabel musi byt ve tvaru 'nazevPC:nazevRozhrani', "
                + "ale ne todlencto: '" + s + "'";
    }

    /**
     * Vrati chybovou hlasku, ze nebyl nalezen zadny pocitac s danym rozhranim.
     * @param pc, jmeno pocitace
     * @param rozh, jmeno rozhrani
     * @return chybova hlaska
     */
    private String vypisChybuPriHledaniRozhrani(String pc, String rozh) {
        return "Pocitac " + pc + " s rozhranim " + rozh + " nebyl nalezen.";
    }

    /**
     * Vrati true, pokud alespon 1 prvek pole je prazdnym retezcem.
     * @param pole
     * @return
     */
    private boolean jeAsponJedenPrvekPrazdnej(String[] pole) {
        for (int i = 0; i < pole.length; i++) {
            if (pole[i].length() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Natahne kabely mezi jednotlivymi rozhranimi.
     * Na jedno rozhrani muze byt pripojen maximalne 1 kabel.
     * @param kabelaz
     */
    private void nactiKabelaz(List<String[]> kabelaz) {
        for (String[] kabel : kabelaz) {
            String[] prvni = kabel[0].split(":");
            String[] druhy = kabel[1].split(":");

            if (!kabel[0].contains(":") || prvni.length != 2 || jeAsponJedenPrvekPrazdnej(prvni)) {
                throw new ChybaKonfigurakuException(vypisChybuKabelu(kabel[0]));
            }
            if (!kabel[1].contains(":") || druhy.length != 2 || jeAsponJedenPrvekPrazdnej(druhy)) {
                throw new ChybaKonfigurakuException(vypisChybuKabelu(kabel[1]));
            }

            SitoveRozhrani iface1 = najdiDaneRozhrani(prvni[0], prvni[1]);
            SitoveRozhrani iface2 = najdiDaneRozhrani(druhy[0], druhy[1]);

            if (iface1 == null) {
                throw new ChybaKonfigurakuException(vypisChybuPriHledaniRozhrani(prvni[0], prvni[1]));
            }
            if (iface2 == null) {
                throw new ChybaKonfigurakuException(vypisChybuPriHledaniRozhrani(druhy[0], druhy[1]));
            }

            if (iface1.pripojenoK != null) {
                throw new ChybaKonfigurakuException("U pocitace " + iface1.getPc().jmeno + ", rozhrani " + iface1.jmeno + " uz jeden kabel vede. Chyba!");
            }
            if (iface2.pripojenoK != null) {
                throw new ChybaKonfigurakuException("U pocitace " + iface2.getPc().jmeno + ", rozhrani " + iface2.jmeno + " uz jeden kabel vede. Chyba!");
            }

            iface1.pripojenoK = iface2;
            iface2.pripojenoK = iface1;
        }
    }

    /**
     * Vraci rozhrani, ktere odpovida jmenu pocitaci + jmenu rozhrani dle parametru.
     * @param pc, jmeno pocitace
     * @param rozh, jmeno rozhrani
     * @return sitove rozhrani od daneho pocitacem s danym jmenem <br />
     *         null - pokud zadny takovy PC s rozhranim neni
     */
    private SitoveRozhrani najdiDaneRozhrani(String pc, String rozh) {
        for (AbstraktniPocitac apc : hotovePocitace) {
            if (apc.jmeno.equals(pc)) {
                for (SitoveRozhrani iface : apc.rozhrani) {
                    if (iface.jmeno.equals(rozh)) {
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
        return hotovePocitace;
    }

    /**
     * Kdyz se maji nacitat pouze prazdne pocitace s rozhrani, tak vsechno efektivne nactu a pak zase smazu. uf.
     * @param pcbuilder
     */
    private void vymazNastaveni(PocitacBuilder pcbuilder) {
        pcbuilder.accessList.clear();
        pcbuilder.pool.clear();
        pcbuilder.poolAccess.clear();
        pcbuilder.routovaciTabulka.clear();
        for (String[] pole : pcbuilder.rozhrani) {
            pole[1] = ""; // ip
            pole[2] = ""; // maska
            pole[5] = ""; // nahozenost
            pole[6] = ""; // NAT
        }
    }
}
