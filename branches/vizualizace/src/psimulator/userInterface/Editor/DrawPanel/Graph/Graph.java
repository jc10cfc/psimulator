package psimulator.userInterface.Editor.DrawPanel.Graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import psimulator.userInterface.Editor.DrawPanel.Actions.RemovedComponentsWrapper;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.BundleOfCables;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel.Components.Markable;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelSizeChangeInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.ZoomManager;

/**
 *
 * @author Martin
 */
public class Graph extends JComponent implements GraphOuterInterface, Observer {

    private List<AbstractHwComponent> components = new ArrayList<AbstractHwComponent>();
    private List<BundleOfCables> bundlesOfCables = new ArrayList<BundleOfCables>();
    
    private List<Cable> markedCables = new ArrayList<Cable>();
    private List<AbstractHwComponent> markedAbstractHwComponentsComponents = new ArrayList<AbstractHwComponent>();
    
    private Grid grid;
    private int widthDefault;
    private int heightDefault;
    private DrawPanelSizeChangeInnerInterface drawPanel;
    private ZoomManager zoomManager;

    public Graph(DrawPanelSizeChangeInnerInterface drawPanel, ZoomManager zoomManager) {
        this.zoomManager = zoomManager;
        this.drawPanel = drawPanel;

        // init grid
        grid = new Grid((GraphOuterInterface) this, zoomManager);

        zoomManager.addObserver((Observer) this);
    }

    @Override
    public void update(Observable o, Object o1) {
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;


        // GRID PAINT
        
        g2.setColor(Color.gray);
        grid.paintComponent(g2);
        g2.setColor(Color.black);
        
        g2.setColor(Color.gray);
        g2.drawLine(getWidth(), 0, getWidth(), getHeight());
        g2.drawLine(0, getHeight(), getWidth(), getHeight());
        g2.setColor(Color.black);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        
        // DRAW cables
        for (AbstractComponent c : getBundlesOfCables()) {
            if (!c.isMarked()) {
                c.paint(g2);
            }
        }
        
        for (AbstractComponent c : markedCables) {
            c.paint(g2); 
        }
        
        
        
        
        // DRAW HWcomponents
        for (AbstractComponent c : getHwComponents()) {
            if (!c.isMarked()) {
                c.paint(g2);
            }
        }
        
        for (AbstractComponent c : markedAbstractHwComponentsComponents) {
            c.paint(g2); 
        }
        

        //System.out.println("marked comp="+markedAbstractHwComponentsComponents.size());
    }

    /**
     * Retruns new ArrayList with marked components
     * @return 
     */
    @Override
    public List<AbstractHwComponent> getMarkedHwComponentsCopy() {
        List<AbstractHwComponent> temp = new ArrayList<AbstractHwComponent>();

        Iterator<AbstractHwComponent> it = components.iterator();

        // get all marked components
        while (it.hasNext()) {
            AbstractHwComponent c = it.next();
            if (c.isMarked()) {
                temp.add(c);
            }
        }

        return temp;
    }

    /**
     * Retruns new ArrayList with marked cables
     * @return 
     */
    public List<Cable> getMarkedCablesCopy() {
        List<Cable> temp = new ArrayList<Cable>();

        Iterator<BundleOfCables> it = bundlesOfCables.iterator();

        // get all marked components
        while (it.hasNext()) {
            BundleOfCables b = it.next();

            for (Cable c : b.getCables()) {
                if (c.isMarked()) {
                    temp.add(c);
                }
            }

        }

        return temp;
    }

    @Override
    public int getCablesCount() {
        int count = 0;
        for (BundleOfCables boc : bundlesOfCables) {
            count += boc.getCables().size();
        }
        return count;
    }

