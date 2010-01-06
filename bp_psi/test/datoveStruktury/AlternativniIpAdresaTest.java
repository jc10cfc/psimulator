/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datoveStruktury;

import javax.print.attribute.standard.MediaSize.NA;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author neiss
 */
public class AlternativniIpAdresaTest {

    public AlternativniIpAdresaTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}


    @Test
    public void testNastavIP() {

        AlternativniIpAdresa ip = new AlternativniIpAdresa();
        String adr;

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        assertEquals(ip.vypisIP(), adr);

        adr = "0.0.0.0";
        ip.nastavIP(adr);
        assertEquals(ip.vypisIP(), adr);

        adr = "255.255.255.255";
        ip.nastavIP(adr);
        assertEquals(ip.vypisIP(), adr);

        adr = "1.1.1.1";
        ip.nastavIP(adr);
        assertEquals(ip.vypisIP(), adr);

        adr = "192.168.1.0";
        ip.nastavIP(adr);
        assertEquals(ip.vypisIP(), adr);
    }


    @Test
    public void testIPMaska(){
        System.out.println("------------------------------------------");
        AlternativniIpAdresa adr=new AlternativniIpAdresa();

        adr.nastavMasku(24);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.255.255.0");

        adr.nastavMasku(25);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.255.255.128");

        adr.nastavMasku(23);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.255.254.0");

        adr.nastavMasku(0);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"0.0.0.0");

        adr.nastavMasku(32);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.255.255.255");

        adr.nastavMasku(7);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"254.0.0.0");
    }

    @Test
    public void testCisloSite(){
        AlternativniIpAdresa ip = new AlternativniIpAdresa();
        String adr;

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        ip.nastavMasku(24);
        assertEquals(ip.vypisCisloSite(), "147.32.125.0");

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        ip.nastavMasku(23);
        assertEquals(ip.vypisCisloSite(), "147.32.124.0");

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        ip.nastavMasku(25);
        assertEquals(ip.vypisCisloSite(), "147.32.125.128");
    }


    @Test
    public void testCisloPocitaceVSiti(){
        AlternativniIpAdresa ip = new AlternativniIpAdresa();
        String adr;

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        ip.nastavMasku(24);
        assertEquals(ip.vypisCisloPocitaceVSiti(), "0.0.0.138");

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        ip.nastavMasku(23);
        assertEquals(ip.vypisCisloPocitaceVSiti(), "0.0.1.138");

        adr = "147.32.125.138";
        ip.nastavIP(adr);
        ip.nastavMasku(25);
        assertEquals(ip.vypisCisloPocitaceVSiti(), "0.0.0.10");
    }


//    @Test
    public void pokus(){
        int a=0;
        a=a|1<<3;
        System.out.println(a);
    }

}