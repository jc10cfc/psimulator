/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import datoveStruktury.IpAdresa;
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
public class Pokusnej {

    public Pokusnej() {
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


    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void pokus1(){
        int a=1<<1;
        System.out.println((int)(1<<0));
        System.out.println((int)(1<<1));
        System.out.println((int)(1<<2));
        System.out.println((int)(1<<3));
        System.out.println((int)(1<<31) + ", nejnizsi cislo - jednicka a samy nuly");
        System.out.println((int)((1<<31)+1));
        System.out.println((int)((1<<31)-1));
        
        System.out.println("long:");

        System.out.println((long)(1<<0));
        System.out.println((long)(1<<1));
        System.out.println((long)(1<<2));
        System.out.println((long)(1<<3));
        System.out.println(((long)1<<31) + ", nejnizsi cislo - jednicka a samy nuly");
        System.out.println((long)((1<<31)+1));
        System.out.println((long)((1<<31)-1));
    }

    @Test
    public void pokus2(){
        for(int i=0;i<3;i++){
            System.out.println( ((Math.random()/5)+0.9) + "" );
        }
    }

    /**
     * To jsem si overoval, ze objekt se vola odkazem. My tohle volani prakticky nepouzivame.
     */
    @Test
    public void pokus3(){
        IpAdresa prvni=new IpAdresa("1.1.1.1"); //vytvari se s maskou 255.0.0.0
        zmen(prvni); //tady se maska meni
        assertEquals(prvni.vypisMasku(), "255.255.255.0");//opravdu se zmenila
    }
    private void zmen(IpAdresa keZmeneni){
        keZmeneni.nastavMasku("255.255.255.0");
    }

    @Test
    public void pokusStartsWith(){
        assertTrue("ahoj".startsWith(""));
    }
}