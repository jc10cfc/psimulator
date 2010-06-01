
package vyjimky;

/**
 * Tahleta vyjimka se hazi, kdyz se pridava novy zaznam do wrapperu a nastane pritom nejaka chyba.
 * @author Stanislav Řehák
 */
public class WrapperException extends RuntimeException {

    public WrapperException(String msg) {
        super(msg);
    }

}
