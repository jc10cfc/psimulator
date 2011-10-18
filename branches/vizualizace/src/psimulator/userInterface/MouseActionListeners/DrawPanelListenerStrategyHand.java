package psimulator.userInterface.MouseActionListeners;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
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

    /**
     * difference between upper left corner of components and mouse position the component clicked. X
     */
    protected int differenceXdefaultZoom;
    /**
     * difference between upper left corner of components and mouse position the component clicked. Y
     */
    protected int differenceYdefaultZoom;
    /**
     * components that are dragged
     */
    protected List<AbstractHwComponent> draggedComponents;
    /**
     * original position, left-upper point in default zoom of dragged components
     */
    protected Point draggedComponentsOriginalPoint;
    /**
     * list for all marked components
     */
    protected List<Markable> markedComponents = new ArrayList<Markable>();
    /**
     * point in actual zoom where started components marking
     */
    protected Point startPointOfMarkingTransparentRectangle;

    public DrawPanelListenerStrategyHand(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow, DataLayerFacade dataLayer) {
        super(drawPanel, undoManager, zoomManager, mainWindow, dataLayer);
    }
    
    @Override
    public void deInitialize() {
        setMarkedComponentsUnmarked();
        drawPanel.repaint();
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
            if (getMarkedHwComponentsCount() > 1) {
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
        boolean addToDragAllMarkedComponents = false;
        if (draggedComponents == null) {
            draggedComponents = new ArrayList<AbstractHwComponent>();
        } else {
            draggedComponents.clear();
        }

        // start painting transparent rectange for marking more components
        startPointOfMarkingTransparentRectangle = e.getPoint();

        Markable clickedComponent = getClickedHwComponent(e.getPoint());

        if (clickedComponent == null) {
            return;
        }

        // try if start dragging of some component
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(e.getPoint())) {
                // if c is marked, we will drag all marked components
                if (c.isMarked()) {
                    addToDragAllMarkedComponents = true;
                }
                // add this component to dragged components
                draggedComponents.add(c);

                // end for
                break;
            }
        }

        // if we have to add all marked components to dragged components
        if (addToDragAllMarkedComponents) {
            // add all marked components to dragged components
            for (AbstractHwComponent c : graph.getMarkedHwComponentsCopy()) {
                if (!draggedComponents.contains(c)) {
                    draggedComponents.add(c);
                }
            }
        }

        if (!draggedComponents.isEmpty()) {
            // get upper left corner of all components
            draggedComponentsOriginalPoint = graph.getUpperLeftBound(draggedComponents);
            // scale properly
            draggedComponentsOriginalPoint = zoomManager.doScaleToDefault(draggedComponentsOriginalPoint);

            // count difference between upper left corner of component and clicked point on component
            differenceXdefaultZoom = zoomManager.doScaleToDefault(e.getX()) - draggedComponentsOriginalPoint.x;
            differenceYdefaultZoom = zoomManager.doScaleToDefault(e.getY()) - draggedComponentsOriginalPoint.y;
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // painting transparent rectange for marking more components
        if (draggedComponents.isEmpty()) {
            // calculate width an height
            int width = startPointOfMarkingTransparentRectangle.x - e.getPoint().x;
            int height = startPointOfMarkingTransparentRectangle.y - e.getPoint().y;

            // calculate x and y position
            int x = width < 0 ? startPointOfMarkingTransparentRectangle.x : e.getPoint().x;
            int y = height < 0 ? startPointOfMarkingTransparentRectangle.y : e.getPoint().y;

            // create rectangle
            Rectangle rect = new Rectangle(x, y, Math.abs(width), Math.abs(height));

            // set paint transparent rect
            drawPanel.setTransparetnRectangleInProgress(true, rect);

            // mark components in rectangle
            doMarkHwComponentsAndItsCablesInRectangle(rect);

            // repaint
            drawPanel.repaint();

            return;
        }

        // --- DRAGGING components-----
        // if are marked other components than dragged, mark only the dragged component (if ctrl not down)
        if(!e.isControlDown() && draggedComponents.size() == 1 && !draggedComponents.get(0).isMarked()){
            setMarkedComponentsUnmarked();
            doMarkHwComponentAndItsCables(true, draggedComponents.get(0));
        }
        

        // get point from mouse in actual zoom shifted by difference
        Point p = new Point(e.getX() - zoomManager.doScaleToActual(differenceXdefaultZoom),
                e.getY() - zoomManager.doScaleToActual(differenceYdefaultZoom));

        // get upper left bound of dragged components
        Point draggedComponentsTmpPoint = graph.getUpperLeftBound(draggedComponents);


        Dimension differenceInActualZoom = new Dimension(p.x - draggedComponentsTmpPoint.x, p.y - draggedComponentsTmpPoint.y);

        // if the corner would be out of panel
        if (draggedComponentsTmpPoint.x + differenceInActualZoom.width < 0) {
            differenceInActualZoom.width = 0 - draggedComponentsTmpPoint.x;
        }
        if (draggedComponentsTmpPoint.y + differenceInActualZoom.height < 0) {
            differenceInActualZoom.height = 0 - draggedComponentsTmpPoint.y;
        }


        // if nothing happend, return
        if (differenceInActualZoom.width == 0 && differenceInActualZoom.height == 0) {
            return;
        }

        // change position of all components
        for (AbstractHwComponent component : draggedComponents) {
            component.doChangePosition(zoomManager.doScaleToDefault(differenceInActualZoom), true);
        }

        drawPanel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // dragging all marked components

        // if we are dragging some components
        if (!draggedComponents.isEmpty()) {
            // new default location for undoable edit  (math.max(0,x) because we dont want to move it to negative position)

            Point newDefaultZoomLocation = new Point(Math.max(0, zoomManager.doScaleToDefault(e.getX()) - differenceXdefaultZoom),
                    Math.max(0, zoomManager.doScaleToDefault(e.getY()) - differenceYdefaultZoom));

            // if were draggedComponents moved
            if (!newDefaultZoomLocation.equals(draggedComponentsOriginalPoint)) {
                // count dimension of change
                Dimension dim = new Dimension(newDefaultZoomLocation.x - draggedComponentsOriginalPoint.x,
                        newDefaultZoomLocation.y - draggedComponentsOriginalPoint.y);

                // add edit to undo manager
                undoManager.undoableEditHappened(new UndoableEditEvent(this,
                        new UndoableMoveComponent(draggedComponents, dim, drawPanel)));

                // inform drawPanel about position change
                drawPanel.updateSize(graph.getLowerRightBound(draggedComponents));

                // update Undo and Redo buttons
                mainWindow.updateUndoRedoButtons();

                /*
                System.out.println("Original default loc: x="+originalDefaultZoomLocation.x+", y="+originalDefaultZoomLocation.y+". "
                + "New default loc: x="+newDefaultZoomLocation.x +", y="+newDefaultZoomLocation.y+".");*/
            }

            drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());

            draggedComponents = null;
            return;
        }

        // painting transparent rectange for marking more components
        drawPanel.setTransparetnRectangleInProgress(false, null);
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
     * Marks only components and its cables that intersects with rectangle in parameter
     * @param rectangle 
     */
    private void doMarkHwComponentsAndItsCablesInRectangle(Rectangle rectangle) {
        // set all components unmarked
        setMarkedComponentsUnmarked();

        // mark only the intersecting components
        for (AbstractHwComponent component : graph.getHwComponents()) {
            if (component.intersects(rectangle)) {
                doMarkHwComponentAndItsCables(true, component);
            }
        }
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
            List<Cable> cables = ((AbstractHwComponent) component).getCables();
            for (Cable c : cables) {
                if (marked) {
                    c.setMarked(marked);
                    markedComponents.add(c);
                } else {
                    // if both ends of calbe not marked, than unmark cable
                    if (!c.getComponent1().isMarked() && !c.getComponent2().isMarked()) {
                        c.setMarked(marked);
                        markedComponents.remove(c);
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

    private Markable getDraggedComponents(Point point){
        Markable component = null;
        
        if(draggedComponents == null || draggedComponents.isEmpty()){
            return component;
        }
        
        // search HwComponents
        for (AbstractHwComponent c : draggedComponents) {
            if (c.intersects(point)) {
                component = c;
                break;
            }
        }

        return component;
    }
    
    private Markable getClickedHwComponent(Point point) {
        Markable clickedComponent = null;

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
     * Return component at point
     * @param point
     * @return component clicked
     */
    private Markable getClickedItem(Point point) {
        // search HwComponents
        Markable clickedComponent = getClickedHwComponent(point);

        if (clickedComponent != null) {
            return clickedComponent;
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

    private int getMarkedHwComponentsCount() {
        int count = 0;
        for (Markable m : markedComponents) {
            if (m instanceof AbstractHwComponent) {
                count++;
            }
        }
        return count;
    }
}
