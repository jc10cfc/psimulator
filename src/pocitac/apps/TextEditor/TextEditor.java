/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.TextEditor;

import pocitac.AbstraktniPocitac;
import pocitac.apps.TerminalApplication;
import telnetd.io.BasicTerminalIO;

/**
 *
 * @author zaltair
 */
public class TextEditor extends  TerminalApplication {

    public TextEditor(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac) {
        super(terminalIO, pocitac);
    }

    @Override
    protected int run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

  

}
