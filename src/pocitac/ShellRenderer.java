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
    private StringBuilder sb = new StringBuilder(50); //buffer načítaného řádku, čtecí buffer

    ShellRenderer(Konsole aThis) {
        this.konsole = aThis;
        termIO = this.konsole.getTerminalIO();
    }




    /**
     * hlavní funkce zobrazování shellu a čtení z terminálu, reakce na různé klávesy ENETER, BACKSCAPE, LEFT ....
     * @return  vrací přečtenou hodnotu z řádku, příkaz
     */
    public String handleInput() {

        this.sb = new StringBuilder(50);
        boolean konecCteni = false; // příznak pro ukončení čtecí smyčky jednoho příkazu
        boolean printOut; // příznak zda přečtený znak bude vytištěn do terminálu a zapsán do čtecího bufferu this.sb
        this.cursor = 0;

        try {

            while (!konecCteni) {

                printOut = true;
                int i = termIO.read();

                if (i == TerminalIO.LEFT) {
                    printOut = false;
                    moveCursorLeft();
                }

                if (i == TerminalIO.RIGHT) {
                    printOut = false;
                    moveCursorRight();
                }

                if (i == TerminalIO.UP) {
                    printOut = false;
                    this.handleHistory(TerminalIO.UP);
                }

                if (i == TerminalIO.DOWN) {
                    printOut = false;
                    this.handleHistory(TerminalIO.DOWN);
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
                    printOut = false;
                    konecCteni = true;
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

    /**
     * funkce která překreslí řádek od pozice kurzoru až do jeho konce dle čtecího bufferu
     * @throws IOException
     */
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

    /**
     * funkce obsluhující historii, respektive funkce volaná při přečtení kláves UP a DOWN
     * @param key typ klávesy který byl přečten
     * @throws IOException
     */
    public void handleHistory(int key) throws IOException {
        if (!(key == TerminalIO.UP || key == TerminalIO.DOWN)) // historie se ovládá pomocí šipek nahoru a dolů, ostatní klávesy ignoruji
        {
            return;
        }

        termIO.eraseLine();
        termIO.moveLeft(100);  // kdyby byla lepsi cesta jak smazat řádku, nenašel jsem

        this.konsole.vypisPrompt();

        if (key == TerminalIO.UP) {
            this.sb = new StringBuilder(this.konsole.getPreviousCommand());
        } else if (key == TerminalIO.DOWN) {
            this.sb = new StringBuilder(this.konsole.getNextCommand());
        }

        termIO.write(this.sb.toString());
        termIO.moveLeft(100);
        termIO.moveRight(sb.length() + this.konsole.prompt.length());
        this.cursor = sb.length();

    }

    /**
     * funkce obstarávající posun kurzoru vlevo.
     * Posouvá "blikající" kurzor, ale i "neviditelný" kurzor značící pracovní místo v čtecím bufferu
     */
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
        System.out.println("VLEVO, pozice: " + cursor);

    }

    /**
     *  funkce obstarávající posun kurzoru vpravo.
     *  Posouvá "blikající" kurzor, ale i "neviditelný" kurzor značící pracovní místo v čtecím bufferu
     */
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
         System.out.println("VPRAVO, pozice: " + cursor);
    }
}
