package psimulator.userInterface;

import java.awt.Component;
import javax.swing.JRootPane;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;

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
}
