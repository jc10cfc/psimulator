package psimulator.userInterface.Editor.DrawPanel.Components;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author Martin
 */
public abstract class AbstractComponent extends JComponent implements Markable{

    private boolean marked = false;
    
    @Override
    public boolean isMarked() {
        return marked;
    }

    @Override
    public void setMarked(boolean marked) {
        this.marked = marked;
    }
    
    public abstract boolean intersects(Point p);
    public abstract boolean intersects(Rectangle r);
}
