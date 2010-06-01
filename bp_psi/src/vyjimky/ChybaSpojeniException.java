/*
 * Vychytavani chyb. Ct 22.4.2010.
 */

package vyjimky;

/**
 * Tuhletu vyjimku hazi trida konsole, kdyz dojde k nejaky chybe komunikace
 * @author Tomáš Pitřinec
 */
public class ChybaSpojeniException extends RuntimeException {

    /**
     * Constructs an instance of <code>ChybaSpojeniException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ChybaSpojeniException(String msg) {
        super(msg);
    }
}
