package Main;

import java.util.Properties;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haldyr
 */
@Deprecated
public class Konfigurak {

    public static void nactiTextak() {
        Properties p = new Properties();

        FileInputStream in;
        try {
            in = new FileInputStream("textak.conf");
            p.load(in);
        } catch (Exception e) {
            Logger.getLogger(Konfigurak.class.getName()).log(Level.SEVERE, null, e);
        }

        System.out.println("Velikost: " + p.size());

        System.out.println("-start-");
        for (Object o : p.values()) {
            System.out.println(o.toString());
        }
        System.out.println("-konec-");
    }

    public static void nactiXML() {
        
    }

    public static void main(String[] args) {
        nactiTextak();

    }
}
