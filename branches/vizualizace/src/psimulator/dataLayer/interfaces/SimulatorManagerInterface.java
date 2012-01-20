package psimulator.dataLayer.interfaces;

import psimulator.dataLayer.Enums.SimulatorPlayerState;
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
    
    public void playerFunctionActivated(SimulatorPlayerState simulatorPlayerState);
    
    public void recordingActivated(boolean activated);
    public void setPacketDetails(boolean activated);
    public void setNamesOfDevices(boolean activated);
}
