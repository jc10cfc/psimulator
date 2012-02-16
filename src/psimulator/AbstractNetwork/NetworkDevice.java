package psimulator.AbstractNetwork;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

/**
 *
 * @author Martin
 */

public class NetworkDevice implements Serializable{
    
    
    private int ID;
    private HwTypeEnum hwType;
    private String name;
    
    
    private int x;
    private int y;
    
    
    private List<NetworkInterface> interfaces;

    public NetworkDevice(int ID, HwTypeEnum hwType, String name,int x, int y) {
        this.ID = ID;
        this.hwType = hwType;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public NetworkDevice() {
    }
    
    @XmlAttribute @XmlID
    public String getIDAsString(){
        return String.valueOf(ID);
    }
    
    public void setIDAsString(String id){
        ID = Integer.valueOf(id);
    }
    

    public int getID() {
        return ID;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }

    @XmlElement(name="interface")
    public List<NetworkInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<NetworkInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    // ---------------------------------------------------------------
    // Martin Svihlik nasledujici metody nepotrebuje
    
    public void setID(int ID) {
        this.ID = ID;
    }
    
    public void setHwType(HwTypeEnum hwType) {
        this.hwType = hwType;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
}
