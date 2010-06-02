/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 * Tato vyjimka se hazi, kdyz chvi nastavit masku, ale ta ma nespravná cisla, napr. maska 32.123.12.23
 * Potomek SpatneVytvorenaAdresaException
 * @author Tomáš Pitřinec
 */
public class SpatnaMaskaException extends SpatneVytvorenaAdresaException {

    /**
     * Creates a new instance of <code>SpatnaMaskaException</code> without detail message.
     */
    public SpatnaMaskaException() {
    }


    /**
     * Constructs an instance of <code>SpatnaMaskaException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SpatnaMaskaException(String msg) {
        super(msg);
    }
}
