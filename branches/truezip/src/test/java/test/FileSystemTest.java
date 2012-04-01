/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import filesystem.ArchiveFileSystem;
import filesystem.FileSystem;
import filesystem.dataStructures.jobs.OutputFileJob;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.junit.*;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class FileSystemTest {
    
    public FileSystemTest() {
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
     public void hello() {
     
         FileSystem filesystem = new ArchiveFileSystem("test.fsm");
         
         filesystem.runOutputFileJob("/home/test/history", new OutputFileJob() {

            @Override
            public int workOnFile(OutputStream output) throws Exception {
                    
                PrintWriter writer = new PrintWriter(output);
                writer.println("fuuujjjii...");
                writer.flush();
                
                return 0;
            }
        });
         
         //filesystem.umount();
     
     
     }
}
