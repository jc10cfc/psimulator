package psimulator.dataLayer.Simulator;

import java.util.Observable;
import psimulator.dataLayer.Enums.UpdateEventType;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;

/**
 *
 * @author Martin
 */
public class SimulatorManager extends Observable implements SimulatorManagerInterface {

    public static final int SPEED_MIN = 10;
    public static final int SPEED_MAX = 100;
    public static final int SPEED_INIT = 50;
   
    private boolean isConnectedToServer = false;
    private int currentSpeed = SPEED_INIT;
    
    private EventTableModel eventTableModel;
    
    public SimulatorManager() {
        eventTableModel = new EventTableModel();
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
    public void setPlayerSpeed(int speed) {
        currentSpeed = speed;
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
}
