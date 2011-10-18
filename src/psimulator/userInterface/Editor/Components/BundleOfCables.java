package psimulator.userInterface.Editor.Components;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Martin
 */
public class BundleOfCables extends AbstractComponent{

    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    
    private List<Cable> cables;
    
    private static final int LINE_WIDTH = 2;
    
    Line2D line = new Line2D.Float();
    
    
    public BundleOfCables(AbstractHwComponent component1, AbstractHwComponent component2){
        cables = new ArrayList<Cable>();
        this.component1 = component1;
        this.component2 = component2;
    }
    
    public AbstractHwComponent getComponent1(){
        return component1;
    }
    
    public AbstractHwComponent getComponent2(){
        return component2;
    }
    
    public Cable getIntersectingCable(Rectangle r){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Cable> getCables() {
        return cables;
    }
 
    public int getCablesCount(){
        return cables.size();
    }
    
    /**
     * adds cable to bundle
     * @param c 
     */
    public void addCable(Cable c){
        cables.add(c);
    }
    
    /**
     * remove cable from bundle
     * @param c 
     */
    public void removeCable(Cable c){
        cables.remove(c);
    }
     
    @Override
    public void paintComponent(Graphics g) {
        for(Cable c : cables){
            c.paintComponent(g);
        }
          
    }
    
    @Override
    public int getWidth() {
        //return Math.abs(getX1()-getX2());
        return LINE_WIDTH;
    }

    @Override
    public int getHeight() {
        //return Math.abs(getY1()-getY2());
        return LINE_WIDTH;
    }

    @Override
    public int getX() {
        if(getX1()<=getX2()){
            return getX1();
        }
        return getX2();
    }

    @Override
    public int getY() {
        if(getY1()<=getY2()){
            return getY1();
        }
        return getY2();
    }

    
    @Override
    public boolean intersects(Point p) {
        Rectangle r = new Rectangle(p);
        return intersects(r);
    }

    @Override
    public boolean intersects(Rectangle r) {
        line.setLine(getP1(), getP2());
        return line.intersects(r);
    }

    public int getX1() {
        return getComponent1().getCenterLocation().x;
    }

    public int getY1() {
        return getComponent1().getCenterLocation().y;
    }

    public Point2D getP1() {
        return getComponent1().getCenterLocation();
    }

    public int getX2() {
        return getComponent2().getCenterLocation().x;
    }

    public int getY2() {
        return getComponent2().getCenterLocation().y;
    }

    public Point2D getP2() {
        return getComponent2().getCenterLocation();
    }
}
