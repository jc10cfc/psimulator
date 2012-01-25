package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Dialogs.ProgressBarGeneticDialog;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm.GeneticGraph;
import psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm.VisualizePanel;
import psimulator.userInterface.Editor.DrawPanel.UndoCommands.UndoableChagePositionOfAllComponents;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public class ActionAutomaticLayout extends AbstractDrawPanelAction {

    private DataLayerFacade dataLayer;

    public ActionAutomaticLayout(UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(undoManager, drawPanel, mainWindow);
        this.dataLayer = dataLayer;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        ProgressBarGeneticDialog dialog = new ProgressBarGeneticDialog(mainWindow, dataLayer, (Graph) drawPanel.getGraphOuterInterface());

        dialog.startGenetic();
        
        dialog.setVisible(true);


        if (dialog.isSuccess()) {
            GeneticGraph graph = dialog.getGeneticGraph();

            
            HashMap<AbstractHwComponent, Dimension> movedComponentsMap = drawPanel.getGraphOuterInterface().doChangePositions(graph);
            
            undoManager.undoableEditHappened(new UndoableEditEvent(this,
                    new UndoableChagePositionOfAllComponents(drawPanel.getGraphOuterInterface(), movedComponentsMap)));

            // update Undo and Redo buttons
            mainWindow.updateUndoRedoButtons();
            
            // repaint draw Panel
            drawPanel.repaint();
            
            
            /*
            JFrame visualizeFrame = new JFrame("Vizualizace prubehu algoritmu");
            VisualizePanel visualizePanel = new VisualizePanel();
            visualizeFrame.add(visualizePanel);

            visualizeFrame.setSize(new Dimension(800, 600));

            visualizeFrame.setVisible(true);

            visualizePanel.setGraph(graph);
            visualizePanel.repaint();
            visualizeFrame.revalidate();
            */
        }
    }
}
