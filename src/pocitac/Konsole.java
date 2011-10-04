/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import Main.Main;
import telnetd.net.ConnectionData;
import telnetd.io.TerminalIO;
import java.util.List;
import telnetd.io.BasicTerminalIO;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import telnetd.net.Connection;
import telnetd.net.ConnectionEvent;
import java.io.IOException;
import pocitac.apps.CommandShell.CommandShell;
import telnetd.shell.Shell;

/**
 * Třída obsluhuje jedno spojení s klientem.
 * @author Tomáš Pitřinec
 */
public class Konsole implements Shell {

    private static Log log = LogFactory.getLog(Konsole.class);
    private Connection m_Connection;
    private BasicTerminalIO m_IO;
    private AbstraktniPocitac pocitac;
    int cislo; //poradove cislo vlakna, jak je v tom listu, spis pro ladeni

    /**
     * Hlavni bezici metoda Konsole, bezi v nekonecny smycce a zastavuje se booleanem ukoncit.
     */
    @Override
    public void run(Connection con) {

        try {
            Main.debug("Vytvářím novou telnet konsoli na portu:" + con.getConnectionData().getSocket().getLocalPort());
            m_Connection = con;
            m_IO = m_Connection.getTerminalIO();
            m_Connection.addConnectionListener(this); //dont forget to register listener


            // potrebuju najit pocitac ktery je urcen pro dany port.
            List<AbstraktniPocitac> vsechnyPocitace = (List<AbstraktniPocitac>) Main.vsechno;
            int port = m_Connection.getConnectionData().getSocket().getLocalPort();
            for (AbstraktniPocitac computer : vsechnyPocitace) {
                if (computer.getPort() == port) {
                    this.pocitac = computer;
                }
            }
            // přídám tento objekt konsole do seznamu konzolí počítače
            this.pocitac.getKonsolePocitace().add(this);
            this.cislo = this.pocitac.getKonsolePocitace().size();



            //clear the screen and start from zero
            m_IO.eraseScreen();
            m_IO.homeCursor();
            m_IO.setLinewrapping(true);


            pocitac.vypis("Konsole c. " + cislo + " startuje.");


            if (Main.debug) {
                this.printKonsoleInfo();
                Main.debug("Konsole č." + this.cislo + " vytvořena pro počítač:" + this.pocitac.jmeno + " který naslouchá na portu:" + port);
            }
            // run command shell
            int exitValue = new CommandShell(m_IO, pocitac).run();

            Main.debug("CommandShell return exit value:"+exitValue);

        } catch (IOException ex) {
            System.err.println("Problem with telnet io connection");
        }


        // před ukončením konsole ji odstraním ze seznamu počítače
        this.pocitac.getKonsolePocitace().remove(this);
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
            m_IO.write("CONNECTION_LOGOUTREQUEST");
            m_IO.flush();
            this.m_Connection.close();
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

    /**
     * this method printOut information about konsole
     */
    private void printKonsoleInfo() {
        try {
            ConnectionData cd = m_Connection.getConnectionData();
            m_IO.write(BasicTerminalIO.CRLF + "DEBUG: Active Connection" + BasicTerminalIO.CRLF);
            m_IO.write("------------------------" + BasicTerminalIO.CRLF);
            //output connection data
            m_IO.write("Connected from: " + cd.getHostName() + "[" + cd.getHostAddress() + ":" + cd.getPort() + "]" + BasicTerminalIO.CRLF);
            m_IO.write("Guessed Locale: " + cd.getLocale() + BasicTerminalIO.CRLF);
            m_IO.write(BasicTerminalIO.CRLF);
            //output negotiated terminal properties
            m_IO.write("Negotiated Terminal Type: " + cd.getNegotiatedTerminalType() + BasicTerminalIO.CRLF);
            m_IO.write("Negotiated Columns: " + cd.getTerminalColumns() + BasicTerminalIO.CRLF);
            m_IO.write("Negotiated Rows: " + cd.getTerminalRows() + BasicTerminalIO.CRLF);
            //output of assigned terminal instance (the cast is a hack, please
            //do not copy for other TCommands, because it would break the
            //decoupling of interface and implementation!
            m_IO.write(BasicTerminalIO.CRLF);
            m_IO.write("Assigned Terminal instance: " + ((TerminalIO) m_IO).getTerminal());
            m_IO.write(BasicTerminalIO.CRLF);
            m_IO.write("Environment: " + cd.getEnvironment().toString());
            m_IO.write(BasicTerminalIO.CRLF);
            //output footer
            m_IO.write("-----------------------------------------------" + BasicTerminalIO.CRLF + BasicTerminalIO.CRLF);
            m_IO.flush();
        } catch (IOException ex) {
            System.err.println("Exception occured when calling printOutDebugInfo method ");
        }

    }

    public static Shell createShell() {
        return new Konsole();
    }//createShell
}
