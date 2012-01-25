package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;

/**
 *
 * @author Martin
 */
public class ManipulationTool extends AbstractTool{

    public ManipulationTool(MainTool tool, String name, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface) {
        super(tool, name, imageIcon, toolChangeInterface);
    }
}
