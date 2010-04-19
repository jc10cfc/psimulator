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
import vyjimky.SpatnaMaskaException;
import static org.junit.Assert.*;

/**
 *
 * @author neiss
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

    @Test
    public void testNastavIP() {

        IpAdresa ip = new IpAdresa("1.1.1.1");
        String adr;

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        assertEquals(ip.vypisAdresu(), adr);

        adr = "0.0.0.0";
        ip = new IpAdresa(adr);
        assertEquals(ip.vypisAdresu(), adr);

        adr = "1.1.1.1";
        ip = new IpAdresa(adr);
        assertEquals(ip.vypisAdresu(), adr);

        adr = "192.168.1.0";
        ip = new IpAdresa(adr);
        assertEquals(ip.vypisAdresu(), adr);
    }


    @Test
    public void testNastavMasku(){
        System.out.println("------------------------------------------");
        IpAdresa adr = new IpAdresa("1.1.1.1");

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
    public void testIPMaskaString(){
        System.out.println("------------------------------------------");
        IpAdresa adr=new IpAdresa("1.1.1.1");

        adr.nastavMasku("255.255.255.0");
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.255.255.0");

        adr.nastavMasku("255.255.255.128");
        System.out.println(adr.vypisMasku());
        assertEquals(adr.vypisMasku(),"255.255.255.128");

        try{
            adr.nastavMasku("43.23.234.43");
            fail();
        } catch (SpatnaMaskaException ex){}
    }

    @Test
    public void testBroadcast(){
        System.out.println("------------------------------------------");
        IpAdresa adr=new IpAdresa("192.168.1.0",24);
        assertEquals(adr.vypisBroadcast(),"192.168.1.255");

        adr.nastavMasku(0); //vsechno je cislo pocitace -> cislo site je 0.0.0.0/32
        assertEquals(adr.vypisBroadcast(),"255.255.255.255");

        adr.nastavMasku(32); //vsechno je cislo site -> cislo site je 192.168.1.0/32
        assertEquals(adr.vypisBroadcast(),"192.168.1.0");

        adr.nastavMasku(30); //  cislo site je 192.168.1.0/30
        assertEquals(adr.vypisBroadcast(),"192.168.1.3");
    }

    @Test
    public void testCisloSite(){
        IpAdresa ip = new IpAdresa("1.1.1.1");
        String adr;

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(24);
        assertEquals(ip.vypisCisloSite(), "147.32.125.0");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(23);
        assertEquals(ip.vypisCisloSite(), "147.32.124.0");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(25);
        assertEquals(ip.vypisCisloSite(), "147.32.125.128");
    }


    @Test
    public void testCisloPocitaceVSiti(){
        IpAdresa ip = new IpAdresa("1.1.1.1");
        String adr;

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(24);
        assertEquals(ip.vypisCisloPocitaceVSiti(), "0.0.0.138");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(23);
        assertEquals(ip.vypisCisloPocitaceVSiti(), "0.0.1.138");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(25);
        assertEquals(ip.vypisCisloPocitaceVSiti(), "0.0.0.10");
    }

    @Test
    public void testAdresaSMaskou(){
        IpAdresa ip = new IpAdresa("1.1.1.1");
        String adr;

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(24);
        assertEquals(ip.vypisAdresuSMaskou(), "147.32.125.138/24");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(23);
        assertEquals(ip.vypisAdresuSMaskou(), "147.32.125.138/23");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(25);
        assertEquals(ip.vypisAdresuSMaskou(), "147.32.125.138/25");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(0);
        assertEquals(ip.vypisAdresuSMaskou(), "147.32.125.138/0");

        adr = "147.32.125.138";
        ip = new IpAdresa(adr);
        ip.nastavMasku(32);
        assertEquals(ip.vypisAdresuSMaskou(), "147.32.125.138/32");
    }

    @Test
    public void testJeNadsiti(){
        IpAdresa adr =new IpAdresa("0.0.0.0",0);
        assertTrue(adr.jeNadsiti(new IpAdresa("1.1.1.1",24)));
        assertTrue(adr.jeNadsiti(new IpAdresa("0.0.0.0",0)));

        adr =new IpAdresa("89.190.94.1",24);
        assertTrue(adr.jeNadsiti(new IpAdresa("89.190.94.128",25)));
        assertTrue(adr.jeNadsiti(new IpAdresa("89.190.94.0",8)));
        assertFalse(adr.jeNadsiti(new IpAdresa("89.190.93.0",8)));
        assertTrue(adr.jeNadsiti(new IpAdresa("89.190.94.1",24)));
    }

    @Test
    public void testJeVRozsahu(){
        IpAdresa adr =new IpAdresa("0.0.0.0",0);
        assertTrue(new IpAdresa("1.1.1.1",24).jeVRozsahu(adr));
        assertTrue(new IpAdresa("0.0.0.0",0).jeVRozsahu(adr));

        adr =new IpAdresa("89.190.94.1",24);
        assertTrue(new IpAdresa("89.190.94.128",25).jeVRozsahu(adr));
        assertTrue(new IpAdresa("89.190.94.0",8).jeVRozsahu(adr));
        assertFalse(new IpAdresa("89.190.222.0",8).jeVRozsahu(adr));
        assertTrue(new IpAdresa("89.190.94.1",24).jeVRozsahu(adr));

        adr =new IpAdresa("192.168.2.0",24);
        assertFalse(new IpAdresa("192.168.10.43",24).jeVRozsahu(adr));
    }

    @Test //jen takovej neuplnej test
    public void vytvareniIp(){
        IpAdresa ip;
        ip=new IpAdresa("1.1.1.1");
        ip=new IpAdresa("19.255.255.0");
        ip=new IpAdresa("10.0.0.0");
        ip=new IpAdresa("192.168.1.1");
        ip=new IpAdresa("255.0.0.0"); // i tohle musi projit (napriklad v LinuxRoute)
    }

    @Test
    public void testJeSpravnaIP(){
        assertTrue(IpAdresa.jeSpravnaIP("255.255.255.0", true));
        assertTrue(IpAdresa.jeSpravnaIP("0.0.0.0", true));
        assertTrue(IpAdresa.jeSpravnaIP("255.255.255.255", true));
        assertTrue(IpAdresa.jeSpravnaIP("255.255.255.128", true));
        assertTrue(IpAdresa.jeSpravnaIP("192.0.0.0", true));
        assertFalse(IpAdresa.jeSpravnaIP("255.255.255.3", true));
        assertFalse(IpAdresa.jeSpravnaIP("255.255.255.32", true));
        assertFalse(IpAdresa.jeSpravnaIP("255.255.255.129", true));
        assertFalse(IpAdresa.jeSpravnaIP("255.0.155.0", true));
    }

    @Test
    public void testVratOJednickuVetsi(){
        IpAdresa ip= new IpAdresa("1.1.1.1");
        assertEquals("1.1.1.2", IpAdresa.vratOJednaVetsi(ip).vypisAdresu());
        ip= new IpAdresa("240.0.0.0");
        assertEquals("240.0.0.1", IpAdresa.vratOJednaVetsi(ip).vypisAdresu());
        ip= new IpAdresa("240.0.0.255");
        assertEquals("240.0.1.0", IpAdresa.vratOJednaVetsi(ip).vypisAdresu());
    }



//    @Test
    public void pokus(){
        int a=0;
        a=a|1<<3;
        System.out.println(a);
    }

}