/*
 * DODELAT:
 *      osetrit vyjimky u zakazanejch IP adres
 */
package datoveStruktury;

import vyjimky.*;

/**
 * Hele, clovece, premejslel jsem dneska, jak dodelat ty metody, ktery budou pocitat cislo site a tak
 * a nakonec jsem tu implementaci zmenil na jeden integer, cislo site a pocitace a podobny veci se
 * pak pocitaj lip.
 * @author neiss
 */
public class IpAdresa {

    private int adresa; //32 bitu ip adresy, zachazi se s tim pomoci bitovejch operaci
    private int maska; //stejne jako skutecna maska, tzn. 32 bitu


//***************************************************************************************************************
//konstruktory

    /**
     * Kdyz uzivatel nezada masku, tak se tato musi dopocitat dle jeji tridy.
     * @param adr
     */
    public IpAdresa(String adr) {
        nastavAdresu(adr);
        dopocitejMasku();
    }

    public IpAdresa(String adr, String maska) {
        nastavAdresu(adr);
        nastavMasku(maska);
    }

    public IpAdresa(String adr, int maska) {
        nastavAdresu(adr);
        nastavMasku(maska);
    }

//**************************************************************************************
//metody, ktere slouzi k nastavovani parametru IP, vsechny zacinaji slovem "nastav"

    /**
     * Nastavi novou adresu, s maskou nic nedela.
     * @param adr
     */
    private void nastavAdresu(String adr) {
        if (!jeSpravnaAdresaNebMaska(adr, false)) {
            throw new SpatnaAdresaException("spatna adresa: " + adr);
        }
        adresa = integerZeStringu(adr);
    }

    /**
     * Ocekava to masku jako integer, kolik prvnich bitu maj bejt jednicky
     * @param maska
     */
    public void nastavMasku(int pocetBitu) {
        if (pocetBitu > 32 || pocetBitu < 0) {
            throw new SpatnaMaskaException("Zadany pocet bitu masky je nesmyslny: " + pocetBitu);
        }
        this.maska = 0;
        for (int i = 0; i < pocetBitu; i++) {
            maska = maska | 1 << (31 - i);
        }
    }

    /**
     * Nastavi masku ze Stringu, pokud je maska spatna, ponecha starou masku!!!
     * @param maska
     * @autor neiss
     */
    public void nastavMasku(String maska) {
        if (!jeSpravnaAdresaNebMaska(maska, true)) {
            throw new SpatnaMaskaException("spatna maska: " + maska);
        }
        this.maska = integerZeStringu(maska);
    }

//*****************************************************************************************************************
//porovnavaci, zjistovaci a vraceci metody

    /**
     * Vrati cislo site jako IpAdresu, maska je stejna, jako ma tato adresa.
     * @return
     */
    public IpAdresa vratCisloSite() {
        return new IpAdresa ( this.vypisCisloSite(), this.pocetBituMasky() );
    }

    /**
     * vrati masku jako 32 bit integer
     * @return
     */
    public int dej32BitMasku(){
        return maska;
    }

    /**
     * vrati adresu jako 32 bit integer
     * @return
     */
    public int dej32BitAdresu(){
        return adresa;
    }

    /**
     * Vrati long hodnotu z adresy. Vhodne pro porovnavani adres.
     * @param ip
     * @return
     * @author haldyr
     */
    public long dejLongIP() {
        long l = 0L;
        String[] pole = vypisAdresu().split("\\.");
        l += Long.valueOf(pole[0]) * 256 * 256 * 256;
        l += Long.valueOf(pole[1]) * 256 * 256;
        l += Long.valueOf(pole[2]) * 256;
        l += Long.valueOf(pole[3]);
        return l;
    }

    /**
     * Vraci true, kdyz maji stejny cislo site a masku. Pozor, pro 147.32.125.128/25 a 147.32.125.128/24 vrati false!
     */
    public boolean jeStejnyCisloSite(IpAdresa jina) {
        if (this.cisloSite() == jina.cisloSite() && maska==jina.maska) { //musi se kontrolovat i maska, protoze
            return true;                                                 //jinak by 1.1.1.0/24 a 1.1.1.0/25 davaly
        }                                                                //stejnej vysledek
        return false;
    }

    /**
     * Vraci true, pokud moje adresa je nadsiti jine adresy, tzn, pokud jina IpAdresa, nehlede na jeji masku,
     * spada do rozsahu moji site.
     */
    public boolean jeNadsiti(IpAdresa jina){
        int pomocny = jina.adresa & maska;
        if (pomocny == cisloSite()) return true;
        else return false;
    }

