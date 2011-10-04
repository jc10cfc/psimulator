/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps;

import pocitac.AbstraktniPocitac;
import telnetd.io.BasicTerminalIO;

/**
 * class that should be inherited when creating a terminal application like command shell or text editor
 * @author zaltair
 */
public abstract class TerminalApplication {

    protected BasicTerminalIO terminalIO;
    protected AbstraktniPocitac pocitac;

    public TerminalApplication(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac) {
        this.terminalIO = terminalIO;
        this.pocitac = pocitac;
    }
    
    /**
     *  execute application
     * @param terminalIO
     * @param pocitac
     * @return return exit value of program retValue == 0 ==> OK , retValue <-1  ==> fail
     */
    protected abstract int run();

}
