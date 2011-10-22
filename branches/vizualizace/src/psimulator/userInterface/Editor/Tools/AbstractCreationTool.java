package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public abstract class AbstractCreationTool extends AbstractTool{
    protected HwTypeEnum hwType;

    public AbstractCreationTool(Tools tool, String name, ImageIcon imageIcon, HwTypeEnum hwType) {
        super(tool, name, imageIcon);
        this.hwType = hwType;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }
}
