/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Nakonec jsem sem tu tridu pro tu sitovou komunikaci stejne hodil. Bude to prehlednejsi i vzhledem k tem vlaknum
 * a tak.
 * @author neiss
 */
public class Komunikace extends Thread
{

    private ServerSocket ss; //socket, kterej posloucha
    int cisloPortu;
    List<Konsole> seznamSpojeni=new ArrayList();
    AbstractPocitac pc;//odkaz na pocitac

    public Komunikace(int cisloPortu, AbstractPocitac pc){
        this.cisloPortu=cisloPortu;
        this.pc=pc;
        this.start(); //tohle casem spusti metodu run()
        
    }

    @Override
    public void run(){
        try { //pokus o vytvareni socketu
            ss = new ServerSocket(cisloPortu);
        } catch (IOException e) {
            System.err.println("Nemuzu poslouchat na portu "+cisloPortu+".");
            System.exit(1);
        }

        try {
            while (true) { // endless loop
                synchronized (ss) { // pri akceptovani vlakna zamykame, po vytvoreni jedeme dal
                        //Krici tady varovani, aby totiz nekdo jinej nechtel synchronisovat ten socket, to by se
                        //ale nemelo stat. Dalo by se to spravit, kdyby promenna ss byla deklarovana az v tyhle metode
                        //nebo kdyby byla final, to by se ale pak zase ten socket nedal vytvorit.
                    Socket s = ss.accept(); // wait for client call
                    Konsole v = new Konsole(s,pc,seznamSpojeni.size()); // create another clerk
                    seznamSpojeni.add(v);
                    System.out.println("akceptoval jsem vlakno c. " + (seznamSpojeni.size()-1) + " "
                            + s.getInetAddress() + ":" + s.getPort());
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
