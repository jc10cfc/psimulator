/*
 * Vychytavani chyb. Ct 22.4.2010.
 */

package vyjimky;

/**
 * Tuto vyjimku hazi trida konsole, kdyz dojde k nejaké chybě komunikace
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
