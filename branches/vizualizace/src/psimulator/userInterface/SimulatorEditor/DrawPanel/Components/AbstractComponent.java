package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author Martin
 */
public abstract class AbstractComponent extends JComponent implements Markable{

    private Integer id;
    
    private boolean marked = false;
    
    public AbstractComponent(){
        id = new Integer(IdGeneratorSingleton.getInstance().getNextId());
    }
    
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
    
    public Integer getId(){
        return id;
    }
}
