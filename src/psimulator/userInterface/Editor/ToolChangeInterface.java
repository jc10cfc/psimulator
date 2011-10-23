package psimulator.userInterface.Editor;

import psimulator.userInterface.Editor.Enums.MainTool;
import psimulator.userInterface.MouseActionListeners.DrawPanelListenerStrategy;

/**
 *
 * @author Martin
 */
public interface ToolChangeInterface {
 
    public void removeCurrentMouseListener();
    
    public DrawPanelListenerStrategy getMouseListener(MainTool tool);
    
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener);
}
