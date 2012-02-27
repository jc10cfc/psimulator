package psimulator.dataLayer.Network;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class NetworkModel implements Identifiable{
    /**
     * Map with components. Identified by component ID.
     */
    private LinkedHashMap<Integer, HwComponentModel> componentsMap;
    /**
     * Map with cables. Identified by cable ID.
     */
    private LinkedHashMap<Integer, CableModel> cablesMap;
    /**
     * Last edit timestamp in milliseconds.
     */
    private long lastEditTimestamp;
    /**
     * Id of network
     */
    private Integer id;

    
    public NetworkModel(LinkedHashMap<Integer, HwComponentModel> componentsMap, LinkedHashMap<Integer, CableModel> cablesMap, long lastEditTimestamp, Integer id) {
        this.componentsMap = componentsMap;
        this.cablesMap = cablesMap;
        this.lastEditTimestamp = lastEditTimestamp;
        this.id = id;
    }

    public long getLastEditTimestamp() {
        return lastEditTimestamp;
    }

    public void setLastEditTimestamp(long lastEditTimestamp) {
        this.lastEditTimestamp = lastEditTimestamp;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
    public int getHwComponentsCount() {
        return componentsMap.size();
    }
    
    public Collection<HwComponentModel> getHwComponents() {
        return componentsMap.values();
    }

    public void addHwComponent(HwComponentModel component) {
        componentsMap.put(component.getId(), component);
    }
    
    public void addHwComponents(List<HwComponentModel> componentList) {
        for (HwComponentModel component : componentList) {
            addHwComponent(component);
        }
    }
    
    public void removeHwComponent(HwComponentModel component) {
        Collection<HwComponentModel> colection = componentsMap.values();
        colection.remove(component);
    }
    
    public void removeHwComponents(List<HwComponentModel> componentList) {
        Collection<HwComponentModel> colection = componentsMap.values();
        colection.removeAll(componentList);
    }

    public void addCable(CableModel cableModel){
        cablesMap.put(cableModel.getId(), cableModel);
    }
    
    public void removeCable(CableModel cableModel){
        cablesMap.remove(cableModel.getId());
    }
    
    public int getCablesCount() {
        return cablesMap.size();
    }
}
