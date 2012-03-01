package psimulator.dataLayer.Simulator;

import java.util.List;
import java.util.Observable;
import javax.swing.SwingUtilities;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
import psimulator.dataLayer.Network.Components.CableModel;
import psimulator.dataLayer.Network.Components.EthInterfaceModel;
import psimulator.dataLayer.Network.Components.HwComponentModel;
import psimulator.dataLayer.SimulatorEvents.SimulatorEvent;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorManager extends Observable implements SimulatorManagerInterface {

    private static boolean DEBUG = false;
    private DataLayerFacade dataLayerFacade;
    // player speeds
    public static final int SPEED_MIN = 10;
    public static final int SPEED_MAX = 100;
    public static final int SPEED_INIT = 50;
    // simulator state variables
    private volatile boolean isConnectedToServer = false;
    private volatile boolean isRecording = false;
    private volatile boolean isRealtime = false;
    private volatile boolean isPlaying = false;
    private volatile int currentSpeed = SPEED_INIT;
    //
    private volatile int currentPositionInList = 0;
    //
    private EventTableModel eventTableModel;

    public SimulatorManager(DataLayerFacade dataLayerFacade) {
        this.dataLayerFacade = dataLayerFacade;
        eventTableModel = new EventTableModel();
        isPlaying = false;
    }

    // ----- OBSERVERS notify methods
    /**
     * Used from another thread
     */
    @Override
    public void connected() {
        isConnectedToServer = true;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
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
        isConnectedToServer = false;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
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
        isConnectedToServer = false;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // turn off recording and realtime
                if(isRecording){
                    setRecordingDeactivated();
                }
                if(isRealtime){
                    setRealtimeDeactivated();
                }
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

        if (DEBUG) {
            System.out.println("State=" + simulatorPlayerState);
        }

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
        if (DEBUG) {
            System.out.println("Recording " + true);
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_RECORDER_ON);
    }

    @Override
    public void setRecordingDeactivated() {
        this.isRecording = false;
        if (DEBUG) {
            System.out.println("Recording " + false);
        }

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
        if (DEBUG) {
            System.out.println("Realtime " + true);
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_REALTIME_ON);
    }

    @Override
    public void setRealtimeDeactivated() {
        // start recording
        setRecordingDeactivated();

        this.isRealtime = false;
        if (DEBUG) {
            System.out.println("Realtime " + false);
        }

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
        if (DEBUG) {
            System.out.println("START Playing ");
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_PLAY);
    }

    @Override
    public void setPlayingStopped() {
        this.isPlaying = false;
        if (DEBUG) {
            System.out.println("STOP Playing ");
        }

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_STOP);
    }

    @Override
    public void setConcreteRawSelected(int row) {
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

    private void setNewPacketRecieved() {
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
        
        // set details to event
        addDetailToSimulatorEvent(simulatorEvent);

        // add to table
        eventTableModel.addSimulatorEvent(simulatorEvent);
        
        // new packet recieved
        setNewPacketRecieved();
        
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                // set details to event
//                addDetailToSimulatorEvent(simulatorEvent);
//
//                // add to table
//                eventTableModel.addSimulatorEvent(simulatorEvent);
//            }
//        });
    }

    @Override
    public void setSimulatorEvents(SimulatorEventsWrapper simulatorEvents) {
        // delete items
        eventTableModel.deleteAllSimulatorEvents();
        currentPositionInList = 0;

        // get simulator event list
        List<SimulatorEvent> simulatorEventsList = simulatorEvents.getSimulatorEvents();

        // add details to events
        addDetailsToSimulatorEvents(simulatorEventsList);

        // add events to table model
        eventTableModel.setEventList(simulatorEvents.getSimulatorEvents());
    }

    /**
     * Used from another thread
     */
    @Override
    public void moveToNextEvent() {
        //if nothing else to play
        if (currentPositionInList >= eventTableModel.getRowCount() - 1) {
            isPlaying = false;
            if (DEBUG) {
                System.out.println("Playing automaticly set to " + isPlaying);
            }

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // notify all observers
                    setChanged();
                    notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_STOP);
                }
            });
        } else {
            currentPositionInList++;

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // notify all observers
                    setChanged();
                    notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_NEXT);
                }
            });
        }
    }

    /**
     * Used from another thread
     */
    @Override
    public void moveToEvent(final int index) {
        currentPositionInList = index;


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // notify all observers
                setChanged();
                notifyObservers(ObserverUpdateEventType.SIMULATOR_PLAYER_NEXT);
            }
        });
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

    /**
     * Used from another thread
     */
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

    @Override
    public SimulatorEventsWrapper getSimulatorEventsCopy() {
        SimulatorEventsWrapper simulatorEvents = new SimulatorEventsWrapper(eventTableModel.getEventListCopy());
        return simulatorEvents;
    }

    private void addDetailsToSimulatorEvents(List<SimulatorEvent> simulatorEvents) {
        for (SimulatorEvent simulatorEvent : simulatorEvents) {
            addDetailToSimulatorEvent(simulatorEvent);
        }
    }

    private void addDetailToSimulatorEvent(SimulatorEvent simulatorEvent) {
        // set details to event
        HwComponentModel c1 = dataLayerFacade.getNetworkFacade().getHwComponentModelById(simulatorEvent.getSourcceId());
        HwComponentModel c2 = dataLayerFacade.getNetworkFacade().getHwComponentModelById(simulatorEvent.getDestId());

        CableModel cable = dataLayerFacade.getNetworkFacade().getCableModelById(simulatorEvent.getCableId());

//        if (cable == null || c1 == null || c2 == null) {
//            return;
//        }

        EthInterfaceModel eth1 = cable.getInterface1();
        EthInterfaceModel eth2 = cable.getInterface2();

        simulatorEvent.setDetails(c1.getName(), c2.getName(), c1, c2, eth1, eth2);
    }
}
