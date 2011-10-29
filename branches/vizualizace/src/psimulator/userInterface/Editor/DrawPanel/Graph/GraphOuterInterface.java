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

/**
 *
 * @author Martin
 */
public interface GraphOuterInterface {
    
    public int getWidth();
    public int getHeight();
    
    public void doChangePositionOfAbstractHwComponent(AbstractHwComponent component, Dimension offsetInDefaultZoom, boolean positive);
    public void doChangePositionOfAbstractHwComponents(List<AbstractHwComponent> components, Dimension offsetInDefaultZoom, boolean positive);
    public HashMap<AbstractHwComponent, Dimension> doAlignComponentsToGrid();
    public RemovedComponentsWrapper doRemoveMarkedComponents();
    // marking
    
    public void doMarkComponentWithCables(Markable component, boolean marked);
    public void doUnmarkAllComponents();
    public int getMarkedAbstractHWComponentsCount();
    // end marking
   
    public List<AbstractHwComponent> getMarkedHwComponentsCopy();
    //public List<Cable> getMarkedCablesCopy();
    //public Point getGraphLowerRightBound();
    //public Point getLowerRightBound(List<AbstractHwComponent> components);
    //public Point getNearestGridPoint(Point originalLocation);
    public Point getUpperLeftBound(List<AbstractHwComponent> components);
    public List<AbstractHwComponent> getHwComponents();
    public List<BundleOfCables> getBundlesOfCables();
    public int getCablesCount();
    
    public void removeCables(List<Cable> cableList);
    public void removeCable(Cable cable);
    public void removeHwComponent(AbstractHwComponent component);
    public void removeHwComponents(List<AbstractHwComponent> componentList);
    
    public void addCables(List<Cable> cableList);
    public void addCable(Cable cable);
    public void addHwComponent(AbstractHwComponent component);
    public void addHwComponents(List<AbstractHwComponent> componentList);
    
    
}
