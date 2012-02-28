package psimulator.dataLayer.Network;

import java.io.Serializable;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractComponentModel implements Identifiable, Serializable{
    /**
     * Type of component
     */
    protected HwTypeEnum hwType;
    /**
     * Id of component
     */
    protected Integer id;

    /**
     * @param id
     * @param hwType 
     */
    public AbstractComponentModel(Integer id, HwTypeEnum hwType){
        this.id = id;
        this.hwType = hwType;
    }
    
    /**
     * Returns unique ID
     * @return 
     */
    @Override
    public Integer getId() {
        return id;
    }
    
    /**
     * Returs HwType of component
     * @return 
     */
    public HwTypeEnum getHwType() {
        return hwType;
    }
    
}
