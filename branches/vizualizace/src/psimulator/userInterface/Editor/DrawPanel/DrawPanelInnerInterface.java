package psimulator.userInterface.Editor.DrawPanel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.AbstractAction;
import psimulator.userInterface.Editor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public interface DrawPanelInnerInterface {
    
    /**
     * returns graph
     * @return 
     */
    public GraphOuterInterface getGraph();
    /**
     * Gets image factory
     * @return 
     */
    public AbstractImageFactory getImageFactory();
    /**
     * Gets AbstractAction corresponding to DrawPanelAction
     * @param action
     * @return 
     */
    public abstract AbstractAction getAbstractAction(DrawPanelAction action);
    public void setCursor(Cursor cursor);
    public void repaint();
    /**
     * Sets that cable is being paint
     * @param lineInProgres
     * @param start
     * @param end 
     */
    public void setLineInProgras(boolean lineInProgres, Point start, Point end);
    /**
     * Sets transparent rectangle that is being paint
     * @param rectangleInProgress
     * @param rectangle 
     */
    public void setTransparetnRectangleInProgress(boolean rectangleInProgress, Rectangle rectangle);
    
    /**
     * Fits area of DrawPanel to area of Graph. Call whenever need to make DrawPanel
     * smaller according to Graph. There is minimum DrawPanel size.
     */
    public abstract void doFitToGraphSize();
}
