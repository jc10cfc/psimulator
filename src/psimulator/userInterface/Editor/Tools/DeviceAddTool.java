package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public class DeviceAddTool extends AbstractCreationTool{

    private int interfaces;
    
    public DeviceAddTool(Tools tool, String name, ImageIcon imageIcon, HwTypeEnum hwType, int interfaces) {
        super(tool, name, imageIcon, hwType);
    }

    public int getInterfaces() {
        return interfaces;
    }

    @Override
    public void setEnabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDisabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
