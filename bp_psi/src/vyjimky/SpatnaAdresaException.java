/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 *
 * @author neiss
 */
public class SpatnaAdresaException extends RuntimeException {

    /**
     * Creates a new instance of <code>SpatnaAdresaException</code> without detail message.
     */
    public SpatnaAdresaException() {
    }


    /**
     * Constructs an instance of <code>SpatnaAdresaException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SpatnaAdresaException(String msg) {
        super(msg);
    }
}
