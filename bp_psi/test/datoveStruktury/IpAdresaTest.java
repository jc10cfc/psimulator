/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datoveStruktury;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author haldyr
 */
public class IpAdresaTest {

    public IpAdresaTest() {
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

    private void vypisIP(IpAdresa adr) {
        int[]ip = adr.dejIP();
        for (int i = 0; i < 4; i++) {
            System.out.print(ip[i]+" ");
        }
        System.out.println("");
    }

    /**
     * Test of nastavIP method, of class IpAdresa.
     */
    @Test
    public void testNastavIP() {
        
        String adr = "147.32.125.138";
        IpAdresa ip = new IpAdresa();
        ip.nastavIP(adr);
        vypisIP(ip);
    }


    @Test
    public void testIPMaska(){
        System.out.println("------------------------------------------");
        IpAdresa adr=new IpAdresa();

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
        
        adr.nastavMasku(8);
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.0.0.0");
    }

}