package psimulator.AbstractNetwork;

import java.io.Serializable;

/**
 *
 * @author Martin
 */
public class NetworkInterface implements Serializable{
    private int ID;
    
    private NetworkDevice device;
    
    private String interfaceName;
    private String ipAddress;
    private String macAddress;

    public NetworkInterface(int ID, NetworkDevice device, String interfaceName, String ipAddress, String macAddress) {
        this.ID = ID;
        this.interfaceName = interfaceName;
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

    public NetworkDevice getDevice() {
        return device;
    }

    public void setDevice(NetworkDevice device) {
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

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    
    
}
