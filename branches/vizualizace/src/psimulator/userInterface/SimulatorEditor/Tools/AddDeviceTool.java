package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.dataLayer.Network.Components.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class AddDeviceTool extends AbstractCreationTool{

    protected int interfaces;
    
    public AddDeviceTool(MainTool tool, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType, String imagePath, int interfaces) {
        super(tool, imageIcon, toolChangeInterface, hwType, imagePath);
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

    @Override
    public String getToolTip(DataLayerFacade dataLayer) {
        return getTranslatedName(dataLayer) + " - "+ dataLayer.getString("INTERFACES") +": " + getParameter();
    }
    
}
