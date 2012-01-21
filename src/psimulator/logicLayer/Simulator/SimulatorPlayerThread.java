package psimulator.logicLayer.Simulator;

import java.util.Observable;
import java.util.Observer;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
import psimulator.dataLayer.Enums.UpdateEventType;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.userInterface.UserInterfaceOuterFacade;

/**
 *
 * @author Martin
 */
public class SimulatorPlayerThread implements Runnable, Observer {

    private SimulatorManagerInterface simulatorManagerInterface;
    
    private int currentSpeed;
    private boolean isPlaying;
    
    private int currentEventPosition;

    public SimulatorPlayerThread(DataLayerFacade model, UserInterfaceOuterFacade view) {
        this.simulatorManagerInterface = model.getSimulatorInterface();
        //simulatorPlayerState = simulatorManagerInterface.getSimulatorPlayerState();
        currentSpeed = simulatorManagerInterface.getSimulatorPlayerSpeed();
        
    }

    @Override
    public void run() {
        int tmpCounter = 0;

        for (int i = 0; i < 100; i++) {
            try {
                System.out.println("Player alive " + tmpCounter++);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
                return;
            }
        }
    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((UpdateEventType) o1) {
            case SIMULATOR_PLAYER_PLAY:
                isPlaying = simulatorManagerInterface.isPlaying();
                break;
            case SIMULATOR_SPEED:
                currentSpeed = simulatorManagerInterface.getSimulatorPlayerSpeed();
                break;
            default:
                return;
        }

        System.out.println("Player update: Playing=" + isPlaying + ", speed=" + currentSpeed);
    }
}
