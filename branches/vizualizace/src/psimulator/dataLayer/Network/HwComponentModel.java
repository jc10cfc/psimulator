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
public class HwComponentModel extends AbstractComponentModel implements PositionInterface, NameInterface{
    
    /**
     * List with bundles of cables that are connected to this component
     */
    private List<BundleOfCablesModel> bundlesOfCables;
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
        
        // create new array list for bundles of cables
        bundlesOfCables = new ArrayList<>();
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
    public int getDefaultZOomYPos() {
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
     * gets all bundles of cables
     * @return
     */
    public List<BundleOfCablesModel> getBundleOfCableses() {
        return bundlesOfCables;
    }

    /**
     * adds bundle of cables to bundle of cables list
     * @param boc
     */
    public void addBundleOfCables(BundleOfCablesModel boc) {
        bundlesOfCables.add(boc);
    }

    /**
     * removes bundle of cables from this components bundle of cables list
     * @param boc
     */
    public void removeBundleOfCables(BundleOfCablesModel boc) {
        bundlesOfCables.remove(boc);
    }
    
    /**
     * Returns collection of interfaces
     */
    public Collection getEthInterfaces(){
        return interfacesMap.values();
    }
    
}
