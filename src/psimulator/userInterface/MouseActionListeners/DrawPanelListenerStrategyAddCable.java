package psimulator.userInterface.MouseActionListeners;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.Cable;
import psimulator.userInterface.Editor.Dialogs.CableConnectToInterfacePopupMenu;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.UndoCommands.UndoableAddCable;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public class DrawPanelListenerStrategyAddCable extends DrawPanelListenerStrategy {

    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private Point startPoint;
    
    private CableConnectToInterfacePopupMenu popupMenu;
    
    public DrawPanelListenerStrategyAddCable(DrawPanel drawPanel, UndoManager undoManager, ZoomManager zoomManager, MainWindowInterface mainWindow) {
        super(drawPanel, undoManager, zoomManager, mainWindow);
        
        popupMenu = new CableConnectToInterfacePopupMenu(drawPanel);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(e.getPoint())) {
                drawPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                return;
            }
        }
        drawPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        boolean isFirstComponent = false;

        for (AbstractHwComponent c : graph.getHwComponents()) {
            if (c.intersects(e.getPoint())) {
                component1 = c;
                isFirstComponent = true;
                startPoint = component1.getCenterLocation();
                break;
            }
        }

        if (!isFirstComponent) {
            component1 = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (component1 != null) {

            drawPanel.setLineInProgras(true, startPoint, e.getPoint());

            drawPanel.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // remove lineInProgres from DrawPanel
        drawPanel.setLineInProgras(false, null, null);

        // if mouse pressed on some component   
        if (component1 != null) {
            boolean isSecondComponent = false;

            for (AbstractHwComponent c : graph.getHwComponents()) {
                if (c.intersects(e.getPoint())) {
                    component2 = c;
                    isSecondComponent = true;
                    break;
                }
            }


            // if there is no second componennt, or the source and target components are the same
            if (!isSecondComponent || (component1 == component2)) {
                component1 = null;
                component2 = null;
            } else {
                // if not yet connected
                if (!graph.isConnection(component1, component2)) {
                    //System.out.println("E:getComponent " + e.getComponent());
                    popupMenu.showPopupInterfaceChoose(component2, e.getX(), e.getY());
                    
                    // create new cabel
                    Cable cable = new Cable(component1, component2);
                    // add cabel to graph
                    graph.addCable(cable);

                    // add to undo manager
                    undoManager.undoableEditHappened(new UndoableEditEvent(this,
                            new UndoableAddCable(cable, graph)));
                }
            }

        }
        // repaint draw panel
        drawPanel.repaint();
    }

}
