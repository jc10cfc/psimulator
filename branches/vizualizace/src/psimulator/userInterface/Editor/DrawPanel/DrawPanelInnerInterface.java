package psimulator.userInterface.Editor.DrawPanel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.AbstractAction;
import psimulator.userInterface.Editor.DrawPanel.Enums.ComponentAction;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public interface DrawPanelInnerInterface {
    
    public AbstractAction getAbstractAction(ComponentAction action);
    
    
    public void updateSize(Point lowerRightCorner);
    
    public Graph getGraph();
    public AbstractImageFactory getImageFactory();
    
    // jPanel related methods
    public void setCursor(Cursor cursor);
    
    // paint related methods
    public void repaint();
    public void setLineInProgras(boolean lineInProgres, Point start, Point end);
    public void setTransparetnRectangleInProgress(boolean rectangleInProgress, Rectangle rectangle);
    
    // used by Grid
    public int getWidth();
    public int getHeight();
    
    
}
