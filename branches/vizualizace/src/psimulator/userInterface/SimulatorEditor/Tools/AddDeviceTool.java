package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin
 */
public class AddDeviceTool extends AbstractCreationTool{

    protected int interfaces;
    
    public AddDeviceTool(MainTool tool, String name, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType, String imagePath, int interfaces) {
        super(tool, name, imageIcon, toolChangeInterface, hwType, imagePath);
        this.interfaces = interfaces;
    }

    public int getInterfaces() {
        return interfaces;
    }

    @Override
    public String getParameterLabel() {
        return " - Interfaces: ";
    }

    @Override
    public int getParameter() {
        return interfaces;
    }
    
}
