package psimulator.userInterface.Editor.DrawPanel.Graph;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import psimulator.userInterface.Editor.DrawPanel.Actions.RemovedComponentsWrapper;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.BundleOfCables;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel.Components.Markable;
import psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm.GeneticGraph;

/**
 *
 * @author Martin
 */
public interface GraphOuterInterface {
    /**
     * Returns width of Graph in actual zoom
     * @return
     */
    public int getWidth();
    /**
     * Returns height of Graph in actual zoom
     * @return 
     */
    public int getHeight();
    /**
     * Changes position of AbstractHwComponent component by Dimension offsetInDefaultZoom
     * according to boolean positive.
     * @param component - Component that is being moved
     * @param offsetInDefaultZoom - offset in default Zoom
     * @param positive - orientation of move
     */
    public void doChangePositionOfAbstractHwComponent(AbstractHwComponent component, Dimension offsetInDefaultZoom, boolean positive);
    /**
     * Changes position of all AbstractHwComponent component in list by Dimension offsetInDefaultZoom
     * according to boolean positive.
     * @param components - List of components to move 
     * @param offsetInDefaultZoom - offset in default Zoom
     * @param positive - orientation of move
     */
    public void doChangePositionOfAbstractHwComponents(List<AbstractHwComponent> components, Dimension offsetInDefaultZoom, boolean positive);
    /**
     * Aligns all components to grid
     * @return HashMap - map of component+dimension pairs
     */
    public HashMap<AbstractHwComponent, Dimension> doAlignComponentsToGrid();
    /**
     * Aligns all components to grid
     * @return HashMap - map of component+dimension pairs
     */
    public HashMap<AbstractHwComponent, Dimension> doAlignMarkedComponentsToGrid();
    /**
     * Removes all marked AbstractHwComponents and Cables and returns them
     * wrapped.
     * @return  RemovedComponentsWrapper - list of AbstractHwComponents and Cables
     */
    public RemovedComponentsWrapper doRemoveMarkedComponents();
    
    /**
     * Changes position of all components according to genetic graph
     * @param geneticGraph
     * @return HashMap - map of component+dimension pairs
     */
    public HashMap<AbstractHwComponent, Dimension> doChangePositions(GeneticGraph geneticGraph);
    
    
    /**
     * If component is cable, then mark or unmark it and add/remove it to marked components.
     * If component is AbstractHwComponent, than mark/unmark it and its all cables and add/remove
     * it to marked components.
     * @param marked True if mark, false if unmark.
     * @param component Component that needs to be marked.
     */
    public void doMarkComponentWithCables(Markable component, boolean marked);
    
    
    /**
     * Marks cable as marked
     * @param cable 
     */
    public void doMarkCable(Cable cable);
    
    public void doMarkAllComponents();
    
    /**
     * unmarks all marked components
     */
    public void doUnmarkAllComponents();
    /**
     * Returns count of marked AbstractHWComponentsCount in graph
     * @return count of marked AbstractHWComponentsCount in graph
     */
    public int getMarkedAbstractHWComponentsCount();
    
    /**
     * 
     * @return marked cables count
     */
    public int getMarkedCablesCount();
    
    /**
     * Retruns new ArrayList with marked components
     * @return 
     */
    public List<AbstractHwComponent> getMarkedHwComponentsCopy();
     /**
     * Gets upper left bound point from all components
     * @param components to look in
     * @return UpperLeft bound point
     */
    public Point getUpperLeftBound(List<AbstractHwComponent> components);
    /**
     * Gets all AbstractHwComponent in list. It is NOT a copy
     * @return List of AbstractHwComponent
     */
    public List<AbstractHwComponent> getHwComponents();
    /**
     * Gets all BundleOfCables in list. It is NOT a copy
     * @return 
     */
    public List<BundleOfCables> getBundlesOfCables();
    /**
     * Gets count of cables in graph
     * @return 
     */
    public int getCablesCount();
    /**
     * Gets count of AbstractHwComponents in graph
     * @return 
     */
    public int getAbstractHwComponentsCount();
    
    /**
     * Removes all cables from cableList in Graph
     * @param cableList 
     */
    public void removeCables(List<Cable> cableList);
    /**
     * removes cable from graph
     * @param cable 
     */
    public void removeCable(Cable cable);
    /**
     * removes AbstractHwComponent from graph
     * @param component 
     */
    public void removeHwComponent(AbstractHwComponent component);
    /**
     * removes all AbstractHwComponents in list from graph
     * @param componentList 
     */
    public void removeHwComponents(List<AbstractHwComponent> componentList);
    
    /**
     * Adds all cables in list to graph
     * @param cableList 
     */
    public void addCables(List<Cable> cableList);
    /**
     * adds cable to graph
     * @param cable 
     */
    public void addCable(Cable cable);
    /**
     * Adds AbstractHwComponent to graph
     * @param component 
     */
    public void addHwComponent(AbstractHwComponent component);
    /**
     * Adds all AbstractHwComponents in list to graph
     * @param componentList 
     */
    public void addHwComponents(List<AbstractHwComponent> componentList);
    
    
}
