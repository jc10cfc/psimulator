package psimulator.dataLayer.Simulator;

import java.util.Observable;
import psimulator.dataLayer.Enums.SimulatorPlayerState;
import psimulator.dataLayer.Enums.UpdateEventType;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;

/**
 *
 * @author Martin
 */
public class SimulatorManager extends Observable implements SimulatorManagerInterface {
    // player speeds
    public static final int SPEED_MIN = 10;
    public static final int SPEED_MAX = 100;
    public static final int SPEED_INIT = 50;
    
   // simulator state variables
    private boolean isPacketDetails = false;
    private boolean isDeviceNames = false;
    private boolean isConnectedToServer = false;
    private boolean isRecording = false;
    private int currentSpeed = SPEED_INIT;
    private SimulatorPlayerState simulatorPlayerState;
    
    //
    private EventTableModel eventTableModel;
    
    public SimulatorManager() {
        eventTableModel = new EventTableModel();
        simulatorPlayerState = SimulatorPlayerState.STOP;
    }

    @Override
    public boolean isConnectedToServer() {
        return isConnectedToServer;
    }

    @Override
    public void pullTriggerTmp() {
        if (isConnectedToServer) {
            isConnectedToServer = false;
        } else {
            isConnectedToServer = true;
        }

        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.SIMULATOR);
    }

    @Override
    public EventTableModel getEventTableModel() {
        return eventTableModel;
    }

    @Override
    public void addSimulatorEvent(SimulatorEvent simulatorEvent) {
        eventTableModel.addSimulatorEvent(simulatorEvent);
    }

    @Override
    public void deleteAllSimulatorEvents() {
        eventTableModel.deleteAllSimulatorEvents();
    }
    
    @Override
    public void setPlayerSpeed(int speed) {
        currentSpeed = speed;
        
        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.SIMULATOR);
    }

    @Override
    public void playerFunctionActivated(SimulatorPlayerState simulatorPlayerState) {
        this.simulatorPlayerState = simulatorPlayerState;
        
        System.out.println("State="+simulatorPlayerState);
        
        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.SIMULATOR);
    }

    @Override
    public void recordingActivated(boolean activated) {
        this.isRecording = activated;
        System.out.println("Recording "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.SIMULATOR);
    }

    @Override
    public void setPacketDetails(boolean activated) {
        isPacketDetails = activated;
        
        System.out.println("Packet details "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.SIMULATOR);
    }

    @Override
    public void setNamesOfDevices(boolean activated) {
        isDeviceNames = activated;
        
        System.out.println("Names of devices "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.SIMULATOR);
    }
    

    
    
}
