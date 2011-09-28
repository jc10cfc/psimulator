package psimulator.userInterface;

import javax.swing.JRootPane;

/**
 *
 * @author Martin
 */
public interface MainWindowInterface {
    public void updateUndoRedoButtons();
    public void updateZoomButtons();
    public JRootPane getRootPane();
    
    //public void repaint();
}
