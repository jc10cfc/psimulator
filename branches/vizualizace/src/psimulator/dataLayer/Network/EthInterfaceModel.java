package psimulator.dataLayer.Network;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class EthInterfaceModel extends AbstractComponentModel implements NameInterface {
    
    /**
     * Component that this interface belongs to
     */
    private HwComponentModel hwComponent;
    /**
     * Cable that is connected to this interface
     */
    private CableModel cable;
    
    // -------------------------------------------------------
    /**
     * Name.
     */
    private String interfaceName;
    /**
     * Ip address of this eth interface
     */
    private String ipAddress;
    /**
     * Mac address of this eth interface
     */
    private String macAddress;

    public EthInterfaceModel(Integer id, HwTypeEnum hwType, HwComponentModel hwComponent, CableModel cable, 
            String ipAddress, String macAddress, String interfaceName) {
        super(id, hwType);
        
        // assign variables
        this.hwComponent = hwComponent;
        this.cable = cable;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.interfaceName = interfaceName;
    }

    /**
     * Returns cable that is connected to interface
     * @return 
     */
    public CableModel getCable() {
        return cable;
    }

    /**
     * Sets cable to interface
     * @param cable 
     */
    public void setCable(CableModel cable) {
        this.cable = cable;
    }
    
    /**
     * Removes cable from interface
     */
    public void removeCable(){
        this.cable = null;
    }
 
    /**
     * Finds out if there is cable connected. 
     * @return true if it is, false if it isn't
     */ 
    public boolean hasCable(){
        if(cable == null){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Gets hwComponent that this interface belong to
     * @return 
     */
    public HwComponentModel getHwComponent() {
        return hwComponent;
    }
    
    /**
     * Sets hw component to this interface
     */
    public void setHwComponent(HwComponentModel hwComponent){
        this.hwComponent = hwComponent;
    }
    
    /**
     * Gets ip address
     * @return 
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets ip address
     * @param ipAddress 
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets mac address
     * @return 
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets mac address
     * @param macAddress 
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Returns name of interface
     */
    @Override
    public String getName() {
        return interfaceName;
    }

    @Override
    public void setName(String name) {
        this.interfaceName = name;
    }
    
    
}
