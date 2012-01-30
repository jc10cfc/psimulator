package psimulator.AbstractNetwork;

import java.io.Serializable;

/**
 *
 * @author Martin
 */
public class Cable implements Serializable{
    private int ID;
    
    private Interface interface1;
    private Interface interface2;
    
    private int delay;

    public Cable(int ID, Interface interface1, Interface interface2, int delay) {
        this.ID = ID;
        this.interface1 = interface1;
        this.interface2 = interface2;
        this.delay = delay;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Interface getInterface1() {
        return interface1;
    }

    public void setInterface1(Interface interface1) {
        this.interface1 = interface1;
    }

    public Interface getInterface2() {
        return interface2;
    }

    public void setInterface2(Interface interface2) {
        this.interface2 = interface2;
    }
    
    
}
