/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prikazy;

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
public class AbstraktniPrikazTest {

    public AbstraktniPrikazTest() {
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
    public void zarovnejTest(){
        String aa="aa";
        assertEquals(AbstraktniPrikaz.zarovnej(aa,4), "aa  ");
        assertEquals(AbstraktniPrikaz.zarovnej(aa,10), "aa        ");
        assertEquals(AbstraktniPrikaz.zarovnej(aa,0), "aa");
        assertEquals(AbstraktniPrikaz.zarovnej(aa,-2), "aa");
        assertEquals(aa, "aa");
    }

    @Test
    public void testRozlozNaMocniny2(){
        assertEquals(AbstraktniPrikaz.rozlozNaMocniny2(100), "4 + 32 + 64");
        assertEquals(AbstraktniPrikaz.rozlozNaMocniny2(9), "1 + 8");
    }

    @Test
    public void testLog2(){
        assertEquals(0, AbstraktniPrikaz.log2(1));
        assertEquals(1, AbstraktniPrikaz.log2(2));
        assertEquals(2, AbstraktniPrikaz.log2(4));
        assertEquals(4, AbstraktniPrikaz.log2(16));
        assertEquals(8, AbstraktniPrikaz.log2(256));
        assertEquals(11, AbstraktniPrikaz.log2(2048));
    }

    

}