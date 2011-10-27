package psimulator.userInterface.Editor.DrawPanel.MouseActionListeners;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.BundleOfCables;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel.Components.Markable;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import psimulator.userInterface.Editor.DrawPanel.SwingComponents.PopupMenuAbstractHwComponent;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.Editor.DrawPanel.ZoomManager;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public abstract class DrawPanelListenerStrategy extends MouseInputAdapter implements MouseWheelListener {

    protected Graph graph;
    protected DrawPanelInnerInterface drawPanel;
    protected MainWindowInnerInterface mainWindow;
    protected UndoManager undoManager;
    protected ZoomManager zoomManager;
    protected DataLayerFacade dataLayer;
    
    /**
     * list for all marked components
     */
    protected List<Markable> markedComponents = new ArrayList<Markable>();

    public DrawPanelListenerStrategy(DrawPanelInnerInterface drawPanel, UndoManager undoManager, ZoomManager zoomManager, 
            MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
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
        // get clicked component
        AbstractHwComponent clickedComponent = getClickedAbstractHwComponent(e.getPoint());
        
        
        // if nothing right clicked, return
        if(clickedComponent == null){
            return;
        }
        
        doMarkHwComponentAndItsCables(true,clickedComponent);
        
        
        PopupMenuAbstractHwComponent popup = new PopupMenuAbstractHwComponent(clickedComponent, drawPanel, dataLayer);
        
        popup.show(drawPanel, e.getPoint().x, e.getPoint().y);
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
    
    /**
     * If component is cable, then mark or unmark it and add/remove it to marked components.
     * If component is AbstractHwComponent, than mark/unmark it and its all cables and add/remove
     * it to marked components.
     * @param marked True if mark, false if unmark.
     * @param component Component that needs to be marked.
     */
    protected void doMarkHwComponentAndItsCables(boolean marked, Markable component) {
        // if component is isntance of AbstractHwComponent
        if (component instanceof AbstractHwComponent) {
            component.setMarked(marked);
            if (marked) {
                markedComponents.add(component);
            } else {
                markedComponents.remove(component);
            }

            // set marked to all its cables
            List<BundleOfCables> bundlesOfCables = ((AbstractHwComponent) component).getBundleOfCableses();

            for (BundleOfCables boc : bundlesOfCables) {
                for (Cable c : boc.getCables()) {
                    if (marked) {
                        c.setMarked(marked);
                        markedComponents.add(c);
                    } else {
                        // if both ends of calbe not marked, than unmark cable
                        if (!boc.getComponent1().isMarked() && !boc.getComponent2().isMarked()) {
                            c.setMarked(marked);
                            markedComponents.remove(c);
                        }
                    }
                }
            }
        } else {
            // component is cable
            component.setMarked(marked);
            if (marked) {
                markedComponents.add(component);
            } else {
                markedComponents.remove(component);
            }

        }
    }
}
