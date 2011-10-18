package psimulator.userInterface.Editor.Components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Martin
 */
public class Cable extends AbstractComponent{

    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private EthInterface eth1;
    private EthInterface eth2;

    private static final int LINE_WIDTH = 2;
    
    Line2D line = new Line2D.Float();

    public Cable(AbstractHwComponent component1, AbstractHwComponent component2, EthInterface eth1, EthInterface eth2) {
        this.component1 = component1;
        this.component2 = component2;
        this.eth1 = eth1;
        this.eth2 = eth2;
    }
    
    
    public AbstractHwComponent getComponent1(){
        return component1;
    }
    
    public AbstractHwComponent getComponent2(){
        return component2;
    }

    public EthInterface getEth1() {
        return eth1;
    }

    public EthInterface getEth2() {
        return eth2;
    }

    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isMarked()) {
            Color color = g2.getColor();
            g2.setColor(Color.blue);
            g2.drawLine(getX1(),getY1(), getX2(), getY2());
            g2.setColor(color);
        }else{
            g2.drawLine(getX1(),getY1(), getX2(), getY2());
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
        return component1.getCenterLocation().x;
    }

    public int getY1() {
        return component1.getCenterLocation().y;
    }

    public Point2D getP1() {
        return component1.getCenterLocation();
    }

    public int getX2() {
        return component2.getCenterLocation().x;
    }

    public int getY2() {
        return component2.getCenterLocation().y;
    }

    public Point2D getP2() {
        return component2.getCenterLocation();
    }
    
}
