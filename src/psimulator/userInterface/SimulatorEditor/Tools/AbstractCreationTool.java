package psimulator.userInterface.SimulatorEditor.Tools;

import javax.swing.ImageIcon;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;

/**
 *
 * @author Martin
 */
public abstract class AbstractCreationTool extends AbstractTool{
    protected HwTypeEnum hwType;
    protected String imagePath;

    public AbstractCreationTool(MainTool tool, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType, String imagePath) {
        super(tool, imageIcon, toolChangeInterface);
        this.hwType = hwType;
        this.imagePath = imagePath;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }

    public String getImagePath() {
        return imagePath;
    }
    
    @Override
    public String getTranslatedName(DataLayerFacade dataLayer){
        return dataLayer.getString(hwType.toString());
    }
    
    @Override
    public abstract String getToolTip(DataLayerFacade dataLayer);
    
    public abstract String getParameterLabel();
    
    public abstract int getParameter();
}