    /**
     * Returns bundle of cables between component1 and component2.
     * If such a bundle does not exist, it creates it and adds it to graph and both components.
     * @param component1
     * @param component2
     * @return 
     */
    private BundleOfCables getBundleOfCables(AbstractHwComponent component1, AbstractHwComponent component2) {
        BundleOfCables bundle = null;

        // find bundle to place the cable in
        for (BundleOfCables boc : bundlesOfCables) {
            if ((boc.getComponent1() == component1 && boc.getComponent2() == component2)
                    || (boc.getComponent1() == component2 && boc.getComponent2() == component1)) {
                bundle = boc;
                break;
            }
        }

        // if there is not a bundle between component1 and component2, we make the bundle
        if (bundle == null) {
            bundle = new BundleOfCables(component1, component2);
            bundlesOfCables.add(bundle);
            component1.addBundleOfCables(bundle);
            component2.addBundleOfCables(bundle);
        }
        return bundle;
    }

    /**
     * removes BundleOfCables from both components and graph
     * @param bundleOfCables 
     */
    private void removeBundleOfCables(BundleOfCables bundleOfCables) {
        bundleOfCables.getComponent1().removeBundleOfCables(bundleOfCables);
        bundleOfCables.getComponent2().removeBundleOfCables(bundleOfCables);

        bundlesOfCables.remove(bundleOfCables);

        bundleOfCables = null;
    }

    /**
     * adds cable to graph
     * @param cable 
     */
    @Override
    public void addCable(Cable cable) {
        // get bundle of cables between c1 and c2
        BundleOfCables boc = getBundleOfCables(cable.getComponent1(), cable.getComponent2());
        boc.addCable(cable);
        cable.getEth1().setCable(cable);
        cable.getEth2().setCable(cable);

    }

    /**
     * Adds all cables to graph
     * @param cableList 
     */
    @Override
    public void addCables(List<Cable> cableList) {
        for (Cable c : cableList) {
            addCable(c);
        }
    }

    /**
     * removes cable from graph
     * @param cable 
     */
    @Override
    public void removeCable(Cable cable) {
        // get bundle of cables between c1 and c2
        BundleOfCables boc = getBundleOfCables(cable.getComponent1(), cable.getComponent2());
        boc.removeCable(cable);
        cable.getEth1().removeCable();
        cable.getEth2().removeCable();

        // if no cable in bundle of cables
        if (boc.getCablesCount() == 0) {
            // remove bundle of cables
            removeBundleOfCables(boc);
        }
    }

    /**
     * Removes all cables from cableList in Graph
     * @param cableList 
     */
    @Override
    public void removeCables(List<Cable> cableList) {
        for (Iterator<Cable> it = cableList.iterator(); it.hasNext();) {
            removeCable(it.next());
        }
    }

    /**
     * Gets upper left bound point from all components
     * @param components to look in
     * @return UpperLeft bound point
     */
    @Override
    public Point getUpperLeftBound(List<AbstractHwComponent> components) {
        Point p = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (AbstractHwComponent c : components) {
            if (c.getX() < p.x) {
                p.x = c.getX();
            }
            if (c.getY() < p.y) {
                p.y = c.getY();
            }
        }
        return p;
    }

    /**
     * Gets lower right bound point from all components
     * @param components to look in
     * @return LowerRight bound point
     */
    private Point getLowerRightBound(List<AbstractHwComponent> components) {
        Point p = new Point(0, 0);

        for (AbstractHwComponent c : components) {
            if (c.getX() + c.getWidth() > p.x) {
                p.x = c.getX() + c.getWidth();
            }
            if (c.getY() + c.getHeight() > p.y) {
                p.y = c.getY() + c.getHeight();
            }
        }
        return p;
    }

    /**
     * Gets lower right bound point from all graph components
     * @return LowerRight bound point
     */
    private Point getGraphLowerRightBound() {
        return getLowerRightBound(components);
    }

    @Override
    public List<AbstractHwComponent> getHwComponents() {
        return components;
    }

    @Override
    public void addHwComponent(AbstractHwComponent component) {
        components.add(component);
        updateSizeAddComponent(component.getLowerRightCornerLocation());
    }

    @Override
    public void addHwComponents(List<AbstractHwComponent> componentList) {
        for(AbstractHwComponent component : componentList){
            addHwComponent(component);
        }
    }

