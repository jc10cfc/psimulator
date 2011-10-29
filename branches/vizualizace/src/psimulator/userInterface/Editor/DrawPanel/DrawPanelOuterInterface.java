
package psimulator.userInterface.Editor.DrawPanel;

import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import psimulator.userInterface.Editor.DrawPanel.Enums.DrawPanelAction;

/**
 *
 * @author Martin
 */
public abstract class DrawPanelOuterInterface extends JPanel implements DrawPanelToolChangeOuterInterface{
   
    // USED BY EDITOR PANEL
    public abstract boolean canUndo();
    public abstract boolean canRedo();
    public abstract void undo();
    public abstract void redo();
    public abstract boolean canZoomIn();
    public abstract boolean canZoomOut();
    public abstract void zoomIn();
    public abstract void zoomOut();
    public abstract void zoomReset();
    
    public abstract void addObserverToZoomManager(Observer obsrvr);
    public abstract AbstractAction getAbstractAction(DrawPanelAction action);
    //public abstract void doFitToGraphSize();
}
