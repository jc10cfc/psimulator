package psimulator.userInterface.Editor.DrawPanel;

import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;

/**
 *
 * @author Martin
 */
public interface ToolChangeOuterInterface {
 
    public void removeCurrentMouseListener();
    
    public DrawPanelListenerStrategy getMouseListener(MainTool tool);
    
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener);
}