    @Override
    public void removeHwComponent(AbstractHwComponent component) {
        components.remove(component);
        //updateSizeRemoveComponents(component.getLowerRightCornerLocation());
        updateSizeByRecalculate();
    }

    @Override
    public void removeHwComponents(List<AbstractHwComponent> componentList) {
        components.removeAll(componentList);
        //updateSizeRemoveComponents(getLowerRightBound(components));
        updateSizeByRecalculate();
    }

    @Override
    public List<BundleOfCables> getBundlesOfCables() {
        return bundlesOfCables;
    }

    @Override
    public int getWidth() {
        return zoomManager.doScaleToActual(widthDefault);
    }

    @Override
    public int getHeight() {
        return zoomManager.doScaleToActual(heightDefault);
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }
    
// ============= MARKING =========    
    
    /**
     * If component is cable, then mark or unmark it and add/remove it to marked components.
     * If component is AbstractHwComponent, than mark/unmark it and its all cables and add/remove
     * it to marked components.
     * @param marked True if mark, false if unmark.
     * @param component Component that needs to be marked.
     */
    @Override
    public void doMarkComponentWithCables(Markable component, boolean marked) {
        // if component is isntance of AbstractHwComponent
        if (component instanceof AbstractHwComponent) {
            component.setMarked(marked);
            if (marked) {
                //markedComponents.add(component);
                markedAbstractHwComponentsComponents.add((AbstractHwComponent)component);
            } else {
                //markedComponents.remove(component);
                markedAbstractHwComponentsComponents.remove((AbstractHwComponent)component);
            }

            // set marked to all its cables
            List<BundleOfCables> bundle = ((AbstractHwComponent) component).getBundleOfCableses();

            for (BundleOfCables boc : bundle) {
                for (Cable c : boc.getCables()) {
                    if (marked) {
                        c.setMarked(marked);
                        //markedComponents.add(c);
                        markedCables.add(c);
                    } else {
                        // if both ends of calbe not marked, than unmark cable
                        if (!boc.getComponent1().isMarked() && !boc.getComponent2().isMarked()) {
                            c.setMarked(marked);
                            //markedComponents.remove(c);
                            markedCables.remove(c);
                        }
                    }
                }
            }

        } else { // component is cable
            component.setMarked(marked);
            if (marked) {
                //markedComponents.add(component);
                markedCables.add((Cable)component);
            } else {
                //markedComponents.remove(component);
                markedCables.remove((Cable)component);
            }

        }
    }

    /**
     * sets all Markable components in markedComponents to marked(false) and
     * clears markedComponents list
     */
    @Override
    public void doUnmarkAllComponents() {
        for (Markable m : markedAbstractHwComponentsComponents) {
            m.setMarked(false);
        }
        markedAbstractHwComponentsComponents.clear();
        
        for (Markable m : markedCables) {
            m.setMarked(false);
        }
        markedCables.clear();
    }

    
    @Override
    public int getMarkedAbstractHWComponentsCount() {
        int count = 0;
        for (Markable m : markedAbstractHwComponentsComponents) {
            if (m instanceof AbstractHwComponent) {
                count++;
            }
        }
        return count;
    }
    
    
// ============== CHANGE POSITION AND RESIZE =======================
    
    
    @Override
    public void doChangePositionOfAbstractHwComponent(AbstractHwComponent component, Dimension offsetInDefaultZoom, boolean positive) {
        // get old position
        Point oldPosition = component.getLowerRightCornerLocation();
        // change position
        component.doChangePosition(offsetInDefaultZoom, positive);
        // get new position
        Point newPosition = component.getLowerRightCornerLocation();
        // update size of graph
        updateSizeMovePosition(oldPosition, newPosition);
        //System.out.println("tady1: oldpos="+oldPosition.x+","+oldPosition.y+"; newpos="+newPosition.x+","+newPosition.y);
    }
    
