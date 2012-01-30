package psimulator.AbstractNetwork;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Device implements Serializable{
    private int ID;
    private HwTypeEnum hwType;
    private String name;

    private int interfaceCount;
    private int x;
    private int y;
    
    private List<Interface> interfaces;
}
