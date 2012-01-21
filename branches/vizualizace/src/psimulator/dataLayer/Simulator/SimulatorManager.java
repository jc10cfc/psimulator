package psimulator.dataLayer.Simulator;

import java.util.Observable;
import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
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
    private boolean isPlaying = false;
    private int currentSpeed = SPEED_INIT;
    //private SimulatorPlayerCommand simulatorPlayerState;
    
    //
    private EventTableModel eventTableModel;
    
    public SimulatorManager() {
        eventTableModel = new EventTableModel();
        isPlaying = false;
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
        notifyObservers(ObserverUpdateEventType.SIMULATOR);
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
        notifyObservers(ObserverUpdateEventType.SIMULATOR_SPEED);
    }

    @Override
    public void setPlayerFunctionActivated(SimulatorPlayerCommand simulatorPlayerState) {
        //this.simulatorPlayerState = simulatorPlayerState;
        
        System.out.println("State="+simulatorPlayerState);
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_LIST_MOVE);
    }

    @Override
    public void setRecordingActivated(boolean activated) {
        this.isRecording = activated;
        System.out.println("Recording "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_RECORDER);
    }
    
    @Override
    public void setPlayingActivated(boolean activated) {
        this.isPlaying = activated;
        System.out.println("Playing "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_PLAY);
    }

    @Override
    public void setPacketDetails(boolean activated) {
        isPacketDetails = activated;
        
        System.out.println("Packet details "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_DETAILS);
    }

    @Override
    public void setNamesOfDevices(boolean activated) {
        isDeviceNames = activated;
        
        System.out.println("Names of devices "+activated);
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_DETAILS);
    }

    @Override
    public void setConcreteRawSelected(int row) {
        System.out.println("Row "+row+" double clicked");
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_LIST_MOVE);
    }

    @Override
    public int getSimulatorPlayerSpeed() {
        return currentSpeed;
    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public int getCurrentEventPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    

    
    
}
