package psimulator.userInterface.SimulatorEditor.DrawPanel.Actions;

import java.util.List;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
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
