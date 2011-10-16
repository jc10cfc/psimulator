package psimulator.userInterface.MouseActionListeners;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.Enums.HwComponentEnum;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.HwComponent;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.UndoCommands.UndoableAddHwComponent;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public class DrawPanelListenerStrategyAddHwComponent extends DrawPanelListenerStrategy{
    
    public DrawPanelListenerStrategyAddHwComponent(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow) {
        super(drawPanel, undoManager, zoomManager, mainWindow);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        AbstractHwComponent component = new HwComponent(drawPanel.getImageFactory(), zoomManager, HwComponentEnum.PC);
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
