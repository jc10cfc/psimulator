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

    public int[] dejIP() {
        return adresa;
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

