/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

    public String vypisIP(){
//        System.out.println("vypisuji: adresa="+adresa);
        return vypisPole(prevedNaPole(adresa));
    }

    public String vypisMasku(){
//        System.out.println("vypisuji: maska="+maska);
        return vypisPole(prevedNaPole(maska));
    }


    /**
     * Ocekava to masku jako integer, kolik prvnich bitu maj bejt jednicky
     * @param maska
     */
    public void nastavMasku(int pocetBitu){
        this.maska=0;
        for(int i=0;i<pocetBitu;i++){
            maska=maska | 1<<(31-i);
        }
//        System.out.println("maska="+maska);
    }
    
    public void nastavIP(String adr) {

        //TODO: dodelat vyjimky

        // jednoducha kontrola
        adresa=0;
        if (!adr.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            System.out.println("1: chyba | vyjimka");
        }

        String[] pole = adr.split("\\.");
        int n;
        for (int i = 0; i < 4; i++) {
            try {
                n = Integer.valueOf(pole[i]);

                // druha kontrola
                if (n < 0 || n > 254 || ( i == 3 && n == 0 )) System.out.println("2: chyba | vyjimka");
                adresa = adresa | n <<(8*(3-i));
            } catch (NumberFormatException e) {
                System.out.println("chyba | vyjimka\n nemelo by k tomu dojit, kdyz prosla kontrola");
            }
        }
//        System.out.println("adresa="+adresa);

    }


    public int cisloSite(){
        return maska & adresa;
    }

    public int cisloPocitaceVSiti(){
        return (~ maska) & adresa;
    }

    public String vypisCisloSite(){
        int cs=maska & adresa;
        return vypisPole(prevedNaPole(cs));
    }

    public String vypisCisloPocitaceVSiti(){
        int cs=(~ maska) & adresa;
        return vypisPole(prevedNaPole(cs));
    }

    public boolean jeVeStejnySiti(IpAdresa jina){
        if (this.cisloSite()==jina.cisloSite()) return true;
        return false;
    }

    int[] prevedNaPole(int cislo){ //prevadi masku nebo adresu do citelny podoby
        int[]pole=new int[4];
        int tmp;
        for(int i=0;i<4;i++){
            tmp= cislo & ( 255 << (3-i)*8);
            pole[i]=tmp >>> ((3-i)*8);
        }
        return pole;
    }

    private String vypisPole(int[]pole){
        String ret=pole[0]+"."+pole[1]+"."+pole[2]+"."+pole[3];
        return ret;
    }


}
