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
    //
   
    public SimulatorPlayerThread(DataLayerFacade model, UserInterfaceOuterFacade view) {
        this.simulatorManagerInterface = model.getSimulatorInterface();
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
                if (isPlaying) {
                    SimulatorEvent event = simulatorManagerInterface.getSimulatorEventAtCurrentPosition();
                    System.out.println("Player alive " + tmpCounter++ + ", Playing=" + isPlaying + ", speed=" + currentSpeed);
                    
                    System.out.println("Event: "+event+"."); 
                    int i = (int)(((double)SimulatorManager.SPEED_MAX) /(double) currentSpeed); // 1-10
                    
                    int time = i*500-4;
                    Thread.sleep(time);
                    
                    simulatorManagerInterface.moveToNextEvent();
                }else{
                    System.out.println("Player going to sleep " + tmpCounter++ + ", Playing=" + isPlaying + ", speed=" + currentSpeed);
                    Thread.sleep(10000);
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
                break;
            case SIMULATOR_PLAYER_STOP:
                isPlaying = simulatorManagerInterface.isPlaying();
                break;
            case SIMULATOR_PLAYER_LIST_MOVE:
                break;
            case SIMULATOR_SPEED:
                currentSpeed = simulatorManagerInterface.getSimulatorPlayerSpeed();
                return;
            default:
                return;
        }
        
        thread.interrupt();
    }
}
