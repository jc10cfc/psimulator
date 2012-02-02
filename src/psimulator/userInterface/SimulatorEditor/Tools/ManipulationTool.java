package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin
 */
public class ManipulationTool extends AbstractTool{

    public ManipulationTool(MainTool tool, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface) {
        super(tool, imageIcon, toolChangeInterface);
    }

    @Override
    public String getToolTip(DataLayerFacade dataLayer) {
        return "bla";
    }

    @Override
    public String getTranslatedName(DataLayerFacade dataLayer) {
        return dataLayer.getString("HAND");
    }
}
