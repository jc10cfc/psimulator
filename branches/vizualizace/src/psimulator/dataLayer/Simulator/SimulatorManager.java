package psimulator.dataLayer.Simulator;

import java.util.Observable;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
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
    private boolean isRealtime = false;
    private boolean isPlaying = false;
    private int currentSpeed = SPEED_INIT;
    //
    private int currentPositionInList = 0;
    //
    private EventTableModel eventTableModel;

    public SimulatorManager() {
        eventTableModel = new EventTableModel();
        isPlaying = false;
    }

    // ----- OBSERVERS notify methods
    @Override
    public void pullTriggerTmp() {
        if (isConnectedToServer) {
            isConnectedToServer = false;

            // turn off recording and realtime
            setRecordingActivated(false);
            setRealtimeActivated(false);
        } else {
            isConnectedToServer = true;
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_CONNECTION);
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

        System.out.println("State=" + simulatorPlayerState);

        switch (simulatorPlayerState) {
            case FIRST:
                if (eventTableModel.getRowCount() >= 1) {
                    currentPositionInList = 0;
                }
                break;
            case PREVIOUS:
                // if not at the beginning of the list
                if (currentPositionInList >= 1) {
                    currentPositionInList--;
                }
                break;
            case NEXT:
                // if not at the end of the list
                if (currentPositionInList < eventTableModel.getRowCount() - 1) {
                    currentPositionInList++;
                }
                break;
            case LAST:
                if (eventTableModel.getRowCount() > 0) {
                    currentPositionInList = eventTableModel.getRowCount() - 1;
                }
                break;
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_LIST_MOVE);
    }

    @Override
    public void setRecordingActivated(boolean activated) {
        this.isRecording = activated;
        System.out.println("Recording " + activated);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_RECORDER);
    }

    @Override
    public void setRealtimeActivated(boolean activated) {
        // if playing active and realtime activated, turn playing off
        if (isPlaying && activated) {
            setPlayingStopped();
        }

        // start recording
        setRecordingActivated(activated);

        this.isRealtime = activated;
        System.out.println("Realtime " + activated);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_REALTIME);
    }

    @Override
    public void setPlayingActivated() {
        if (eventTableModel.getRowCount() <= 0) {
            // if nothing to play - stop playing (the toggle button is deselected )
            setPlayingStopped();
            return;
        }

        this.isPlaying = true;
        System.out.println("START Playing ");

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_PLAY);
    }

    @Override
    public void setPlayingStopped() {
        this.isPlaying = false;
        System.out.println("STOP Playing ");

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_STOP);
    }

    @Override
    public void setPacketDetails(boolean activated) {
        isPacketDetails = activated;

        System.out.println("Packet details " + activated);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_DETAILS);
    }

    @Override
    public void setNamesOfDevices(boolean activated) {
        isDeviceNames = activated;

        System.out.println("Names of devices " + activated);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_DETAILS);
    }

    @Override
    public void setConcreteRawSelected(int row) {
        System.out.println("Row " + row + " double clicked");
        currentPositionInList = row;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_LIST_MOVE);
    }

    @Override
    public void deleteAllSimulatorEvents() {
        // stop playing and notify all observers
        setPlayingStopped();

        // delete items
        eventTableModel.deleteAllSimulatorEvents();
        currentPositionInList = 0;
    }

    // ----- GETTERS and SETTERS
    @Override
    public boolean isConnectedToServer() {
        return isConnectedToServer;
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
    public int getCurrentPositionInList() {
        return currentPositionInList;
    }

    @Override
    public int getListSize() {
        return eventTableModel.getRowCount();
    }

    @Override
    public void moveToNextEvent() {
        // if nothing else to play
        if (currentPositionInList >= eventTableModel.getRowCount() - 1) {
            this.isPlaying = false;
            System.out.println("Playing automaticly set to " + isPlaying);

            // notify all observers
            setChanged();
            notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_STOP);
        } else {
            currentPositionInList++;

            // notify all observers
            setChanged();
            notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_NEXT);
        }
    }

    @Override
    public void moveToEvent(int index) {
        currentPositionInList = index;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_NEXT);
    }

    @Override
    public SimulatorEvent getSimulatorEventAtCurrentPosition() {
        return eventTableModel.getSimulatorEvent(currentPositionInList);
    }

    @Override
    public boolean isRealtime() {
        return isRealtime;
    }

    @Override
    public void setNewPacketRecieved() {
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_NEW_PACKET);

    }

    @Override
    public boolean hasEvents() {
        return eventTableModel.hasEvents();
    }
}
