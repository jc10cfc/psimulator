package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public class AddDeviceTool extends AbstractCreationTool{

    private int interfaces;
    protected String imagePath;
    
    public AddDeviceTool(Tools tool, String name, ImageIcon imageIcon, HwTypeEnum hwType, int interfaces, String imagePath) {
        super(tool, name, imageIcon, hwType);
        this.imagePath = imagePath;
        this.interfaces = interfaces;
    }

    public int getInterfaces() {
        return interfaces;
    }
    
    public String getImagePath() {
        return imagePath;
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
