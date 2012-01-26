package psimulator.userInterface.SimulatorEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public abstract class UserInterfaceMainPanelOuterInterface extends JPanel{
    
    public UserInterfaceMainPanelOuterInterface(BorderLayout borderLayout){
        super(borderLayout);
    }
    
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
    
    /**
     * Removes graph and returns it in parameter
     * @return 
     */
    public abstract Graph removeGraph();
    /**
     * Sets graph 
     * @param graph 
     */
    public abstract void setGraph(Graph graph);
    
    public abstract boolean hasGraph();
  
    public abstract Graph getGraph();
    
    public abstract void init();
    
    
    public abstract void doChangeMode(UserInterfaceMainPanelState userInterfaceState);
    
    public abstract UserInterfaceMainPanelState getUserInterfaceState();
    
    public abstract void addNewProjectActionListener(ActionListener listener);
    
    public abstract void addOpenProjectActionListener(ActionListener listener);
}
