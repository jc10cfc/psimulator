package psimulator.AbstractNetwork;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Network implements Serializable{
    private int ID;
    private String name;
    
    private List<Cable> cables;
    private List<Device> devices;

    /*
    private int idCounter = 0;
    
    public int getNewId(){
        return idCounter++;
    }*/
}
