package psimulator.dataLayer.devices;

import psimulator.dataLayer.Network.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractDevice {
    private HwTypeEnum hwType;
    private String name;

    public AbstractDevice(HwTypeEnum hwType, String name) {
        this.hwType = hwType;
        this.name = name;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }

    public String getName() {
        return name;
    }

    
    
}
