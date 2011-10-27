package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.DrawPanel.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.ToolChangeOuterInterface;

/**
 *
 * @author Martin
 */
public class CreateCableTool extends AbstractCreationTool{

    public CreateCableTool(MainTool tool, String name, ImageIcon imageIcon, ToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType) {
        super(tool, name, imageIcon, toolChangeInterface, hwType);
    }
  
}
