/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac.apps.CommandShell;

import Main.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pocitac.AbstraktniPocitac;
import pocitac.apps.TerminalApplication;
import pocitac.apps.CommandShell.prikazy.ParserPrikazu;
import telnetd.io.BasicTerminalIO;
import vyjimky.ChybaSpojeniException;

/**
 *
 * @author zaltair
 */
public class CommandShell extends TerminalApplication {

    private ShellRenderer shellRenderer;
    private History history = new History();
    public boolean vypisPrompt = true; // v ciscu obcas potrebuju zakazat si vypisovani promptu
    public String prompt = "default promt:~# ";
    private boolean ukoncit = false;
    private ParserPrikazu parser;
    private Object zamek;

    public CommandShell(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac) {
        super(terminalIO, pocitac);
        this.shellRenderer = new ShellRenderer(terminalIO, this);
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public List<String> getCommandList() {
        return this.pocitac.getCommandList();
    }

    /**
     * method that read  till \r\n occured
     * @return whole line without \r\n
     */
    public String readLine() {
        return shellRenderer.handleInput();
    }

    public String readCharacter(){
        try {
            return String.valueOf((char) this.terminalIO.read());
        } catch (IOException ex) {
            System.err.println("IOException, cannot read a single character from terminal");
        }

        return "";
    }

    /**
     * print text with program name
     * @param text
     */
    public void printWithSimulatorName(String text) {
        printLine(Main.jmenoProgramu + ": " + text);
    }

    /**
     * method used to printLine to the terminal, this method call print(text+"\r\n") nothing more
     * @param text text to be printed to the terminal
     */
    public void printLine(String text) {
            this.print((text + "\r\n"));
    }

    
    /**
     * method used to print text to the terminal
     * @param text text to be printed to the terminal
     * @throws ChybaSpojeniException
     */
    public void print(String text) throws ChybaSpojeniException {
        try {
            terminalIO.write(text);
            terminalIO.flush();

            if (Main.debug) {
                pocitac.vypis("Print): " + text);
            }
        } catch (IOException ex) {
            throw new ChybaSpojeniException("Method CommandShell.print failed");
        }
    }

    /**
     * method that print lines with delay
     * @param lines
     * @param delay in milliseconds
     *
     */
    public void printWithDelay(String lines, int delay){
        try {
            BufferedReader input = new BufferedReader(new StringReader(lines));
            String singleLine = "";
            while ((singleLine = input.readLine()) != null) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    System.err.println("Thread interruped exception occured in printWithDelay method");
                }

                printLine(singleLine);
            }
        } catch (IOException ex) {
            System.err.println("IO exception occured in printWithDelay method");
        }
        
    }

    /**
     * just print prompt
     */
    public void printPrompt() {
        if (vypisPrompt) {
            print(prompt);
        }
    }

    /**
     * close session, terminal connection will be closed
     */
    public void closeSession() {
        if (Main.debug) {
            pocitac.vypis("Zavolala se metoda ukonci.");
        }
        ukoncit = true;
    }

    public void setParser(ParserPrikazu parser) {
        this.parser = parser;
    }

    @Override
    public final int run() {

        pocitac.configureCommandShell(this);
        this.zamek = this.pocitac.zamekPocitace;

        String radek;


        while (!ukoncit) {

            printPrompt();

            radek = readLine();
            this.history.add(radek);

            Main.debug("PRECETL JSEM :" + radek);

            synchronized (zamek) {
                parser.zpracujRadek(radek);
            }
            try {
                terminalIO.flush();
            } catch (IOException ex) {
                return -1;
            }
        }

        return 0;
    }
}
