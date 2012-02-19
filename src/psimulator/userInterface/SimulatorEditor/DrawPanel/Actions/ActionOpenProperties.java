package psimulator.userInterface.SimulatorEditor.DrawPanel.Actions;

import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.CableProperties;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.HwComponentProperties;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class ActionOpenProperties extends AbstractDrawPanelAction {

    private DataLayerFacade dataLayer;

    public ActionOpenProperties(UndoManager undoManager, DrawPanelInnerInterface drawPanel, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(undoManager, drawPanel, mainWindow);

        this.dataLayer = dataLayer;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        GraphOuterInterface graph = drawPanel.getGraphOuterInterface();

        // only one AbstractHwComponent or one cable should be marked

        // if one AbstractHwComponent marked
        if (graph.getMarkedAbstractHWComponentsCount() == 1) {
            // get component
            AbstractHwComponent abstractHwComponent = graph.getMarkedHwComponentsCopy().get(0);

            // unmark all components
            graph.doUnmarkAllComponents();

            // repaint draw Panel
            drawPanel.repaint();

            // open properties window
            HwComponentProperties hwComponentProperties = new HwComponentProperties(mainWindow.getMainWindowComponent(), dataLayer, drawPanel, abstractHwComponent);
            
            // 
            //drawPanel.doUpdateImages();
            
            return;
        }else if(graph.getMarkedCablesCount() == 1){
            // get calbe
            Cable cable = graph.getMarkedCablesCopy().get(0);
            
            // unmark all components
            graph.doUnmarkAllComponents();

            // repaint draw Panel
            drawPanel.repaint();
            
            // open properties window
            CableProperties cableProperties = new CableProperties(mainWindow.getMainWindowComponent(), dataLayer, cable);
       
            // 
            //drawPanel.doUpdateImages();
            
            return;
        }

        // should never happen
        System.err.println("ActionOpenProperties error1");
        return;
    }
}
