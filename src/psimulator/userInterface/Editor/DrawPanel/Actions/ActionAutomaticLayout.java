package psimulator.userInterface.Editor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Dialogs.ProgressBarGeneticDialog;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm.AutomaticLayoutFacade;
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
        
        ProgressBarGeneticDialog dialog = new ProgressBarGeneticDialog(mainWindow, dataLayer);
        
        dialog.setVisible(true);
        
        
        
        
        /*
        AutomaticLayoutFacade geneticFasade = new AutomaticLayoutFacade();

        geneticFasade.automaticLayout((Graph) drawPanel.getGraphOuterInterface());
        */
    }
}
