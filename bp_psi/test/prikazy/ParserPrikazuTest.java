/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prikazy;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ParserPrikazuTest {

    public ParserPrikazuTest() {
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
    public void pokusIndexOf() {
        ParserPrikazu par = new LinuxParserPrikazu(null, null);
        par.zpracujRadek("ifconfig wlan0 1.2.3.4/24");
    }

    /**
     * Test of zpracujRadek method, of class ParserPrikazu.
     */
    @Test
    public void testZpracujRadek() {
        System.out.println("zpracujRadek");
        String s = "";
        ParserPrikazu instance = null;
        instance.zpracujRadek(s);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /*
    Method method = ParserPrikazu.getDeclaredMethod(rozsekejLepe, argClasses);
    method.setAccessible(true);
    return method.invoke(targetObject, argObjects);
     */
    //@Test
    public void testZpracujLepe() {
        System.out.println("testZpracujLepe");

        //Konsole k = new Konsole(null, null, 0);
        ParserPrikazu pp = new LinuxParserPrikazu(null, null);

        //pp.setRadek("ip link set wlan0 down");

        /*
        final Field fields[] = ParserPrikazu.class.getFields();
        for (int i = 0; i < fields.length; ++i) {
            System.out.println("Field: " + fields[i]);
        }
         */

        System.out.println("------------");

        final Method[] methods = pp.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            System.out.println("metoda c."+i+":  "+methods[i]);

            if (methods[i].getName().equals("rozsekejLepe")) {
                methods[i].setAccessible(true);
                Object ret = null;
                try {
                    final Object params[] = null;
                    ret = methods[i].invoke(pp, params);
                } catch (Exception ex) {
                    Logger.getLogger(ParserPrikazuTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("ret: "+ ret);
            }
        }
    }
}
