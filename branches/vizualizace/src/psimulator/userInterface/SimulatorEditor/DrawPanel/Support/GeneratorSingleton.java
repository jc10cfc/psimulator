package psimulator.userInterface.SimulatorEditor.DrawPanel.Support;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin
 */
public class GeneratorSingleton {

    /**
     * Unique identificator
     */
    private int nextId = 0;
    //
    private EnumMap<HwTypeEnum, Integer> nextNumberMap;
    //
    private int nextMacAddress = 0;

    private GeneratorSingleton() {

        nextNumberMap = new EnumMap<HwTypeEnum, Integer>(HwTypeEnum.class);

        for (HwTypeEnum hwTypeEnum : HwTypeEnum.values()) {
            nextNumberMap.put(hwTypeEnum, new Integer(0));
        }
    }

    public static GeneratorSingleton getInstance() {
        return IdGeneratorSingletonHolder.INSTANCE;
    }

    private static class IdGeneratorSingletonHolder {

        private static final GeneratorSingleton INSTANCE = new GeneratorSingleton();
    }
    
    /**
     * Sets all counters to zero
     */
    public void initialize(){
        // init nextId
        nextId = 0;
        
        // init next mac address
        nextMacAddress = 0;
        
        // init next number map
        for (HwTypeEnum hwTypeEnum : HwTypeEnum.values()) {
            nextNumberMap.put(hwTypeEnum, new Integer(0));
        }
    }
    
    
    public void setNextId(int nextId){
        this.nextId = nextId;
    }
    
    public void setNextMacAddress(int nextMacAddress){
        this.nextMacAddress = nextMacAddress;
    }
    
    public void putIntoNextNumberMap(HwTypeEnum hwType, Integer value){
        this.nextNumberMap.put(hwType, value);
    }
    
    public Integer getFromNextNumberMap(HwTypeEnum hwType){
        return nextNumberMap.get(hwType);
    }  

    /**
     * Returns free id and incremets id by 1.
     *
     * @return Free id.
     */
    public int getNextId() {
        return nextId++;
    }
    
    public int getCurrentId(){
        return nextId;
    }
    
    public int getCurrentMacAddress(){
        return nextMacAddress;
    }

    /**
     * Creates list with names of interfaces for one single device. Names are
     * created according to hwType.
     *
     * @param hwTpe HwType for wich the names are generated.
     * @param count Count of generated names.
     * @return List with generated names.
     */
    public List<String> getInterfaceNames(HwTypeEnum hwType, int count) {
        List<String> names = new ArrayList<String>();

        int counter = 0;
        String prefix = "";
        String suffix = "";

        switch (hwType) {
            case LINUX_ROUTER:
            case LINUX_SWITCH:
            case END_DEVICE_NOTEBOOK:
            case END_DEVICE_PC:
            case END_DEVICE_WORKSTATION:
            case REAL_PC:
                prefix = "Eth";
                break;
            case CISCO_ROUTER:
            case CISCO_SWITCH:
                prefix = "FastEthernet0/";
                break;
            default:
                // this should never happen
                System.err.println("error in GeneratorSingleton2");
        }

        for (int i = 0; i < count; i++) {
            names.add(prefix + counter + suffix);
            counter++;
        }

        return names;
    }

    /**
     * Generates name for hwType. The name has a number from a number line for
     * each hwType extra. For example for PC will be generated PC0, for Switch
     * swich
     *
     * @param hwTp
     * @return generated name.
     */
    public String getNextDeviceName(HwTypeEnum hwType) {
        String name;

        String prefix = "";
        int number = 0;

        switch (hwType) {
            case LINUX_ROUTER:
            case CISCO_ROUTER:
                prefix = "Router";
                // get number
                number = nextNumberMap.get(HwTypeEnum.LINUX_ROUTER).intValue();
                // increase counter
                nextNumberMap.put(HwTypeEnum.LINUX_ROUTER, new Integer(number + 1));
                break;
            case LINUX_SWITCH:
            case CISCO_SWITCH:
                prefix = "Switch";
                // get number
                number = nextNumberMap.get(HwTypeEnum.LINUX_SWITCH).intValue();
                // increase counter
                nextNumberMap.put(HwTypeEnum.LINUX_SWITCH, new Integer(number + 1));
                break;
            case END_DEVICE_NOTEBOOK:
                prefix = "Notebook";
                // get number
                number = nextNumberMap.get(hwType).intValue();
                // increase counter
                nextNumberMap.put(hwType, new Integer(number + 1));
                break;
            case END_DEVICE_PC:
                prefix = "PC";
                // get number
                number = nextNumberMap.get(hwType).intValue();
                // increase counter
                nextNumberMap.put(hwType, new Integer(number + 1));
                break;
            case END_DEVICE_WORKSTATION:
                prefix = "Workstation";
                // get number
                number = nextNumberMap.get(hwType).intValue();
                // increase counter
                nextNumberMap.put(hwType, new Integer(number + 1));
                break;
            case REAL_PC:
                prefix = "RealPC";
                // get number
                number = nextNumberMap.get(hwType).intValue();
                // increase counter
                nextNumberMap.put(hwType, new Integer(number + 1));
                break;
            default:
                // this should never happen
                System.err.println("error in GeneratorSingleton1");
        }

        name = prefix + number;

        return name;
    }

    /**
     * Creates mac address in format AA-11-E0-XX-XX-XX, where Xes are generated
     * in line from 0 in each project.
     *
     * @return Generated mac address
     */
    public String getNextMacAddress() {
        String macAddress;

        String macAddressManufacturerPrefix = "AA-11-E0-";
        String macAddressDeviceSuffix = "";

        String tmp = Integer.toHexString(nextMacAddress).toUpperCase();

        // fill the rest of address
        for (int i = 0; i < 6; i++) {
            // insert zeros until tmp hit
            if (6 - i <= tmp.length()) {
                macAddressDeviceSuffix += tmp.charAt(tmp.length() - (6 - i));
            } else {
                macAddressDeviceSuffix += "0";
            }

            // if we have to make dash
            if (i % 2 == 1 && i < 6 - 1) {
                macAddressDeviceSuffix += "-";
            }
        }

        // increase counter
        nextMacAddress++;

        // glue two parts together
        macAddress = macAddressManufacturerPrefix + macAddressDeviceSuffix;

        return macAddress;
    }
}
