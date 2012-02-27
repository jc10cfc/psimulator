package psimulator.dataLayer.Network;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class NetworkFacade {
    
    private NetworkModel network; 
    private NetworkComponentsFactory networkComponentsFactory;
    
    public NetworkFacade(){
        networkComponentsFactory = new NetworkComponentsFactory();
    }

    public NetworkModel getNetwork() {
        return network;
    }

    public void setNetwork(NetworkModel network) {
        this.network = network;
    }
    
    // -----------------------------------------------
    
    public int getCablesCount(){
        return network.getCablesCount();
    }
    
    public int getHwComponentsCount(){
        return network.getHwComponentsCount();
    }
    
    /**
     * Adds calbe to proper bundle of cables, eth interfaces
     * @param cable 
     */
    public void addCable(CableModel cable) {
        cable.getInterface1().setCable(cable);
        cable.getInterface2().setCable(cable);
        
        // add cable to hash map
        network.addCable(cable);

        // set timestamp of edit
        editHappend();
    }
    
    public void addCables(List<CableModel> cableList) {
        for (CableModel c : cableList) {
            addCable(c);
        }
    }
    
    /**
     * removes cable from graph
     *
     * @param cable
     */
    public void removeCable(CableModel cable) {
        cable.getInterface1().removeCable();
        cable.getInterface2().removeCable();

        // remove cable from hash map
        network.removeCable(cable);
        
        // set timestamp of edit
        editHappend();
    }
    
    public void removeCables(List<CableModel> cableList) {
        for (Iterator<CableModel> it = cableList.iterator(); it.hasNext();) {
            removeCable(it.next());
        }
    }
    
    public Collection<HwComponentModel> getHwComponents() {
        return network.getHwComponents();
    }
    
    
    public void addHwComponent(HwComponentModel component) {
        network.addHwComponent(component);
        
        // set timestamp of edit
        editHappend();
    }
    
    public void addHwComponents(List<HwComponentModel> componentList) {
        for (HwComponentModel component : componentList) {
            addHwComponent(component);
        }
    }
    
    public void removeHwComponent(HwComponentModel component) {
        network.removeHwComponent(component);

        // set timestamp of edit
        editHappend();
    }
    
    public void removeHwComponents(List<HwComponentModel> componentList) {
        network.removeHwComponents(componentList);

        // set timestamp of edit
        editHappend();
    }

    /**
     * Creates new cableModel in network component factory.
     * @param hwType
     * @param component1
     * @param component2
     * @param interface1
     * @param interface2
     * @return 
     */
    public CableModel createCable(HwTypeEnum hwType, HwComponentModel component1, HwComponentModel component2, EthInterfaceModel interface1, EthInterfaceModel interface2){
        return networkComponentsFactory.createCable(hwType, component1, component2, interface1, interface2);
    }
    
    /**
     * Creates new hwComponent in network component factory.
     * @param hwType
     * @param interfacesCount
     * @param defaultZoomXPos
     * @param defaultZoomYPos
     * @return 
     */
    public HwComponentModel createHwComponent(HwTypeEnum hwType, int interfacesCount, int defaultZoomXPos, int defaultZoomYPos){
        return networkComponentsFactory.createHwComponent(hwType, interfacesCount, defaultZoomXPos, defaultZoomYPos);
    }
    
    // TODO
    public void editHappend(){
        //
        network.setLastEditTimestamp(System.currentTimeMillis());        
    }
}
