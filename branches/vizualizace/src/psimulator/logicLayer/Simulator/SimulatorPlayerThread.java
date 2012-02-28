package psimulator.logicLayer.Simulator;

import java.util.Observable;
import java.util.Observer;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Simulator.SimulatorManager;
import psimulator.dataLayer.SimulatorEvents.SimulatorEvent;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanelOuterInterface;
import psimulator.userInterface.UserInterfaceOuterFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorPlayerThread implements Runnable, Observer {

    private static boolean DEBUG = false;
    private Thread thread;
    //
    private SimulatorManagerInterface simulatorManagerInterface;
    //
    private int currentSpeed;
    private boolean isPlaying;
    private boolean isRealtime;
    //
    private boolean isNewPacket;
    //
    private AnimationPanelOuterInterface animationPanelOuterInterface;

    public SimulatorPlayerThread(DataLayerFacade model, UserInterfaceOuterFacade view) {
        this.simulatorManagerInterface = model.getSimulatorManager();

        // set speed according to model
        currentSpeed = simulatorManagerInterface.getSimulatorPlayerSpeed();

        // getn animation panel
        animationPanelOuterInterface = view.getAnimationPanelOuterInterface();
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
                    int index = simulatorManagerInterface.getListSize() - 1;

                    // move to the last event
                    simulatorManagerInterface.moveToEvent(index);

                    // get event
                    SimulatorEvent event = simulatorManagerInterface.getSimulatorEventAtCurrentPosition();
                    
                    //calculate speed coeficient
                    int speedCoeficient = calculateSpeedCoefifient();

                    // get time
                    int time = animationPanelOuterInterface.getAnimationDuration(event.getCableId(), speedCoeficient);
                    
                    // start animation
                    animationPanelOuterInterface.createAnimation(event.getPacketType(), time, event.getSourcceId(), event.getDestId());

                    // play event
                    if (DEBUG) {
                        System.out.println("Player alive " + tmpCounter++ + ", Next event=" + event);
                    }
                    
                    // sleep for short time
                    Thread.sleep(200);

                } else if (isPlaying) { // if in playing mode
                    //glassPanelPainter.doPaintRedDots(true);


                    SimulatorEvent event = simulatorManagerInterface.getSimulatorEventAtCurrentPosition();
                    if (DEBUG) {
                        System.out.println("Player alive " + tmpCounter++ + ", Playing=" + isPlaying + ", speed=" + currentSpeed);
                    }

                    if (DEBUG) {
                        System.out.println("Event: " + event + ".");
                    }
                    
                    
                    //calculate speed coeficient
                    int speedCoeficient = calculateSpeedCoefifient();
                    
                    // calculate time
                    int time = animationPanelOuterInterface.getAnimationDuration(event.getCableId(), speedCoeficient);

                    // start animation
                    animationPanelOuterInterface.createAnimation(event.getPacketType(), time, event.getSourcceId(), event.getDestId());

                    // sleep thread for the same time as the animation takes
                    Thread.sleep(time);

                    // move to next event
                    simulatorManagerInterface.moveToNextEvent();

                } else {
                    //glassPanelPainter.doPaintRedDots(false);
                    if (DEBUG) {
                        System.out.println("Player going to sleep " + tmpCounter++ + ", Playing=" + isPlaying + ", speed=" + currentSpeed);
                    }
                    Thread.sleep(Long.MAX_VALUE);
                }


            } catch (InterruptedException ex) {
                if (DEBUG) {
                    System.out.println("Player Interrupted");
                }
            }

        }

    }
    
    private int calculateSpeedCoefifient(){
        return (int) (((double) SimulatorManager.SPEED_MAX) / (double) currentSpeed); // 1-10
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
                if (isRealtime) {
                    // do not interrupt
                    return;
                }
                // interrupt
                break;
            case SIMULATOR_REALTIME_OFF:
                // if nothing changed, do not interrupt
                if (isRealtime == false && simulatorManagerInterface.isRealtime() == false) {
                    // do not interrupt
                    return;
                }
            // goes through!
            case SIMULATOR_REALTIME_ON:
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
                // do not interrupt
                return;
        }

        thread.interrupt();
    }
}
