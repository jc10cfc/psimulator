package psimulator.userInterface.Editor.UndoCommands;

import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Components.Cable;
import psimulator.userInterface.Editor.DrawPanel;
import psimulator.userInterface.Editor.Graph;

/**
 *
 * @author Martin
 */
public class UndoableRemoveComponents extends AbstractUndoableEdit {
    protected List<AbstractHwComponent> components;
    protected List<Cable> cables;
    protected Graph graph;
    protected DrawPanel drawPanel;
    
    public UndoableRemoveComponents(List<AbstractHwComponent> components, List<Cable> cables, Graph graph, DrawPanel drawPanel){
        super();
        this.components = components;
        this.graph = graph;
        this.cables = cables;
        this.drawPanel = drawPanel;
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
      drawPanel.updateSize(graph.getLowerRightBound(components));
    }

    @Override
    public void redo() {
      super.redo();
      //System.out.println("Redo - Removing "+components.size()+ " components and "+cables.size()+" cables" );
      graph.removeHwComponents(components);
      graph.removeCables(cables);
    }
}
