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
 
    private long timeStamp;
    private String from;
    private String to;
    
    private int sourcceId;
    private int destId;
    private int cableId;
    
    private PacketType packetType;
    private Color color;
    
    private Object [] list;
    
    //
    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private EthInterface eth1;
    private EthInterface eth2;

    public SimulatorEvent(long timeStamp, int sourcceId, int destId, int cableId, 
            String from, String to, PacketType packetType) {
        this.timeStamp = timeStamp;
        this.from = from;
        this.to = to;
        this.packetType = packetType;
        this.sourcceId = sourcceId;
        this.destId = destId;
        this.cableId = cableId;
        
        this.color =  ColorMixerSignleton.getColorAccodringToPacketType(packetType);
        
        Object [] tmp = {timeStamp, from, to, packetType, color};
        list = tmp;
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

    public Object getValueAt(int i){
        return list[i];
    }
    
    @Override
    public String toString(){
        return "time="+timeStamp+", sourceId"+sourcceId+", destId"+destId+", from="+from+", to="+to+", type="+packetType+", color="+color;
    }
    
    
    //////

    public AbstractHwComponent getComponent1() {
        return component1;
    }

    public void setComponent1(AbstractHwComponent component1) {
        this.component1 = component1;
    }

    public AbstractHwComponent getComponent2() {
        return component2;
    }

    public void setComponent2(AbstractHwComponent component2) {
        this.component2 = component2;
    }

    public EthInterface getEth1() {
        return eth1;
    }

    public void setEth1(EthInterface eth1) {
        this.eth1 = eth1;
    }

    public EthInterface getEth2() {
        return eth2;
    }

    public void setEth2(EthInterface eth2) {
        this.eth2 = eth2;
    }
    
}
