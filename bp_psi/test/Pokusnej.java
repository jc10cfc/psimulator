/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

    // TODO add test methods here.
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

}