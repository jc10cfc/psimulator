package psimulator.logicLayer.Simulator;

import java.util.*;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Simulator.PacketType;
import psimulator.dataLayer.Simulator.SimulatorEvent;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.EthInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.UserInterfaceOuterFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorClientEventRecieverThread implements Runnable, Observer {

    private static boolean DEBUG=true;
    //
    private Thread thread;
    //
    private SimulatorManagerInterface simulatorManagerInterface;
    private Random tmpRandom = new Random();
    private UserInterfaceOuterFacade userInterfaceOuterFacade;
    //
    private long timeOfFirstEvent;
    //
    private boolean isRecording;
    private boolean doConnect;
    private boolean doDisconnect;

    public SimulatorClientEventRecieverThread(DataLayerFacade model, UserInterfaceOuterFacade userInterfaceOuterFacade) {
        this.simulatorManagerInterface = model.getSimulatorManager();
        this.isRecording = simulatorManagerInterface.isRecording();

        this.userInterfaceOuterFacade = userInterfaceOuterFacade;
    }
    
    public void startThread(Thread t) {
        this.thread = t;
        t.start();
    }
    
    @Override
    public void run() {
        int tmpCounter = 0;
        
        /*
        while (true) {
            if (isRecording) {

                simulatorManagerInterface.addSimulatorEvent(generateSimulatorEvent());
                simulatorManagerInterface.setNewPacketRecieved();
            }

            try {
                int time = tmpRandom.nextInt(10) + 10; // 1000 + 100

                Thread.sleep(time);
                //Thread.sleep(1);
            } catch (InterruptedException ex) {
                System.out.println("Recorder Interrupted");
                return;
            }
        }*/
        
        while(true){
            try{
                if(doConnect){
                    if(DEBUG)System.out.println("Reciever do connect " + tmpCounter++);
                    
                    int i = tmpRandom.nextInt(2);
                    
                    //i =0;
                    
                    if(i==0){
                        Thread.sleep(2000);
                        simulatorManagerInterface.connected();
                    }else{
                        Thread.sleep(3000);
                        simulatorManagerInterface.connectingFailed();
                    }

  
                    //
                    doConnect = false;
                }else if (doDisconnect){
                    if(DEBUG)System.out.println("Reciever do disconnect " + tmpCounter++);

                    simulatorManagerInterface.disconnected();
                    
                    //
                    doDisconnect = false;
                }

                if(isRecording){
                    if(DEBUG)System.out.println("Reciever recording " + tmpCounter++);
                    
                    simulatorManagerInterface.addSimulatorEvent(generateSimulatorEvent());
                    simulatorManagerInterface.setNewPacketRecieved();
                    
                    int time = tmpRandom.nextInt(10) + 10; // 1000 + 100

                    Thread.sleep(time);
                    //Thread.sleep(1);

                } else{
                    if(DEBUG)System.out.println("Reciever going to long sleep " + tmpCounter++);
                    // sleep for a long time
                    Thread.sleep(Long.MAX_VALUE);
                }
            } catch (InterruptedException ex) {
                if(DEBUG)System.out.println("Reciever interrupted");
            }
            
        }
        

    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case CONNECTION_DO_CONNECT:
                if(DEBUG)System.out.println("Event reciever: DO_CONNECT");
                this.doConnect = true;
                break;
            case CONNECTION_DO_DISCONNECT:
                if(DEBUG)System.out.println("Event reciever: DO_DISCONNECT");
                this.doDisconnect = true;
                break;
            case SIMULATOR_RECORDER:
                if(DEBUG)System.out.println("Event reciever: RECORDER");
                this.isRecording = simulatorManagerInterface.isRecording();
                break;
            default:
                return;
        }

        //System.out.println("Event reciever: recording=" + isRecording);
        
        thread.interrupt();
    }
    
    
    
    /**
     * Generates simulator event from real graph. It tries to create event with random 
     * components connected with cable.
     * @return 
     */
    private SimulatorEvent generateSimulatorEvent() {
        Graph graph = userInterfaceOuterFacade.getAnimationPanelOuterInterface().getGraph();
        
        // for now it is Random, the ids in parameter not valid
        List<AbstractHwComponent> list = new ArrayList<AbstractHwComponent>(graph.getHwComponents());
        int componentCount = graph.getAbstractHwComponentsCount();


        AbstractHwComponent c1 = null;
        AbstractHwComponent c2 = null;

        Cable cable = null;

        int i1;
        int i2;

        // find connected components
        int counter = 0;
        while (counter < 50) {
            i1 = tmpRandom.nextInt(componentCount);
            i2 = tmpRandom.nextInt(componentCount);

            if (i1 == i2) {
                continue;
            }

            c1 = list.get(i1);
            c2 = list.get(i2);

            for (EthInterface eth1 : c1.getInterfaces()) {
                if (!eth1.hasCable()) {
                    continue;
                }
                for (EthInterface eth2 : c2.getInterfaces()) {
                    if (!eth2.hasCable()) {
                        continue;
                    }
                    if (eth1.getCable().getId().intValue() == eth2.getCable().getId().intValue()) {
                        cable = eth1.getCable();
                        break;
                    }
                }
                if (cable != null) {
                    break;
                }
            }
            if (cable != null) {
                break;
            }
        }


        // if reset time
        if(simulatorManagerInterface.isTimeReset()){
            timeOfFirstEvent = System.currentTimeMillis();
        }
        
        // generate packet type
        int index = tmpRandom.nextInt(PacketType.values().length);
        PacketType packetType = PacketType.values()[index];

        // generate time
        //int time = (int) (System.currentTimeMillis() % (86400000));
        int time = (int) (System.currentTimeMillis() - timeOfFirstEvent);
        
        //
        int cableId;
        if(cable != null){
            cableId = cable.getId().intValue();
        }else{
            cableId = -1;
        }

        SimulatorEvent simulatorEvent = new SimulatorEvent(time, c1.getId(), c2.getId(), 
                cableId, c1.getDeviceName(), c2.getDeviceName(), packetType);
        
        return simulatorEvent;
    }
}
