package psimulator.userInterface.Editor.Tools;

import java.awt.Image;
import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public abstract class AbstractTool {
    
    protected String name;
    protected Tools tool;
    protected ImageIcon imageIcon;
    
    public AbstractTool(Tools tool, String name, ImageIcon imageIcon) {
        this.tool = tool;
        this.name = name;
        this.imageIcon = imageIcon;
    }

    public String getName() {
        return name;
    }

    public Tools getTool() {
        return tool;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    
    public ImageIcon getImageIcon(int size) {
        return (new ImageIcon(imageIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
    }

    public abstract void setEnabled();
    public abstract void setDisabled();
}
