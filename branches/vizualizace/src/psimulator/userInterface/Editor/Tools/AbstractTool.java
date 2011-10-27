package psimulator.userInterface.Editor.Tools;

import java.awt.Image;
import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.ToolChangeOuterInterface;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;

/**
 *
 * @author Martin
 */
public abstract class AbstractTool {
    
    protected String name;
    protected MainTool tool;
    protected ImageIcon imageIcon;
    protected ToolChangeOuterInterface toolChangeInterface;
    
    public AbstractTool(MainTool tool, String name, ImageIcon imageIcon, ToolChangeOuterInterface toolChangeInterface) {
        this.tool = tool;
        this.name = name;
        this.imageIcon = imageIcon;
        this.toolChangeInterface = toolChangeInterface;
    }

    public String getName() {
        return name;
    }

    public MainTool getTool() {
        return tool;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    
    public ImageIcon getImageIcon(int size) {
        return (new ImageIcon(imageIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
    }
 
    /**
     * Sets proper DrawPanelListenerStrategy in toolChangeInterface
     */
    public void setEnabled(){
        // remove current mouse listener
        toolChangeInterface.removeCurrentMouseListener();
        // get mouse listener for needed tool
        DrawPanelListenerStrategy listener =  toolChangeInterface.getMouseListener(tool);
        // tell mouse listener about tool change
        listener.setTool(this);
        // add mouse listener to toolChangeInterface
        toolChangeInterface.setCurrentMouseListener(listener);
    }
}
