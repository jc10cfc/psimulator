package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.util.List;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;

/**
 *
 * @author Martin
 */
public class RemovedComponentsWrapper {
    private List<AbstractHwComponent> removedComponents;
    private List<Cable> removedCables;

    public RemovedComponentsWrapper(List<AbstractHwComponent> removedComponents, List<Cable> removedCables) {
        this.removedComponents = removedComponents;
        this.removedCables = removedCables;
    }

    public List<Cable> getRemovedCables() {
        return removedCables;
    }

    public List<AbstractHwComponent> getRemovedComponents() {
        return removedComponents;
    }   
}
