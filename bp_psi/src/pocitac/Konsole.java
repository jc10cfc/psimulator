/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neiss
 */
public class Konsole extends Thread{
    Socket s;
    AbstractPocitac pocitac;
    ParserPrikazu parser;

    public Konsole(Socket s,AbstractPocitac pc){
        this.s=s;
        pocitac=pc;
        parser=pocitac.parser;

        this.start();
    }


    /**
     * metoda nacita z proudu dokud neprijde \r\n
     * Prevzata z KarelServer
     * @param in
     * @return prvnich 7 znaku nactenyho stringu
     */
    public String ctiRadek(BufferedReader in){
        //String v=in.//vstupni
        //System.out.println("string: "+v);
        String r = ""; //radek nacitany
        char predchozi;
        char z;
        try {
            z = (char) in.read();
        } catch (Exception ex) {
            return null;
        }
        int i=0;
        for (;;){ //v tomhle cyklu se nacita vsechno az do znaku \r\n
            if(i>0){
                predchozi=z;
                try {
                    z=(char)in.read();
                } catch (Exception ex) {
                    return null;
                }
                if (z=='\n' && predchozi=='\r')break;
            }
            if (i<7&&(z!='\r')){
                r=r+z; //do vyslednyho stringu pridavam znak; ale jen prvnich 7 znaku
            }
            //System.out.println(i+": "+z);
            //if(i==1000)break;

            i++;
        }
        return r;
    }

    /**
     * Metoda na posilani do produ vystupniho a zaroven na standartni vystup
     * Prevzata z KarelServer
     * @param out
     * @param s
     * @throws java.io.IOException
     */
    public void posli(OutputStream out,String s) throws IOException{
        out.write((s + "\r\n").getBytes());
//        System.out.println("(socket c. "+cislo+" posilam): "+s);
    }

    @Override
    public void run(){
        OutputStream out = null;
        BufferedReader in = null;
        String radek;
        boolean ukoncit;
        String vystup=null;
//        System.out.println("vlakno c. "+cislo+" startuje");

        try {//vsechno je hozeny do ochrannyho bloku
            in = new BufferedReader(new InputStreamReader(s.getInputStream( ) ) );
            out = s.getOutputStream();
            ukoncit=false;
            posli(out,"220 Tajk je robot Karel");
            while(! ukoncit ) {
                radek = ctiRadek(in);
//                System.out.println("(klient c. "+cislo+" poslal): " + radek);
                posli(out,vystup);
                if(vystup.substring(0,3).equals("221"))ukoncit=true; //uspech
                if(vystup.substring(0,3).equals("530"))ukoncit=true; //havarka
                if(vystup.substring(0,3).equals("550"))ukoncit=true; //nelze zvednout znacku
            }

        } catch ( Exception ex ) {
            System.err.println( "nastala nejaka chyba" );
        } finally {
//            System.out.println("Ukoncuji vlakno a socket c. "+cislo);
            try { //ten socket sice urcite existuje, ale java to jinak nedovoli
                s.close();
            } catch (IOException ex) { ex.printStackTrace();}
        }

    }

}
