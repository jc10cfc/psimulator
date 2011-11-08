package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public class ActionFitToSize extends AbstractDrawPanelAction{
    
    public ActionFitToSize(UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow) {
        super(undoManager, drawPanel, mainWindow);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        drawPanel.doFitToGraphSize();
    }
    
}
