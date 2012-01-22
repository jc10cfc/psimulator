package psimulator.dataLayer.Simulator;

/**
 *
 * @author Martin
 */
public class SimulatorEvent {
    
    
    private double time;
    private String from;
    private String to;
    private String type;
    private String info;
    
    private Object [] list;

    public SimulatorEvent(double time, String from, String to, String type, String info) {
        this.time = time;
        this.from = from;
        this.to = to;
        this.type = type;
        this.info = info;
        
        Object [] tmp = {time, from, to, type, info};
        list = tmp;
    }

    public Object getValueAt(int i){
        return list[i];
    }
    
    @Override
    public String toString(){
        return "time="+time+", from="+from+", to="+to+", type="+type+", info="+info;
    }
}
