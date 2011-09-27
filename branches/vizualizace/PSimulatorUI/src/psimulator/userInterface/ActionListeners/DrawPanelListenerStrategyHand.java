package psimulator.userInterface.ActionListeners;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.Cable;
import psimulator.userInterface.Editor.Components.Markable;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.UndoCommands.UndoableMoveComponent;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public class DrawPanelListenerStrategyHand extends DrawPanelListenerStrategy {

    protected int differenceX;
    protected int differenceY;
    protected AbstractHwComponent draggedComponent;
    protected Point originalDefaultLocation;
    protected Point newDefaultLocation;
    protected List<Markable> markedComponents = new ArrayList<Markable>();
    
    protected Point startPointOfMarking;

    public DrawPanelListenerStrategyHand(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow) {
        super(drawPanel, undoManager, zoomManager, mainWindow);
    }

    /**
     * Marks component at Point clicked if any
     * IF control pressed, than add component to marked components
     * IF control not pressed, mark only the clicked component
     * @param e 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // get clicked component
        Markable clickedComponent = getClickedItem(e.getPoint());

        // if nothing clicked
        if (clickedComponent == null) {
            System.out.println("Graph - " + graph.getHwComponents().size() + " components and " + graph.getCables().size() + " cables");
            if (e.isControlDown()) {
                // do nothing
            } else {
                // unmark all marked components and clear marked components
                setMarkedComponentsUnmarked();
                drawPanel.repaint();
            }
            // end
            return;
        }

        //-- if something clicked --//

        // if control down
        if (e.isControlDown()) {
            // if component marked, remove from marked components
            if (clickedComponent.isMarked()) {
                //markedComponents.remove(clickedComponent);
                doMarkHwComponentAndItsCables(false, clickedComponent);
            } else {
                // if not marked, add to marked components
                doMarkHwComponentAndItsCables(true, clickedComponent);
            }
            //repaint
            drawPanel.repaint();
            return;
        }

        // if control not down

        // if is clicked component marked 
        if (clickedComponent.isMarked()) {
            // if there are more marked components, mark only clicked one
            if (markedComponents.size() >= 2) {
                setMarkedComponentsUnmarked();
                doMarkHwComponentAndItsCables(true, clickedComponent);
            } else { // if only one marked component remove marking 
                // remove from marked components and unmark it
                doMarkHwComponentAndItsCables(false, clickedComponent);
            }
        } else {
            // if clicked component not marked
            // unmark all components
            setMarkedComponentsUnmarked();
            // set clicked component marked and add to marked components
            doMarkHwComponentAndItsCables(true, clickedComponent);
        }
        //repaint
        drawPanel.repaint();
        return;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // try if start dragging of some component
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(e.getPoint())) {
                draggedComponent = c;

                differenceX = e.getX() - c.getX();
                differenceY = e.getY() - c.getY();

                // set original location for undoable edit
                originalDefaultLocation = new Point(zoomManager.doScaleToDefault(e.getX() - differenceX),
                        zoomManager.doScaleToDefault(e.getY() - differenceY));
                return;
            }
        }
        
        // start painting transparent rectange for marking more components
        startPointOfMarking = e.getPoint();
   
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // painting transparent rectange for marking more components
        if (draggedComponent == null) {
            drawPanel.setTransparetnRectangleInProgress(true, startPointOfMarking, e.getPoint());
            
            drawPanel.repaint();
            
            return;
        }

        // drag of component
        Point p = new Point(e.getX() - differenceX, e.getY() - differenceY);

        draggedComponent.setLocation(p);

        drawPanel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // dragging components
        if (draggedComponent != null) {
            // new default location for undoable edit
            newDefaultLocation = new Point(zoomManager.doScaleToDefault(e.getX() - differenceX),
                    zoomManager.doScaleToDefault(e.getY() - differenceY));

            // if was draggedComponent moved
            if (!newDefaultLocation.equals(originalDefaultLocation)) {
                // add edit to undo manager
                undoManager.undoableEditHappened(new UndoableEditEvent(this,
                        new UndoableMoveComponent(draggedComponent, originalDefaultLocation, newDefaultLocation, drawPanel)));

                // inform drawPanel about position change
                drawPanel.updateSize(draggedComponent.getLowerRightCornerLocation());

                // update Undo and Redo buttons
                mainWindow.updateUndoRedoButtons();

                /*
                System.out.println("Original default loc: x="+originalDefaultLocation.x+", y="+originalDefaultLocation.y+". "
                + "New default loc: x="+newDefaultLocation.x +", y="+newDefaultLocation.y+".");*/
            }
            draggedComponent = null;
            return;
        }

        // painting transparent rectange for marking more components
        drawPanel.setTransparetnRectangleInProgress(false, null, null);
        drawPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(e.getPoint())) {
                drawPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return;
            }
        }

        Rectangle r = new Rectangle(e.getX() - 1, e.getY() - 1, 3, 3);
        for (Cable c : graph.getCables()) {
            if (c.intersects(r)) {
                drawPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return;
            }
        }

        drawPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    
    /**
     * If component is cable, then mark or unmark it and add/remove it to marked components.
     * If component is AbstractHwComponent, than mark/unmark it and its all cables and add/remove
     * it to marked components.
     * @param marked True if mark, false if unmark.
     * @param component Component that needs to be marked.
     */
    private void doMarkHwComponentAndItsCables(boolean marked, Markable component) {
        // if component is isntance of AbstractHwComponent
        if (component instanceof AbstractHwComponent) {
            component.setMarked(marked);
            if (marked) {
                markedComponents.add(component);
            } else {
                markedComponents.remove(component);
            }

            // set marked to all its cables
            List<Cable> cables = ((AbstractHwComponent)component).getCables();
            for (Cable c : cables) {
                if (marked) {
                    c.setMarked(marked);
                    markedComponents.add(c);
                } else {
                    // if both ends of calbe not marked, than unmark cable
                    if(!c.getComponent1().isMarked() && !c.getComponent2().isMarked()){
                      c.setMarked(marked);
                      markedComponents.remove(c);  
                    }
                }
            }
        }else{
            // component is cable
            component.setMarked(marked);
            if (marked) {
                markedComponents.add(component);
            } else {
                markedComponents.remove(component);
            }
            
        }
    }

    /**
     * Return component at point
     * @param point
     * @return component clicked
     */
    private Markable getClickedItem(Point point) {
        Markable clickedComponent = null;

        // search HwComponents
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(point)) {
                clickedComponent = c;
                return clickedComponent;
            }
        }

        // create small rectangle arround clicked point
        Rectangle r = new Rectangle(point.x - 1, point.y - 1, 3, 3);
        // search cables
        for (Cable c : graph.getCables()) {
            if (c.intersects(r)) {
                clickedComponent = c;
                return clickedComponent;
            }
        }
        return clickedComponent;
    }

    /**
     * sets all Markable components in markedComponents to marked(false) and
     * clears markedComponents list
     */
    private void setMarkedComponentsUnmarked() {
        for (Markable m : markedComponents) {
            m.setMarked(false);
        }
        markedComponents.clear();
    }
}