    /**
     * Vraci true, pokud tato IP, nehlede na jeji masku, spada do rozsahu jine IP. Opacna metoda k jeNadsiti.
     * Napr. do rozsahu 0.0.0.0/0 patri celej internet, do rozsahu 1.1.1.0/24 patri i adresa 1.1.1.129/25,
     * ackoliv nemaji stejny cislo site.
     */
    public boolean jeVRozsahu(IpAdresa jina){
        int pomocny = adresa & jina.maska;
        if (pomocny == jina.cisloSite()) return true;
        else return false;
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

    /**
     * vraci true, kdyz IP adresa neni skutecnou IP adresou ale jenom broadcastem site
     * @return
     */
    public boolean jeBroadcastemSite() {
        if (adresa == broadcast()) {
            return true;
        }
        return false;
    }

    /**
     * Vrati true, kdyz je stejna adresa, na masce nezalezi.
     * @param jina
     * @return
     */
    public boolean jeStejnaAdresa(IpAdresa jina) {
        if (this.adresa == jina.adresa) {
            return true;
        }
        return false;
    }

    /**
     * Vrati true, kdyz IP patri do A tridy.
     * @return
     * @author haldyr
     */
    public boolean jeAckovehoRozsahu() {
        int[] pole = prevedNaPole(adresa);
        if (pole[0] < 128) return true;
        return false;
    }

    /**
     * Vrati pocet jednickovych bitu masky.
     * @return
     */
    public int pocetBituMasky() {
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


    /**
     * Kontroluje zda je typu IpAdresa + se museji rovnat adresy i masky.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj){
        if(obj.getClass()!=IpAdresa.class) return false;
        if ( maska==((IpAdresa)obj).maska && adresa==((IpAdresa)obj).adresa ) return true;
        return false;
    }

//**************************************************************************************************************
//verejny staticky metody

    public static boolean jeSpravnaIP(String adresa, boolean jeToMaska){
        try{
            return jeSpravnaAdresaNebMaska(adresa, jeToMaska);
        }catch(ZakazanaIpAdresaException ex){
            return true;
        }
    }

     /**
     * Vrati pocet bitu masky ze zadane IP ve tvaru stringu. Vyuziva se, kdyz uzivatel zada
     * jen IP bez masky a ta se pak musi doplnit automaticky.
     * @param ip
     * @return
     * @author haldyr
     */
    @Deprecated
    public static int vratMaskuzIPadresy(String ip) {
        /*
            A 	0 	0–127    	255.0.0.0 	7 	24 	126 	16 777 214
            B 	10 	128-191 	255.255.0.0 	14 	16 	16384 	65534
            C 	110 	192-223 	255.255.255.0 	21 	8 	2 097 152 	254
            D 	1110 	224-239 	multicast
            E 	1111 	240-255 	vyhrazeno jako rezerva
        */
        String[] pole = ip.split(".");
        int cislo;
        int mask = -1;
        try {
            cislo = Integer.parseInt(pole[0]);
        } catch (NumberFormatException e) {
            cislo = 0;
        }

        if (jeVIntervalu(0, 127, cislo)) {
            mask = 8;
        }
        if (jeVIntervalu(128, 191, cislo)) {
            mask = 16;
        }
        if (jeVIntervalu(192, 223, cislo)) {
            mask = 24;
        }

        // pro ostatni pripady bude maska 24, k tomu by ale nemelo dochazet (kontrola spravnosti IP adresy
        //by to mela resit)
        if (mask == -1) {
            mask = 24;
        }

        return mask;
    }

//****************************************************************************************************************
//tady zacinaj verejny metody pro pekny vypis adresy, masky atd. jako String

    /**
     * vrati IP jako String
     * @return
     */
    public String vypisAdresu() {
        return vypisPole(prevedNaPole(adresa));
    }

    public String vypisMasku() {
        return vypisPole(prevedNaPole(maska));
    }

    /**
     * Vypise IP jako string s maskou za lomitkem
     */
    public String vypisAdresuSMaskou() {
        return (vypisPole(prevedNaPole(adresa)) + "/" + pocetBituMasky());
    }

    public String vypisCisloSite() {
        return vypisPole(prevedNaPole(cisloSite()));
    }

    public String vypisCisloPocitaceVSiti() {
        return vypisPole(prevedNaPole(cisloPocitaceVSiti()));
    }

    public String vypisBroadcast() {
        return vypisPole(prevedNaPole(broadcast()));
    }

//*******************************************************************************************************************
//tady zacinaj dynamicky privatni metody

    private int cisloSite() { //vraci 32bit integer
        return maska & adresa;
    }

    private int cisloPocitaceVSiti() { //vraci 32bit integer
        return (~maska) & adresa;
    }

    /**
     * Dopocita masku, podle tridy IP. Vyuziva se v konstruktoru, kdyz neni maska zadana.
     */
    private void dopocitejMasku(){
        int bajt = prevedNaPole(adresa)[0]; //tady je ulozenej prvni bajt adresy
        /*
            A 	0 	0–127    	255.0.0.0 	7 	24 	126 	16 777 214
            B 	10 	128-191 	255.255.0.0 	14 	16 	16384 	65534
            C 	110 	192-223 	255.255.255.0 	21 	8 	2 097 152 	254
            D 	1110 	224-239 	multicast
            E 	1111 	240-255 	vyhrazeno jako rezerva
        */
        if( bajt<128 ) nastavMasku(8);
        if( bajt>=128 && bajt<192 ) nastavMasku(16);
        if( bajt>=192 ) nastavMasku(24);
        //System.out.println("1. bajt je "+ bajt+" a tak jsem nastavil "+ pocetBituMasky());
    }

    /**
     * vraci 32 bitu adresy broadcastu site
     * @return
     */
    private int broadcast() { 
        return (cisloSite() | (~maska));
    }

//**************************************************************************************************************
//tady zacinaj privatni staticky, vetsinou pomocny metody

    /**
     * Převádí ip nebo masku z bitový podoby na pole integeru.
     * @param cislo
     * @return
     */
    private static int[] prevedNaPole(int cislo) { //prevadi masku nebo adresu do citelny podoby
        int[] pole = new int[4];
        int tmp;
        for (int i = 0; i < 4; i++) {
            tmp = cislo & (255 << (3 - i) * 8);
            pole[i] = tmp >>> ((3 - i) * 8);
        }
        return pole;
    }

    /**
     * vraci string - citelnou formu IP adresy ze zadaneho pole integeru
     * @param pole
     * @return
     * @autor neiss
     */
    private static String vypisPole(int[] pole) {
        String ret = pole[0] + "." + pole[1] + "." + pole[2] + "." + pole[3];
        return ret;
    }


    /**
     * Pomocna metoda kontroly IP, dle regularniho vyrazu
     * @param adr
     * @return
     * @author haldyr
     */
    private static boolean jednoduchaKontrola(String adr) { //kontrola IP, jestli to jsou cisla s teckama
        if (adr == null) {
            //System.out.println("Chyba: ip adresa je null!"); // pro testovani, pak smazat
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

    
    /**
     * Vraci true, kdyz je zadany integer maskou, tzn., kdyz jsou to nejdriv jednicky a pak nuly.
     * @param maska
     * @return
     */
    private static boolean jeMaskou(int maska) {
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

    /**
     * Vraci true, kdyz zadane cislo je v intervalu od a do z.
     */
    private static boolean jeVIntervalu(int a, int z, int cislo) {
        if (cislo >= a && cislo <= z) {
            return true;
        }
        return false;
    }

    /**
     * Zkontroluje, jestli zadany retezec je IP adresa (bez masky za lomitkem)
     * @param adr kontrolovana adresa
     * @param jeToMaska urcuje, zda to chci kontrolovat jako masku (spravnou)
     * @return true, kdyz je adresa spravna
     * @author haldyr
     */
    private static boolean jeSpravnaAdresaNebMaska(String adr, boolean jeToMaska) {
        if (!jednoduchaKontrola(adr)) {
            return false;
        }
        //kontrola spravnosti jednotlivejch cisel:
        String[] pole = adr.split("\\.");
        int n;

        for (int i = 0; i < 4; i++) {
            n = Integer.valueOf(pole[i]);
            if (n < 0 || n > 255) { //podle me ta kontrola musi bejt takhle
                return false;
            }
        }

        if(jeToMaska){ //kontroluje se, jestli to je spravna maska
            int m=integerZeStringu(adr);
            return jeMaskou(m);
        }
        
        return true;
    }

    /**
     * Zkontroluje, zda dany retezec je IP adresa z rozsahu (1.* - 223.*).
     * Ostatni adresy jsou pro multicast + vyhrazeny 
     * @param adr
     * @return
     * @author haldyr
     */
    public static boolean jeZakazanaIpAdresa(String adr) {

        String[] pole = adr.split("\\.");
        if (Integer.valueOf(pole[0]) >= 224 && Integer.valueOf(pole[0]) <= 255) {
            // -> adresy rozsahu 224.* - 239.* jsou vyhrazeny pro multicast, vyssi ifconfig nezere
            return true;
        }
        return false;
    }
}
