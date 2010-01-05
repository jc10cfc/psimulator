/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datoveStruktury;

/**
 *
 * @author neiss
 */
public class IpAdresa {

    private int[] adresa;

    // muze se hodit pro testovani
    public IpAdresa() {
        adresa = new int[4];
        adresa[0] = 6;
        adresa[1] = 7;
        adresa[2] = 8;
        adresa[3] = 9;
    }

    /**
     * Nastavi adresu na pozadovanou hodnotu. Predpoklada string s IP bez mezer na zacatku nebo na konci.
     * @param adr
     */
    public void nastavIP(String adr){
        int[]bajty=new int[4];
        int posT=adr.indexOf(".");
        //String bajt
        
        // kontrola
        if (! adr.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            System.out.println("chyba | vyjimka");
        }

        
        

    }

    

}

