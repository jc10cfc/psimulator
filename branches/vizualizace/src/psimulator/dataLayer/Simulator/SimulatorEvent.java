package psimulator.dataLayer.Simulator;

/**
 *
 * @author Martin
 */
public class SimulatorEvent {
    
    
    private double timeStamp;
    private String from;
    private String to;
    
    private int sourcceId;
    private int destId;
    private int cableId;
    
    private PacketType packetType;
    private String info;
    
    private Object [] list;

    public SimulatorEvent(double timeStamp, int sourcceId, int destId, int cableId, String from, String to, PacketType packetType, String info) {
        this.timeStamp = timeStamp;
        this.from = from;
        this.to = to;
        this.packetType = packetType;
        this.info = info;
        this.sourcceId = sourcceId;
        this.destId = destId;
        this.cableId = cableId;
        
        Object [] tmp = {timeStamp, from, to, packetType, info};
        list = tmp;
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
        return "time="+timeStamp+", sourceId"+sourcceId+", destId"+destId+", from="+from+", to="+to+", type="+packetType+", info="+info;
    }
}
