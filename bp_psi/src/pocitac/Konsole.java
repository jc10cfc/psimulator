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
import java.io.StringReader;
import java.net.Socket;
import prikazy.cisco.CiscoParserPrikazu;
import prikazy.linux.LinuxParserPrikazu;
import vyjimky.ChybaSpojeniException;
import vyjimky.NeznamyTypPcException;
import static prikazy.AbstraktniPrikaz.*;

/**
 *
 * @author neiss
 */
public class Konsole extends Thread {
    boolean ladiciVypisovani = false;

    private Socket s;
    private AbstraktniPocitac pocitac;
    private ParserPrikazu parser;
    int cislo; //poradove cislo vlakna, jak je v tom listu, spis pro ladeni
    public String prompt="divnej defaultni promt:~# ";
    private boolean ukoncit;
    private OutputStream out;
    private BufferedReader in;
    public boolean vypisPrompt = true; // v ciscu obcas potrebuju zakazat si vypisovani promptu
    public boolean doplnovani = false;

    public Konsole(Socket s,AbstraktniPocitac pc, int cislo){
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
    public String ctiRadek(BufferedReader in) throws ChybaSpojeniException{
        String ret = ""; //radek nacitany
        char z;
        int citac = 0;

        for(;;){
            try {
                
                citac++;
                if (citac > 200) { // pocitam, ze zadny prikaz nebude delsi, mozna muzem i snizit
                    throw new ChybaSpojeniException("Konsole cislo "+cislo+", metoda ctiRadek, " +
                            "klient poslal moc znaku.");
                }

                z = (char) in.read();
                ret+=z;
                                
            } catch (Exception ex) {
                //ex.printStackTrace();
                throw new ChybaSpojeniException("Konsole cislo "+cislo+", metoda ctiRadek, nastala chyba.");
            }
            if(ret.length()>=2){ //tzn. uz je to dost dlouhy na to, aby tam mohlo bejt \r\n
                if ( ret.charAt(ret.length()-2)=='\r' && ret.charAt(ret.length()-1)=='\n'){ //kdyz uz jsou ukoncovaci znaky
                    ret=ret.substring(0, ret.length()-2);
                    break;
                }
            }
            if(ladiciVypisovani){
                pocitac.vypis("vypisuju po znaku: "+ret);
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
    public void posliRadek(String ret) throws ChybaSpojeniException{
        try {
            out.write((ret + "\r\n").getBytes());
            pocitac.vypis("(socket c. " + cislo + " posilam radek): " + ret);
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Konsole cislo "+cislo+", metoda posliRadek, nastala chyba.");
        }
    }

    /**
     * Metoda na posilani do výstupního proudu. Zaroven vypisuje poslané řetězce na standartni vystup.
     * Prevzata z KarelServer
     * @param out
     * @param ret
     * @throws java.io.IOException
     */
    public void posli(String ret) throws ChybaSpojeniException{
        try {
            out.write((ret).getBytes());
            pocitac.vypis("(socket c. " + cislo + " posilam): " + ret);
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Konsole cislo "+cislo+", metoda posli, nastala chyba.");
        }
    }

    /**
     * Posila po radcich se zpozdenim v ms. Vyuziva metodu posliRadek().
     * @param s, retezec, ktery ma posilat
     * @param cekej, prodleva v ms mezi jednotlivejma radkama
     * @author haldyr
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
            throw new ChybaSpojeniException("Konsole cislo "+cislo+", metoda posliPoRadcich, nastala chyba.");
        }
    }

    /**
     * Vypise prompt na prikazou radku.
     * @throws IOException
     */
    public void vypisPrompt() throws ChybaSpojeniException{
        try{
            out.write((prompt).getBytes());
            //pocitac.vypis("(socket c. "+cislo+" posilam): "+prompt);
        } catch (IOException ex) {
            //ex.printStackTrace();
            throw new ChybaSpojeniException("Konsole cislo "+cislo+", metoda vypisPrompt, nastala chyba.");
        }
    }

    /**
     * Hlavni bezici metoda Konsole, bezi v nekonecny smycce a zastavuje se booleanem ukoncit.
     */
    @Override
    public void run() {

        String radek;

        try {
            pocitac.vypis("vlakno c. " + cislo + " startuje");
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = s.getOutputStream();
            } catch (IOException ex) {
                throw new ChybaSpojeniException("Konsole cislo " + cislo + ", metoda run, nastala chyba. " +
                        "Nepodarilo se inicialisovat vstupni nebo vystupni proud.");
            }
            ukoncit = false;
            //posliBajtyTelnetu();
            while (!ukoncit) {
                if (vypisPrompt) {
                    vypisPrompt();
                }
                radek = ctiRadek(in);
                pocitac.vypis("(klient c. " + cislo + " poslal): '" + radek + "'");
                //pocitac.vypis("dylka predchoziho radku: "+radek.length());
                //posliRadek(out,radek);
                parser.zpracujRadek(radek);
            }

        } catch (ChybaSpojeniException ex) {
            ex.printStackTrace();
            pocitac.vypis("Nastala chyba v komunikaci: "+ex.getMessage());
        } finally {
            pocitac.vypis("Ukoncuji vlakno a socket c. " + cislo);
            try { //ten socket sice urcite existuje, ale java to jinak nedovoli
                s.close();
            } catch (IOException ex) {
                pocitac.vypis("Konsole cislo " + cislo + ", metoda run: Spojeni se nepodarilo " +
                        "korektne uzavrit.");
            }
        }

    }

    /**
     * Ukonci spojeni.
     */
    public void ukonciSpojeni() {
        pocitac.vypis("Zavolala se metoda ukonci.");
        ukoncit=true;
    }

}
