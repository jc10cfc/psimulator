package psimulator.dataLayer.SimulatorEvents;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorEventsWrapper implements Serializable{
    
    private List<SimulatorEvent> simulatorEvents;

    public SimulatorEventsWrapper(List<SimulatorEvent> simulatorEvents) {
        this.simulatorEvents = simulatorEvents;
    }

    public List<SimulatorEvent> getSimulatorEvents() {
        return simulatorEvents;
    }

    public void setSimulatorEvents(List<SimulatorEvent> simulatorEvents) {
        this.simulatorEvents = simulatorEvents;
    }
}
