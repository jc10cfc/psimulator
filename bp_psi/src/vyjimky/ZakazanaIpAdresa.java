/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 * Tahleta vyjimka se hazi, kdyz chci nastavit ip, ktera je v zakazanem rozsahu 224.* - 255.* (rezervovane pro multicast + do zasoby)
 * @author haldyr
 */
public class ZakazanaIpAdresa extends RuntimeException {

    public ZakazanaIpAdresa() {
    }

    public ZakazanaIpAdresa(String msg) {
        super("Tato adresa neni povolena byti IP adresou: "+msg+ "  (adresa je v zakazanem rozsahu 224.* - 255.*)");
    }
}
