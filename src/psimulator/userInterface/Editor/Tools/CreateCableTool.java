package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.MainTool;
import psimulator.userInterface.Editor.ToolChangeInterface;

/**
 *
 * @author Martin
 */
public class CreateCableTool extends AbstractCreationTool{

    public CreateCableTool(MainTool tool, String name, ImageIcon imageIcon, ToolChangeInterface toolChangeInterface, HwTypeEnum hwType) {
        super(tool, name, imageIcon, toolChangeInterface, hwType);
    }
  
}
