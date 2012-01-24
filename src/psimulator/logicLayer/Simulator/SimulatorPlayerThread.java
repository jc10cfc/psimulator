package psimulator.logicLayer.Simulator;

import java.util.Observable;
import java.util.Observer;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Simulator.SimulatorEvent;
import psimulator.dataLayer.Simulator.SimulatorManager;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.userInterface.UserInterfaceOuterFacade;

/**
 *
 * @author Martin
 */
public class SimulatorPlayerThread implements Runnable, Observer {

    private Thread thread;
    //
    private SimulatorManagerInterface simulatorManagerInterface;
    //
    private int currentSpeed;
    private boolean isPlaying;
    private boolean isRealtime;
    //
    private boolean isNewPacket;

    public SimulatorPlayerThread(DataLayerFacade model, UserInterfaceOuterFacade view) {
        this.simulatorManagerInterface = model.getSimulatorManager();
        //simulatorPlayerState = simulatorManagerInterface.getSimulatorPlayerState();
        currentSpeed = simulatorManagerInterface.getSimulatorPlayerSpeed();

    }

    public void startThread(Thread t) {
        this.thread = t;
        t.start();
    }

    @Override
    public void run() {
        int tmpCounter = 0;

        while (true) {
            try {
                // interrupted in realtime and isNewPacket set to true = new packet came
                if (isRealtime && isNewPacket) {
                    // set isNewPacket to false
                    isNewPacket = false;

                    // get last event
                    int index = simulatorManagerInterface.getListSize()-1;
                    
                    // move to the last event
                    simulatorManagerInterface.moveToEvent(index);
                    
                    // get event
                    SimulatorEvent event = simulatorManagerInterface.getSimulatorEventAtCurrentPosition();

                    // play event
                    System.out.println("Player alive " + tmpCounter++ + ", Next event=" + event);
                    Thread.sleep(200);
                
                } else if (isPlaying) { // if in playing mode
                    
                    SimulatorEvent event = simulatorManagerInterface.getSimulatorEventAtCurrentPosition();
                    System.out.println("Player alive " + tmpCounter++ + ", Playing=" + isPlaying + ", speed=" + currentSpeed);

                    System.out.println("Event: " + event + ".");
                    int i = (int) (((double) SimulatorManager.SPEED_MAX) / (double) currentSpeed); // 1-10

                    int time = i * 500 - 4;
                    Thread.sleep(time);

                    simulatorManagerInterface.moveToNextEvent();

                } else {
                    System.out.println("Player going to sleep " + tmpCounter++ + ", Playing=" + isPlaying + ", speed=" + currentSpeed);
                    Thread.sleep(Long.MAX_VALUE);
                }


            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }

        }

    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case SIMULATOR_PLAYER_PLAY:
                isPlaying = simulatorManagerInterface.isPlaying();
                // interrupt
                break;
            case SIMULATOR_PLAYER_STOP:
                isPlaying = simulatorManagerInterface.isPlaying();
                // interrupt
                break;
            case SIMULATOR_PLAYER_LIST_MOVE:
                if(isRealtime){
                    // do not interrupt
                    return;
                }
                // interrupt
                break;
            case SIMULATOR_REALTIME:
                isRealtime = simulatorManagerInterface.isRealtime();
                isNewPacket = false;
                // interrupt
                break;
            case SIMULATOR_SPEED:
                currentSpeed = simulatorManagerInterface.getSimulatorPlayerSpeed();
                // do not interrupt
                return;
            case SIMULATOR_NEW_PACKET:
                if (isRealtime) {
                    isNewPacket = true;
                    // interrupt
                    break;
                } else {
                    // do not interrupt
                    return;
                }
            default:
                return;
        }

        thread.interrupt();
    }
}
