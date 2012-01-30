package psimulator.userInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;

/**
 *
 * @author Martin
 */
public class MainWindowGlassPane extends JPanel {

    private Point point;
    private boolean paintEnabled = false;

    public MainWindowGlassPane() {
    }

    public void setPaintEnabled(boolean enabled){
        this.paintEnabled = enabled;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//

        if (paintEnabled) {

            point = new Point(100, 100);
            g.setColor(Color.red);
            g.fillOval(point.x - 10, point.y - 10, 20, 20);
        }
        
    }
}
