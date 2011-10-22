package psimulator.dataLayer.devices;

import psimulator.userInterface.Editor.Enums.HwTypeEnum;

/**
 *
 * @author Martin
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
