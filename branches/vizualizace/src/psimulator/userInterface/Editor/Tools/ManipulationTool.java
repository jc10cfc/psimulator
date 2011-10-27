package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.ToolChangeOuterInterface;

/**
 *
 * @author Martin
 */
public class ManipulationTool extends AbstractTool{

    public ManipulationTool(MainTool tool, String name, ImageIcon imageIcon, ToolChangeOuterInterface toolChangeInterface) {
        super(tool, name, imageIcon, toolChangeInterface);
    }
}
