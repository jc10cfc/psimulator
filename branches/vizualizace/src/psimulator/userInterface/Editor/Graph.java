package psimulator.userInterface.Editor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.BundleOfCables;
import psimulator.userInterface.Editor.Components.Cable;

/**
 *
 * @author Martin
 */
public class Graph {

    private List<AbstractHwComponent> components = new ArrayList<AbstractHwComponent>();
    private List<BundleOfCables> bundlesOfCables = new ArrayList<BundleOfCables>();

    /**
     * Retruns new ArrayList with marked components
     * @return 
     */
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

    public List<AbstractHwComponent> getHwComponents() {
        return components;
    }

    public void addHwComponent(AbstractHwComponent component) {
        components.add(component);
    }

    public void addHwComponents(List<AbstractHwComponent> componentList) {
        components.addAll(componentList);
    }

    public void removeHwComponent(AbstractHwComponent component) {
        components.remove(component);
    }

    public void removeHwComponents(List<AbstractHwComponent> componentList) {
        components.removeAll(componentList);
    }

    public List<BundleOfCables> getBundlesOfCables() {
        return bundlesOfCables;
    }

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
            if ((boc.getComponent1() == component1 && boc.getComponent2() == component2) || 
                    (boc.getComponent1() == component2 && boc.getComponent2() == component1)){
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
    private void removeBundleOfCables(BundleOfCables bundleOfCables){
        bundleOfCables.getComponent1().removeBundleOfCables(bundleOfCables);
        bundleOfCables.getComponent2().removeBundleOfCables(bundleOfCables);
        
        bundlesOfCables.remove(bundleOfCables);
        
        bundleOfCables = null;
    }

    /**
     * adds cable to graph
     * @param cable 
     */
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
    public void addCables(List<Cable> cableList) {
        for (Cable c : cableList) {
            addCable(c);
        }
    }

    /**
     * removes cable from graph
     * @param cable 
     */
    public void removeCable(Cable cable) {
         // get bundle of cables between c1 and c2
        BundleOfCables boc = getBundleOfCables(cable.getComponent1(), cable.getComponent2());
        boc.removeCable(cable);
        cable.getEth1().removeCable();
        cable.getEth2().removeCable();
        
        // if no cable in bundle of cables
        if(boc.getCablesCount() == 0){
            // remove bundle of cables
            removeBundleOfCables(boc);
        }
    }

    /**
     * Removes all cables from cableList in Graph
     * @param cableList 
     */
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
    public Point getLowerRightBound(List<AbstractHwComponent> components) {
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
    public Point getGraphLowerRightBound() {
        return getLowerRightBound(components);
    }
}
