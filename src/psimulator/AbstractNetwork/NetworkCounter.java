package psimulator.AbstractNetwork;

import psimulator.AbstractNetwork.xml.EnumMapAdapter;
import java.io.Serializable;
import java.util.EnumMap;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin
 */
public class NetworkCounter implements Serializable {

    private int nextId;
    private int nextMacAddress;
    private EnumMap<HwTypeEnum, Integer> nextNumberMap;

    public NetworkCounter(int nextId, int nextMacAddress) {
        this.nextId = nextId;
        this.nextMacAddress = nextMacAddress;
        this.nextNumberMap = new EnumMap<HwTypeEnum, Integer>(HwTypeEnum.class);
    }

    public NetworkCounter() {
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    @XmlJavaTypeAdapter(EnumMapAdapter.class)
    public EnumMap<HwTypeEnum, Integer> getNextNumberMap() {
        return nextNumberMap;
    }

    public void setNextNumberMap(EnumMap<HwTypeEnum, Integer> nextNumberMap) {
        this.nextNumberMap = nextNumberMap;
    }

    public int getNextID() {
        return nextId;
    }

    public int getNextMacAddress() {
        return nextMacAddress;
    }

    public void putIntoNextNumberMap(HwTypeEnum hwType, Integer value) {
        nextNumberMap.put(hwType, value);
    }

    public Integer getFromNextNumberMap(HwTypeEnum hwType) {
        return nextNumberMap.get(hwType);
    }
    // ---------------------------------------------------------------
    // Martin Svihlik nasledujici metody nepotrebuje
}
