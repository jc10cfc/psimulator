package psimulator.userInterface.Editor.DrawPanel.UndoCommands;

import java.awt.Dimension;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;

/**
 *
 * @author Martin
 */
public class UndoableMoveComponent extends AbstractUndoableEdit {
    
    protected GraphOuterInterface graph;
    
    protected List<AbstractHwComponent> components;
    protected Dimension offsetInDefaultZoom;
    
    
    public UndoableMoveComponent(GraphOuterInterface graph, List<AbstractHwComponent> components, Dimension offsetInDefaultZoom) {
        super();
        this.components = components;
        this.offsetInDefaultZoom = offsetInDefaultZoom;
        this.graph = graph;
    }
 
    @Override
    public String getPresentationName() {
      return "Component move";
    }

    @Override
    public void undo() {
      super.undo();
      
      graph.changePositionOfAbstractHwComponents(components, offsetInDefaultZoom, false);
      
      /*
      for(AbstractHwComponent component : components){
          component.doChangePosition(offsetInDefaultZoom, false);
      }
      
      // panel could be resized before undo, so we need to update its size
     drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
     */
    }

    @Override
    public void redo() {
      super.redo();
      
      graph.changePositionOfAbstractHwComponents(components, offsetInDefaultZoom, true);
      
      /*
      for(AbstractHwComponent component : components){
          component.doChangePosition(offsetInDefaultZoom, true);
      }
      // panel could be resized before redo, so we need to update its size
      drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
       */
    }
  }