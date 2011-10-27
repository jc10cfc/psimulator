package psimulator.userInterface.Editor.DrawPanel.UndoCommands;

import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.Editor.DrawPanel.Graph;

/**
 *
 * @author Martin
 */
public class UndoableAddHwComponent extends AbstractUndoableEdit {
    protected AbstractHwComponent component;
    protected Graph graph;
    protected DrawPanelInnerInterface drawPanel;
    
    public UndoableAddHwComponent(AbstractHwComponent component, Graph graph, DrawPanelInnerInterface drawPanel){
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
