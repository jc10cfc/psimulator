package psimulator.dataLayer.Network;

import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class CableModel extends AbstractComponentModel{
    
    /**
     * First component of cable
     */
    private HwComponentModel component1;
    /**
     * Second component of cable
     */
    private HwComponentModel component2;
    /**
     * First interface of cable
     */
    private EthInterfaceModel interface1;
    /**
     * Second interface of cable
     */
    private EthInterfaceModel interface2;
    
    // -------------------------------------------------------
    /**
     * Delay of cable
     */
    private int delay;

    public CableModel(Integer id, HwTypeEnum hwType, HwComponentModel component1, HwComponentModel component2, EthInterfaceModel interface1, EthInterfaceModel interface2, int delay) {
        super(id, hwType);
        
        // assign values
        this.component1 = component1;
        this.component2 = component2;
        this.interface1 = interface1;
        this.interface2 = interface2;
        this.delay = delay;
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
     * Gets first interface
     * @return 
     */
    public EthInterfaceModel getInterface1() {
        return interface1;
    }

    /**
     * Gets second interface
     * @return 
     */
    public EthInterfaceModel getInterface2() {
        return interface2;
    }
 
    /**
     * Gets cable delay
     * @return 
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets cable delay
     * @param delay 
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    /**
     * Turns cable around. Swaps both ends of cable.
     */
    public void swapComponentsAndEthInterfaces(){
        HwComponentModel tmpComponent = component1;
        component1 = component2;
        component2 = tmpComponent;
        
        EthInterfaceModel tmpImterface = interface1;
        interface1 = interface2;
        interface2 = tmpImterface;
    }
}
