package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.JComponent;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Actions.RemovedComponentsWrapper;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.*;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.LayoutAlgorithm.GeneticGraph;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.CustomObservable;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class Graph extends JComponent implements GraphOuterInterface {

    private LinkedHashMap<Integer, AbstractHwComponent> componentsMap = new LinkedHashMap<Integer, AbstractHwComponent>();
    private List<BundleOfCables> bundlesOfCables = new ArrayList<BundleOfCables>();
    private List<Cable> markedCables = new ArrayList<Cable>();
    private List<AbstractHwComponent> markedAbstractHwComponents = new ArrayList<AbstractHwComponent>();
    private Grid grid;
    private int widthDefault;
    private int heightDefault;
    //
    private long lastEditTimestamp;
    //
    private CustomObservable customObservable = new CustomObservable();
    
    public Graph() {
    }

    public void initialize(DrawPanelInnerInterface drawPanel, DataLayerFacade dataLayer,
            AbstractImageFactory imageFactory) {

        setInitReferencesToComponents(dataLayer, imageFactory);

        // update size of graph with all components
        updateSizeWithAllComponents();

        // init grid
        grid = new Grid((GraphOuterInterface) this);
        
        // set timestamp of edit
        editHappend();
    }

    /**
     * Use in initialize to put references of parameters to all components in
     * graph.
     *
     * @param dataLayer
     * @param imageFactory
     */
    private void setInitReferencesToComponents(DataLayerFacade dataLayer, AbstractImageFactory imageFactory) {
        // get Collection of values contained in LinkedHashMap
        Collection<AbstractHwComponent> colection = componentsMap.values();
        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = colection.iterator();

        // get all marked components
        while (it.hasNext()) {
            AbstractHwComponent component = it.next();
            // set references
            component.setInitReferences(dataLayer, imageFactory);
            // initialize
            component.initialize();
        }

        // set references to all BundleOfCables and Cables
        for (BundleOfCables boc : bundlesOfCables) {
            // boc performs the same operation on its cables
            boc.setInitReferences(dataLayer, imageFactory);
        }

        // initialize cables
        for (Cable cable : getCables()) {
            cable.initialize();
        }
    }

    /**
     * Use in initialize to make the graph size according to all components
     */
    private void updateSizeWithAllComponents() {
        // get Collection of values contained in LinkedHashMap
        Collection<AbstractHwComponent> colection = componentsMap.values();
        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = colection.iterator();

        // update graph with all components
        while (it.hasNext()) {
            AbstractHwComponent component = it.next();
            updateSizeAddComponent(component.getLowerRightCornerLocation());
        }
    }

    public void doUpdateImages() {
        // get Collection of values contained in LinkedHashMap
        Collection<AbstractHwComponent> colection = componentsMap.values();
        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = colection.iterator();

        // get all marked components
        while (it.hasNext()) {
            it.next().doUpdateImages();
        }

        for (BundleOfCables boc : bundlesOfCables) {
            boc.doUpdateImages();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;


        // GRID PAINT
        /*
         * g2.setColor(Color.gray); grid.paintComponent(g2);
         * g2.setColor(Color.black);
         *
         * g2.setColor(Color.gray); g2.drawLine(getWidth(), 0, getWidth(),
         * getHeight()); g2.drawLine(0, getHeight(), getWidth(), getHeight());
         * g2.setColor(Color.black);
         */

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

        for (AbstractComponent c : markedAbstractHwComponents) {
            c.paint(g2);
        }


        //System.out.println("marked comp="+markedAbstractHwComponentsComponents.size());
    }

    @Override
    public List<AbstractHwComponent> getMarkedHwComponentsCopy() {
        List<AbstractHwComponent> temp = new ArrayList<AbstractHwComponent>();

        //Iterator<AbstractHwComponent> it = components.iterator();

        // get Collection of values contained in LinkedHashMap
        Collection<AbstractHwComponent> colection = componentsMap.values();
        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = colection.iterator();

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
     *
     * @return
     */
    @Override
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

    public List<Cable> getCables() {
        List<Cable> temp = new ArrayList<Cable>();

        Iterator<BundleOfCables> it = bundlesOfCables.iterator();

        // get all marked components
        while (it.hasNext()) {
            BundleOfCables b = it.next();
            // add all cables to temp
            temp.addAll(b.getCables());
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

    @Override
    public int getAbstractHwComponentsCount() {
        return componentsMap.size();
    }

    /**
     * Returns bundle of cables between component1 and component2. If such a
     * bundle does not exist, it creates it and adds it to graph and both
     * components.
     *
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
     *
     * @param bundleOfCables
     */
    private void removeBundleOfCables(BundleOfCables bundleOfCables) {
        bundleOfCables.getComponent1().removeBundleOfCables(bundleOfCables);
        bundleOfCables.getComponent2().removeBundleOfCables(bundleOfCables);

        bundlesOfCables.remove(bundleOfCables);

        bundleOfCables = null;
    }

    @Override
    public void addCable(Cable cable) {


        // get bundle of cables between c1 and c2
        BundleOfCables boc = getBundleOfCables(cable.getComponent1(), cable.getComponent2());

        // set component1 and component2 in calbe and bundle of cables the same
        if (cable.getComponent1() != boc.getComponent1()) {
            cable.swapComponentsAndEthInterfaces();
        }

        boc.addCable(cable);
        cable.getEth1().setCable(cable);
        cable.getEth2().setCable(cable);

        // set timestamp of edit
        editHappend();
    }

    @Override
    public void addCables(List<Cable> cableList) {
        for (Cable c : cableList) {
            addCable(c);
        }
    }

    /**
     * removes cable from graph
     *
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
        
        // set timestamp of edit
        editHappend();
    }

    @Override
    public void removeCables(List<Cable> cableList) {
        for (Iterator<Cable> it = cableList.iterator(); it.hasNext();) {
            removeCable(it.next());
        }
    }

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
     *
     * @param components to look in
     * @return LowerRight bound point
     */
    private Point getLowerRightBound(Collection<AbstractHwComponent> components) {
        Point p = new Point(0, 0);

        for (AbstractHwComponent c : components) {
            Point tmp = c.getLowerRightCornerLocation();

            if (tmp.x > p.x) {
                p.x = tmp.x;
            }
            if (tmp.y > p.y) {
                p.y = tmp.y;
            }

        }
        return p;
    }

    /**
     * Gets lower right bound point from all graph components
     *
     * @return LowerRight bound point
     */
    private Point getGraphLowerRightBound() {
        //return getLowerRightBound(components);
        return getLowerRightBound(componentsMap.values());
    }

    @Override
    public Collection<AbstractHwComponent> getHwComponents() {
        //return components;
        return componentsMap.values();
    }

    @Override
    public void addHwComponent(AbstractHwComponent component) {
        //components.add(component);
        componentsMap.put(component.getId(), component);
        updateSizeAddComponent(component.getLowerRightCornerLocation());
        
        // set timestamp of edit
        editHappend();
    }

    /**
     * Use when components not initialized (do not have references on zoom
     * manager and etc.)
     */
    public void addHwComponentWithoutGraphSizeChange(AbstractHwComponent component) {
        componentsMap.put(component.getId(), component);
    }

    @Override
    public void addHwComponents(List<AbstractHwComponent> componentList) {
        for (AbstractHwComponent component : componentList) {
            addHwComponent(component);
        }
    }

    @Override
    public void removeHwComponent(AbstractHwComponent component) {
        //components.remove(component);
        Collection<AbstractHwComponent> colection = componentsMap.values();
        colection.remove(component);


        //updateSizeRemoveComponents(component.getLowerRightCornerLocation());
        updateSizeByRecalculate();
        
        // set timestamp of edit
        editHappend();
    }

    @Override
    public void removeHwComponents(List<AbstractHwComponent> componentList) {
        //components.removeAll(componentList);
        Collection<AbstractHwComponent> colection = componentsMap.values();
        colection.removeAll(componentList);


        //updateSizeRemoveComponents(getLowerRightBound(components));
        updateSizeByRecalculate();
        
        // set timestamp of edit
        editHappend();
    }

    @Override
    public List<BundleOfCables> getBundlesOfCables() {
        return bundlesOfCables;
    }

    @Override
    public int getWidth() {
        return ZoomManagerSingleton.getInstance().doScaleToActual(widthDefault);
    }

    @Override
    public int getHeight() {
        return ZoomManagerSingleton.getInstance().doScaleToActual(heightDefault);
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    /**
     * Returns preffered size in actual zoom
     * @return 
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(),getHeight());
    }

// ============= MARKING =========    
    @Override
    public void doMarkComponentWithCables(Markable component, boolean marked) {
        // if component is isntance of AbstractHwComponent
        if (component instanceof AbstractHwComponent) {
            component.setMarked(marked);
            if (marked) {
                //markedComponents.add(component);
                markedAbstractHwComponents.add((AbstractHwComponent) component);
            } else {
                //markedComponents.remove(component);
                markedAbstractHwComponents.remove((AbstractHwComponent) component);
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
                markedCables.add((Cable) component);
            } else {
                //markedComponents.remove(component);
                markedCables.remove((Cable) component);
            }

        }
    }

    @Override
    public void doMarkCable(Cable cable) {
        cable.setMarked(true);
        markedCables.add(cable);
    }

    /**
     * sets all Markable components in markedComponents to marked(false) and
     * clears markedComponents list
     */
    @Override
    public void doUnmarkAllComponents() {
        for (Markable m : markedAbstractHwComponents) {
            m.setMarked(false);
        }
        markedAbstractHwComponents.clear();

        for (Markable m : markedCables) {
            m.setMarked(false);
        }
        markedCables.clear();
    }

    @Override
    public int getMarkedAbstractHWComponentsCount() {
        return markedAbstractHwComponents.size();
    }

    @Override
    public int getMarkedCablesCount() {
        return markedCables.size();
    }

    @Override
    public void doMarkAllComponents() {
        // get Collection of values contained in LinkedHashMap
        Collection<AbstractHwComponent> colection = componentsMap.values();
        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = colection.iterator();

        // mark all AbstractHwComponents
        while (it.hasNext()) {
            AbstractHwComponent m = it.next();
            m.setMarked(true);
            markedAbstractHwComponents.add(m);
        }

        for (BundleOfCables boc : bundlesOfCables) {
            for (Cable c : boc.getCables()) {
                c.setMarked(true);
                markedCables.add(c);
            }
        }
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
        
        // set timestamp of edit
        editHappend();
    }

    @Override
    public void doChangePositionOfAbstractHwComponents(List<AbstractHwComponent> components, Dimension offsetInDefaultZoom, boolean positive) {
        // get old lowerRightCorner of all components
        Point oldPosition = getLowerRightBound(components);
        // change position of all components
        for (AbstractHwComponent component : components) {
            // change position of component
            component.doChangePosition(offsetInDefaultZoom, positive);
        }
        // get new lowerRightCorner of all components
        Point newPosition = getLowerRightBound(components);
        // update size of graph
        updateSizeMovePosition(oldPosition, newPosition);
        //System.out.println("tady2: oldpos="+oldPosition.x+","+oldPosition.y+"; newpos="+newPosition.x+","+newPosition.y);
        
        // set timestamp of edit
        editHappend();
    }

    /**
     * Updates graph's dimension. Call after AbstractHwComponent move.
     *
     * @param oldPositionLowerRightCorner
     * @param newPositionLowerRightCorner
     */
    private void updateSizeMovePosition(Point oldPositionLowerRightCorner,
            Point newPositionLowerRightCorner) {

        // 4. case - position not changed
        // if nothing changed
        if (oldPositionLowerRightCorner.x == newPositionLowerRightCorner.x
                && oldPositionLowerRightCorner.y == newPositionLowerRightCorner.y) {
            // do nothing
            //System.out.println("update size move nothingto do, sizes the same, case 4");
            return;
        }

        // 3.case - moved out of graph dimension
        // if x or y is out of current width or height
        if ((newPositionLowerRightCorner.x >= ZoomManagerSingleton.getInstance().doScaleToActual(widthDefault))
                && newPositionLowerRightCorner.y >= ZoomManagerSingleton.getInstance().doScaleToActual(heightDefault)) {
            // update like addComponent
            //System.out.println("update size move calling updateSizeAddComponent, case 3");
            updateSizeAddComponent(newPositionLowerRightCorner);
            return;
        }


        // 1.case - moved right or down in graph dimension (not out of them)
        // if (oldX <= newX <=  width) and (oldY <= newY <= height)
        if ((oldPositionLowerRightCorner.x <= newPositionLowerRightCorner.x
                && newPositionLowerRightCorner.x <= ZoomManagerSingleton.getInstance().doScaleToActual(widthDefault))
                && (oldPositionLowerRightCorner.y <= newPositionLowerRightCorner.y
                && newPositionLowerRightCorner.y <= ZoomManagerSingleton.getInstance().doScaleToActual(heightDefault))) {
            // do nothing, graph size is not changed
            //System.out.println("update size move nothingto do, case 1");
            return;
        }

        // 2. case - moved left or up in graph dimension (not out of them)
        if ((newPositionLowerRightCorner.x < oldPositionLowerRightCorner.x)
                || newPositionLowerRightCorner.y < oldPositionLowerRightCorner.y) {
            // size could change, the same asi in remove
            //System.out.println("update size move calling update size remove components, case 2");
            //updateSizeRemoveComponents(oldPositionLowerRightCorner);
            updateSizeByRecalculate();
            return;
        }

        // 5. case 
        if ((oldPositionLowerRightCorner.x <= newPositionLowerRightCorner.x
                && newPositionLowerRightCorner.x <= ZoomManagerSingleton.getInstance().doScaleToActual(widthDefault))
                || (oldPositionLowerRightCorner.y <= newPositionLowerRightCorner.y
                && newPositionLowerRightCorner.y <= ZoomManagerSingleton.getInstance().doScaleToActual(heightDefault))) {
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
    private void updateSizeByRecalculate() {
        Point p = ZoomManagerSingleton.getInstance().doScaleToDefault(getGraphLowerRightBound());
        this.widthDefault = p.x;
        this.heightDefault = p.y;
        //System.out.println("update size recalculate");
        doInformAboutSizeChange();
    }

    /**
     * Updates size of Graph. Call after AbstractHwComponent ADD only.
     *
     * @param lowerRightCorner LowerRightCorner in ActualZoom
     */
    private void updateSizeAddComponent(Point lowerRightCorner) {
        // if width changed
        if (lowerRightCorner.x > ZoomManagerSingleton.getInstance().doScaleToActual(widthDefault)) {
            // resize width
            this.widthDefault = ZoomManagerSingleton.getInstance().doScaleToDefault(lowerRightCorner.x);
        }
        // if height changed
        if (lowerRightCorner.y > ZoomManagerSingleton.getInstance().doScaleToActual(heightDefault)) {
            // resize height
            this.heightDefault = ZoomManagerSingleton.getInstance().doScaleToDefault(lowerRightCorner.y);
        }
        //System.out.println("update size add");
        doInformAboutSizeChange();
    }

    /**
     * Informs drawPanel about change of graph size
     */
    public void doInformAboutSizeChange() {
        customObservable.notifyAllObservers(ObserverUpdateEventType.GRAPH_SIZE_CHANGED);
    }

    @Override
    public HashMap<AbstractHwComponent, Dimension> doAlignComponentsToGrid() {
        return doAlignComponentsToGrid(componentsMap.values());
    }

    @Override
    public HashMap<AbstractHwComponent, Dimension> doAlignMarkedComponentsToGrid() {
        return doAlignComponentsToGrid(markedAbstractHwComponents);
    }

    /**
     * Aligns all components in List to grid. Works with default zoom sizes. In
     * returned HashMap are all components that has been moved and the dimension
     * of position change in default zoom.
     *
     * @param componentsToAlign
     * @return HashMap with all components that has been moved and the dimension
     * of position change in default zoom.
     */
    private HashMap<AbstractHwComponent, Dimension> doAlignComponentsToGrid(Collection<AbstractHwComponent> componentsToAlign) {
        HashMap<AbstractHwComponent, Dimension> movedComponentsMap = new HashMap<AbstractHwComponent, Dimension>();

        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = componentsToAlign.iterator();

        // mark all AbstractHwComponents
        while (it.hasNext()) {
            AbstractHwComponent c = it.next();

            Point originalLocation = c.getCenterLocationDefaultZoom();
            Point newLocation = grid.getNearestGridPointDefaultZoom(originalLocation);

            Dimension differenceInDefaultZoom = new Dimension(originalLocation.x - newLocation.x,
                    originalLocation.y - newLocation.y);

            // if component moved, add to moved 
            if (differenceInDefaultZoom.getWidth() != 0 || differenceInDefaultZoom.getHeight() != 0) {
                this.doChangePositionOfAbstractHwComponent(c, differenceInDefaultZoom, false);

                movedComponentsMap.put(c, differenceInDefaultZoom);
            }
        }
        return movedComponentsMap;
    }

    @Override
    public HashMap<AbstractHwComponent, Dimension> doChangePositions(GeneticGraph geneticGraph) {
        int maxX = 0;
        int maxY = 0;

        for (int i = 0; i < geneticGraph.getNodes().length; i++) {
            if (maxX < geneticGraph.getNodes()[i][0]) {
                maxX = geneticGraph.getNodes()[i][0];
            }

            if (maxY < geneticGraph.getNodes()[i][1]) {
                maxY = geneticGraph.getNodes()[i][1];
            }
        }

        HashMap<AbstractHwComponent, Dimension> movedComponentsMap = new HashMap<AbstractHwComponent, Dimension>();

        // get Collection of values contained in LinkedHashMap
        Collection<AbstractHwComponent> colection = componentsMap.values();
        // obtain an Iterator for Collection
        Iterator<AbstractHwComponent> it = colection.iterator();

        int i = 0;

        while (it.hasNext()) {
            AbstractHwComponent c = it.next();

            Point originalLocation = c.getCenterLocationDefaultZoom();
            Point newLocation = new Point(geneticGraph.getNodes()[i][0] * 30 + ZoomManagerSingleton.getInstance().getIconWidthDefaultZoom(),
                    geneticGraph.getNodes()[i][1] * 30 + ZoomManagerSingleton.getInstance().getIconWidthDefaultZoom());

            Dimension differenceInDefaultZoom = new Dimension(originalLocation.x - newLocation.x,
                    originalLocation.y - newLocation.y);

            this.doChangePositionOfAbstractHwComponent(c, differenceInDefaultZoom, false);

            movedComponentsMap.put(c, differenceInDefaultZoom);

            i++;
        }
        return movedComponentsMap;
    }

    @Override
    public RemovedComponentsWrapper doRemoveMarkedComponents() {
        // get all marked components
        List<AbstractHwComponent> markedComponents = this.getMarkedHwComponentsCopy();

        // put all marked cables to cables toRemove
        List<Cable> cablesToRemove = this.getMarkedCablesCopy();

        // if there is no marked cable and no component
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
            //this.doMarkComponentWithCables(c, false);
        }

        // unmark all components
        this.doUnmarkAllComponents();

        // remove cables from graph
        this.removeCables(cablesToRemove);

        // remove marked components from graph
        this.removeHwComponents(markedComponents);

        this.markedAbstractHwComponents.clear();
        this.markedCables.clear();

        return new RemovedComponentsWrapper(markedComponents, cablesToRemove);
    }

    public AbstractHwComponent getAbstractHwComponent(int id) {
        return componentsMap.get(id);
    }

    private void editHappend() {
        //
        lastEditTimestamp = System.currentTimeMillis();
    }

    @Override
    public long getLastEditTimestamp() {
        return lastEditTimestamp;
    }

    @Override
    public synchronized void addObserver(Observer obsrvr) {
        customObservable.addObserver(obsrvr);
    }

    @Override
    public synchronized void deleteObserver(Observer obsrvr) {
        customObservable.deleteObserver(obsrvr);
    }
}
