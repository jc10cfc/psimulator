package psimulator.userInterface.Editor.Tools;

import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.DrawPanel.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelToolChangeOuterInterface;

/**
 *
 * @author Martin
 */
public abstract class AbstractCreationTool extends AbstractTool{
    protected HwTypeEnum hwType;

    public AbstractCreationTool(MainTool tool, String name, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface, HwTypeEnum hwType) {
        super(tool, name, imageIcon, toolChangeInterface);
        this.hwType = hwType;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }
}
