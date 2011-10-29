package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.Editor.DrawPanel.UndoCommands.UndoableRemoveComponents;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public class ActionOnDelete extends AbstractDrawPanelAction {

    public ActionOnDelete(GraphOuterInterface graph, UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow) {
        super(graph, undoManager, drawPanel, mainWindow);
    }

    /**
     * Removes all marked AbstractComponents from graph and all cables connecting those AbstractComponents
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        // remove marked components from graph
        RemovedComponentsWrapper removedComponents = graph.doRemoveMarkedComponents();
        
        // if no component removed
        if(removedComponents == null){
            // do nothing
            return;
        }

        //System.out.println("Removing "+markedComponents.size()+ " components and "+cablesToRemove.size()+" cables" );
        undoManager.undoableEditHappened(
                new UndoableEditEvent(this,
                new UndoableRemoveComponents(graph, removedComponents.getRemovedComponents(), removedComponents.getRemovedCables())));

        // update undo redo buttons
        mainWindow.updateUndoRedoButtons();
       
        // reapaint draw panel
        drawPanel.repaint();
    }
}
