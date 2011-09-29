package psimulator.userInterface.Editor.UndoCommands;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel;

/**
 *
 * @author Martin
 */
public class UndoableMoveComponent extends AbstractUndoableEdit {
    
    //protected AbstractHwComponent component;
    protected List<AbstractHwComponent> components;
    protected Point originalDefaultLocation;
    protected Point newDefaultLocation;
    protected DrawPanel drawPanel;
    
    protected Dimension offsetInDefaultZoom;
    
    public UndoableMoveComponent(List<AbstractHwComponent> components, Dimension offsetInDefaultZoom, DrawPanel drawPanel) {
        super();
        this.components = components;
        this.offsetInDefaultZoom = offsetInDefaultZoom;
        this.drawPanel = drawPanel;
    }
 
    @Override
    public String getPresentationName() {
      return "Component move";
    }

    @Override
    public void undo() {
      super.undo();
      
      for(AbstractHwComponent component : components){
          component.doChangePosition(offsetInDefaultZoom, false);
      }
      
      // panel could be resized before undo, so we need to update its size
     drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
    }

    @Override
    public void redo() {
      super.redo();
      
      for(AbstractHwComponent component : components){
          component.doChangePosition(offsetInDefaultZoom, true);
      }
      // panel could be resized before redo, so we need to update its size
      drawPanel.updateSize(drawPanel.getGraph().getGraphLowerRightBound());
    }
  }