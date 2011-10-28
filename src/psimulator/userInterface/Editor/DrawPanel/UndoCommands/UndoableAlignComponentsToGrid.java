package psimulator.userInterface.Editor.DrawPanel.UndoCommands;

import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;

/**
 *
 * @author Martin
 */
public class UndoableAlignComponentsToGrid extends AbstractUndoableEdit {

    protected GraphOuterInterface graph;
    protected HashMap<AbstractHwComponent, Dimension> map;
    
    public UndoableAlignComponentsToGrid(GraphOuterInterface graph, HashMap<AbstractHwComponent, Dimension> map) {
        super();
        this.map = map;
    }

    @Override
    public void undo() {
        super.undo();

   
        for (AbstractHwComponent component : map.keySet()) {
            graph.changePositionOfAbstractHwComponent(component, map.get(component), true);
        }
        
        /*
        for (AbstractHwComponent component : map.keySet()) {
            component.doChangePosition(map.get(component), true);
        }
        // panel could be resized before undo, so we need to update its size
        drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
        */
    }

    @Override
    public void redo() {
        super.redo();

        for (AbstractHwComponent component : map.keySet()) {
            graph.changePositionOfAbstractHwComponent(component, map.get(component), false);
        }
        
        /*
        for (AbstractHwComponent component : map.keySet()) {
            component.doChangePosition(map.get(component), false);
        }
        // panel could be resized before redo, so we need to update its size
        drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
         */
    }
}
