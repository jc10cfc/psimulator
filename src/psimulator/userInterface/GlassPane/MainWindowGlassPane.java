package psimulator.userInterface.GlassPane;

import java.awt.Graphics;
import javax.swing.JPanel;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanel;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MainWindowGlassPane extends JPanel {
    
    private UserInterfaceMainPanel userInterfaceMainPanel;
    
    public MainWindowGlassPane(UserInterfaceMainPanel userInterfaceMainPanel) {
        this.userInterfaceMainPanel = userInterfaceMainPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//

    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}
