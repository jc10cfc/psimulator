package psimulator.userInterface.Editor.DrawPanel.Graph;

import java.awt.Point;
import java.util.List;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.BundleOfCables;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;

/**
 *
 * @author Martin
 */
public interface GraphInterface {
    
    public List<AbstractHwComponent> getMarkedHwComponentsCopy();
    public List<Cable> getMarkedCablesCopy();
    public Point getGraphLowerRightBound();
    public Point getLowerRightBound(List<AbstractHwComponent> components);
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
