package psimulator.userInterface.GlassPane;

import psimulator.userInterface.GlassPane.MainWindowGlassPane;
import javax.swing.JPanel;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelOuterInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class GlassPanelPainter {
    
    public MainWindowGlassPane glassPanel;
    public UserInterfaceMainPanelOuterInterface drawPanelInterface;

    public GlassPanelPainter(MainWindowGlassPane glassPanel, UserInterfaceMainPanelOuterInterface drawPanelInterface) {
        this.glassPanel = glassPanel;
        this.drawPanelInterface = drawPanelInterface;
    }
    
    public void doPaintRedDots(boolean enabled){
        if(enabled){
            glassPanel.setPaintEnabled(enabled);
            
            glassPanel.repaint();
        }else{
            glassPanel.setPaintEnabled(enabled);
            
            glassPanel.repaint();
        }
        
        
    }
    
}
