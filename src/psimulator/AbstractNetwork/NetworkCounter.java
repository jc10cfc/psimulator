package psimulator.AbstractNetwork;

import java.io.Serializable;
import java.util.EnumMap;

/**
 *
 * @author Martin
 */
public class NetworkCounter implements Serializable{
    private int nextId;
    private int nextMacAddress;
    private EnumMap<HwTypeEnum, Integer> nextNumberMap;

    public NetworkCounter(int nextId, int nextMacAddress) {
        this.nextId = nextId;
        this.nextMacAddress = nextMacAddress;
        this.nextNumberMap = new EnumMap<HwTypeEnum, Integer>(HwTypeEnum.class);
    }

    public int getNextID() {
        return nextId;
    }

    public int getNextMacAddress() {
        return nextMacAddress;
    }
    
    public void putIntoNextNumberMap(HwTypeEnum hwType, Integer value){
        nextNumberMap.put(hwType, value);
    }
    
    public Integer getFromNextNumberMap(HwTypeEnum hwType){
        return nextNumberMap.get(hwType);
    }  
    
    
    // ---------------------------------------------------------------
    // Martin Svihlik nasledujici metody nepotrebuje
}
