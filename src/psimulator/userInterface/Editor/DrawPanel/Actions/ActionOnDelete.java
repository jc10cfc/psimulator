package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.BundleOfCables;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.Editor.DrawPanel.UndoCommands.UndoableRemoveComponents;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public class ActionOnDelete extends AbstractAction {

    private GraphOuterInterface graph;
    private UndoManager undoManager;
    private DrawPanelInnerInterface drawPanel;
    protected MainWindowInnerInterface mainWindow;

    public ActionOnDelete(GraphOuterInterface graph, UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow) {
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
            for (BundleOfCables boc : c.getBundleOfCableses()) {
                for (Cable cable : boc.getCables()) {
                    // if collection doesnt contain, than add cable
                    if (!cablesToRemove.contains(cable)) {
                        cablesToRemove.add(cable);
                    }
                }
            }
            // unmark component
            graph.doMarkComponentWithCables(c, false);
        }
        // remove cables from graph
        graph.removeCables(cablesToRemove);

        //System.out.println("Removing "+markedComponents.size()+ " components and "+cablesToRemove.size()+" cables" );
        undoManager.undoableEditHappened(
                new UndoableEditEvent(this,
                new UndoableRemoveComponents(graph, markedComponents, cablesToRemove)));

        // update undo redo buttons
        mainWindow.updateUndoRedoButtons();

        
        
        // reapaint draw panel
        drawPanel.repaint();
        drawPanel.repaint();
    }
}
