package psimulator.userInterface.Editor.DrawPanel.MouseActionListeners;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.HwComponent;
import psimulator.userInterface.Editor.DrawPanel.DrawPanel;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.Editor.Tools.AddDeviceTool;
import psimulator.userInterface.Editor.DrawPanel.UndoCommands.UndoableAddHwComponent;
import psimulator.userInterface.Editor.DrawPanel.ZoomManager;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin
 */
public final class DrawPanelListenerStrategyAddHwComponent extends DrawPanelListenerStrategy{
    
    private AddDeviceTool addDeviceTool;

    public DrawPanelListenerStrategyAddHwComponent(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(drawPanel, undoManager, zoomManager, mainWindow, dataLayer);
    }
    
    @Override
    public void deInitialize() {
        
    }
    
    @Override
    public void setTool(AbstractTool tool) {
        this.addDeviceTool = (AddDeviceTool) tool;
    }
    
    @Override
    public void mousePressedLeft(MouseEvent e) {
        // create new component
        AbstractHwComponent component = new HwComponent(drawPanel.getImageFactory(), zoomManager, 
                addDeviceTool.getHwType(), addDeviceTool.getInterfaces(), 
                addDeviceTool.getImagePath(), addDeviceTool.getName());
        
        // set position of new component
        component.setLocationByMiddlePoint(e.getPoint());
        
        // add component to graph
        graph.addHwComponent(component);
        
        // inform drawPanel about size change if component placed out of draw panel
        drawPanel.updateSize(component.getLowerRightCornerLocation());
        
        drawPanel.repaint();
        
        // add to undo manager
        undoManager.undoableEditHappened(new UndoableEditEvent(this,
                        new UndoableAddHwComponent(component, drawPanel.getGraph(), drawPanel)));
        
        mainWindow.updateUndoRedoButtons();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
       drawPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
       drawPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
