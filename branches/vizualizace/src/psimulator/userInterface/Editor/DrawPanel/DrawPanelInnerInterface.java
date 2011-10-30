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
     * Sets that cable is being paint. Actual zoom in end poitn is used because when zooming, the end
     * point has to be at the mouse position
     * @param lineInProgres
     * @param start - point in defaultZoom
     * @param end - point in actualZoom
     */
    public void setLineInProgras(boolean lineInProgres, Point startInDefaultZoom, Point endInActualZoom);
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
    
    /**
     * Sets default tool in EditorPanels toolBar
     */
    public void doSetDefaultToolInEditorToolBar();
}
