package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haldyr
 */
public class Telnet {

    /**
     * metoda nacita z proudu dokud neprijde \r\n, prevzato z vlastni prace KarelKlient.java
     * @param in vstupni proud na pojeny na server
     * @return string, ve kterem je ulozeno prvnich 100 znaku (pokud prislo drive \r\n, tak je tam znaku mene)
     */
    public static String ctiRadek(BufferedReader in) {
        //String v=in.//vstupni
        //System.out.println("string: "+v);
        String r = ""; //radek nacitany
        char predchozi;
        char z = 'x';
        try {
            z = (char) in.read();
        } catch (Exception e) {
            System.out.println("Chyba: spojeni bylo ztraceno."); // timeout je nastaven na 10s
            System.exit(1);
            //return null;
        }
        int i = 0;
        for (;;) { //v tomhle cyklu se nacita vsechno az do znaku \r\n
            if (i > 0) {
                predchozi = z;
                try {
                    z = (char) in.read();
                } catch (Exception ex) {
                    System.out.println("Chyba: spojeni bylo ztraceno.");
                    System.exit(1);
                    //return null;
                }
                if (predchozi == '\r' && z == '\n') {
                    break;
                }
            }
            if (i < 100) {
                r = r + z; //do vyslednyho stringu pridavam znak; ale jen prvnich 100 znaku (kdyby bylo tajemstvi moc dlouhy
            }
            //System.out.println(i+": "+z);
            //if(i==1000)break;

            i++;
        }
        return r;
    }

    /**
     * Metoda posila string prikaz (VLEVO, KROK, ZVEDNI) na server. Prilepi spravny konec radku podle pouziteho OS,
     * prevzato z vlastni prace KarelKlient.java
     * dale vypisuje na std.out, co posila.
     * @param out
     * @param s
     */
    public static void posli(OutputStream out, String s) {

        try {
            out.write((s + "\r\n").getBytes()); // taky ne

        } catch (IOException ex) {
            Logger.getLogger(Telnet.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("(posilam): " + s);

    }

    public static void main(String[] args) throws IOException {
        String radek = null;
        String prikaz;
        String uv; //uzivatelsky vstup
        Socket echoSocket = null;

        OutputStream out = null;
        BufferedReader in = null;
        String IP = "";
        int PORT = 0;


        if (args.length < 1) {
            System.err.println("Musite si vybrat alespon port!");
            System.exit(1);

        } else if (args.length == 1) { // jeden param = IP, port = 3555 implicitni
            IP = "localhost";
            try {
                PORT = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.err.println("Chyba: neplatny port.");
                System.exit(1);
            }
        } else {
            IP = args[0];
            try {
                PORT = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.err.println("Chyba: neplatny port.");
                System.exit(1);
            }
        }

        System.out.println("IP: " + IP);
        System.out.println("port: " + PORT);

        try {
            echoSocket = new Socket(IP, PORT);
            echoSocket.setSoTimeout(60000); // cekame na read() maximalne 60s, jinak ukoncime spojeni.
            out = echoSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + IP + ":" + PORT);
            System.exit(1);
        } catch (SocketTimeoutException ste) {
            System.err.println("Vyprselo spojeni. " + ste);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for " + IP + ":" + PORT);
            System.exit(1);
        }
    }
}
