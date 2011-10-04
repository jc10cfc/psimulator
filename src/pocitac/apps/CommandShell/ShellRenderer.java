/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac.apps.CommandShell;

import Main.Main;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pocitac.Konsole;
import telnetd.io.BasicTerminalIO;
import telnetd.io.TerminalIO;

/**
 *
 * @author zaltair
 */
public class ShellRenderer {

    private static Log log = LogFactory.getLog(ShellRenderer.class);
    private CommandShell commandShell;
    private BasicTerminalIO termIO;
    private int cursor = 0;
    private StringBuilder sb = new StringBuilder(50); //buffer načítaného řádku, čtecí buffer


    ShellRenderer(BasicTerminalIO terminalIO, CommandShell commandShell) {
        this.termIO = terminalIO;
        this.commandShell = commandShell;
    }

    /**
     * hlavní funkce zobrazování shellu a čtení z terminálu, reakce na různé klávesy ENETER, BACKSCAPE, LEFT ....
     * @return  vrací přečtenou hodnotu z řádku, příkaz
     */
    public String handleInput() {

        this.sb.setLength(0); // clear string builder
        boolean konecCteni = false; // příznak pro ukončení čtecí smyčky jednoho příkazu
        boolean printOut; // příznak zda přečtený znak bude vytištěn do terminálu a zapsán do čtecího bufferu this.sb
        List<String> nalezenePrikazy = new LinkedList<String>(); // seznam nalezenych příkazů po zmáčknutí tabu
        this.cursor = 0;

        try {

            while (!konecCteni) {


                int i = termIO.read();
                printOut = isPrintable(i);


                if (i == TerminalIO.DEL || i == TerminalIO.DELETE) {
                    printOut = false;
                    termIO.eraseLine();
                    termIO.moveLeft(100);  // kdyby byla lepsi cesta jak smazat řádku, nenašel jsem
                    this.cursor = 0;
                    this.sb.setLength(0);
                    this.commandShell.vypisPrompt();
                }

                if (i == TerminalIO.TABULATOR) {
                    printOut = false;
                    this.handleTabulator(nalezenePrikazy);


                } else {
                    nalezenePrikazy = new LinkedList<String>(); // vyčistím pro další hledání
                }

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
                        Main.debug("Pozice kurzoru: " + cursor);
                    }
                }

                if (i == 12) {
                    printOut = false;
                    this.clearScreen();
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

                Main.debug("Pozice kurzoru: " + cursor + " Tisknul jsem znak? TRUE/FALSE " + printOut);
            }

        } catch (IOException ex) {
            Logger.getLogger(ShellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();

    }

    private boolean isPrintable(int znakInt) {

        String s = String.valueOf((char) znakInt);

        return s.matches("[a-zA-Z]|"
                + "[0-9]|"
                + "\\s|"
                + "[(]|"
                + "[)]|"
                + "[{]|"
                + "[}]"
                + "[\\[]|"
                + "[\\]]|"
                + "[$]|"
                + "[\"]|"
                + "[\']|"
                + "[;]|"
                + "[:]|"
                + "[.]|"
                + "[,]|"
                + "[+]|"
                + "[-]|"
                + "[/]|"
                + "[\\*]|"
                + "[!]|"
                + "[%]|"
                + "[\\|]|"
                + "[&]|"
                + "[#]|"
                + "[~]|"
                + "[@]|"
                + "[?]|"
                + "[<]|"
                + "[>]|"
                + "[=]|");

    }

    /**
     * funkce která překreslí řádek od pozice kurzoru až do jeho konce dle čtecího bufferu
     * @throws IOException
     */
    private void renderRestOfLine() throws IOException {

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
     * překreslí celou řádku, umístí kurzor na konec řádky
     * @throws IOException
     */
    private void updateWholeLine() throws IOException {

        this.cursor = 0;

        while (this.cursor != this.sb.length()) {
            termIO.write(sb.charAt(cursor));
            // možná RIGHT
            cursor++;
        }

    }

    /**
     * funkce obsluhující historii, respektive funkce volaná při přečtení kláves UP a DOWN
     * @param key typ klávesy který byl přečten
     * @throws IOException
     */
    private void handleHistory(int key) throws IOException {
        if (!(key == TerminalIO.UP || key == TerminalIO.DOWN)) // historie se ovládá pomocí šipek nahoru a dolů, ostatní klávesy ignoruji
        {
            return;
        }

        termIO.eraseLine();
        termIO.moveLeft(100);  // kdyby byla lepsi cesta jak smazat řádku, nenašel jsem

        this.commandShell.vypisPrompt();

        if (key == TerminalIO.UP) {
            this.sb.setLength(0);
            this.sb.append(this.commandShell.getHistory().getPreviousCommand());
        } else if (key == TerminalIO.DOWN) {
            this.sb.setLength(0);
            this.sb.append(this.commandShell.getHistory().getNextCommand());
        }

        termIO.write(this.sb.toString());
        termIO.moveLeft(100);
        termIO.moveRight(sb.length() + this.commandShell.prompt.length());
        this.cursor = sb.length();

    }

    /**
     * funkce obstarávající posun kurzoru vlevo.
     * Posouvá "blikající" kurzor, ale i "neviditelný" kurzor značící pracovní místo v čtecím bufferu
     */
    private void moveCursorLeft() {
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
        Main.debug("VLEVO, pozice: " + cursor);

    }

    /**
     *  funkce obstarávající posun kurzoru vpravo.
     *  Posouvá "blikající" kurzor, ale i "neviditelný" kurzor značící pracovní místo v čtecím bufferu
     */
    private void moveCursorRight() {
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
        Main.debug("VPRAVO, pozice: " + cursor);
    }

    /**
     *
     * @param nalezenePrikazy seznam nalezených příkazů z předchozího hledání,
     * pokud prázdný, tak jde o první stisk tabulatoru
     */
    private void handleTabulator(List<String> nalezenePrikazy) throws IOException {

        if (!nalezenePrikazy.isEmpty() && nalezenePrikazy.size() > 1) { // dvakrat zmacknuty tab a mám více než jeden výsledek

            termIO.write(TerminalIO.CRLF); // nový řádek

            for (String nalezeny : nalezenePrikazy) {
                termIO.write(nalezeny + "  ");
            }

            termIO.write(TerminalIO.CRLF); // nový řádek
            this.commandShell.vypisPrompt();
            termIO.write(this.sb.toString());


            return;
        }


// nové hledání

        String hledanyPrikaz = this.sb.substring(0, cursor);
        List<String> prikazy = this.commandShell.getCommandList();


        for (String temp : prikazy) {
            if (temp.startsWith(hledanyPrikaz)) {
                nalezenePrikazy.add(temp);
            }

        }

        if (nalezenePrikazy.isEmpty()) // nic jsem nenašel, nic nedělám :)
        {
            return;
        }


        if (nalezenePrikazy.size() == 1) // našel jsem jeden odpovídající příkaz tak ho doplním
        {

            termIO.eraseLine();
            termIO.moveLeft(100);  // kdyby byla lepsi cesta jak smazat řádku, nenašel jsem

            this.commandShell.vypisPrompt();
            this.sb.setLength(0); // empty string builder
            this.sb.append(nalezenePrikazy.get(0)).append(" ");

            updateWholeLine();

        }


    }

    private void clearScreen() throws IOException {
        this.termIO.eraseScreen();
        termIO.setCursor(0, 0);
        this.commandShell.vypisPrompt();
        
        updateWholeLine();


    }
}
