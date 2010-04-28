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
    String[] staticke;
    public int port = -1;
    List<List> pripojeno;
    List<PocitacBuilder> seznamPocitacBuilder;
    private int aktualniPC = -1;
    boolean bezNastaveni = false;
    boolean vypis = false; // pro vypis kostry xml dokumentu
    boolean vypis2 = false; // vypis pocitacu

    public SAXHandler(int port, boolean bezNastaveni) {
        hotovePocitace = new ArrayList<AbstraktniPocitac>();
        rozhrani = new String[7];
        zaznam = new String[4];
        accessList = new String[3];
        pool = new String[4];
        poolAccess = new String[3];
        pripojeno = new ArrayList<List>();
        staticke = new String[2];
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
        String[] pole = {"jmeno", "ip", "mac", "pripojenoK", "maska", "nahozene", "nat"};
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
    private int dejIndexVNatAccessListu(String s) {
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
    private int dejIndexVNatPoolu(String s) {
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
    private int dejIndexVNatPrirazeni(String s) {
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
        } else if (s.equals("nat")) {
            i = 6;
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
    private boolean jePolePlne(String[] pole) {
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
        // ...
    }

    /**
     * Obsluha události "konec dokumentu".
     * V teto metode se vyrabeji vsechny pocitace, natahujou draty mezi "fyzicky" spojenymi rozhranimi.
     */
    @Override
    public void endDocument() throws SAXException {


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
                System.out.println("\nPC");
                System.out.println(" jmeno: " + pcbuilder.jmeno);
                System.out.println(" typ:   " + pcbuilder.typ + "\n");
            }

            zpracujPooly(pcbuilder, pocitac);
            zpracujAccessListy(pcbuilder, pocitac);
            zpracujPoolAccess(pcbuilder, pocitac);
            zpracujRozhrani(pcbuilder, pocitac);
            zpracujStatickyNat(pcbuilder, pocitac);

            if (pocitac instanceof CiscoPocitac) {
                ((CiscoPocitac) pocitac).getWrapper().update();
            }

            zpracujRoutovaciTabulku(pcbuilder, pocitac);

            if (pocitac instanceof LinuxPocitac) {
                if (pocitac.natTabulka.lzePrelozit(new IpAdresa("1.2.3.4"))) {
                    pocitac.natTabulka.nastavZKonfigurakuLinuxBooleanTrue();
                }
            }
            pocitac.natTabulka.lPool.updateIpNaRozhrani();
            pocitac.natTabulka.pridejIpAdresyZeStatickychPravidel(pocitac.natTabulka.vratVerejne());
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
        for (AbstraktniPocitac apc : hotovePocitace) {
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

    private void zpracujPooly(PocitacBuilder pcbuilder, AbstraktniPocitac pocitac) {
        for (String[] pul : pcbuilder.pool) {

            IpAdresa ip_start = null;
            IpAdresa ip_konec = null;

            try {
                ip_start = new IpAdresa(pul[dejIndexVNatPoolu("ip_start")]);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Prvni IP je spatna: " + pul[dejIndexVNatPoolu("ip_start")]);
            }

            try {
                ip_konec = new IpAdresa(pul[dejIndexVNatPoolu("ip_konec")]);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Druha IP je spatna: " + pul[dejIndexVNatPoolu("ip_konec")]);
            }

            String pJmeno = pul[dejIndexVNatPoolu("pJmeno")];
            String cislo = pul[dejIndexVNatPoolu("prefix")];

            int i = -1;
            try {
                i = Integer.parseInt(cislo);
            } catch (NumberFormatException e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Neni cislo: " + cislo);
            }

            try {
                int n = pocitac.natTabulka.lPool.pridejPool(ip_start, ip_konec, i, pJmeno);
                if (n != 0) {
                    System.err.println("Pool je spatne zadan: " + vypisPole(pul));
                }
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("Pool je spatne zadan: " + vypisPole(pul) + ", preskakuji.. ");
            }
        }
    }

    private void zpracujAccessListy(PocitacBuilder pcbuilder, AbstraktniPocitac pocitac) {
        for (String[] access : pcbuilder.accessList) {
            try {
                String jmeno = access[dejIndexVNatAccessListu("cislo")];
                int cislo = Integer.parseInt(jmeno);

                IpAdresa ip = new IpAdresa(access[dejIndexVNatAccessListu("ipA")]);
                IpAdresa wccc = new IpAdresa(access[dejIndexVNatAccessListu("ipAWildcard")]);
                String maska = IpAdresa.vratMaskuZWildCard(wccc);
                ip.nastavMasku(maska);

                pocitac.natTabulka.lAccess.pridejAccessList(ip, cislo);
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("access-list je spatne zadan: " + vypisPole(accessList) + ", preskakuji.. ");
            }
        }
    }

    private void zpracujPoolAccess(PocitacBuilder pcbuilder, AbstraktniPocitac pocitac) {
        for (String[] poolAcc : pcbuilder.poolAccess) {
            try {
                String acc = poolAcc[dejIndexVNatPrirazeni("accessCislo")];
                int cislo = Integer.parseInt(acc);
                String jmeno = poolAcc[dejIndexVNatPrirazeni("poolJmeno")];

                String overload = poolAcc[dejIndexVNatPrirazeni("overload")];

                boolean ovrld;
                if (overload.equals("true") || overload.equals("1")) {
                    ovrld = true;
                    pocitac.natTabulka.lPoolAccess.pridejPoolAccess(cislo, jmeno, ovrld);
                } else if (overload.equals("false") || overload.equals("0")) {
                    ovrld = false;
                    pocitac.natTabulka.lPoolAccess.pridejPoolAccess(cislo, jmeno, ovrld);
                } else {
                    System.err.println("prirazeni je spatne zadano: " + vypisPole(poolAccess) + ", preskakuji.. ");
                }
            } catch (Exception e) {
                if (debug) {
                    e.printStackTrace();
                }
                System.err.println("prirazeni je spatne zadano: " + vypisPole(poolAccess) + ", preskakuji.. ");
            }
        }
    }

    private void zpracujRozhrani(PocitacBuilder pcbuilder, AbstraktniPocitac pocitac) {
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
                sr.zmenPrvniAdresu(ip);
            } else if (!iface[dejIndexVRozhrani("ip")].equals("") && !iface[dejIndexVRozhrani("maska")].equals("")) { // kdyz je tu oboje
                IpAdresa ip = new IpAdresa(iface[dejIndexVRozhrani("ip")], iface[dejIndexVRozhrani("maska")]);
                sr.zmenPrvniAdresu(ip);
            } else if (!iface[dejIndexVRozhrani("maska")].equals("") && iface[dejIndexVRozhrani("ip")].equals("")) { // vypisem, ze preskakujem
                System.err.println("Preskakuji masku z duvodu nepritomnosti IP adresy..");
            }

            if (iface[dejIndexVRozhrani("nahozene")].equals("1") || iface[dejIndexVRozhrani("nahozene")].equals("true")) {
                sr.nastavRozhrani(true);
            } else if (iface[dejIndexVRozhrani("nahozene")].equals("0") || iface[dejIndexVRozhrani("nahozene")].equals("false")) {
                sr.nastavRozhrani(false);
            }

            pocitac.pridejRozhrani(sr);

            // nastaveni inside/outside rozhrani
            if (iface[dejIndexVRozhrani("nat")].equals("soukrome")) {
                pocitac.natTabulka.pridejRozhraniInside(sr);
            } else if (iface[dejIndexVRozhrani("nat")].equals("verejne")) {
                pocitac.natTabulka.nastavRozhraniOutside(sr);
            } else if (iface[dejIndexVRozhrani("nat")].equals("")) {
                //ok
            } else {
                System.out.println("Neznama volba " + iface[dejIndexVRozhrani("nat")] + " byla preskocena. "
                        + "Povolene jsou jen soukrome/verejna");
            }

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
    }

    private void zpracujRoutovaciTabulku(PocitacBuilder pcbuilder, AbstraktniPocitac pocitac) {
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
            if (pocitac instanceof LinuxPocitac) {
                if (iface == null) {
                    System.err.println("Nepodarilo se najit rozhrani s nazvem: " + jmeno
                            + ", Preskakuji zaznam " + adresat.vypisAdresuSMaskou() + " v routovaci tabulce..");
                    continue;
                }
            }

            if (pocitac instanceof CiscoPocitac) {
                if (!adresat.jeCislemSite()) {
                    throw new ChybaKonfigurakuException("Adresa " + adresat.vypisAdresuSMaskou() + " neni cislem site!");
                }
            }

            if (IpAdresa.jeZakazanaIpAdresa(adresat.vypisAdresu())) {
                System.err.println("IpAdresa " + adresat.vypisAdresuSMaskou() + " je ze zakazaneho rozsahu 224.* - 255.*, preskakuji..");
                continue;
            }

            if (mujzaznam[dejIndexVZaznamu("brana")].equals("")
                    || mujzaznam[dejIndexVZaznamu("brana")].equals("null")) { // kdyz to je na rozhrani

                if (pocitac instanceof CiscoPocitac) {
                    if (iface == null) {
                        System.err.println("Nepodarilo se najit rozhrani s nazvem " + jmeno
                                + ", Preskakuji zaznam " + adresat.vypisAdresuSMaskou() + " v routovaci tabulce..");
                        continue;
                    }
                    ((CiscoPocitac) pocitac).getWrapper().pridejZaznam(adresat, iface);
                } else {
                    pocitac.routovaciTabulka.pridejZaznamBezKontrol(adresat, null, iface);
                }

            } else { // vcetne brany
                IpAdresa brana = new IpAdresa(mujzaznam[dejIndexVZaznamu("brana")]);

                if (pocitac instanceof CiscoPocitac) {
                    if (IpAdresa.jeZakazanaIpAdresa(brana.vypisAdresu())) {
                        System.err.println("IpAdresa " + brana.vypisAdresuSMaskou() + " je ze zakazaneho rozsahu 224.* - 255.*, preskakuji..");
                        continue;
                    }
                    ((CiscoPocitac) pocitac).getWrapper().pridejZaznam(adresat, brana);
                } else {
                    pocitac.routovaciTabulka.pridejZaznamBezKontrol(adresat, brana, iface);
                }
            }
        }
    }

    private void zpracujStatickyNat(PocitacBuilder pcbuilder, AbstraktniPocitac pocitac) {
        for (String[] stat : pcbuilder.staticke) {
            if (!jePolePlne(stat)) {
                System.err.println("Staticky zaznam (in/out) pro NAT neni uplny: "+vypisPole(stat) + ", preskakuji..");
                return;
            }
            IpAdresa in;
            IpAdresa out;
            try {
                in = new IpAdresa(stat[0]);
            } catch (Exception e) {
                System.err.println("Staticky zaznam, element 'in' neni platnou IP adresou: "+stat[0]+ ", preskakuji..");
                return;
            }
            try {
                out = new IpAdresa(stat[1]);
            } catch (Exception e) {
                System.err.println("Staticky zaznam, element 'out' neni platnou IP adresou: "+stat[1]+ ", preskakuji..");
                return;
            }
            int n = pocitac.natTabulka.pridejStatickePravidloCisco(in, out);

            if ( n == 1 ) System.err.println("chyba, in adresa pro staticky zaznam: "+in.vypisAdresu()+" tam uz je, preskakuji..");
            if ( n == 2 ) System.err.println("chyba, out adresa pro staticky zaznam: "+out.vypisAdresu()+" tam uz je, preskakuji..");
        }
    }
}
