package psimulator.dataLayer;

import psimulator.dataLayer.devices.HwDevice;
import java.util.ArrayList;
import java.util.List;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.dataLayer.devices.AbstractDevice;
import psimulator.dataLayer.devices.CableDevice;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public class HardwareDevicesManager {
    
    public List<AbstractDevice> getDevices(Tools tool) {
        List<AbstractDevice> devices = new ArrayList<AbstractDevice> ();
        
        switch(tool){
            case ROUTER:
                devices.add(new HwDevice(HwTypeEnum.LINUX_ROUTER, "Linux Router Generic", 4));
                devices.add(new HwDevice(HwTypeEnum.CISCO_ROUTER, "CISCO Router Generic", 4));
                break;
            case SWITCH:
                devices.add(new HwDevice(HwTypeEnum.LINUX_SWITCH, "Linux Switch Generic", 4));
                devices.add(new HwDevice(HwTypeEnum.LINUX_ROUTER, "Linux Switch Generic", 4));
                break;
            case END_DEVICE:
                devices.add(new HwDevice(HwTypeEnum.END_DEVICE, "PC", 1));
                devices.add(new HwDevice(HwTypeEnum.END_DEVICE, "MAC", 1));
                break;
            case CABLE:
                devices.add(new CableDevice(HwTypeEnum.CABLE, "Cable"));
                break;
            case REAL_PC:
                devices.add(new HwDevice(HwTypeEnum.REAL_PC, "Cable", 1));
            default:
                break;
        }
        
        return devices;
    }
    
}
