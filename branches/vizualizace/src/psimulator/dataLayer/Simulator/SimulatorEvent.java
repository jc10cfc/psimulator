package psimulator.dataLayer.Simulator;

import java.awt.Color;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.EthInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorEvent {
    // those variables has to be loaded from file / recieved via TCP connection, SAVE THEM
    private long timeStamp;
    private int sourcceId;
    private int destId;
    private int cableId;
    private PacketType packetType;
    private String detailsText;
   
    // those variables will be set using set details method, DO NOT SAVE THEM
    private transient Color color;
    private transient String from;
    private transient String to;
    private transient AbstractHwComponent component1;
    private transient AbstractHwComponent component2;
    private transient EthInterface eth1;
    private transient EthInterface eth2;
    private transient Object[] list;

    // USE THIS CONSTRUCTOR when creating event
    public SimulatorEvent(long timeStamp, int sourcceId, int destId, int cableId,
            PacketType packetType, String detailsText) {
        this.timeStamp = timeStamp;
        this.packetType = packetType;
        this.sourcceId = sourcceId;
        this.destId = destId;
        this.cableId = cableId;
        this.detailsText = detailsText;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public int getCableId() {
        return cableId;
    }

    public int getDestId() {
        return destId;
    }

    public int getSourcceId() {
        return sourcceId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getDetailsText() {
        return detailsText;
    }

    public Object getValueAt(int i) {
        return list[i];
    }

    @Override
    public String toString() {
        return "time=" + timeStamp + ", sourceId" + sourcceId + ", destId" + destId + ", from=" + from + ", to=" + to + ", type=" + packetType + ", color=" + color;
    }

    //////
  
    /**
     * Used in PSImulator UI after finding components by ID.
     * @param from
     * @param to
     * @param component1
     * @param component2
     * @param eth1
     * @param eth2 
     */
    public void setDetails(String from, String to, AbstractHwComponent component1, 
            AbstractHwComponent component2, EthInterface eth1, EthInterface eth2){
        this.from = from;
        this.to = to;
        this.component1 = component1;
        this.component2 = component2;
        this.eth1 = eth1;
        this.eth2 = eth2;
        
        this.color = ColorMixerSignleton.getColorAccodringToPacketType(packetType);
        
        Object[] tmp = {timeStamp, from, to, packetType, color};
        list = tmp;
    }
    
    public AbstractHwComponent getComponent1() {
        return component1;
    }

    public AbstractHwComponent getComponent2() {
        return component2;
    }

    public EthInterface getEth1() {
        return eth1;
    }

    public EthInterface getEth2() {
        return eth2;
    }
}
