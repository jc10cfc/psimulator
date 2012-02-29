package psimulator.dataLayer.devices;

import psimulator.dataLayer.Network.Components.HwTypeEnum;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class CableDevice extends AbstractDevice{
    
    public CableDevice(HwTypeEnum hwType, String name) {
        super(hwType, name);
    }
}
