package psimulator.userInterface.Editor.DrawPanel.UndoCommands;

import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.DrawPanel;

/**
 *
 * @author Martin
 */
public class UndoableAlignComponentsToGrid extends AbstractUndoableEdit {

    private HashMap<AbstractHwComponent, Dimension> map;
    private DrawPanel drawPanel;

    public UndoableAlignComponentsToGrid(HashMap<AbstractHwComponent, Dimension> map, DrawPanel drawPanel) {
        super();
        this.map = map;
        this.drawPanel = drawPanel;
    }

    @Override
    public void undo() {
        super.undo();

        for (AbstractHwComponent component : map.keySet()) {
            component.doChangePosition(map.get(component), true);
        }
        // panel could be resized before undo, so we need to update its size
        drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
    }

    @Override
    public void redo() {
        super.redo();

        for (AbstractHwComponent component : map.keySet()) {
            component.doChangePosition(map.get(component), false);
        }
        // panel could be resized before redo, so we need to update its size
        drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
    }
}
