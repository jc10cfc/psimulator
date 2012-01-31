package psimulator.dataLayer;

import psimulator.dataLayer.devices.HwDevice;
import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.devices.AbstractDevice;
import psimulator.dataLayer.devices.CableDevice;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin
 */
public class HardwareDevicesManager {
    
    /*
    
    public List<AbstractDevice> getDevices(MainTool tool) {
        List<AbstractDevice> devices = new ArrayList<AbstractDevice> ();
        
        switch(tool){
            case ADD_ROUTER:
                devices.add(new HwDevice(HwTypeEnum.LINUX_ROUTER, "Linux Router Generic", 4));
                devices.add(new HwDevice(HwTypeEnum.CISCO_ROUTER, "CISCO Router Generic", 4));
                break;
            case ADD_SWITCH:
                devices.add(new HwDevice(HwTypeEnum.LINUX_SWITCH, "Linux Switch Generic", 4));
                devices.add(new HwDevice(HwTypeEnum.LINUX_ROUTER, "Linux Switch Generic", 4));
                break;
            case ADD_END_DEVICE:
                devices.add(new HwDevice(HwTypeEnum.END_DEVICE, "PC", 1));
                devices.add(new HwDevice(HwTypeEnum.END_DEVICE, "MAC", 1));
                break;
            case ADD_CABLE:
                devices.add(new CableDevice(HwTypeEnum.CABLE, "Cable"));
                break;
            case ADD_REAL_PC:
                devices.add(new HwDevice(HwTypeEnum.REAL_PC, "Cable", 1));
            default:
                break;
        }
        
        return devices;
    }
    */
}
