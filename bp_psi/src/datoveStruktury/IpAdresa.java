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
    public int port = 0; //pro natovani


//***************************************************************************************************************
//konstruktory

    /**
     * Kdyz uzivatel nezada masku, tak se tato musi dopocitat dle jeji tridy.
     * @throws SpatnaAdresaException
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

    /**
     * Vytvori adresu ze zadaneho Stringu, kde muze nebo nemusi byt zadana adresa za lomitkem. <br />
     * Je-li moduloMaska nastaveno na true, maska za lomitkem se vymoduluje 32. POZOR: tzn., ze i
     * maska /32 se vymoduluje na /0! (takhle funguje LinuxIfconfig i LinuxRoute)<br />
     * Je-li modulo maska nastaveno na false, musi byt maska spravna, tzn. v intervalu <0,32>. <br />
     * Na chybny vstupy to hazi SpatnaMaskaException nebo SpatnaAdresaException, pricemz, kdyz
     * je spatny oboje, ma SpatnaAdresaException prednost. <br />
     * @param adrm
     * @param defMaska Nabyva hodnot v intervalu <-1,32>. Nastavuje se tehdy, kdyz zadany string masku
     * za lomitkem neobsahuje. -1 zanmena, ze se ma maska dopocitat podle tridy.
     * @param moduloMaska
     * @throws SpatnaMaskaException
     * @throws SpatnaAdresaException
     * @author neiss
     */
    public IpAdresa(String adrm, int defMaska, boolean moduloMaska) {
        //nejdriv se pro jistotu zkontrolujou zadany hodnoty:
        if(moduloMaska && defMaska<-1 && defMaska>32){
            throw new RuntimeException("V programu nastala chyba, kontaktujte prosim tvurce softwaru.");
        }

        int lomitko = adrm.indexOf('/');
        if (lomitko == -1) { // retezec neobsahuje lomitko
            nastavAdresu(adrm); //nastavuje se adresa
            if(defMaska<0){
                dopocitejMasku();
            }else{
                nastavMasku(defMaska);
            }
        } else {  // je to s lomitkem, musi se to s nim zparsovat
            String adr = adrm.substring(0, lomitko);
            nastavAdresu(adr); //nastavuje se uz tady, aby prvni vyjimka se hazela na adresu
            String maska = adrm.substring(lomitko + 1, adrm.length());
            int m;
            //kontrola masky, jestli je to integer:
            try {
                m = Integer.parseInt(maska);
            } catch (NumberFormatException ex) {
                throw new SpatnaMaskaException();
            }
            if (moduloMaska) { //pripadne prepocitani masky:
                m = m % 32;
            }
            nastavMasku(m);  // nastaveni masky
        }
    }

