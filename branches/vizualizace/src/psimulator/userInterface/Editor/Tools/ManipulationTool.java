package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.MainTool;
import psimulator.userInterface.Editor.ToolChangeInterface;

/**
 *
 * @author Martin
 */
public class ManipulationTool extends AbstractTool{

    public ManipulationTool(MainTool tool, String name, ImageIcon imageIcon, ToolChangeInterface toolChangeInterface) {
        super(tool, name, imageIcon, toolChangeInterface);
    }
}
