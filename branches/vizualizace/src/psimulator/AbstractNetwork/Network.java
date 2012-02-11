package psimulator.AbstractNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Network implements Serializable{
    private int ID;
    private String name;
    
    private List<NetworkCable> cables;
    private List<NetworkDevice> devices;

    public Network(/*int ID, String name*/) {
        //this.ID = ID;
        //this.name = name;
        
        this.cables = new ArrayList<NetworkCable>();
        this.devices = new ArrayList<NetworkDevice>();
    }
 
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<NetworkCable> getCables() {
        return cables;
    }

    public void setCables(List<NetworkCable> cables) {
        this.cables = cables;
    }

    public List<NetworkDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<NetworkDevice> devices) {
        this.devices = devices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
