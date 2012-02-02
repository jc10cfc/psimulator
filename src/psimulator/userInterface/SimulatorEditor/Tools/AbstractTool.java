package psimulator.userInterface.SimulatorEditor.Tools;

import java.awt.Image;
import javax.swing.ImageIcon;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelToolChangeOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;

/**
 *
 * @author Martin
 */
public abstract class AbstractTool {
    
    protected MainTool tool;
    protected ImageIcon imageIcon;
    protected DrawPanelToolChangeOuterInterface toolChangeInterface;
    
    public AbstractTool(MainTool tool, ImageIcon imageIcon, DrawPanelToolChangeOuterInterface toolChangeInterface) {
        this.tool = tool;
        this.imageIcon = imageIcon;
        this.toolChangeInterface = toolChangeInterface;
    }

     public abstract String getTranslatedName(DataLayerFacade dataLayer);
    
    public abstract String getToolTip(DataLayerFacade dataLayer);

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
