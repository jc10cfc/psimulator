package psimulator.dataLayer.interfaces;

import psimulator.dataLayer.Simulator.EventTableModel;
import psimulator.dataLayer.Simulator.SimulatorEvent;

/**
 *
 * @author Martin
 */
public interface SimulatorManagerInterface {
    public boolean isConnectedToServer();
    public EventTableModel getEventTableModel();
    
    public void setPlayerSpeed(int speed);
    public void addSimulatorEvent(SimulatorEvent simulatorEvent);
    public void deleteAllSimulatorEvents();
    
    
    public void pullTriggerTmp();
}
