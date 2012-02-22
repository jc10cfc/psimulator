package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class Grid{

    private GraphOuterInterface graph;
    private int distance;
    
    
    public Grid(GraphOuterInterface graph) {
        this.graph = graph;
    }
    
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        int width = graph.getWidth();
        int height = graph.getHeight();
        
        distance = ZoomManagerSingleton.getInstance().getIconWidth()/2;

        for(int i = distance;i<width; i= i + distance){
            g2.drawLine(i, 0, i, height-1);
        }
        
        for(int i = distance;i<height; i= i + distance){
            g2.drawLine(0, i, width-1, i);
        }
        
    }
    
    public Point getNearestGridPointDefaultZoom(Point centerPoint){
        distance = (int) (ZoomManagerSingleton.getInstance().getIconWidthDefaultZoom() / 2);

        
        return getNearestGridPoint(centerPoint.x, centerPoint.y, distance);
    }
    
    public Point getNearestGridPoint(int x, int y, int distance){
        Point p = new Point();
        
        int zbytek = x % distance;
        
        // set of X coordinate;
        if(zbytek < distance / 2){
            // stick left
            p.x = x - zbytek;
            
            // just for sure
            if(p.x < distance){
                p.x = distance;
            }
        }else{
            // stick right
            p.x = x - zbytek + distance;
        }
        
        // set of Y coordinate
        zbytek = y % distance;
        
        if(zbytek < distance / 2){
            // stick up
            p.y = y - zbytek;
            
            // just for sure
            if(p.y < distance){
                p.y = distance;
            }
        }else{
            //stick down
            p.y = y - zbytek + distance;
        }
        
        return p;
    }
        
    
    
}