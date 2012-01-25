package psimulator.userInterface.Editor.DrawPanel.UndoCommands;

import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;

/**
 *
 * @author Martin
 */
public class UndoableAddHwComponent extends AbstractUndoableEdit {
    protected GraphOuterInterface graph;
    protected AbstractHwComponent component;
    
    public UndoableAddHwComponent(GraphOuterInterface graph, AbstractHwComponent component){
        super();
        this.component = component;
        this.graph = graph;
    }

    @Override
    public String getPresentationName() {
      return "HW component add/remove";
    }

    @Override
    public void undo() {
      super.undo();
      graph.removeHwComponent(component);
    }

    @Override
    public void redo() {
      super.redo();
      graph.addHwComponent(component);
      
      // panel could be resized before undo, so we need to update its size
      //drawPanel.updateSize(component.getLowerRightCornerLocation());
    }
}
