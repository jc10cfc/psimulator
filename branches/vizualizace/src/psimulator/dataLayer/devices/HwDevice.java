package psimulator.dataLayer.devices;

import psimulator.dataLayer.Network.Components.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
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
