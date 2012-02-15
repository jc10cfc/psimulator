package psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners;

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
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.*;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.PopupMenuAbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.PopupMenuCable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.UndoCommands.UndoableMoveComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.SimulatorEditor.Tools.AbstractTool;
import psimulator.userInterface.SimulatorEditor.Tools.ManipulationTool;

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
     * point in actual zoom where started components marking
     */
    protected Point startPointOfMarkingTransparentRectangle;
    
    protected boolean transparentRectangleInProgress;
            
    private ManipulationTool manipulationTool;

    public DrawPanelListenerStrategyHand(DrawPanelInnerInterface drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(drawPanel, undoManager, zoomManager, mainWindow, dataLayer);
    }

    @Override
    public void initialize() {
        super.initialize();
        
        drawPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        transparentRectangleInProgress = false;
    }

    @Override
    public void deInitialize() {
        if(drawPanel.getGraphOuterInterface()!=null){
           drawPanel.getGraphOuterInterface().doUnmarkAllComponents(); 
        }
        
        drawPanel.repaint();
    }

    /**
     * Abstract Tool can set itself as
     * @param tool 
     */
    @Override
    public void setTool(AbstractTool tool) {
        this.manipulationTool = (ManipulationTool) tool;
    }

    /**
     * Marks component at Point clicked if any
     * IF control pressed, than add component to marked components
     * IF control not pressed, mark only the clicked component
     * @param e 
     */
    @Override
    public void mouseClickedLeft(MouseEvent e) {
        // get clicked component
        Markable clickedComponent = getClickedItem(convertPoint(e.getPoint()));
        
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        // if nothing clicked
        if (clickedComponent == null) {
            System.out.println("Graph - " + graph.getHwComponents().size() + " components, " + graph.getCablesCount()
                    + " cables and " + graph.getBundlesOfCables().size() + " bundles of cables");
            if (e.isControlDown()) {
                // do nothing
            } else {
                // unmark all marked components and clear marked components
                graph.doUnmarkAllComponents();
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
                graph.doMarkComponentWithCables(clickedComponent, false);
            } else {
                // if not marked, add to marked components
                graph.doMarkComponentWithCables(clickedComponent, true);
            }
            //repaint
            drawPanel.repaint();
            return;
        }

        // if control not down

        // if is clicked component marked 
        if (clickedComponent.isMarked()) {
            // if there are more marked components, mark only clicked one
            if (graph.getMarkedAbstractHWComponentsCount() > 1) {
                graph.doUnmarkAllComponents();
                graph.doMarkComponentWithCables(clickedComponent, true);
            } else { // if only one marked component remove marking 
                // remove from marked components and unmark it
                graph.doMarkComponentWithCables(clickedComponent, false);
            }
        } else {
            // if clicked component not marked
            // unmark all components
            graph.doUnmarkAllComponents();
            // set clicked component marked and add to marked components
            graph.doMarkComponentWithCables(clickedComponent, true);

        }
        //repaint
        drawPanel.repaint();
        return;
    }

    @Override
    public void mousePressedLeft(MouseEvent e) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        boolean addToDragAllMarkedComponents = false;
        if (draggedComponents == null) {
            draggedComponents = new ArrayList<AbstractHwComponent>();
        } else {
            // if control not down, clear selection
        }

        Markable clickedComponent = getClickedAbstractHwComponent(convertPoint(e.getPoint()));

        if (clickedComponent == null) {
            // start painting transparent rectange for marking more components
            startPointOfMarkingTransparentRectangle = convertPoint(e.getPoint());
            transparentRectangleInProgress = true;
            return;
        }else{
            transparentRectangleInProgress = false;
        }

        // try if start dragging of some component
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(convertPoint(e.getPoint()))) {
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
    public void mouseDraggedLeft(MouseEvent e) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        if (draggedComponents == null) {
            return;
        }

        // painting transparent rectange for marking more components
        if (draggedComponents.isEmpty() && transparentRectangleInProgress == true) {
            // calculate width an height
            int width = startPointOfMarkingTransparentRectangle.x - convertPoint(e.getPoint()).x;
            int height = startPointOfMarkingTransparentRectangle.y - convertPoint(e.getPoint()).y;

            // calculate x and y position
            int x = width < 0 ? startPointOfMarkingTransparentRectangle.x : convertPoint(e.getPoint()).x;
            int y = height < 0 ? startPointOfMarkingTransparentRectangle.y : convertPoint(e.getPoint()).y;

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
        if (!e.isControlDown() && draggedComponents.size() == 1 && !draggedComponents.get(0).isMarked()) {
            graph.doUnmarkAllComponents();
            graph.doMarkComponentWithCables(draggedComponents.get(0), true);
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
        graph.doChangePositionOfAbstractHwComponents(draggedComponents, zoomManager.doScaleToDefault(differenceInActualZoom), true);


        drawPanel.repaint();
    }

    @Override
    public void mouseReleasedLeft(MouseEvent e) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        // dragging all marked components

        // if we are dragging some components
        if (draggedComponents != null && !draggedComponents.isEmpty()) {
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
                        new UndoableMoveComponent(graph, draggedComponents, dim)));

                // update Undo and Redo buttons
                mainWindow.updateUndoRedoButtons();

                /*
                System.out.println("Original default loc: x="+originalDefaultZoomLocation.x+", y="+originalDefaultZoomLocation.y+". "
                + "New default loc: x="+newDefaultZoomLocation.x +", y="+newDefaultZoomLocation.y+".");*/
            }

            draggedComponents = null;
            return;
        }

        // painting transparent rectange for marking more components
        transparentRectangleInProgress = false;
        drawPanel.setTransparetnRectangleInProgress(false, null);
        
        drawPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(convertPoint(e.getPoint()))) {
                drawPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return;
            }
        }

        //Rectangle r = new Rectangle(e.getX() - 1, e.getY() - 1, 3, 3);

        for (BundleOfCables boc : graph.getBundlesOfCables()) {
            if (boc.intersects(convertPoint(e.getPoint()))) {
                drawPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return;
            }
        }

        drawPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressedRight(MouseEvent e) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        transparentRectangleInProgress = false;
        
        // get clicked component
        AbstractComponent clickedComponent = getClickedItem(convertPoint(e.getPoint()));

        // if there are more marked AbstractHwComponent components and we clicked one
        if (graph.getMarkedAbstractHWComponentsCount() > 1 && (clickedComponent != null && clickedComponent.isMarked())) {
            // show popup for more components
            PopupMenuAbstractHwComponent popup = new PopupMenuAbstractHwComponent(drawPanel, dataLayer, graph.getMarkedAbstractHWComponentsCount());

            popup.show(drawPanel, convertPoint(e.getPoint()).x, convertPoint(e.getPoint()).y);
            return;
        }

        // if nothing right clicked and nothing marked and there are some components
        if (clickedComponent == null && graph.getMarkedAbstractHWComponentsCount() == 0 && graph.getAbstractHwComponentsCount() > 0) {
            // show popup for all components
            PopupMenuAbstractHwComponent popup = new PopupMenuAbstractHwComponent(drawPanel, dataLayer, 0);

            popup.show(drawPanel, convertPoint(e.getPoint()).x, convertPoint(e.getPoint()).y);
            
            return;
        }

        // if clicled on cable
        if(clickedComponent != null && clickedComponent instanceof Cable){
            if(!clickedComponent.isMarked()){
                graph.doUnmarkAllComponents();
                graph.doMarkCable((Cable) clickedComponent);
                drawPanel.repaint();
            }
            
            PopupMenuCable popup = new PopupMenuCable(drawPanel, dataLayer, graph.getMarkedCablesCount());

            popup.show(drawPanel, convertPoint(e.getPoint()).x, convertPoint(e.getPoint()).y);
            return;
        }
        
        // if there is one marked component or no marked component and a component was clicked
        if (clickedComponent != null) {
            // unmark all components
            graph.doUnmarkAllComponents();

            // mark the clicked component
            graph.doMarkComponentWithCables(clickedComponent, true);

            drawPanel.repaint();

            // show popup for one component
            PopupMenuAbstractHwComponent popup = new PopupMenuAbstractHwComponent(drawPanel, dataLayer, 1);

            popup.show(drawPanel, convertPoint(e.getPoint()).x, convertPoint(e.getPoint()).y);
            return;
        }

        // if there are more marked components 
        if (graph.getMarkedAbstractHWComponentsCount() >= 1) {
            // unmark all components
            graph.doUnmarkAllComponents();

            drawPanel.repaint();

            PopupMenuAbstractHwComponent popup = new PopupMenuAbstractHwComponent(drawPanel, dataLayer, 0);

            popup.show(drawPanel, convertPoint(e.getPoint()).x, convertPoint(e.getPoint()).y);
            return;
        }
    }

    /**
     * Marks only components and its cables that intersects with rectangle in parameter
     * @param rectangle 
     */
    private void doMarkHwComponentsAndItsCablesInRectangle(Rectangle rectangle) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        // set all components unmarked
        graph.doUnmarkAllComponents();

        // mark only the intersecting components
        for (AbstractHwComponent component : graph.getHwComponents()) {
            if (component.intersects(rectangle)) {
                graph.doMarkComponentWithCables(component, true);
            }
        }

        // if no abstract component marked, mark only cables in rectangle
        if (graph.getMarkedAbstractHWComponentsCount() == 0) {
            for (BundleOfCables bundle : graph.getBundlesOfCables()) {
                for (Cable c : bundle.getCables()) {
                    if (c.intersects(rectangle)) {
                        graph.doMarkCable(c);
                    }
                }
            }

        }

    }
}
