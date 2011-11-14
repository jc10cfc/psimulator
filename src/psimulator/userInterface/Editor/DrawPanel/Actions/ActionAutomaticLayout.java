package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm.AutomaticLayoutFacade;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public class ActionAutomaticLayout extends AbstractDrawPanelAction{
    
    public ActionAutomaticLayout(UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow) {
        super(undoManager, drawPanel, mainWindow);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("tady");
        
        AutomaticLayoutFacade geneticFasade = new AutomaticLayoutFacade();
        
        geneticFasade.automaticLayout((Graph)drawPanel.getGraphOuterInterface());
    }
    
}
