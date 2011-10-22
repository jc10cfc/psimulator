package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public class CableCreateTool extends AbstractCreationTool{

    public CableCreateTool(Tools tool, String name, ImageIcon imageIcon, HwTypeEnum hwType) {
        super(tool, name, imageIcon, hwType);
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
