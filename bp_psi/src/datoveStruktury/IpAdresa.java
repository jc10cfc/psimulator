package datoveStruktury;

/**
 * Hele, clovece, premejslel jsem dneska, jak dodelat ty metody, ktery budou pocitat cislo site a tak
 * a nakonec jsem tu implementaci zmenil na jeden integer, cislo site a pocitace a podobny veci se pak pocitaj
 * lip.
 * @author neiss
 */
public class IpAdresa {

    int adresa; //32 bitu ip adresy, zachazi se s tim pomoci bitovejch operaci
    int maska; //stejne jako skutecna maska, tzn. 32 bitu

    public IpAdresa(){
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
     * @return true, kdyz je adresa spravna
     */
    public static boolean jeSpravnaIP(String adr) {
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
        return true;
    }


    /**
     * Ocekava to masku jako integer, kolik prvnich bitu maj bejt jednicky
     * @param maska
     */
    public void nastavMasku(int pocetBitu) {
        if(pocetBitu>32 || pocetBitu<0){
            throw new RuntimeException("Zadany pocet bitu masky je nesmyslny: "+ pocetBitu);
        }
        this.maska = 0;
        for (int i = 0; i < pocetBitu; i++) {
            maska = maska | 1 << (31 - i);
        }
    }


    public void nastavIP(String adr) {
        if(!jeSpravnaIP(adr)){
            throw new RuntimeException("spatna adresa: "+adr);
        }
        adresa = integerZeStringu(adr);
    }

    public void nastavMasku(String maska) {
        if(!jeSpravnaIP(maska)){
            throw new RuntimeException("spatna maska: "+maska);
        }
        int moznaMaska = integerZeStringu(maska);
        if(jeSpravnaMaska(moznaMaska)){
            this.maska=moznaMaska;
        } else {
            throw new RuntimeException("Spatna maska, nejsou to jednickyu a pak nuly");
        }
    }

    public String vypisIP() {
        return vypisPole(prevedNaPole(adresa));
    }

    public String vypisMasku() {
        return vypisPole(prevedNaPole(maska));
    }

    public String vypisAdresuSMaskou(){
        return ( vypisPole(prevedNaPole(adresa)) + "/" + pocetBituMasky() );
    }


    public String vypisCisloSite() {
        return vypisPole(prevedNaPole(cisloSite()));
    }

    public String vypisCisloPocitaceVSiti() {
        int cs = (~maska) & adresa;
        return vypisPole(prevedNaPole(cs));
    }

    public boolean jeVeStejnySiti(IpAdresa jina) {
        if (this.cisloSite() == jina.cisloSite()) {
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

    private static boolean jednoduchaKontrola(String adr) { //kontrola IP, jestli to jsou cisla s teckama
        if (!adr.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            return false;
        }
        return true;
    }

    private int integerZeStringu(String ret){
        int a = 0;
        String[] pole = ret.split("\\.");
        int n;
        for (int i = 0; i < 4; i++) {
            n = Integer.valueOf(pole[i]);
            a = a | n << (8 * (3 - i));
        }
        return a;
    }

    private int pocetBituMasky(){
        if(maska==0)return 0;
        int pocet=32;
        int maska=this.maska;
        while( (maska & 1) == 0){
            maska = maska >> 1;
            pocet--;
        }
        return pocet;
    }

    private boolean jeSpravnaMaska(int maska){
        int i=0;
        while ( i<32 && ( maska & 1) == 0 ){ //tady prochazeji nuly
            i++;
            maska=maska>>1;
        }
        while ( i<32 && ( maska & 1) == 1 ){ //tady prochazeji jednicky
            i++;
            maska=maska>>1;
        }
        if(i==32) return true;
        return false;
    }
}
