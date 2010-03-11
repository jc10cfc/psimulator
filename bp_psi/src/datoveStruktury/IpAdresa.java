package datoveStruktury;

import vyjimky.*;

/**
 * Hele, clovece, premejslel jsem dneska, jak dodelat ty metody, ktery budou pocitat cislo site a tak
 * a nakonec jsem tu implementaci zmenil na jeden integer, cislo site a pocitace a podobny veci se pak pocitaj
 * lip.
 * @author neiss
 */
public class IpAdresa {

    int adresa; //32 bitu ip adresy, zachazi se s tim pomoci bitovejch operaci
    int maska; //stejne jako skutecna maska, tzn. 32 bitu

    public IpAdresa(String adr) {
        nastavIP(adr);
        nastavMasku(vratMaskuzIPadresy(adr));
    }

    public IpAdresa(String adr, String maska) {
        nastavIP(adr);
        nastavMasku(maska);
    }

    public IpAdresa(String adr, int maska) {
        nastavIP(adr);
        nastavMasku(maska);
    }

    /**
     * Zkontroluje, jestli zadany retezec je IP adresa (bez masky za lomitkem)
     * @param adr - kontrolovana adresa
     * @param jeToMask - urcuje, zda to chci kontrolovat jako IP (1.* - 223.*) nebo jako masku (1.* - 255.*)
     * @return true, kdyz je adresa spravna
     * @author haldyr
     */
    public static boolean jeSpravnaIP(String adr, boolean jeToMaska) {
        if (!jednoduchaKontrola(adr)) {
            return false;
        }
        //kontrola spravnosti jednotlivejch cisel:
        String[] pole = adr.split("\\.");
        int n;

        if (! jeToMaska) { // je to IP
            if (Integer.valueOf(pole[0]) >= 224 && Integer.valueOf(pole[0]) <= 255) { // adresy rozsahu 224.* - 239.* jsou vyhrazeny pro multicast, vyssi ifconfig nezere
                throw new ZakazanaIpAdresa(adr);
            }
        }

        for (int i = 0; i < 4; i++) {

            n = Integer.valueOf(pole[i]);
            if (n < 0 || n > 255) { //podle me ta kontrola musi bejt takhle
                return false;
            }
        }
        return true;
    }

    /**
     * Ocekava to masku jako integer, kolik prvnich bitu maj bejt jednicky
     * @param maska
     */
    public void nastavMasku(int pocetBitu) {
        if (pocetBitu > 32 || pocetBitu < 0) {
            throw new RuntimeException("Zadany pocet bitu masky je nesmyslny: " + pocetBitu);
        }
        this.maska = 0;
        for (int i = 0; i < pocetBitu; i++) {
            maska = maska | 1 << (31 - i);
        }
    }

    public void nastavIP(String adr) {
        if (!jeSpravnaIP(adr, false)) {
            throw new RuntimeException("spatna adresa: " + adr);
        }
        adresa = integerZeStringu(adr);
    }

    /**
     * Nastavi masku ze Stringu, pokud je maska spatna, ponecha starou masku!!!
     * @param maska
     */
    public void nastavMasku(String maska) {
        if (!jeSpravnaIP(maska, true)) {
            throw new RuntimeException("spatna maska: " + maska);
        }
        int moznaMaska = integerZeStringu(maska);
        if (jeMaskou(moznaMaska)) {
            this.maska = moznaMaska;
        } else {
            throw new SpatnaMaskaException("Spatna maska, nejsou to jednicky a pak nuly");
        }
    }

    public String vypisIP() {
        return vypisPole(prevedNaPole(adresa));
    }

    public String vypisMasku() {
        return vypisPole(prevedNaPole(maska));
    }

    public String vypisAdresuSMaskou() {
        return (vypisPole(prevedNaPole(adresa)) + "/" + pocetBituMasky());
    }

    public String vypisCisloSite() {
        return vypisPole(prevedNaPole(cisloSite()));
    }

    public String vypisCisloPocitaceVSiti() {
        return vypisPole(prevedNaPole(cisloPocitaceVSiti()));
    }

    public boolean jeVeStejnySiti(IpAdresa jina) {
        if (this.cisloSite() == jina.cisloSite()) {
            return true;
        }
        return false;
    }

    public String vypisBroadcast() {
        return vypisPole(prevedNaPole(broadcast()));
    }

