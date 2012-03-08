package psimulator.userInterface.SimulatorEditor.Tools;

import java.util.ArrayList;
import java.util.List;
import shared.Components.HwTypeEnum;
import psimulator.dataLayer.Singletons.ImageFactory.ImageFactorySingleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class ToolsFactory {
    
    public static List<AbstractTool> getTools(MainTool tool, DrawPanelToolChangeOuterInterface toolChangeInterface) {
        List<AbstractTool> tools = new ArrayList<AbstractTool>();
        
        String path;
        
        switch(tool){
            case DRAG_MOVE:
                path = ImageFactorySingleton.TOOL_DRAG_MOVE_PATH;
                
                tools.add(new ManipulationTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), toolChangeInterface));
                break;
            case HAND:
                path = ImageFactorySingleton.TOOL_HAND_PATH;
                
                tools.add(new ManipulationTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), toolChangeInterface));
                break;
            case ADD_ROUTER:
                //path = AbstractImageFactory.TOOL_ROUTER_PATH;
                path = ImageFactorySingleton.TOOL_ROUTER_LINUX_PATH;
                
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_ROUTER, path, 2));
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_ROUTER, path, 4));
                
                path = ImageFactorySingleton.TOOL_ROUTER_CISCO_PATH;
                
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface,HwTypeEnum.CISCO_ROUTER, path, 2));
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface,HwTypeEnum.CISCO_ROUTER, path, 4));
                break;
            case ADD_SWITCH:
                path = ImageFactorySingleton.TOOL_SWITCH_PATH;
                //path = ImageFactorySingleton.TOOL_SWITCH_LINUX_PATH;
                
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_SWITCH, path, 4));
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_SWITCH, path, 8));
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.LINUX_SWITCH, path, 16));
                /*
                path = ImageFactorySingleton.TOOL_SWITCH_CISCO_PATH;
                
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CISCO_SWITCH, path, 4));
                tools.add(new AddDeviceTool(tool,  ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CISCO_SWITCH, path, 8));
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CISCO_SWITCH, path, 16));*/
                break;
            case ADD_END_DEVICE:
                path = ImageFactorySingleton.TOOL_END_DEVICE_PC_PATH;
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.END_DEVICE_PC, path, 1));
                
                path = ImageFactorySingleton.TOOL_END_DEVICE_NOTEBOOK_PATH;
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.END_DEVICE_NOTEBOOK, path, 1));
                
                path = ImageFactorySingleton.TOOL_END_DEVICE_WORKSTATION_PATH;
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.END_DEVICE_WORKSTATION, path, 4));
                break;
            case ADD_REAL_PC:
                path = ImageFactorySingleton.TOOL_REAL_PC_PATH;
                
                tools.add(new AddDeviceTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.REAL_PC, path, 1));
                break;
            case ADD_CABLE:
                path = ImageFactorySingleton.TOOL_CABLE_ETHERNET_PATH;
                tools.add(new CreateCableTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CABLE_ETHERNET, path, 10));
                
                path = ImageFactorySingleton.TOOL_CABLE_OPTICS_PATH;
                tools.add(new CreateCableTool(tool, ImageFactorySingleton.getInstance().getImageIconForToolbar(tool, path), 
                        toolChangeInterface, HwTypeEnum.CABLE_OPTIC, path, 5));
                break;
            default:
                break;
        }
        
        return tools;
    }
}
