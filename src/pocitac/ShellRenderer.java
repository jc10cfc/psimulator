/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import telnetd.io.BasicTerminalIO;
import telnetd.io.TerminalIO;

/**
 *
 * @author zaltair
 */
public class ShellRenderer {

    private static Log log = LogFactory.getLog(ShellRenderer.class);
    private Konsole konsole;
    private BasicTerminalIO termIO;
    private int cursor = 0;
    private StringBuilder sb = new StringBuilder(50); //radek nacitany

    ShellRenderer(Konsole aThis) {
        this.konsole = aThis;
        termIO = this.konsole.getTerminalIO();
    }

    public String handleInput() {

        sb = new StringBuilder(50);
        boolean konecCteni = false;
        boolean printOut;
        this.cursor = 0;

        try {

            while (!konecCteni) {

                printOut = true;
                int i = termIO.read();

                if (i == TerminalIO.LEFT) {

                    moveCursorLeft();
                    printOut = false;

                    System.out.println("VLEVO, pozice: " + cursor);

                }


                if (i == TerminalIO.RIGHT) {

                    moveCursorRight();
                    printOut = false;
                    System.out.println("VPRAVO, pozice: " + cursor);

                }

                if (i == TerminalIO.BACKSPACE) {
                    printOut = false;

                    if (cursor != 0) {
                        sb.deleteCharAt(cursor - 1);
                        moveCursorLeft();
                        renderRestOfLine();
                        System.out.println("Pozice kurzoru: " + cursor);
                    }
                }

                if (i == -1 || i == -2) {
                    log.debug("Input(Code):" + i);
                    konecCteni = true;
                    printOut = false;
                }
                if (i == TerminalIO.ENTER) {
                    konecCteni = true;
                    printOut = false;
                    termIO.write(BasicTerminalIO.CRLF);
                }

                if (printOut) {
                    termIO.write(i);
                    sb.insert(cursor, (char) i);
                    cursor++;
                    renderRestOfLine();
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(ShellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();

    }

    public void renderRestOfLine() throws IOException {

        if (cursor <= sb.length()) // pokud nejsem na konci řádku
        {
            termIO.storeCursor();
            termIO.eraseToEndOfScreen();
            termIO.restoreCursor();
            termIO.storeCursor();

            int tempCursor = cursor;

            while (tempCursor < sb.length()) { //dokud jsem nevypsal zbytek textu
                termIO.write(sb.charAt(tempCursor));
                tempCursor++;
            }

        }
        termIO.restoreCursor();
        termIO.moveLeft(1);
        termIO.moveRight(1);

    }

    public void moveCursorLeft() {
        if (cursor == 0) {
            return;
        } else {
            try {
                termIO.moveLeft(1);
                cursor--;
            } catch (IOException ex) {
                Logger.getLogger(ShellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }


        }

    }

    public void moveCursorRight() {
        if (cursor >= this.sb.length()) {
            return;
        } else {
            try {
                termIO.moveRight(1);
                cursor++;
            } catch (IOException ex) {
                Logger.getLogger(ShellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
    }
}
