package psimulator.userInterface.Editor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.Cable;

/**
 *
 * @author Martin
 */
public class Graph {

    private List<AbstractHwComponent> components = new ArrayList<AbstractHwComponent>();
    private List<Cable> cables = new ArrayList<Cable>();

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

        Iterator<Cable> it = cables.iterator();

        // get all marked components
        while (it.hasNext()) {
            Cable c = it.next();
            if (c.isMarked()) {
                temp.add(c);
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

    /**
     * Gets all cables from graph
     * @return 
     */
    public List<Cable> getCables() {
        return cables;
    }

    /**
     * adds cable to graph
     * @param cable 
     */
    public void addCable(Cable cable) {
        // add cable to cables
        cables.add(cable);
        // add cables to both components
        cable.getComponent1().addCable(cable);
        cable.getComponent2().addCable(cable);
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
        // unmark cable
        cable.setMarked(false);
        // remove cable from cables
        cables.remove(cable);
        // remove cables from both components
        cable.getComponent1().removeCable(cable);
        cable.getComponent2().removeCable(cable);
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
     * Finds out whether there is connetction between components
     * @param component1
     * @param component2
     * @return true if there is connection, otherwise false
     */
    public boolean isConnection(AbstractHwComponent component1, AbstractHwComponent component2) {
        for (Cable source : component1.getCables()) {
            if (component2.containsCable(source)) {
                return true;
            }
        }
        return false;
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
