package psimulator.dataLayer.Simulator;

import java.awt.Color;
import psimulator.dataLayer.ColorMixerSignleton;

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
}
