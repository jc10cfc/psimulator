package psimulator.userInterface.SimulatorEditor.Tools;

import java.util.ArrayList;
import java.util.List;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class ToolsFactory {
    
    public static List<AbstractTool> getTools(MainTool tool, AbstractImageFactory imageFactory, DrawPanelToolChangeOuterInterface toolChangeInterface) {
        List<AbstractTool> tools = new ArrayList<AbstractTool>();
        
        String path;
        
        switch(tool){
            case HAND:
                path = AbstractImageFactory.TOOL_HAND_PATH;
                
                tools.add(new ManipulationTool(tool, "hand", imageFactory.getImageIconForToolbar(tool, path), toolChangeInterface));
                break;
            case ADD_ROUTER:
                path = AbstractImageFactory.TOOL_ROUTER_PATH;
                
                tools.add(new AddDeviceTool(tool, "Router Linux Generic", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_ROUTER, 4, path));
                tools.add(new AddDeviceTool(tool, "Router Cisco Generic", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface,HwTypeEnum.CISCO_ROUTER, 20, path));
                break;
            case ADD_SWITCH:
                path = AbstractImageFactory.TOOL_SWITCH_PATH;
                
                tools.add(new AddDeviceTool(tool, "Switch Linux Generic", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_SWITCH, 4, path));
                tools.add(new AddDeviceTool(tool, "Switch Cisco Generic", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CISCO_SWITCH, 4, path));
                break;
            case ADD_END_DEVICE:
                path = AbstractImageFactory.TOOL_END_DEVICE_PC_PATH;
                tools.add(new AddDeviceTool(tool, "PC", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.END_DEVICE, 1, path));
                
                path = AbstractImageFactory.TOOL_END_DEVICE_NOTEBOOK_PATH;
                tools.add(new AddDeviceTool(tool, "Notebook", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.END_DEVICE, 1, path));
                
                path = AbstractImageFactory.TOOL_END_DEVICE_WORKSTATION_PATH;
                tools.add(new AddDeviceTool(tool, "Workstation", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.END_DEVICE, 4, path));
                break;
            case ADD_REAL_PC:
                path = AbstractImageFactory.TOOL_REAL_PC_PATH;
                
                tools.add(new AddDeviceTool(tool, "Real PC", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.REAL_PC, 1, path));
                break;
            case ADD_CABLE:
                path = AbstractImageFactory.TOOL_CABLE_PATH;
                
                tools.add(new CreateCableTool(tool, "Cable", imageFactory.getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CABLE));
                break;
            default:
                break;
        }
        
        return tools;
    }
}
