package psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.SimulatorEditor.Tools.AbstractTool;

/**
 *
 * @author Martin
 */
public class DrawPanelListenerStrategySimulator extends DrawPanelListenerStrategy{

    
    public DrawPanelListenerStrategySimulator(DrawPanelInnerInterface drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(drawPanel, undoManager, zoomManager, mainWindow, dataLayer);
    }
    
    @Override
    public void initialize() {
        //System.out.println("Simulator mouse listener init");
        drawPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void deInitialize() {
        drawPanel.repaint();
    }

    @Override
    public void setTool(AbstractTool tool) {
        // should never happen
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void mousePressedRight(MouseEvent e) {
        // do nothink = do not change to HAND tool after right button pressed
    }
    
}
