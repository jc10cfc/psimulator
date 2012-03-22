

package de.mud.jta;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public final class OutputSingleton {

    public static final DelegatePrintStream out = new DelegatePrintStream(System.out, true);
    public static final DelegatePrintStream err = new DelegatePrintStream(System.err, true);
    
    
}
