package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import psimulator.dataLayer.Network.Identifiable;
import psimulator.dataLayer.Network.HwTypeEnum;
import psimulator.dataLayer.Singletons.GeneratorSingleton;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class EthInterface implements Identifiable{
    private Integer id;
    
    private String name;
    private Cable cable;
    
    // IP address
    private String ipAddress;
    
    // MAC address
    private String macAddress;

    /**
     * For creating ethInterface during runtime
     * @param name
     * @param cable
     * @param hwComponentType 
     */
    public EthInterface(String name, HwTypeEnum hwComponentType){
        this.name = name;
       
        this.id = new Integer(GeneratorSingleton.getInstance().getNextId());
        
        // do not generate MAC for switches and real pc
        switch(hwComponentType){
            case LINUX_SWITCH:
            case CISCO_SWITCH:
            case REAL_PC:
                this.macAddress = "";
                this.ipAddress = "";
                break;
            default:
                this.macAddress = GeneratorSingleton.getInstance().getNextMacAddress();
                this.ipAddress = "";
                break;
        }
    }
    
    /**
     * For creating ethInterface when loading network from file
     * @param id
     * @param name
     * @param ipAddress
     * @param macAddress 
     */
    public EthInterface(int id, String name, String ipAddress, String macAddress){
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.cable = null;
    }

    public boolean hasCable(){
        if(cable == null){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean hasCable(Cable c){
        return c == cable;
    }
    
    public Cable getCable() {
        return cable;
    }

    public void setCable(Cable cable) {
        this.cable = cable;
    }
    
    public void removeCable(){
        this.cable = null;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
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
