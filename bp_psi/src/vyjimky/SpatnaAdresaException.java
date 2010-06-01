/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 * Hazi se v konstruktoru IpAdresy, kdyz je zadanej spatnej String.
 * Potomek SpatneVytvorenaAdresaException
 * @author Tomáš Pitřinec
 */
public class SpatnaAdresaException extends SpatneVytvorenaAdresaException {

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
