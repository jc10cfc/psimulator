package psimulator.userInterface.Editor.DrawPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Martin
 */
public class Grid{

    private DrawPanelInnerInterface drawPanel;
    private ZoomManager zoomManager;
    private int distance;
    
    
    public Grid(DrawPanelInnerInterface drawPanel, ZoomManager zoomManager) {
        this.drawPanel = drawPanel;
        this.zoomManager = zoomManager;
    }
    
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        int width = drawPanel.getWidth();
        int height = drawPanel.getHeight();
        
        distance = zoomManager.getIconSize()/2;
    
        
        
        for(int i = distance;i<width; i= i + distance){
            g2.drawLine(i, 0, i, height-1);
        }
        
        for(int i = distance;i<height; i= i + distance){
            g2.drawLine(0, i, width-1, i);
        }
        
    }
    
    public Point getNearestGridPoint(Point centerPoint){
        return getNearestGridPoint(centerPoint.x, centerPoint.y);
    }
    
    public Point getNearestGridPoint(int x, int y){
        Point p = new Point();
        distance = zoomManager.getIconSize()/2;
        
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
