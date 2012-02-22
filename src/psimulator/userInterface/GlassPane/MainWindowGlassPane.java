package psimulator.userInterface.GlassPane;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanel;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MainWindowGlassPane extends JPanel {

    private Point point;
    private boolean paintEnabled = false;
    private UserInterfaceMainPanel userInterfaceMainPanel;
    private Graph graph;

    public MainWindowGlassPane(UserInterfaceMainPanel userInterfaceMainPanel) {
        this.userInterfaceMainPanel = userInterfaceMainPanel;
    }

    public void setPaintEnabled(boolean enabled) {
        this.paintEnabled = enabled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//

        /*
        
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // buffer image and repaint only on change

        point = new Point(100, 100);
        g2.setColor(Color.red);
        g2.fillOval(point.x - 10, point.y - 10, 20, 20);


        this.graph = userInterfaceMainPanel.getGraph();
        if (graph == null) {
            return;
        }
        
        int w = graph.getWidth();
        int h = graph.getHeight();
        
        if(w == 0 || h == 0){
            return;
        }
        */
        
        
        /*
        BufferedImage graphImage = new BufferedImage(w/2, h/2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2bi = graphImage.createGraphics();
        
        g2bi.scale(1/2, 1/2);
        graph.paint(g2bi);*/
                
        /*
        System.out.println("");

        

        int desiredW = 200;
        int desiredH = 200;

        //g2.scale(1/2, 1/2);

        graph.paint(g2);




        g2.dispose();
        */

    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}
