package psimulator.userInterface.Editor.Actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.Graph;
import psimulator.userInterface.Editor.UndoCommands.UndoableRemoveComponents;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public class ActionOnDelete extends AbstractAction {

    private Graph graph;
    private UndoManager undoManager;
    private DrawPanel drawPanel;
    protected MainWindowInterface mainWindow;

    public ActionOnDelete(Graph graph, UndoManager undoManager, DrawPanel drawPanel, MainWindowInterface mainWindow) {
        this.graph = graph;
        this.undoManager = undoManager;
        this.drawPanel = drawPanel;
        this.mainWindow = mainWindow;
    }

    /**
     * Removes all marked AbstractComponents from graph and all cables connecting those AbstractComponents
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        // get all marked components
        List<AbstractHwComponent> markedComponents = graph.getMarkedHwComponentsCopy();
        
        // put all marked cables to cables toRemove
        List<Cable> cablesToRemove = graph.getMarkedCablesCopy();
        
        // if there is no marked cable or component
        if (markedComponents.isEmpty() && cablesToRemove.isEmpty()) {
            markedComponents = null;
            cablesToRemove = null;
            return;
        }

        // remove marked components from graph
        graph.removeHwComponents(markedComponents);
        
        
        // for all removed components
        for (AbstractHwComponent c : markedComponents) {
            // all its cables add to cablesToRemove
            for(Cable cable: c.getCables()){
                // if collection doesnt contain, than add cable
                if(!cablesToRemove.contains(cable)){
                    cablesToRemove.add(cable);
                }
            }
            
            
            // unmark component
            c.setMarked(false);
        }
        // remove cables from graph
        graph.removeCables(cablesToRemove);
        
        //System.out.println("Removing "+markedComponents.size()+ " components and "+cablesToRemove.size()+" cables" );
        
        undoManager.undoableEditHappened(new UndoableEditEvent(this,
            new UndoableRemoveComponents(markedComponents, cablesToRemove, drawPanel.getGraph(), drawPanel)));
        
        // update undo redo buttons
        mainWindow.updateUndoRedoButtons(); 
        
        // reapaint draw panel
        drawPanel.repaint();
    }
}