//**************************************************************************************
//metody, ktere slouzi k nastavovani parametru IP, vsechny zacinaji slovem "nastav"

    /**
     * Nastavi novou adresu, s maskou nic nedela.
     * @param adr
     * @throws SpatnaAdresaException
     */
    private void nastavAdresu(String adr) {
        if (!spravnaAdresaNebMaska(adr, false)) {
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
        if (!spravnaAdresaNebMaska(maska, true)) {
            throw new SpatnaMaskaException("spatna maska: " + maska);
        }
        this.maska = integerZeStringu(maska);
    }

//*****************************************************************************************************************
//verejny porovnavaci, zjistovaci a vraceci metody

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
        if(jina==null) return false;
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
        if(jina==null) return false;
        if (this.adresa == jina.adresa) {
            return true;
        }
        return false;
    }

    /**
     * Vrati true, kdyz je stejna adresa i port, na masce nezalezi.
     * @param jina
     * @return
     */
    public boolean jeStejnaAdresaSPortem(IpAdresa jina) {
        if(jina==null) return false;
        if (jeStejnaAdresa(jina) && this.port == jina.port) {
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
     * Nemusi se rovnat port.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj){
        if(obj==null) return false;
        if(obj.getClass()!=IpAdresa.class) return false;
        if ( maska==((IpAdresa)obj).maska && adresa==((IpAdresa)obj).adresa ) return true;
        return false;
    }

    /**
     * Vrati kopii IpAdresy se stejnou adresou, maskou a portem.
     * @return
     */
    public IpAdresa vratKopii() {
        IpAdresa vratCopy = new IpAdresa(this.vypisAdresu());
        vratCopy.maska = this.maska;
        vratCopy.port = this.port;
        return vratCopy;
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
     * Spocita wildcard z masky a vrati ho jako retezec.
     * @return
     */
    public String vypisWildcard() {
        long broadcast = (long)(new IpAdresa("255.255.255.255").adresa);
        long mask = (long)maska;
        long wc = broadcast - mask;
        IpAdresa wildcard = new IpAdresa("1.1.1.1");
        wildcard.adresa = (int)wc;
        return wildcard.vypisAdresu();
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

    /**
     * Vrati adresu s portem, oddelenym dvojteckou.
     * @return
     */
    public String vypisAdresuSPortem(){
        return vypisAdresu()+":"+port;
    }


//**************************************************************************************************************
//verejny staticky metody

    /**
     * Kontroluje, jestli zadanej String je adresa nebo maska. Kdyz je jeToMaska true,
     * porovnava to jako masku, jinak jako adresu.
     * @param adresa
     * @param jeToMaska kdyz je to true, porovnava to jako masku
     * @return
     * @author haldyr
     */
    public static boolean spravnaAdresaNebMaska(String adresa, boolean jeToMaska){
        if (!jednoduchaKontrola(adresa)) {
            return false;
        }
        //kontrola spravnosti jednotlivejch cisel:
        String[] pole = adresa.split("\\."); //pole Stringu s jednotlivejma cislama
        int n;
        for (int i = 0; i < 4; i++) {
            n = Integer.valueOf(pole[i]);
            if (n < 0 || n > 255) { //podle me ta kontrola musi bejt takhle
                return false;
            }
        }

        if (! jeToMaska){//neni to maska
            return true; //to uz na kontrolu staci
        }else{ //ma to bejt maska
            int m=integerZeStringu(adresa);
            return jeMaskou(m); //jeste se kontroluje, jestli je to spravna maska
        }

    }

    /**
     * Udelany metosou pokus - omyl, ale testy prosly.
     * @param p
     * @return adresu o jednicku vetsi, maska bude 255.0.0.0
     */
    public static IpAdresa vratOJednaVetsi(IpAdresa p){
        int nova=(int) ( (long)(p.adresa) + 1L );
        IpAdresa vratit=new IpAdresa("192.168.1.1"); //neco natvrdo musim vytvorit, jinak to nejde
        vratit.adresa=nova;
        return vratit;
    }

    /**
     * Jako parametr vstupuje IpAdresa wildcard, ktera ma nastanenou adresu na wildcard (jako maska by to neproslo).
     * @param wildcard
     * @return maska - retezec, ktery je maskou z wildcard <br />
     *         null - kdyz to nebyla validni maska (po preklopeni)
     */
    public static String vratMaskuZWildCard(IpAdresa wildcard) {
        long wc = (long)wildcard.adresa;
        long broadcast = (long)(new IpAdresa("255.255.255.255").adresa);
        int mask = (int)(broadcast - wc);
        if (!jeMaskou(mask)) {
            return null;
        }
        wildcard.maska = mask;
        return wildcard.vypisMasku();
    }

    /**
     * Zkontroluje, zda dany retezec je IP adresa z rozsahu (1.* - 223.*).
     * Ostatni adresy jsou pro multicast + vyhrazeny
     * @param adr
     * @return
     * @author haldyr
     */
    public static boolean jeZakazanaIpAdresa(String adr) {
        /*
        A 	0 	0–127    	255.0.0.0 	7 	24 	126 	16 777 214
        B 	10 	128-191 	255.255.0.0 	14 	16 	16384 	65534
        C 	110 	192-223 	255.255.255.0 	21 	8 	2 097 152 	254
        D 	1110 	224-239 	multicast
        E 	1111 	240-255 	vyhrazeno jako rezerva
         */

        String[] pole = adr.split("\\.");
        if (Integer.valueOf(pole[0]) >= 224 && Integer.valueOf(pole[0]) <= 255) {
            // -> adresy rozsahu 224.* - 239.* jsou vyhrazeny pro multicast, vyssi ifconfig nezere
            return true;
        }
        return false;
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


}
