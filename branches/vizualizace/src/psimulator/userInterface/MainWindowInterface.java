package psimulator.userInterface;

import javax.swing.JRootPane;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.logicLayer.Controller;

/**
 *
 * @author Martin
 */
public interface MainWindowInterface {
    // used by view:
    public void updateUndoRedoButtons();
    public void updateZoomButtons();
    public void updateToolBarIconsSize(ToolbarIconSizeEnum size);
    public JRootPane getRootPane();
    
    // used by controller:
    public void initView(Controller controller);
    
}
