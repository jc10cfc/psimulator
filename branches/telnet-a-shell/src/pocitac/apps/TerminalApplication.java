/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps;

import pocitac.AbstraktniPocitac;
import telnetd.io.BasicTerminalIO;

/**
 *
 * @author zaltair
 */
public abstract class TerminalApplication {

    protected abstract int run(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac);

}
