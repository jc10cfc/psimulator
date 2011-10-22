package psimulator.userInterface.Editor.Tools;

import java.util.ArrayList;
import java.util.List;
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
        
        String path;
        
        switch(tool){
            case HAND:
                path = AbstractImageFactory.HAND_PATH;
                
                tools.add(new ManipulationTool(tool, "hand", imageFactory.getImageIconForToolbar(tool, path)));
                break;
            case FIT_TO_SIZE:
                // TODO !! DODELAT
                tools.add(new ManipulationTool(tool, "fit to size", imageFactory.getImageIconForToolbar(tool)));
                break;
            case ADD_ROUTER:
                path = AbstractImageFactory.ROUTER_PATH;
                
                tools.add(new AddDeviceTool(tool, "Router Linux Generic", imageFactory.getImageIconForToolbar(tool, path), 
                        HwTypeEnum.LINUX_ROUTER, 4, path));
                tools.add(new AddDeviceTool(tool, "Router Cisco Generic", imageFactory.getImageIconForToolbar(tool, path), 
                        HwTypeEnum.CISCO_ROUTER, 4, path));
                break;
            case ADD_SWITCH:
                path = AbstractImageFactory.SWITCH_PATH;
                
                tools.add(new AddDeviceTool(tool, "Switch Linux Generic", imageFactory.getImageIconForToolbar(tool, path), HwTypeEnum.LINUX_SWITCH, 4, path));
                tools.add(new AddDeviceTool(tool, "Switch Cisco Generic", imageFactory.getImageIconForToolbar(tool, path), HwTypeEnum.CISCO_SWITCH, 4, path));
                break;
            case ADD_END_DEVICE:
                path = AbstractImageFactory.END_DEVICE_PATH;
                tools.add(new AddDeviceTool(tool, "PC", imageFactory.getImageIconForToolbar(tool, path), HwTypeEnum.END_DEVICE, 1, path));
                tools.add(new AddDeviceTool(tool, "Notebook", imageFactory.getImageIconForToolbar(tool, path), HwTypeEnum.END_DEVICE, 1, path));
                break;
            case ADD_REAL_PC:
                path = AbstractImageFactory.REAL_PC_PATH;
                
                tools.add(new AddDeviceTool(tool, "Real PC", imageFactory.getImageIconForToolbar(tool, path), HwTypeEnum.REAL_PC, 1, path));
                break;
            case ADD_CABLE:
                path = AbstractImageFactory.CABLE_PATH;
                
                tools.add(new CreateCableTool(tool, "Cable", imageFactory.getImageIconForToolbar(tool, path), HwTypeEnum.CABLE));
                break;
            default:
                break;
        }
        
        return tools;
    }
}
