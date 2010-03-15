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
import pocitac.SitoveRozhrani;
import static org.junit.Assert.*;

/**
 *
 * @author neiss
 */
public class RoutovaciTabulkaTest {

    public RoutovaciTabulkaTest() {
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

    @Test
    public void prvniTest(){
        assertTrue(new IpAdresa("1.1.1.1").jeVRozsahu(new IpAdresa("1.1.1.0",24)));

        SitoveRozhrani eth0=new SitoveRozhrani("eth0", null, null);
        SitoveRozhrani wlan0=new SitoveRozhrani("wlan0", null, null);
        
        RoutovaciTabulka rt=new RoutovaciTabulka(null);
        assertEquals( 2 , rt.pridejZaznam(new IpAdresa("0.0.0.0",0),new IpAdresa("1.1.1.1")));
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.1.0",24), eth0));
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("0.0.0.0",0),new IpAdresa("1.1.1.1")));
        assertEquals( 1 , rt.pridejZaznam(new IpAdresa("0.0.0.0",0),new IpAdresa("1.1.1.1")));
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25), wlan0) );
        assertEquals( 1 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25), wlan0) );
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("1.1.2.128",25),eth0) );
        assertEquals( 0 , rt.pridejZaznam(new IpAdresa("2.0.0.0",1),wlan0) );

        System.out.println(rt.vypisSeLinuxove());
        
        assertTrue(rt.smazZaznam((new IpAdresa("1.1.1.0",24)), null, null));
        assertTrue(rt.smazZaznam((new IpAdresa("1.1.2.128",25)), null, wlan0));
        assertFalse(rt.smazZaznam((new IpAdresa("1.1.2.128",25)), null, wlan0));
        assertTrue(rt.smazZaznam((new IpAdresa("1.1.2.128",25)), null, eth0));
        assertFalse(rt.smazZaznam((new IpAdresa("0.0.0.0",0)), null, wlan0));
        assertTrue(rt.smazZaznam((new IpAdresa("0.0.0.0",0)), new IpAdresa("1.1.1.1"),null));
        assertFalse(rt.smazZaznam((new IpAdresa("2.0.0.0",1)), new IpAdresa("1.1.1.1"), wlan0));
        assertTrue(rt.smazZaznam((new IpAdresa("2.0.0.0",1)), null, wlan0));
        
        System.out.println(rt.vypisSeLinuxove());
    }

}