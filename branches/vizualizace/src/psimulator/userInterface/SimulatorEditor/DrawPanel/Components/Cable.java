package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;

/**
 *
 * @author Martin
 */
public class Cable extends AbstractComponent {

    private ZoomManager zoomManager;
    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private EthInterface eth1;
    private EthInterface eth2;
    
    private static final int LINE_WIDTH = 2;
    
    private Line2D line = new Line2D.Float();
    
    private Stroke stroke = new BasicStroke(3.5f);
    
    int x1, y1, x2, y2;
    Shape shape;
    
    int[] XArray = {0, 0, 0, 0};
    int[] YArray = {0, 0, 0, 0};

    public Cable(AbstractHwComponent component1, AbstractHwComponent component2, EthInterface eth1, EthInterface eth2, ZoomManager zoomManager) {
        this.component1 = component1;
        this.component2 = component2;
        this.eth1 = eth1;
        this.eth2 = eth2;
        this.zoomManager = zoomManager;
    }

    public AbstractHwComponent getComponent1() {
        return component1;
    }

    public AbstractHwComponent getComponent2() {
        return component2;
    }

    public EthInterface getEth1() {
        return eth1;
    }

    public EthInterface getEth2() {
        return eth2;
    }

    public void paintComponent(Graphics g, int x1, int y1, int x2, int y2){
        Graphics2D g2 = (Graphics2D) g;
        
        stroke = new BasicStroke(zoomManager.getStrokeWidth());
        
        line.setLine(x1, y1, x2, y2);
        
        Color tmpC = g2.getColor();
        Stroke tmpS = g2.getStroke();
        
        if (isMarked()) {
            g2.setColor(Color.blue);
            g2.setStroke(stroke); 
            g2.drawLine(x1, y1, x2, y2);
        } else {
            g2.setColor(Color.black);
            g2.setStroke(stroke); 
            g2.drawLine(x1, y1, x2, y2);
        }
        
        g2.setColor(tmpC);
        g2.setStroke(tmpS);
    }
    /*
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        
        float strokeWidth = Math.max((float)(5f*zoomManager.getCurrentScale()), 1f);
        
        stroke = new BasicStroke(strokeWidth);
        
        if (isMarked()) {
            Color color = g2.getColor();
            g2.setColor(Color.blue);
            g2.setStroke(stroke); 
            g2.drawLine(getX1(), getY1(), getX2(), getY2());
            g2.setColor(color);
        } else {
            g2.setStroke(stroke); 
            g2.drawLine(getX1(), getY1(), getX2(), getY2());
        }

    }*/

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
        if (getX1() <= getX2()) {
            return getX1();
        }
        return getX2();
    }

    @Override
    public int getY() {
        if (getY1() <= getY2()) {
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
        //line.setLine(getP1(), getP2());
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
