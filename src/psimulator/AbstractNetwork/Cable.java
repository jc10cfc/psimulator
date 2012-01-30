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
}
