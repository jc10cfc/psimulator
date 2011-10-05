/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 * Tato vyjimka se hazi, kdyz nastane nejaka chyba pri cteni z XML souboru.
 * @author Stanislav Řehák
 */
public class ChybaKonfigurakuException extends RuntimeException {

    /**
     * Creates a new instance of <code>ChybaKonfigurakuException</code> without detail message.
     */
    public ChybaKonfigurakuException() {
    }


    /**
     * Constructs an instance of <code>ChybaKonfigurakuException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ChybaKonfigurakuException(String msg) {
        super(msg);
    }
}
