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
}
