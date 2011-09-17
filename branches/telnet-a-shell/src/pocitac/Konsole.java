/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import telnetd.net.ConnectionData;
import telnetd.io.TerminalIO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import telnetd.io.BasicTerminalIO;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import telnetd.net.Connection;
import telnetd.net.ConnectionEvent;
import prikazy.ParserPrikazu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import telnetd.shell.Shell;
import vyjimky.ChybaSpojeniException;
import static prikazy.AbstraktniPrikaz.*;

/**
 * Třída obsluhuje jedno spojení s klientem.
 * @author Tomáš Pitřinec
 */
public class Konsole implements Shell {

    private static Log log = LogFactory.getLog(Konsole.class);
    private Connection m_Connection;
    private BasicTerminalIO m_IO;
    private ArrayList<String> history = new ArrayList<String>();
    private int historyIterator = 0;
    boolean ladiciVypisovani = true;
    private AbstraktniPocitac pocitac;
    private ParserPrikazu parser;
    int cislo; //poradove cislo vlakna, jak je v tom listu, spis pro ladeni
    public String prompt = "divnej defaultni promt:~# ";
    private boolean ukoncit;
    public boolean vypisPrompt = true; // v ciscu obcas potrebuju zakazat si vypisovani promptu
    public boolean doplnovani = false;
    private boolean vypisy = false;
    private Object zamek;
    private ShellRenderer renderer;

    public BasicTerminalIO getTerminalIO() {
        return m_IO;
    }

    /**
     * metoda nacita z proudu dokud neprijde \r\n
     * Prevzata z KarelServer, ale pak jsem ji stejne celou prepsal.
     * @param in
     * @return celej radek do \r\n jako string. kterej to \r\n uz ale neobsahuje
     */
    public String ctiRadek() {

        return renderer.handleInput();

    }

    /**
     * Posle servisni vypis pres posliRadek. Prida tam navic jmeno programu s dvojteckou a mezerou.
     * @param ret
     */
    public void posliServisne(String ret) {
        posliRadek(Main.Main.jmenoProgramu + ": " + ret);
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

            m_IO.write((ret + "\r\n"));
            m_IO.flush();
            if (vypisy) {
                pocitac.vypis("(socket c. " + cislo + " posilam radek): " + ret);
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Konsole cislo " + cislo + ", metoda posliRadek, nastala chyba.");
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
            m_IO.write(ret);
            m_IO.flush();
            if (vypisy) {
                pocitac.vypis("(socket c. " + cislo + " posilam): " + ret);
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Konsole cislo " + cislo + ", metoda posli: nastala chyba.");
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
                cekej(cekej);
                posliRadek(lajna);
            }
        } catch (IOException e) { //tohleto chyta vyjimku z BufferedReadru, ne z posliRadek
            throw new ChybaSpojeniException("Konsole cislo " + cislo + ", metoda posliPoRadcich: nastala chyba.");
        }
    }

    /**
     * Vypise prompt na prikazou radku.
     * @throws IOException
     */
    public void vypisPrompt() throws ChybaSpojeniException {
        try {
            m_IO.write(prompt);
            m_IO.flush();
            //pocitac.vypis("(socket c. "+cislo+" posilam): "+prompt);
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Konsole cislo " + cislo + ", metoda vypisPrompt, nastala chyba.");
        }
    }

