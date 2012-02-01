package psimulator.userInterface.SimulatorEditor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.HwComponentProperties;

/**
 *
 * @author Martin
 */
public class ActionOpenProperties extends AbstractDrawPanelAction{

    private DataLayerFacade dataLayer;
    
    public ActionOpenProperties(UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(undoManager, drawPanel, mainWindow);
        
        this.dataLayer = dataLayer;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();
        
        if(graph.getMarkedAbstractHWComponentsCount() !=1){
            // should never happen
            return;
        }
        
        // get component
        AbstractHwComponent abstractHwComponent = graph.getMarkedHwComponentsCopy().get(0);
        
        // unmark all components
        graph.doUnmarkAllComponents();
        
        // repaint draw Panel
        drawPanel.repaint();
        
        // open properties window
        HwComponentProperties hwComponentProperties = new HwComponentProperties(mainWindow.getMainWindowComponent(), dataLayer, abstractHwComponent);
        
        
    }
    
}
