package psimulator.dataLayer.devices;

import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin
 */
public class HwDevice extends AbstractDevice{
    private int interfaces;

    public HwDevice(HwTypeEnum hwType, String name, int interfaces) {
        super(hwType, name);
        this.interfaces = interfaces;
    }

    public int getInterfaces() {
        return interfaces;
    }   
}
