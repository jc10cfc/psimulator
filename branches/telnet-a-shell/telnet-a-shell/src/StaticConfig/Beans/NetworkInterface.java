/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StaticConfig.Beans;

/**
 *
 * @author zaltair
 */
public class NetworkInterface {


    Property<String> name = new Property<String>(Util.getText("NetworkInterface.Name.Name"), Util.getText("NetworkInterface.Name.Description"));
    Property<String> ipAddress = new Property<String>(Util.getText("NetworkInterface.ipAddress.Name"), Util.getText("NetworkInterface.ipAddress.Description"));
    Property<String> ipMask = new Property<String>(Util.getText("NetworkInterface.ipMask.Name"), Util.getText("NetworkInterface.ipMask.Description"));
    Property<String> macAddress = new Property<String>(Util.getText("NetworkInterface.mac.Name"), Util.getText("NetworkInterface.mac.Description"));
    /**
     * true - interface is UP
     * false - interface is down
     */
    Property<Boolean> up=new Property<Boolean>(Util.getText("NetworkInterface.up.Name"), Util.getText("NetworkInterface.up.Description"));

    /**
     * link to computer with this network interface
     */
    Computer computer;

    public Computer getComputer() {
        return computer;
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    public Property<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(Property<String> ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Property<String> getIpMask() {
        return ipMask;
    }

    public void setIpMask(Property<String> ipMask) {
        this.ipMask = ipMask;
    }

    public Property<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(Property<String> macAddress) {
        this.macAddress = macAddress;
    }

    public Property<String> getName() {
        return name;
    }

    public void setName(Property<String> name) {
        this.name = name;
    }

    public Property<Boolean> getUp() {
        return up;
    }

    public void setUp(Property<Boolean> up) {
        this.up = up;
    }


    

}
