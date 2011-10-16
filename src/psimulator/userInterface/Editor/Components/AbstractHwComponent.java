package psimulator.userInterface.Editor.Components;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public abstract class AbstractHwComponent extends AbstractComponent implements Observer {// implements DragGestureListener, DragSourceListener{

    // position in current zoom
    protected int xPos = 50;
    protected int yPos = 50;
    
    // position in 1:1 zoom
    protected int defaultZoomXPos = 50;
    protected int defaultZoomYPos = 50;
    
    protected ZoomManager zoomManager;
    protected AbstractImageFactory imageFactory;
    
    private List<Cable> cables = new ArrayList<Cable>();
    
    private List<EthInterface> interfaces = new ArrayList<EthInterface>();
    
    protected BufferedImage bi;

    public AbstractHwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager) {
        super();
        this.zoomManager = zoomManager;
        this.imageFactory = imageFactory;
        
        for(int i =0;i<3;i++){
            interfaces.add(new EthInterface("Eth"+i));
        }
    }

    public List<EthInterface> getInterfaces(){
        return interfaces;
    }
    
    public boolean containsCable(Cable cable){
        return cables.contains(cable);
    }
    
    public void addCable(Cable cable){
        cables.add(cable);
    }
    
    public void removeCable(Cable cable){
        cables.remove(cable);
    }
    
    public Iterator<Cable> getCableIterator(){
        return cables.iterator();
    }
    
    public List<Cable> getCables(){
        return cables;
    }
 
    public Point getCenterLocation() {
        return new Point(xPos + zoomManager.getIconSize() / 2, yPos + zoomManager.getIconSize() / 2);
    }
    
    /**
     * Gets Point in actual scale of lower right corner of component
     * @return Actual scale ponint
     */
    public Point getLowerRightCornerLocation(){
        return new Point(xPos + zoomManager.getIconSize(), yPos + zoomManager.getIconSize());
    }

    @Override
    public boolean intersects(Point p) {
        if ((p.x >= xPos && p.x <= xPos + bi.getWidth()) && (p.y >= yPos && p.y <= yPos + bi.getHeight())) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean intersects(Rectangle r) {
        Rectangle rect = new Rectangle(xPos, yPos, bi.getWidth(), bi.getHeight());
        
        return r.intersects(rect);
    }
    
    @Override
    public int getWidth() {
        return zoomManager.getIconSize();
    }

    @Override
    public int getHeight() {
        return zoomManager.getIconSize();
    }

    @Override
    public int getX() {
        return xPos;
    }

    @Override
    public int getY() {
        return yPos;
    }
    
    public abstract void doChangePosition(Dimension offsetInDefaultZoom, boolean positive);

    //protected abstract void setUndoRedoPostition(Point defaultZoomP);

    public abstract void setLocationByMiddlePoint(Point middlePoint);
 
}
