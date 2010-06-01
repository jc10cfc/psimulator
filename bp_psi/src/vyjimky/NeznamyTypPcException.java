/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vyjimky;

/**
 * Tahleta vyjimka se hazi, kdyz se snazi SAXHandler nacist pocitac z XML a nezna typ pocitace (zatim zna jen Linux a Cisco).
 * @author Stanislav Řehák
 */
public class NeznamyTypPcException extends RuntimeException {

    /**
     * Creates a new instance of <code>NeznamyTypPcException</code> without detail message.
     */
    public NeznamyTypPcException() {
    }


    /**
     * Constructs an instance of <code>NeznamyTypPcException/code> with the specified detail message.
     * @param msg the detail message.
     */
    public NeznamyTypPcException(String msg) {
        super(msg);
    }
}
