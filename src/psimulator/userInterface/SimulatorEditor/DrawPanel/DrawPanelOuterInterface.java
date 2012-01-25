
package psimulator.userInterface.SimulatorEditor.DrawPanel;

import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public abstract class DrawPanelOuterInterface extends JPanel implements DrawPanelToolChangeOuterInterface{
   
    // USED BY EDITOR PANEL
    /**
     * Finds if UNDO can be performed
     * @return true if yes, false if no
     */
    public abstract boolean canUndo();
    /**
     * Finds if REDO can be performed
     * @return true if yes, false if no
     */
    public abstract boolean canRedo();
    /**
     * Calls UNDO operation
     */
    public abstract void undo();
    /**
     * Calls REDO operation
     */
    public abstract void redo();
    /**
     * Finds if ZOOM IN can be performed
     * @return true if yes, false if no
     */
    public abstract boolean canZoomIn();
    /**
     * Finds if ZOOM OUT can be performed
     * @return true if yes, false if no
     */
    public abstract boolean canZoomOut();
    /**
     * Calls ZOOM IN
     */
    public abstract void zoomIn();
    /**
     * Calls ZOOM OUT
     */
    public abstract void zoomOut();
    /**
     * Calls ZOOM RESET
     */
    public abstract void zoomReset();
    /*
     * Adds Observer to ZoomManager.
     */
    public abstract void addObserverToZoomManager(Observer obsrvr);
    /**
     * Gets Action corresponding to DrawPanelAction in parameter
     * @param action
     * @return AbstractAction
     */
    public abstract AbstractAction getAbstractAction(DrawPanelAction action);
    
    
    /**
     * removes graph from draw panel a resets state of draw panel
     * @return 
     */
    public abstract Graph removeGraph();
    /**
     * Sets graph 
     * @param graph 
     */
    public abstract void setGraph(Graph graph);
    
    /**
     * finds whether has graph
     * @return 
     */
    public abstract boolean hasGraph();
    
    public abstract Graph getGraph();
}
