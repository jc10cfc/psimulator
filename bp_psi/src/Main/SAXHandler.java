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
    List<AbstraktniPocitac> hotovePocitace = new ArrayList<AbstraktniPocitac>(); // tady drzim seznam vytvorenych objektu tridy AbstraktPocitac
    String[] rozhrani = new String[7]; // jmeno, ip mac, maska, pripojenoK, nahozene, nat
    String[] zaznam = new String[4]; //adresat, maskaAdresata, brana, rozhrani
    String[] accessList = new String[3]; // cislo, ipA, ipAWildcard
    String[] pool = new String[4]; // pJmeno, ip_start, ip_konec, prefix
    String[] poolAccess = new String[3]; // accessCislo, poolJmeno, overload
    static public int port = -1;
    List<List> pripojeno = new ArrayList<List>();
    List<PocitacBuilder> seznamPocitacBuilder = new ArrayList<PocitacBuilder>();
    private int aktualniPC = -1;
    boolean vypis = false; // pro vypis kostry xml dokumentu
    boolean vypis2 = false; // vypis pocitacu

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
    private boolean jeTam(String[] pole, String s) {
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
        return jeTam(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do zaznamu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoZaznamu(String localName) {
        String[] pole = {"adresat", "maskaAdresata", "brana", "rozhraniKam"};
        return jeTam(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do poolu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoNatPoolu(String localName) {
        String[] pole = {"pJmeno", "ip_start", "ip_konec", "prefix"};
        return jeTam(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do Nat prirazeni.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoNatPrirazeni(String localName) {
        String[] pole = {"accessCislo", "poolJmeno", "overload"};
        return jeTam(pole, localName);
    }

    /**
     * Vrati true, pokud localName je jmeno elementu, ktere patri do poolu.
     * @param localName jmeno elementu
     * @return
     */
    private boolean patriDoNatAccessListu(String localName) {
        String[] pole = {"cislo", "ipA", "ipAWildcard"};
        return jeTam(pole, localName);
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
        String s = "";
        for (int i = 0; i < pole.length; i++) {
            s += " " + pole[i];
        }
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

        if (localName.equals("pool")) {
            if (jePolePlne(pool)) {
                dejOdkazNaAktualniPC().pool.add(vratKopiiPole(pool));
            } else {
                System.out.println("Neni uplny zaznam, preskakuji: "+vypisPole(pool));
            }
            
        }

        if (localName.equals("prirazeni")) {
            if (jePolePlne(poolAccess)) {
                dejOdkazNaAktualniPC().poolAccess.add(vratKopiiPole(poolAccess));
            } else {
                System.out.println("Neni uplny zaznam, preskakuji: "+vypisPole(poolAccess));
            }
            
        }

        if (localName.equals("access-list")) {
            if (jePolePlne(accessList)) {
                dejOdkazNaAktualniPC().accessList.add(vratKopiiPole(accessList));
            } else {
                System.out.println("Neni uplny zaznam, preskakuji: "+vypisPole(accessList));
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
        if (patriDoNatAccessListu(jmenoElementu)) {
            accessList[dejIndexVNatAccessListu(jmenoElementu)] = s;
        }
        if (patriDoNatPoolu(jmenoElementu)) {
            pool[dejIndexVNatPoolu(jmenoElementu)] = s;
        }
        if (patriDoNatPrirazeni(jmenoElementu)) {
            poolAccess[dejIndexVNatPrirazeni(jmenoElementu)] = s;
        }

        if (jmenoElementu.equals("ip_forward")) {
            if (s.equals("true") || s.equals("1")) {
                dejOdkazNaAktualniPC().ip_forward = true;
            } else if (s.equals("false") || s.equals("0")) {
                dejOdkazNaAktualniPC().ip_forward = false;
            } else {
                System.out.println("ip_forward musi byt bud true/false nebo 1/0, ne todlencto: " + s);
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

            for (String[] pul : pcbuilder.pool) {
                try {
                    IpAdresa ip_start = new IpAdresa(pul[dejIndexVNatPoolu("ip_start")]);
                    IpAdresa ip_konec = new IpAdresa(pul[dejIndexVNatPoolu("ip_konec")]);
                    String pJmeno = pul[dejIndexVNatPoolu("pJmeno")];
                    String cislo = pul[dejIndexVNatPoolu("prefix")];

                    int i = Integer.parseInt(cislo);

                    int n = pocitac.natTabulka.NATseznamPoolu.pridejPool(ip_start, ip_konec, i, pJmeno);
                    if (n != 0) {
                        System.err.println("Pool je spatne zadan: " + vypisPole(pul));
                    }
                } catch (Exception e) {
                    System.err.println("Pool je spatne zadan: " + vypisPole(pul)+ ", preskakuji.. ");
                }
            }

            for (String[] access : pcbuilder.accessList) {
                try {
                    String jmeno = access[dejIndexVNatAccessListu("cislo")];
                    int cislo = Integer.parseInt(jmeno);

                    IpAdresa ip = new IpAdresa(access[dejIndexVNatAccessListu("ipA")]);
                    IpAdresa wccc = new IpAdresa(access[dejIndexVNatAccessListu("ipAWildcard")]);
                    String maska = IpAdresa.vratMaskuZWildCard(wccc);
                    ip.nastavMasku(maska);

                    pocitac.natTabulka.NATseznamAccess.pridejAccessList(ip, cislo);
                } catch (Exception e) {
                    System.err.println("access-list je spatne zadan: " + vypisPole(accessList) + ", preskakuji.. ");
                }
            }

            for (String[] poolAcc : pcbuilder.poolAccess) {
                try {
                    String acc = poolAcc[dejIndexVNatPrirazeni("accessCislo")];
                    int cislo = Integer.parseInt(acc);
                    String jmeno = poolAcc[dejIndexVNatPrirazeni("poolJmeno")];

                    String overload = poolAcc[dejIndexVNatPrirazeni("overload")];

                    boolean ol;
                    if (overload.equals("true") || overload.equals("1")) {
                        ol = true;
                        pocitac.natTabulka.NATseznamPoolAccess.pridejPoolAccess(cislo, jmeno, ol);
                    } else if (overload.equals("false") || overload.equals("0")) {
                        ol = false;
                        pocitac.natTabulka.NATseznamPoolAccess.pridejPoolAccess(cislo, jmeno, ol);
                    } else {
                        System.err.println("prirazeni je spatne zadano: " + vypisPole(poolAccess) + ", preskakuji.. ");
                    }
                } catch (Exception e) {
                    System.err.println("prirazeni je spatne zadano: " + vypisPole(poolAccess) + ", preskakuji.. ");
                }

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
                    sr.seznamAdres.add(ip);
                } else if (!iface[dejIndexVRozhrani("ip")].equals("") && !iface[dejIndexVRozhrani("maska")].equals("")) { // kdyz je tu oboje
                    IpAdresa ip = new IpAdresa(iface[dejIndexVRozhrani("ip")], iface[dejIndexVRozhrani("maska")]);
                    sr.seznamAdres.add(ip);
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

            if (pocitac instanceof CiscoPocitac) {
                ((CiscoPocitac) pocitac).getWrapper().update();
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
                if (pocitac instanceof LinuxPocitac) {
                    if (iface == null) {
                        System.err.println("Nepodarilo se najit rozhrani s nazvem: " + jmeno
                                + ", Preskakuji zaznam " + adresat.vypisAdresuSMaskou() + " v routovaci tabulce..");
                        continue;
                    }
                }

                if (pocitac instanceof CiscoPocitac) {//TODO: if (!adresat.jeCislemSite()) dat i pro linux?
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
}