    /**
     * Hlavni bezici metoda Konsole, bezi v nekonecny smycce a zastavuje se booleanem ukoncit.
     */
    @Override
    public void run(Connection con) {
        System.out.println("Vytvářím novou telnet konsoli na portu:" + con.getConnectionData().getSocket().getLocalPort());
        try {
            m_Connection = con;
            m_IO = m_Connection.getTerminalIO();
            m_Connection.addConnectionListener(this); //dont forget to register listener
            this.renderer = new ShellRenderer(this);
            String radek = null;


            // potrebuju najit pocitac ktery je urcen pro dany port.
            List<AbstraktniPocitac> vsechnyPocitace = (List<AbstraktniPocitac>) Main.Main.vsechno;
            int port = m_Connection.getConnectionData().getSocket().getLocalPort();
            for (AbstraktniPocitac computer : vsechnyPocitace) {
                if (computer.getPort() == port) {
                    this.pocitac = computer;
                }
            }

            // přídám tento objekt konsole do seznamu konzolí počítače
            this.pocitac.getKonsolePocitace().add(this);
            this.cislo = this.pocitac.getKonsolePocitace().size();

            System.out.println("Konsole č." + this.cislo + " vytvořena pro počítač:" + this.pocitac.jmeno + " který naslouchá na portu:" + port);

            // nastavím prompt a parser podle typu počítače, předtím tu bylo instanceof :(
            this.pocitac.nastavKonsoli(this);

            this.zamek = this.pocitac.zamekPocitace;

            //clear the screen and start from zero
            m_IO.eraseScreen();
            m_IO.homeCursor();
            m_IO.setLinewrapping(true);

            pocitac.vypis("Konsole c. " + cislo + " startuje.");

            ukoncit = false;
/////////////////////////////DEBUG////////////////////////////////////////////
            ConnectionData cd = m_Connection.getConnectionData();
            m_IO.write(BasicTerminalIO.CRLF
                    + "DEBUG: Active Connection"
                    + BasicTerminalIO.CRLF);
            m_IO.write("------------------------" + BasicTerminalIO.CRLF);

            //output connection data
            m_IO.write("Connected from: " + cd.getHostName()
                    + "[" + cd.getHostAddress() + ":" + cd.getPort() + "]"
                    + BasicTerminalIO.CRLF);
            m_IO.write("Guessed Locale: " + cd.getLocale()
                    + BasicTerminalIO.CRLF);
            m_IO.write(BasicTerminalIO.CRLF);
            //output negotiated terminal properties
            m_IO.write("Negotiated Terminal Type: "
                    + cd.getNegotiatedTerminalType() + BasicTerminalIO.CRLF);
            m_IO.write("Negotiated Columns: " + cd.getTerminalColumns()
                    + BasicTerminalIO.CRLF);
            m_IO.write("Negotiated Rows: " + cd.getTerminalRows()
                    + BasicTerminalIO.CRLF);

            //output of assigned terminal instance (the cast is a hack, please
            //do not copy for other TCommands, because it would break the
            //decoupling of interface and implementation!
            m_IO.write(BasicTerminalIO.CRLF);
            m_IO.write("Assigned Terminal instance: "
                    + ((TerminalIO) m_IO).getTerminal());
            m_IO.write(BasicTerminalIO.CRLF);
            m_IO.write("Environment: " + cd.getEnvironment().toString());
            m_IO.write(BasicTerminalIO.CRLF);
            //output footer
            m_IO.write("-----------------------------------------------"
                    + BasicTerminalIO.CRLF + BasicTerminalIO.CRLF);

            m_IO.flush();

/////////////////////////////DEBUG////////////////////////////////////////////

            while (!ukoncit) {
                if (vypisPrompt) {
                    vypisPrompt();
                }



                radek = ctiRadek();
                this.history.add(radek);
                this.historyIterator=0;

                if (ladiciVypisovani) {
                    System.out.println("PRECETL JSEM :" + radek);
                }

                if (vypisy) {
                    pocitac.vypis("(klient c. " + cislo + " poslal): '" + radek + "'");
                }
                //pocitac.vypis("dylka predchoziho radku: "+radek.length());
                //posliRadek(out,radek);
                synchronized (zamek) {
                    parser.zpracujRadek(radek);
                }

                m_IO.flush();
            }

            ////////////////////
        } catch (IOException ex) {
            Logger.getLogger(Konsole.class.getName()).log(Level.SEVERE, null, ex);
        }
        // po ukončení konsole ji odstraním ze seznamu počítače
        this.pocitac.getKonsolePocitace().remove(this);
    }

    /**
     * Ukonci spojeni.
     */
    public void ukonciSpojeni() {
        if (ladiciVypisovani) {
            pocitac.vypis("Zavolala se metoda ukonci.");
        }
        ukoncit = true;
    }

    public void setParser(ParserPrikazu parser) {
        this.parser = parser;
    }

    public String getPreviousCommand() {

        if (history.isEmpty()) {
            return "";
        }

        if (historyIterator < history.size()) {
            historyIterator++;
        }

        return history.get(history.size() - historyIterator);

    }

    public String getNextCommand() {

        if (history.isEmpty()) {
            return "";
        }

        if (historyIterator > 1) {
            historyIterator--;
        } else if(historyIterator <= 1){
            historyIterator=0;
            return "";
        }

        return history.get(history.size() - historyIterator);

    }


    public List<String> getCommandList(){
        return this.pocitac.getCommandList();
    }

    //this implements the ConnectionListener!
    @Override
    public void connectionTimedOut(ConnectionEvent ce) {
        try {
            m_IO.write("CONNECTION_TIMEDOUT");
            m_IO.flush();
            //close connection
            m_Connection.close();
        } catch (Exception ex) {
            log.error("connectionTimedOut()", ex);
        }
    }//connectionTimedOut

    @Override
    public void connectionIdle(ConnectionEvent ce) {
        try {
            m_IO.write("CONNECTION_IDLE");
            m_IO.flush();
        } catch (IOException e) {
            log.error("connectionIdle()", e);
        }

    }//connectionIdle

    @Override
    public void connectionLogoutRequest(ConnectionEvent ce) {
        try {
            this.ukonciSpojeni();
            //m_IO.write("CONNECTION_LOGOUTREQUEST");
            m_IO.flush();
        } catch (Exception ex) {
            log.error("connectionLogoutRequest()", ex);
        }
    }//connectionLogout

    @Override
    public void connectionSentBreak(ConnectionEvent ce) {
        try {
            m_IO.write("CONNECTION_BREAK");
            m_IO.flush();
        } catch (Exception ex) {
            log.error("connectionSentBreak()", ex);
        }
    }//connectionSentBreak

    public static Shell createShell() {
        return new Konsole();
    }//createShell
}
