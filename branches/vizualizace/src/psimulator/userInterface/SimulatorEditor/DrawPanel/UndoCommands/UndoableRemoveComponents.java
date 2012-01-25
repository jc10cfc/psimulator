package psimulator.userInterface.Editor.DrawPanel.UndoCommands;

import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;

/**
 *
 * @author Martin
 */
public class UndoableRemoveComponents extends AbstractUndoableEdit {
    protected List<AbstractHwComponent> components;
    protected List<Cable> cables;
    protected GraphOuterInterface graph;
    
    public UndoableRemoveComponents(GraphOuterInterface graph, List<AbstractHwComponent> components, List<Cable> cables){
        super();
        this.components = components;
        this.graph = graph;
        this.cables = cables;
    }

    @Override
    public String getPresentationName() {
      return "HW component add/remove";
    }

    @Override
    public void undo() {
      super.undo();
      //System.out.println("Undo - Adding "+components.size()+ " components and "+cables.size()+" cables" );
      graph.addHwComponents(components);
      graph.addCables(cables);
      
      // panel could be resized before undo, so we need to update its size
      //drawPanel.updateSizeToFitComponents();
      //drawPanel.updateSize(graph.getLowerRightBound(components));
    }

    @Override
    public void redo() {
      super.redo();
      //System.out.println("Redo - Removing "+components.size()+ " components and "+cables.size()+" cables" );
      graph.removeHwComponents(components);
      graph.removeCables(cables);
    }
}
