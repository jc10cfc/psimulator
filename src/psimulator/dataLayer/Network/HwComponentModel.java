package psimulator.dataLayer.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public final class HwComponentModel extends AbstractComponentModel implements PositionInterface, NameInterface{
    
    /**
     * LinkedHashMap of EthInterfaces that component owns. Key is the ethInterface ID.
     */
    private LinkedHashMap<Integer, EthInterfaceModel> interfacesMap;
    // -------------------------------------------------------
    /**
     * Device name.
     */
    private String deviceName;
    /**
     * X position of component in Default zoom
     */
    private int defaultZoomXPos;
    /**
     * Y position of component in Default zoom
     */
    private int defaultZoomYPos;
      
    public HwComponentModel(Integer id, HwTypeEnum hwType, String deviceName, List<EthInterfaceModel> ethInterfaces, int defaultZoomXPos, int defaultZoomYPos){
        super(id, hwType);
        
        interfacesMap = new LinkedHashMap<>();
        
        // add values to variables
        this.deviceName = deviceName;
        this.defaultZoomXPos = defaultZoomXPos;
        this.defaultZoomYPos = defaultZoomYPos;
        
        // add interfaces to map
        for(EthInterfaceModel eth : ethInterfaces){
            interfacesMap.put(eth.getId(), eth);
        }
    }
    
    /**
     * Gets first avaiable ethInterface, if no avaiable null is renturned
     *
     * @return
     */
    public EthInterfaceModel getFirstFreeInterface() {
        for (EthInterfaceModel ei : interfacesMap.values()) {
            if (!ei.hasCable()) {
                return ei;
            }
        }
        return null;
    }

    /**
     * finds out whether component has any free EthInterface
     *
     * @return
     */
    public boolean hasFreeInterace() {
        for (EthInterfaceModel ei : interfacesMap.values()) {
            if (!ei.hasCable()) {
                return true;
            }
        }
        return false;
    }
 
    public Object[] getInterfacesNames() {
        Object[] list = new Object[interfacesMap.size()];

        int i=0;
        for(EthInterfaceModel ei : interfacesMap.values()){
            list[i] = ei.getName();
            i++;
        }
        return list;
    }

    public EthInterfaceModel getEthInterface(Integer id) {
        return interfacesMap.get(id);
    }

    public EthInterfaceModel getEthInterfaceAtIndex(int index) {
        List<EthInterfaceModel> list = new ArrayList<>(interfacesMap.values());
        return list.get(index);
    }

    /**
     * Gets X position of component in default zoom
     * @return 
     */
    @Override
    public int getDefaultZoomXPos() {
        return defaultZoomXPos;
    }

    /**
     * Gets Y position of component in default zoom
     * @return 
     */
    @Override
    public int getDefaultZoomYPos() {
        return defaultZoomYPos;
    }

    /**
     * Sets X position of component in default zoom
     * @param defaultZoomXPos 
     */
    @Override
    public void setDefaultZoomXPos(int defaultZoomXPos) {
        this.defaultZoomXPos = defaultZoomXPos;
    }

    /**
     * Sets Y position of component in default zoom
     * @param defaultZoomYPos 
     */
    @Override
    public void setDefaultZoomYPos(int defaultZoomYPos) {
        this.defaultZoomYPos = defaultZoomYPos;
    }

    /**
     * Sets name of component
     * @param name 
     */
    @Override
    public void setName(String name) {
        this.deviceName = name;
    }

    /**
     * Gets name of component
     * @return 
     */
    @Override
    public String getName() {
        return this.deviceName;
    }
    
    /**
     * Returns collection of interfaces
     */
    public Collection getEthInterfaces(){
        return interfacesMap.values();
    } 
    
}
