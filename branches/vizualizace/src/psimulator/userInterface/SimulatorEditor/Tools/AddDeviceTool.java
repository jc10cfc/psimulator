package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.DrawPanel.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelToolChangeOuterInterface;

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
