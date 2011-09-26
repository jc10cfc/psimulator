package psimulator.userInterface.Editor.UndoCommands;

import java.awt.Point;
import javax.swing.undo.AbstractUndoableEdit;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.DrawPanel;

/**
 *
 * @author Martin
 */
public class UndoableMoveComponent extends AbstractUndoableEdit {
    
    protected AbstractHwComponent component;
    protected Point originalDefaultLocation;
    protected Point newDefaultLocation;
    protected DrawPanel drawPanel;
    
    public UndoableMoveComponent(AbstractHwComponent component, Point originalLocation, Point newLocation, DrawPanel drawPanel) {
        super();
        this.component = component;
        this.originalDefaultLocation = originalLocation;
        this.newDefaultLocation = newLocation;
        this.drawPanel = drawPanel;
    }
 
    @Override
    public String getPresentationName() {
      return "Component move";
    }

    @Override
    public void undo() {
      super.undo();
      component.setUndoRedoPostition(originalDefaultLocation);
      // panel could be resized before undo, so we need to update its size
      drawPanel.updateSize(component.getLowerRightCornerLocation());
    }

    @Override
    public void redo() {
      super.redo();
      component.setUndoRedoPostition(newDefaultLocation);
      // panel could be resized before redo, so we need to update its size
      drawPanel.updateSize(component.getLowerRightCornerLocation());
    }
  }