package psimulator.AbstractNetwork;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Network implements Serializable{
    private int ID;
    private String name;
    
    private List<Cable> cables;
    private List<Device> devices;

    /*
    private int idCounter = 0;
    
    public int getNewId(){
        return idCounter++;
    }*/

    public Network(int ID, String name) {
        this.ID = ID;
        this.name = name;
        
        this.cables = new ArrayList<Cable>();
        this.devices = new ArrayList<Device>();
    }
 
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<Cable> getCables() {
        return cables;
    }

    public void setCables(List<Cable> cables) {
        this.cables = cables;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
