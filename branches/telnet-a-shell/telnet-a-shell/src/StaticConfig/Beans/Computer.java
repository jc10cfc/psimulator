/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StaticConfig.Beans;

import java.util.List;

/**
 *
 * @author zaltair
 */
public class Computer {

    Property<String> name = new Property<String>(Util.getText("Computer.Name.Name"), Util.getText("Computer.Name.Description"));
    
    List<NetworkInterface> interfaces;

    /** 
     * true - enabled
     * false - disabled
     */
    Property<Boolean> ipForwarding = new Property<Boolean>(Util.getText("Computer.IpForwarding.Name"), Util.getText("Computer.IpForwarding.Description"));

    /**
     * link to network which contains other computers
     */
    Network network;
    List<Settings> settings;

    public List<NetworkInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<NetworkInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Property<Boolean> getIpForwarding() {
        return ipForwarding;
    }

    public void setIpForwarding(Property<Boolean> ipForwarding) {
        this.ipForwarding = ipForwarding;
    }

    public Property<String> getName() {
        return name;
    }

    public void setName(Property<String> name) {
        this.name = name;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public List<Settings> getSettings() {
        return settings;
    }

    public void setSettings(List<Settings> settings) {
        this.settings = settings;
    }
    

    

}
