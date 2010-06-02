/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac;

import Main.Main;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Trida pro sitovou komunikaci s uzivatelem.
 * Prevzato z KarelServer.
 * Posloucha na zadaným portu a vytváří vlákna pro jednotlivý Konsole (připojení).
 * @author Tomáš Pitřinec
 */
public class Komunikace extends Thread {

    private ServerSocket ss; //socket, kterej posloucha
    int cisloPortu;
    List<Konsole> seznamSpojeni = new ArrayList();
    AbstraktniPocitac pc;//odkaz na pocitac
    private final Object zamekPocitace = new Object(); //zamek celyho pocitace, slouzi k tomu, aby se zmeny
    // v nastaveni poctace (tedy vykonavani prikazu)
    /**
     * Kdyz true, tak se vypisuje port u zamknutych pocitacu pri startu serveru.
     */
    private boolean vypisPort = false;

    public Komunikace(int cisloPortu, AbstraktniPocitac pc) {
        this.cisloPortu = cisloPortu;
        this.pc = pc;
        this.start(); //tohle casem spusti metodu run()
    }

    /**
     * Vrati cislo portu.
     */
    public int getPort() {
        return cisloPortu;
    }

    @Override
    public void run() {
        if (Main.chyba_spusteni) {
            System.exit(1);
        }

        try { //pokus o vytvareni socketu
            ss = new ServerSocket(cisloPortu);
        } catch (IOException e) {
            Main.chyba_spusteni = true;
            pc.vypis("Nemuzu poslouchat na portu " + cisloPortu + ".");
            if (cisloPortu < 1024) {
                pc.vypis("Spoustet programy poslouchajici na portu " + cisloPortu + " muze pouze root. "
                        + "Zkuste server spustit s portem > 1023.");
            } else {
                pc.vypis("Port " + cisloPortu + " je pravdepodobne obsazen jinym programem, "
                        + "pocitac " + pc.jmeno + " nemohl byt nastartovan. "
                        + "Zkuste server spustit s jinym portem.\n"
                        + "Ukoncuji..");
            }
            System.exit(1);
        }

        try { // timeout je tady kvuli spravnym vypisum; nemazat!!!
            sleep(100);
        } catch (InterruptedException ex) {
            // ok
        }
        if (pc.zamknute) {
            if (vypisPort) {
                pc.vypis("zamknute: "+cisloPortu);
            } else {
                pc.vypis("zamknute");
            }
            
            try {
                ss.close();
            } catch (IOException ex) {
                pc.vypis("Nepodarilo se ukoncit socket.");
            }
            return;
        } else {
            pc.vypis("Posloucham na portu " + cisloPortu);
        }

        try {
            while (true) { // endless loop
                synchronized (ss) { // pri akceptovani vlakna zamykame, po vytvoreni jedeme dal
                    //Krici tady varovani, aby totiz nekdo jinej nechtel synchronisovat ten socket, to by se
                    //ale nemelo stat. Dalo by se to spravit, kdyby promenna ss byla deklarovana az v tyhle metode
                    //nebo kdyby byla final, to by se ale pak zase ten socket nedal vytvorit.
                    Socket s = ss.accept(); // wait for client call
                    Konsole v = new Konsole(s, pc, seznamSpojeni.size(), zamekPocitace); // create another clerk
                    seznamSpojeni.add(v);
                    pc.vypis("Ke konsoli c. " + (seznamSpojeni.size() - 1) + " se prihlasil klient: "
                            + s.getInetAddress().getHostAddress() + ":" + s.getPort());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            ss.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