    /**
     * vraci true, kdyz IP adresa neni skutecnou IP adresou ale jenom cislem site
     * @return
     */
    public boolean jeCislemSite() {
        if (adresa == cisloSite()) {
            return true;
        }
        return false;
    }

    public boolean stejnaAdresa(IpAdresa jina) {
        if (this.adresa == jina.adresa) {
            return true;
        }
        return false;
    }

    /**
     * Převádí ip nebo masku z bitový podoby na pole integeru.
     * @param cislo
     * @return
     */
    private int[] prevedNaPole(int cislo) { //prevadi masku nebo adresu do citelny podoby
        int[] pole = new int[4];
        int tmp;
        for (int i = 0; i < 4; i++) {
            tmp = cislo & (255 << (3 - i) * 8);
            pole[i] = tmp >>> ((3 - i) * 8);
        }
        return pole;
    }

    /**
     * vraci citelnou formu IP adresy ze zadaneho pole integeru
     * @param pole
     * @return
     */
    private String vypisPole(int[] pole) {
        String ret = pole[0] + "." + pole[1] + "." + pole[2] + "." + pole[3];
        return ret;
    }

    private int cisloSite() { //vraci 32bit integer
        return maska & adresa;
    }

    private int cisloPocitaceVSiti() { //vraci 32bit integer
        return (~maska) & adresa;
    }

    /**
     * Pomocna metoda kontroly IP, dle regularniho vyrazu
     * @param adr
     * @return
     * @author haldyr
     */
    private static boolean jednoduchaKontrola(String adr) { //kontrola IP, jestli to jsou cisla s teckama
        if (adr == null) {
            System.out.println("Chyba: ip adresa je null!"); // pro testovani, pak smazat
        }
        if (!adr.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            return false;
        }
        return true;
    }

    private static int integerZeStringu(String ret) {
        int a = 0;
        String[] pole = ret.split("\\.");
        int n;
        for (int i = 0; i < 4; i++) {
            n = Integer.valueOf(pole[i]);
            a = a | n << (8 * (3 - i));
        }
        return a;
    }

    private int pocetBituMasky() {
        if (maska == 0) {
            return 0;
        }
        int pocet = 32;
        int maska = this.maska;
        while ((maska & 1) == 0) {
            maska = maska >> 1;
            pocet--;
        }
        return pocet;
    }

    private static boolean jeMaskou(int maska) { //jestli je zadany integer maskou
        int i = 0;
        while (i < 32 && (maska & 1) == 0) { //tady prochazeji nuly
            i++;
            maska = maska >> 1;
        }
        while (i < 32 && (maska & 1) == 1) { //tady prochazeji jednicky
            i++;
            maska = maska >> 1;
        }
        if (i == 32) {
            return true;
        }
        return false;
    }

    private int broadcast() { //vraci 32 bitu adresy broadcastu site
        return (cisloSite() | (~maska));
    }

    /*
    A 	0 	0–127    	255.0.0.0 	7 	24 	126 	16 777 214
    B 	10 	128-191 	255.255.0.0 	14 	16 	16384 	65534
    C 	110 	192-223 	255.255.255.0 	21 	8 	2 097 152 	254
    D 	1110 	224-239 	multicast
    E 	1111 	240-255 	vyhrazeno jako rezerva
     */
    /**
     * Vrati pocet bitu masky ze zadane IP ve tvaru stringu.
     * @param ip
     * @return
     * @author haldyr
     */
    public int vratMaskuzIPadresy(String ip) {
        String[] pole = ip.split(".");
        int cislo;
        int mask = -1;
        try {
            cislo = Integer.parseInt(pole[0]);
        } catch (NumberFormatException e) {
            cislo = 0;
        }

        if (jeRozsah(0, 127, cislo)) {
            mask = 8;
        }
        if (jeRozsah(128, 191, cislo)) {
            mask = 16;
        }
        if (jeRozsah(192, 223, cislo)) {
            mask = 24;
        }

        // pro ostatni pripady bude maska 24, k tomu by ale nemelo dochazet (kontrola spravnosti IP adresy by to mela resit)
        if (mask == -1) {
            mask = 24;
        }

        return mask;
    }

    private boolean jeRozsah(int a, int z, int cislo) {
        if (cislo >= a && cislo <= z) {
            return true;
        }
        return false;
    }
}
