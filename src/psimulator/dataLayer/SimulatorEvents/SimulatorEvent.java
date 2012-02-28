package psimulator.dataLayer.SimulatorEvents;

import java.awt.Color;
import java.io.Serializable;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.dataLayer.Network.EthInterfaceModel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.HwComponentGraphic;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorEvent implements Serializable{
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
    private transient HwComponentGraphic component1;
    private transient HwComponentGraphic component2;
    private transient EthInterfaceModel eth1;
    private transient EthInterfaceModel eth2;
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
    public void setDetails(String from, String to, HwComponentGraphic component1, 
            HwComponentGraphic component2, EthInterfaceModel eth1, EthInterfaceModel eth2){
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
    
    public HwComponentGraphic getComponent1() {
        return component1;
    }

    public HwComponentGraphic getComponent2() {
        return component2;
    }

    public EthInterfaceModel getEth1() {
        return eth1;
    }

    public EthInterfaceModel getEth2() {
        return eth2;
    }
}
