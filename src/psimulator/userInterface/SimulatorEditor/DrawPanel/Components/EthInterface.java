package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GeneratorSingleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.Validator;

/**
 *
 * @author Martin
 */
public class EthInterface implements Identifiable{
    private Integer id;
    
    private String name;
    private Cable cable;
    
    // IP address
    private String ipAddress;
    
    // MAC address
    private String macAddress;

    public EthInterface(String name, Cable cable){
        this.name = name;
        this.cable = cable;
        
        this.id = new Integer(GeneratorSingleton.getInstance().getNextId());
        this.macAddress = GeneratorSingleton.getInstance().getNextMacAddress();
        this.ipAddress = "";
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
