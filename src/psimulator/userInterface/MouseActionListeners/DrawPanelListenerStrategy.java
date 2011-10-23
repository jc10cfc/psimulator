package psimulator.userInterface.MouseActionListeners;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.Graph;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public abstract class DrawPanelListenerStrategy extends MouseInputAdapter implements MouseWheelListener {

    protected Graph graph;
    protected DrawPanel drawPanel;
    protected MainWindowInterface mainWindow;
    protected UndoManager undoManager;
    protected ZoomManager zoomManager;
    protected DataLayerFacade dataLayer;

    public DrawPanelListenerStrategy(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow, DataLayerFacade dataLayer) {
        super();

        this.drawPanel = drawPanel;
        this.undoManager = undoManager;
        this.mainWindow = mainWindow;
        this.graph = drawPanel.getGraph();
        this.zoomManager = zoomManager;
        this.dataLayer = dataLayer;
    }
    
    public abstract void deInitialize();
    
    public abstract void setTool(AbstractTool tool);
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            //System.out.println("Point:"+e.getPoint());
            // scroll down
            if(e.getWheelRotation() == 1){
                zoomManager.zoomIn(e.getPoint());
            //scroll up    
            }else if(e.getWheelRotation() == -1){
                zoomManager.zoomOut(e.getPoint());
            }
        }
    }
}
