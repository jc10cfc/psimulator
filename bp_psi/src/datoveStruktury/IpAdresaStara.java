/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datoveStruktury;

/**
 * Puvodni trida pro IP adresu. Nehodi se pro "pocitani" -> zavrzena.
 * @author neiss
 */
@Deprecated
public class IpAdresaStara {

    private int[] adresa;
    private int maska; //dal bych ji sem jako pocet bytu cisla site
    // muze se hodit pro testovani

    public IpAdresaStara() {
        adresa = new int[4];
        adresa[0] = 6;
        adresa[1] = 7;
        adresa[2] = 8;
        adresa[3] = 9;
    }

    public int[] dejIP() {
        return adresa;
    }

    /**
     * Vraci ip adresu jako string
     * @return
     */
    public String vypisIP(){
        return (adresa[0]+"."+adresa[1]+"."+adresa[2]+"."+adresa[3]);
    }

    public void nastavMasku(int maska){
        this.maska=maska;
    }

    private int mocninaDvojky(int naKolikatou){
        int c=1;
        for(int i=0;i<naKolikatou;i++){
            c=c*2;
        }
        return c;
    }

    public String vypisMasku(){
        int [] pole = new int[4];
        int i;
        for(i=0;i<maska/8;i++){ //to, co je cely plny se vyplni.
            pole[i]=255;
        }
        int m = maska % 8;
        if(i<4 && m!=0){
            pole[i]=256-mocninaDvojky(8-m);
        }
        return (pole[0]+"."+pole[1]+"."+pole[2]+"."+pole[3]);
    }

    /**
     * Nastavi adresu na pozadovanou hodnotu. Predpoklada string s IP bez mezer na zacatku nebo na konci.
     * @param adr
     */
    public void nastavIP(String adr) {

        //TODO: dodelat vyjimky

        // jednoducha kontrola
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
                adresa[i] = n;
            } catch (NumberFormatException e) {
                System.out.println("chyba | vyjimka\n nemelo by k tomu dojit, kdyz prosla kontrola");
            }
        }

    }
}

