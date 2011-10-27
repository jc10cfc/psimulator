package psimulator.userInterface.Editor;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author Martin
 */
public abstract class EditorOuterInterface extends JPanel{
    
    public EditorOuterInterface(BorderLayout borderLayout){
        super(borderLayout);
    }
    
    public abstract boolean canUndo();
    public abstract boolean canRedo();
    public abstract void undo();
    public abstract void redo();
    public abstract boolean canZoomIn();
    public abstract boolean canZoomOut();
    public abstract void zoomIn();
    public abstract void zoomOut();
    public abstract void zoomReset();
    
    public abstract void init();
}
