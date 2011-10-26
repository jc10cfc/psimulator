package psimulator.userInterface.Editor.MouseActionListeners;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.Components.AbstractComponent;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.BundleOfCables;
import psimulator.userInterface.Editor.Components.Markable;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.Graph;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public abstract class DrawPanelListenerStrategy extends MouseInputAdapter implements MouseWheelListener {

    protected Graph graph;
    protected DrawPanel drawPanel;
    protected MainWindowInterface mainWindow;
    protected UndoManager undoManager;
    protected ZoomManager zoomManager;
    protected DataLayerFacade dataLayer;

    public DrawPanelListenerStrategy(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow, DataLayerFacade dataLayer) {
        super();

        this.drawPanel = drawPanel;
        this.undoManager = undoManager;
        this.mainWindow = mainWindow;
        this.graph = drawPanel.getGraph();
        this.zoomManager = zoomManager;
        this.dataLayer = dataLayer;
    }

    public abstract void deInitialize();

    public abstract void setTool(AbstractTool tool);

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            //System.out.println("Point:"+e.getPoint());
            // scroll down
            if (e.getWheelRotation() == 1) {
                zoomManager.zoomIn(e.getPoint());
                //scroll up    
            } else if (e.getWheelRotation() == -1) {
                zoomManager.zoomOut(e.getPoint());
            }
        }
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseClickedLeft(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            mouseClickedRight(e);
        }
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mousePressedLeft(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            mousePressedRight(e);
        }
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseReleasedLeft(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            mouseReleasedRight(e);
        }
    }

    @Override
    public final void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseDraggedLeft(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            mouseDraggedRight(e);
        }
    }
    
    public void mousePressedRight(MouseEvent e) {
        System.out.println("pressed right");
        
        Markable clickedComponent = getClickedItem(e.getPoint());
        
        
        
    }

    public void mouseClickedLeft(MouseEvent e) {
    }

    public void mouseClickedRight(MouseEvent e) {
    }

    public void mousePressedLeft(MouseEvent e) {
    }

    public void mouseReleasedLeft(MouseEvent e) {
    }

    public void mouseReleasedRight(MouseEvent e) {
    }

    public void mouseDraggedLeft(MouseEvent e) {
    }

    public void mouseDraggedRight(MouseEvent e) {
    }

    /**
     * Return component at point
     * @param point
     * @return component clicked
     */
    protected AbstractComponent getClickedItem(Point point) {
        // search HwComponents
        AbstractComponent clickedComponent = getClickedAbstractHwComponent(point);

        if (clickedComponent != null) {
            return clickedComponent;
        }

        // create small rectangle arround clicked point
        Rectangle r = new Rectangle(point.x - 1, point.y - 1, 3, 3);
        // search cables
        for (BundleOfCables boc : graph.getBundlesOfCables()) {
            clickedComponent = boc.getIntersectingCable(r);
            if(clickedComponent != null){
               return clickedComponent; 
            }
        }

        return clickedComponent;
    }
    
    /**
     * Get clicked AbstractHWComponent at point
     * @param point
     * @return 
     */
    protected AbstractHwComponent getClickedAbstractHwComponent(Point point) {
        AbstractHwComponent clickedComponent = null;

        // search HwComponents
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(point)) {
                clickedComponent = c;
                break;
            }
        }

        return clickedComponent;
    }
}
