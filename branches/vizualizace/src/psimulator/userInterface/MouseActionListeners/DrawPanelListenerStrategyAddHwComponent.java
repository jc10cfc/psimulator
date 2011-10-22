package psimulator.userInterface.MouseActionListeners;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.HwComponent;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.Tools.AbstractTool;
import psimulator.userInterface.Editor.Tools.AddDeviceTool;
import psimulator.userInterface.Editor.UndoCommands.UndoableAddHwComponent;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public class DrawPanelListenerStrategyAddHwComponent extends DrawPanelListenerStrategy{
    
    public DrawPanelListenerStrategyAddHwComponent(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow, DataLayerFacade dataLayer) {
        super(drawPanel, undoManager, zoomManager, mainWindow, dataLayer);
    }
    
    @Override
    public void deInitialize() {
        
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // new code
        AddDeviceTool t = (AddDeviceTool)drawPanel.getCurrentTool();
        
        AbstractHwComponent component = new HwComponent(drawPanel.getImageFactory(), zoomManager, 
                HwTypeEnum.END_DEVICE, t.getInterfaces(), t.getImagePath(), t.getName());
        
        //end new code
        
        //AbstractHwComponent component = new HwComponent(drawPanel.getImageFactory(), zoomManager, HwTypeEnum.END_DEVICE);
        
        component.setLocationByMiddlePoint(e.getPoint());
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
