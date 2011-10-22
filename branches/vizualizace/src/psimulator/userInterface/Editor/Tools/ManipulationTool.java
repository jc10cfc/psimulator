package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public class ManipulationTool extends AbstractTool{

    public ManipulationTool(Tools tool, String name, ImageIcon imageIcon) {
        super(tool, name, imageIcon);
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
