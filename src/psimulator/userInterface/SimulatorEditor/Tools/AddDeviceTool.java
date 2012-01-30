package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;

/**
 *
 * @author Martin
 */
public class AddDeviceTool extends AbstractCreationTool{

    protected int interfaces;
    protected String imagePath;
    
    public AddDeviceTool(MainTool tool, String name, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType, int interfaces, String imagePath) {
        super(tool, name, imageIcon, toolChangeInterface, hwType);
        this.imagePath = imagePath;
        this.interfaces = interfaces;
    }

    public int getInterfaces() {
        return interfaces;
    }
    
    public String getImagePath() {
        return imagePath;
    }
}
