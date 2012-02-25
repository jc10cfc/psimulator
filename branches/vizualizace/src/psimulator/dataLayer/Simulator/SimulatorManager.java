package psimulator.dataLayer.Simulator;

import java.util.Observable;
import javax.swing.SwingUtilities;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
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
    private volatile int currentPositionInList = 0;
    //
    private EventTableModel eventTableModel;

    public SimulatorManager() {
        eventTableModel = new EventTableModel();
        isPlaying = false;
    }

    // ----- OBSERVERS notify methods
    /**
     * Used from another thread
     */
    @Override
    public void connected() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                isConnectedToServer = true;

                // notify all observers
                setChanged();
                notifyObservers(ObserverUpdateEventType.SIMULATOR_CONNECTED);
            }
        });
    }

    /**
     * Used from another thread
     */
    @Override
    public void disconnected() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            }
        });
    }

    /**
     * Used from another thread
     */
    @Override
    public void connectingFailed() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                isConnectedToServer = false;

                // notify all observers
                setChanged();
                notifyObservers(ObserverUpdateEventType.CONNECTION_CONNECTING_FAILED);
            }
        });
    }

    /**
     * Used from another thread
     */
    @Override
    public void connectionFailed() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                isConnectedToServer = false;

                // turn off recording and realtime
                setRecordingDeactivated();
                setRealtimeDeactivated();

                // notify all observers
                setChanged();
                notifyObservers(ObserverUpdateEventType.CONNECTION_CONNECTION_FAILED);
            }
        });
    }

    @Override
    public void doConnect() {
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.CONNECTION_DO_CONNECT);
    }

    @Override
    public void doDisconnect() {
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.CONNECTION_DO_DISCONNECT);

        isConnectedToServer = false;

        // turn off recording and realtime
        //setRecordingActivated(false);
        //setRealtimeActivated(false);

        if (isRealtime) {
            setRealtimeDeactivated();
        }

        if (isRecording) {
            setRecordingDeactivated();
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_DISCONNECTED);
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
    public void setRecordingActivated() {
        this.isRecording = true;
        System.out.println("Recording " + true);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_RECORDER_ON);
    }

    @Override
    public void setRecordingDeactivated() {
        this.isRecording = false;
        System.out.println("Recording " + false);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_RECORDER_OFF);
    }

    @Override
    public void setRealtimeActivated() {
        // if playing active and realtime activated, turn playing off
        if (isPlaying) {
            setPlayingStopped();
        }

        // start recording
        setRecordingActivated();

        this.isRealtime = true;
        System.out.println("Realtime " + true);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_REALTIME_ON);
    }

    @Override
    public void setRealtimeDeactivated() {
        // start recording
        setRecordingDeactivated();

        this.isRealtime = false;
        System.out.println("Realtime " + false);

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_REALTIME_OFF);
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

    /**
     * Used from another thread
     */
    @Override
    public void setNewPacketRecieved() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // notify all observers
                setChanged();
                notifyObservers(ObserverUpdateEventType.SIMULATOR_NEW_PACKET);
            }
        });
    }

    /**
     * Used from another thread
     */
    @Override
    public void addSimulatorEvent(final SimulatorEvent simulatorEvent) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                eventTableModel.addSimulatorEvent(simulatorEvent);
            }
        });
    }

    /**
     * Used from another thread
     */
    @Override
    public void moveToNextEvent() {
//        SwingUtilities.invokeLater(new Runnable() {
//
//           @Override
//            public void run() {
                //if nothing else to play
                if (currentPositionInList >= eventTableModel.getRowCount() - 1) {
                    isPlaying = false;
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
//            }
//        });
    }

    /**
     * Used from another thread
     */
    @Override
    public void moveToEvent(final int index) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//           @Override
//            public void run() {

                currentPositionInList = index;

                // notify all observers
                setChanged();
                notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_NEXT);
//            }
//        });
    }

    @Override
    public boolean isConnectedToServer() {
        return isConnectedToServer;
    }

    @Override
    public EventTableModel getEventTableModel() {
        return eventTableModel;
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
    public synchronized int getCurrentPositionInList() {
        return currentPositionInList;
    }

    @Override
    public int getListSize() {
        return eventTableModel.getRowCount();
    }

    /**
     * Used from another thread
     */
    @Override
    public SimulatorEvent getSimulatorEventAtCurrentPosition() {
        return eventTableModel.getSimulatorEvent(getCurrentPositionInList());
    }

    @Override
    public boolean isRealtime() {
        return isRealtime;
    }

    @Override
    public boolean hasEvents() {
        return eventTableModel.hasEvents();
    }

    @Override
    public boolean isTimeReset() {
        return eventTableModel.isTimeReset();
    }
}
