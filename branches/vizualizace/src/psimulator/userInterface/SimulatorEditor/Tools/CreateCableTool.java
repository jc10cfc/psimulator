package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin
 */
public class CreateCableTool extends AbstractCreationTool{

    protected int delay;
    
    public CreateCableTool(MainTool tool, String name, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType, String imagePath, int delay) {
        super(tool, name, imageIcon, toolChangeInterface, hwType, imagePath);
        
        this.delay = delay;
    }
  
    public int getDelay(){
        return delay;
    }

    @Override
    public String getParameterLabel() {
        return " - Delay: ";
    }

    @Override
    public int getParameter() {
        return delay;
    }
    
}