    @Override
    public void doChangePositionOfAbstractHwComponents(List<AbstractHwComponent> components, Dimension offsetInDefaultZoom, boolean positive) {
        // get old lowerRightCorner of all components
        Point oldPosition = getLowerRightBound(components);
        // change position of all components
        for(AbstractHwComponent component : components){
            // change position of component
            component.doChangePosition(offsetInDefaultZoom, positive);
        }
        // get new lowerRightCorner of all components
        Point newPosition = getLowerRightBound(components);
        // update size of graph
        updateSizeMovePosition(oldPosition, newPosition);
        //System.out.println("tady2: oldpos="+oldPosition.x+","+oldPosition.y+"; newpos="+newPosition.x+","+newPosition.y);
    }
    
    /**
     * Updates graph's dimension. Call after AbstractHwComponent move.
     * @param oldPositionLowerRightCorner
     * @param newPositionLowerRightCorner 
     */
    private void updateSizeMovePosition(Point oldPositionLowerRightCorner, 
            Point newPositionLowerRightCorner){
        
        // 4. case - position not changed
        // if nothing changed
        if(oldPositionLowerRightCorner.x == newPositionLowerRightCorner.x
                && oldPositionLowerRightCorner.y == newPositionLowerRightCorner.y){
            // do nothing
            //System.out.println("update size move nothingto do, sizes the same, case 4");
            return;
        }
        
        // 3.case - moved out of graph dimension
        // if x or y is out of current width or height
        if((newPositionLowerRightCorner.x >= zoomManager.doScaleToActual(widthDefault))
                && newPositionLowerRightCorner.y >= zoomManager.doScaleToActual(heightDefault)){
            // update like addComponent
            //System.out.println("update size move calling updateSizeAddComponent, case 3");
            updateSizeAddComponent(newPositionLowerRightCorner);
            return;
        }
        
        
        // 1.case - moved right or down in graph dimension (not out of them)
        // if (oldX <= newX <=  width) and (oldY <= newY <= height)
        if((oldPositionLowerRightCorner.x <= newPositionLowerRightCorner.x
                && newPositionLowerRightCorner.x <= zoomManager.doScaleToActual(widthDefault))
                && (oldPositionLowerRightCorner.y <= newPositionLowerRightCorner.y
                && newPositionLowerRightCorner.y <= zoomManager.doScaleToActual(heightDefault))){
            // do nothing, graph size is not changed
            //System.out.println("update size move nothingto do, case 1");
            return;
        }
        
        // 2. case - moved left or up in graph dimension (not out of them)
        if((newPositionLowerRightCorner.x < oldPositionLowerRightCorner.x)
                || newPositionLowerRightCorner.y < oldPositionLowerRightCorner.y){
            // size could change, the same asi in remove
            //System.out.println("update size move calling update size remove components, case 2");
            //updateSizeRemoveComponents(oldPositionLowerRightCorner);
            updateSizeByRecalculate();
            return;
        }
        
        // 5. case 
        if((oldPositionLowerRightCorner.x <= newPositionLowerRightCorner.x
                && newPositionLowerRightCorner.x <= zoomManager.doScaleToActual(widthDefault))
                || (oldPositionLowerRightCorner.y <= newPositionLowerRightCorner.y
                && newPositionLowerRightCorner.y <= zoomManager.doScaleToActual(heightDefault))){
            // update like addComponent
            //System.out.println("update size move nothingto do, case 5");
            updateSizeAddComponent(newPositionLowerRightCorner);
            return;
        }
        
        //System.out.println("dalsi moznost neni");
    }
    
    /**
     * call when need to go through all components to lower right bound
     */
    private void updateSizeByRecalculate(){
        Point p = zoomManager.doScaleToDefault(getGraphLowerRightBound());
        this.widthDefault = p.x;
        this.heightDefault = p.y;
        //System.out.println("update size recalculate");
        doInformDrawPanelAboutSizeChange();
    }
    
