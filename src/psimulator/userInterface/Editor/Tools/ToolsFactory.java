package psimulator.userInterface.Editor.Tools;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.Tools;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class ToolsFactory {
    
    public static List<AbstractTool> getTools(Tools tool, AbstractImageFactory imageFactory) {
        List<AbstractTool> tools = new ArrayList<AbstractTool>();
        
        ImageIcon image = imageFactory.getImageIconForToolbar(tool);
        
        switch(tool){
            case HAND:
                tools.add(new ManipulationTool(tool, "hand", image));
                break;
            case FIT_TO_SIZE:
                tools.add(new ManipulationTool(tool, "fit to size", image));
                break;
            case ROUTER:
                tools.add(new DeviceAddTool(tool, "Router Linux Generic", image, HwTypeEnum.LINUX_ROUTER, 4));
                tools.add(new DeviceAddTool(tool, "Router Cisco Generic", image, HwTypeEnum.CISCO_ROUTER, 4));
                break;
            case SWITCH:
                tools.add(new DeviceAddTool(tool, "Switch Linux Generic", image, HwTypeEnum.LINUX_SWITCH, 4));
                tools.add(new DeviceAddTool(tool, "Switch Cisco Generic", image, HwTypeEnum.CISCO_SWITCH, 4));
                break;
            case END_DEVICE:
                tools.add(new DeviceAddTool(tool, "PC", image, HwTypeEnum.END_DEVICE, 1));
                tools.add(new DeviceAddTool(tool, "Notebook", image, HwTypeEnum.END_DEVICE, 1));
                break;
            case REAL_PC:
                tools.add(new DeviceAddTool(tool, "Real PC", image, HwTypeEnum.REAL_PC, 1));
                break;
            case CABLE:
                tools.add(new CableCreateTool(tool, "Cable", image, HwTypeEnum.CABLE));
                break;
            default:
                break;
        }
        
        return tools;
    }
}
