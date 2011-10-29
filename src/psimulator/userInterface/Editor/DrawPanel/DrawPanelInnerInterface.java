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
    
    // getters
    public GraphOuterInterface getGraph();
    public AbstractImageFactory getImageFactory();
    public abstract AbstractAction getAbstractAction(DrawPanelAction action);
    
    // jPanel related methods
    public void setCursor(Cursor cursor);
    
    // paint related methods
    public void repaint();
    public void setLineInProgras(boolean lineInProgres, Point start, Point end);
    public void setTransparetnRectangleInProgress(boolean rectangleInProgress, Rectangle rectangle);
    
    public abstract void doFitToGraphSize();
}
