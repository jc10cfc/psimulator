package psimulator.userInterface.Editor.DrawPanel.Actions;

import javax.swing.AbstractAction;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public abstract class AbstractDrawPanelAction extends AbstractAction{
 
    protected UndoManager undoManager;
    protected DrawPanelInnerInterface drawPanel;
    protected MainWindowInnerInterface mainWindow;
    
    public AbstractDrawPanelAction(UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow) {
        this.undoManager = undoManager;
        this.drawPanel = drawPanel;
        this.mainWindow = mainWindow;
    }
}
