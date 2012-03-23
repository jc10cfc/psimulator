package psimulator.userInterface;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import shared.SimulatorEvents.SerializedComponents.SimulatorEventsWrapper;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface MainWindowInnerInterface {
    // used by view:
    /**
     * Updates Undo and Redo APP buttons according to undo manager
     */
    public void updateUndoRedoButtons();
    /**
     * Updates ZoomIn and ZoomOut APP buttons according to zoom manager
     */
    public void updateZoomButtons();
    /**
     * Updates icons in toolbar according to size 
     * @param size Size to update to
     */
    public void updateToolBarIconsSize(ToolbarIconSizeEnum size);
    
    public JRootPane getRootPane();

    public Component getMainWindowComponent();
    
    public void saveEventsAction(SimulatorEventsWrapper simulatorEventsWrapper);
    
    public SimulatorEventsWrapper loadEventsAction();
    
    // telnet windows add/remove
//    public void removeTelnetWindow(Integer key);
//    public void addTelnetWindow(Integer key, JFrame frame);
//    public boolean hasTelnetWindow(Integer key);
//    public JFrame getTelnetWindow(Integer key);
}