    /**
     * Updates size of Graph after remove of AbstractHwComponent (s).
     * If point lies on (or beyond) width or height of graph, we have to go through all 
     * components to determine size of it.
     * Otherwise nothing to do.
     
    private void updateSizeRemoveComponents(Point removedComponentLowerRightCorner){
        // if removed component touches width with x, or height with y
        if(removedComponentLowerRightCorner.x >= zoomManager.doScaleToActual(widthDefault)
                || removedComponentLowerRightCorner.y >= zoomManager.doScaleToActual(heightDefault)){
            //System.out.println("update size remove calling recalculate");
            updateSizeByRecalculate();
            return;
        }
        // if removed component do not touch width or height position, nothing to do
        //System.out.println("update size remove nothingto do");
    }*/
    
    /**
     * Updates size of Graph. Call after AbstractHwComponent ADD only.
     * @param lowerRightCorner LowerRightCorner in ActualZoom
     */
    private void updateSizeAddComponent(Point lowerRightCorner) {
        // if width changed
        if ( lowerRightCorner.x > zoomManager.doScaleToActual(widthDefault)){
            // resize width
            this.widthDefault = zoomManager.doScaleToDefault(lowerRightCorner.x);
        }
        // if height changed
        if(lowerRightCorner.y > zoomManager.doScaleToActual(heightDefault)){
            // resize height
            this.heightDefault = zoomManager.doScaleToDefault(lowerRightCorner.y);
        }
        //System.out.println("update size add");
        doInformDrawPanelAboutSizeChange();
    }
    
    /**
     * Informs drawPanel about change of graph size
     */
    private void doInformDrawPanelAboutSizeChange(){
        Dimension d = new Dimension(zoomManager.doScaleToActual(widthDefault), zoomManager.doScaleToActual(heightDefault));
        //System.out.println("new size of graph = "+d.width+","+d.height);
        drawPanel.updateSize(d);
    }

    @Override
    public HashMap<AbstractHwComponent, Dimension> doAlignComponentsToGrid() {
        HashMap<AbstractHwComponent, Dimension> movedComponentsMap = new HashMap<AbstractHwComponent, Dimension>();

        for (AbstractHwComponent c : this.getHwComponents()) {
            Point originalLocation = c.getCenterLocation();
            Point newLocation = grid.getNearestGridPoint(originalLocation);

            Dimension differenceInActualZoom = new Dimension(originalLocation.x - newLocation.x,
                    originalLocation.y - newLocation.y);
            Dimension differenceInDefaultZoom = zoomManager.doScaleToDefault(differenceInActualZoom);

            // if component moved, add to moved 
            if (differenceInDefaultZoom.getWidth() != 0 || differenceInDefaultZoom.getHeight() != 0) {
                //c.doChangePosition(zoomManager.doScaleToDefault(differenceInActualZoom), false);
                this.doChangePositionOfAbstractHwComponent(c, differenceInDefaultZoom, false);

                movedComponentsMap.put(c, differenceInDefaultZoom);
            }

        }
        return movedComponentsMap;
    }

    @Override
    public RemovedComponentsWrapper doRemoveMarkedComponents() {
        // get all marked components
        List<AbstractHwComponent> markedComponents = this.getMarkedHwComponentsCopy();

        // put all marked cables to cables toRemove
        List<Cable> cablesToRemove = this.getMarkedCablesCopy();
        
         // if there is no marked cable or component
        if (markedComponents.isEmpty() && cablesToRemove.isEmpty()) {
            return null;
        }
        
        // for all removed components
        for (AbstractHwComponent c : markedComponents) {
            // all its cables add to cablesToRemove
            for (BundleOfCables boc : c.getBundleOfCableses()) {
                for (Cable cable : boc.getCables()) {
                    // if collection doesnt contain, than add cable
                    if (!cablesToRemove.contains(cable)) {
                        cablesToRemove.add(cable);
                    }
                }
            }
            // unmark component
            this.doMarkComponentWithCables(c, false);
        }
        
        // remove cables from graph
        this.removeCables(cablesToRemove);
        
        // remove marked components from graph
        this.removeHwComponents(markedComponents);
        
        return new RemovedComponentsWrapper(markedComponents, cablesToRemove);
    }

    
    
    
}
