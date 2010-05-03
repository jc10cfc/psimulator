/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 * Abstraktni predek vyjimek hazenejch predevsim v konstruktorech IpAdresy.
 * Udelal jsem ji, kdyz potrebuju chytat vyjimku a je mi jedno, jestli je kvuli masce nebo kvuli adrese.
 * @author neiss
 */
public abstract class SpatneVytvorenaAdresaException extends RuntimeException {

    /**
     * Creates a new instance of <code>SpatneVytvorenaAdresaException</code> without detail message.
     */
    public SpatneVytvorenaAdresaException() {
    }


    /**
     * Constructs an instance of <code>SpatneVytvorenaAdresaException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SpatneVytvorenaAdresaException(String msg) {
        super(msg);
    }
}
