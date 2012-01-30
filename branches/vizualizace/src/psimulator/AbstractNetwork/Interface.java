package psimulator.AbstractNetwork;

import java.io.Serializable;

/**
 *
 * @author Martin
 */
public class Interface implements Serializable{
    private int ID;
    
    private Device device;
    
    private String ipAddress;
    private String macAddress;

    public Interface(int ID, Device device, String ipAddress, String macAddress) {
        this.ID = ID;
        this.device = device;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    
}
