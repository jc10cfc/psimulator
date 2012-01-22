package psimulator.logicLayer.Simulator;

import java.util.Observable;
import java.util.Observer;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Simulator.SimulatorEvent;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;

/**
 *
 * @author Martin
 */
public class SimulatorClientEventRecieverThread implements Runnable, Observer{

    private SimulatorManagerInterface simulatorManagerInterface;
    
    private boolean isRecording;
    
    public SimulatorClientEventRecieverThread(DataLayerFacade model){
        this.simulatorManagerInterface = model.getSimulatorInterface();
        this.isRecording = simulatorManagerInterface.isRecording();
    }
    
    
    
    @Override
    public void run() {
        
        int tmpCounter = 0;
        
        while(true){
            if(isRecording){
                simulatorManagerInterface.addSimulatorEvent(new SimulatorEvent(tmpCounter++,"Router1", "Router2", "PING", ""));
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
                return;
            }
        }
  
    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case SIMULATOR_RECORDER:
                this.isRecording = simulatorManagerInterface.isRecording();
                break;
            default:
                return;
        }

        System.out.println("Event reciever: recording=" + isRecording );
    }
    
}
