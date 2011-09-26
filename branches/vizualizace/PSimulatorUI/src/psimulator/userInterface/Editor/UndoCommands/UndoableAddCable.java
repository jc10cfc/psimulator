package psimulator.userInterface.Editor.UndoCommands;

import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.Components.Cable;
import psimulator.userInterface.Editor.Graph;

/**
 *
 * @author Martin
 */
public class UndoableAddCable extends AbstractUndoableEdit {
    protected Cable cable;
    protected Graph graph;
    
    public UndoableAddCable(Cable cable, Graph graph){
        super();
        this.cable = cable;
        this.graph = graph;
    }

    @Override
    public String getPresentationName() {
      return "HW component add/remove";
    }

    @Override
    public void undo() {
      super.undo();
      graph.removeCable(cable);
    }

    @Override
    public void redo() {
      super.redo();
      graph.addCable(cable);
    }
}