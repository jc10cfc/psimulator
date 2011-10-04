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
    private BasicTerminalIO terminalIO;
    private History history = new History();
    private AbstraktniPocitac pocitac;
    public boolean vypisPrompt = true; // v ciscu obcas potrebuju zakazat si vypisovani promptu
    public String prompt = "default promt:~# ";
    private boolean ukoncit=false;
    private ParserPrikazu parser;
    private Object zamek;

    public CommandShell(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac) {

        this.shellRenderer = new ShellRenderer(terminalIO, this);
        this.pocitac = pocitac;
        this.terminalIO = terminalIO;

        this.run(terminalIO, pocitac);

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
     * metoda nacita z proudu dokud neprijde \r\n
     * Prevzata z KarelServer, ale pak jsem ji stejne celou prepsal.
     * @param in
     * @return celej radek do \r\n jako string. kterej to \r\n uz ale neobsahuje
     */
    public String ctiRadek() {

        return shellRenderer.handleInput();

    }

    /**
     * Posle servisni vypis pres posliRadek. Prida tam navic jmeno programu s dvojteckou a mezerou.
     * @param ret
     */
    public void posliServisne(String ret) {
        posliRadek(Main.jmenoProgramu + ": " + ret);
    }

    /**
     * Metoda na posilani celeho radku do výstupního proudu. Zaroven vypisuje poslané řetězce na standartni vystup.
     * Prevzata z KarelServer
     * @param out
     * @param ret
     * @throws java.io.IOException
     */
    public void posliRadek(String ret) throws ChybaSpojeniException {
        try {

            terminalIO.write((ret + "\r\n"));
            terminalIO.flush();
            if (Main.debug) {
                pocitac.vypis("posilam radek): " + ret);
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Metoda posliRadek, nastala chyba.");
        }
    }

    /**
     * Metoda na posilani do výstupního proudu. Zaroven vypisuje poslané řetězce na standartni vystup.
     * Prevzata z KarelServer
     * @param out
     * @param ret
     * @throws java.io.IOException
     */
    public void posli(String ret) throws ChybaSpojeniException {
        try {
            terminalIO.write(ret);
            terminalIO.flush();
            if (Main.debug) {
                pocitac.vypis("posilam): " + ret);
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Metoda posli: nastala chyba.");
        }
    }

    /**
     * Posila po radcich se zpozdenim v ms. Vyuziva metodu posliRadek().
     * @param s, retezec, ktery ma posilat
     * @param cekej, prodleva v ms mezi jednotlivejma radkama
     * @author Stanislav Řehák
     */
    public void posliPoRadcich(String s, int cekej) throws ChybaSpojeniException {
        BufferedReader input = new BufferedReader(new StringReader(s));
        String lajna = "";
        try {
            while ((lajna = input.readLine()) != null) {
                try {
                    Thread.sleep(cekej);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CommandShell.class.getName()).log(Level.SEVERE, null, ex);
                }
                posliRadek(lajna);
            }
        } catch (IOException e) { //tohleto chyta vyjimku z BufferedReadru, ne z posliRadek
            throw new ChybaSpojeniException("Metoda posliPoRadcich: nastala chyba.");
        }
    }

    /**
     * Vypise prompt na prikazou radku.
     * @throws IOException
     */
    public void vypisPrompt() throws ChybaSpojeniException {

        if (!vypisPrompt) {
            return;
        }

        try {
            terminalIO.write(prompt);
            terminalIO.flush();
            //pocitac.vypis("(socket c. "+cislo+" posilam): "+prompt);
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Metoda vypisPrompt, nastala chyba.");
        }
    }

    /**
     * Ukonci spojeni.
     */
    public void ukonciSpojeni() {
        if (Main.debug) {
            pocitac.vypis("Zavolala se metoda ukonci.");
        }
        ukoncit = true;
    }

    public void setParser(ParserPrikazu parser) {
        this.parser = parser;
    }

    @Override
    public final int run(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac) {

        pocitac.nastavKonsoli(this);
        this.zamek = this.pocitac.zamekPocitace;

        String radek ;


                    while (!ukoncit) {

                vypisPrompt();

                radek = ctiRadek();
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
