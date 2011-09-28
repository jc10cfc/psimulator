package psimulator.userInterface.Editor.UndoCommands;

import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.Graph;

/**
 *
 * @author Martin
 */
public class UndoableAddHwComponent extends AbstractUndoableEdit {
    protected AbstractHwComponent component;
    protected Graph graph;
    protected DrawPanel drawPanel;
    
    public UndoableAddHwComponent(AbstractHwComponent component, Graph graph, DrawPanel drawPanel){
        super();
        this.component = component;
        this.graph = graph;
        this.drawPanel = drawPanel;
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
      drawPanel.updateSize(component.getLowerRightCornerLocation());
    }
}
