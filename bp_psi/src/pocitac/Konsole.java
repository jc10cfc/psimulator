/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import prikazy.ParserPrikazu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import prikazy.CiscoParserPrikazu;
import prikazy.LinuxParserPrikazu;
import vyjimky.NeznamyTypPcException;

/**
 *
 * @author neiss
 */
public class Konsole extends Thread{
    private Socket s;
    private AbstractPocitac pocitac;
    private ParserPrikazu parser;
    int cislo; //poradove cislo vlakna, jak je v tom listu, spis pro ladeni
    public String prompt="divnej defaultni promt:~# ";
    boolean ukoncit;
    private OutputStream out;
    private BufferedReader in;
    public boolean vypisPrompt = true; // v ciscu obcas potrebuju zakazat si vypisovani promptu
    public boolean doplnovani = false;

    public Konsole(Socket s,AbstractPocitac pc, int cislo){
        this.s = s;
        pocitac = pc;
        if (pc instanceof LinuxPocitac) {
            parser=new LinuxParserPrikazu(pc, this);
            prompt = pc.jmeno+":~# ";
        } else if (pc instanceof CiscoPocitac) {
            parser = new CiscoParserPrikazu(pc, this);
            prompt = pc.jmeno+">";
        } else throw new NeznamyTypPcException("Neznamy typ PC. Nelze pro nej vytvorit parser.");
        this.cislo=cislo;

        this.start();
    }


    /**
     * metoda nacita z proudu dokud neprijde \r\n
     * Prevzata z KarelServer, ale pak jsem ji stejne celou prepsal.
     * @param in
     * @return celej radek do \r\n jako string. kterej to \r\n uz ale neobsahuje
     */
    public String ctiRadek(BufferedReader in){
        String ret = ""; //radek nacitany
        char z;
        for(;;){
            try {
                z = (char) in.read();

//                System.out.println(z);

                if (z == '\t') {
                    doplnovani = true;
                    break;
                }
                ret+=z;
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(ret.length()>=2){ //tzn. uz je to dost dlouhy na to, aby tam mohlo bejt \r\n
                if ( ret.charAt(ret.length()-2)=='\r' && ret.charAt(ret.length()-1)=='\n'){ //kdyz uz jsou ukoncovaci znaky
                    ret=ret.substring(0, ret.length()-2);
                    break;
                }
            }

        }
        return ret;
    }

    /**
     * Metoda na posilani celeho radku do výstupního proudu. Zaroven vypisuje poslané řetězce na standartni vystup.
     * Prevzata z KarelServer
     * @param out
     * @param ret
     * @throws java.io.IOException
     */
    public void posliRadek(String ret){
        try {
            out.write((ret + "\r\n").getBytes());
            pocitac.vypis("(socket c. " + cislo + " posilam radek): " + ret);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Metoda na posilani do výstupního proudu. Zaroven vypisuje poslané řetězce na standartni vystup.
     * Prevzata z KarelServer
     * @param out
     * @param ret
     * @throws java.io.IOException
     */
    public void posli(String ret){
        try {
            out.write((ret).getBytes());
            pocitac.vypis("(socket c. " + cislo + " posilam): " + ret);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void vypisPrompt() throws IOException{
        out.write((prompt).getBytes());
        //pocitac.vypis("(socket c. "+cislo+" posilam): "+ret);
    }

    /**
     * Hlavni bezici metoda Konsole, bezi v nekonecny smycce a zastavuje se booleanem ukoncit.
     */
    @Override
    public void run(){
        
        String radek;
        
        pocitac.vypis("vlakno c. "+cislo+" startuje");

        try {//vsechno je hozeny do ochrannyho bloku
            in = new BufferedReader(new InputStreamReader(s.getInputStream( ) ) );
            out = s.getOutputStream();
            ukoncit=false;
            while(! ukoncit ) {
                if (vypisPrompt) {
                    vypisPrompt();
                }
                radek = ctiRadek(in);
                pocitac.vypis("(klient c. "+cislo+" poslal): '" + radek+"'");
                //pocitac.vypis("dylka predchoziho radku: "+radek.length());
                //posliRadek(out,radek);
                parser.zpracujRadek(radek);
            }

        } catch ( Exception ex ) {
            ex.printStackTrace();
            pocitac.vypis( "nastala nejaka chyba" );
        } finally {
            pocitac.vypis("Ukoncuji vlakno a socket c. "+cislo);
            try { //ten socket sice urcite existuje, ale java to jinak nedovoli
                s.close();
            } catch (IOException ex) { ex.printStackTrace();}
        }

    }

    public void ukonciSpojeni() {
        ukoncit=true;
    }

}
