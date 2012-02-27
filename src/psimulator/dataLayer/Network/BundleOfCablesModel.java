package psimulator.dataLayer.Network;

import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class BundleOfCablesModel extends AbstractComponentModel{
    
    /**
     * First component of bundle
     */
    private HwComponentModel component1;
    /**
     * Second component of bundle
     */
    private HwComponentModel component2;
    /**
     * Cables that belongs to this bundle
     */
    private List<CableModel> cables;

    public BundleOfCablesModel(Integer id, HwTypeEnum hwType, HwComponentModel component1, HwComponentModel component2, List<CableModel> cables) {
        super(id, hwType);
        
        // assign variables
        this.component1 = component1;
        this.component2 = component2;
        this.cables = cables;
    }
    
    /**
     * Gets first component
     * @return 
     */
    public HwComponentModel getComponent1() {
        return component1;
    }

    /**
     * Gets second component
     * @return 
     */
    public HwComponentModel getComponent2() {
        return component2;
    }
    
    /**
     * Returns cable list
     * @return 
     */
    public List<CableModel> getCables() {
        return cables;
    }
 
    /**
     * Returns count of cables
     * @return 
     */
    public int getCablesCount(){
        return cables.size();
    }
    
    /**
     * adds cable to bundle
     * @param c 
     */
    public void addCable(CableModel c){
        cables.add(c);
    }
    
    /**
     * remove cable from bundle
     * @param c 
     */
    public void removeCable(CableModel c){
        cables.remove(c);
    }
    
}
