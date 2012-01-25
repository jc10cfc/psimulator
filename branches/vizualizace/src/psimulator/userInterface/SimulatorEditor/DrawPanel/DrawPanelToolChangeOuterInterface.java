package psimulator.userInterface.Editor.DrawPanel;

import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;

/**
 *
 * @author Martin
 */
public interface DrawPanelToolChangeOuterInterface {
 
    /**
     * Removes current active mouse listener.
     */
    public void removeCurrentMouseListener();
    
    /**
     * Returns mouse listener according to tool in parameter.
     * @param tool 
     * @return MouseListener - listener that corresponds MainTool
     */
    public DrawPanelListenerStrategy getMouseListener(MainTool tool);
    
    /**
     * Sets active mouse listener to listener in pamrameter
     * @param mouseListener 
     */
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener);
}
