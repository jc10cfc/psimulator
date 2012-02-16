package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Image;
import java.awt.Point;
import java.util.Random;
import javax.swing.ImageIcon;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class Animation{
    
    //
    private ZoomManager zoomManager;
    private AbstractImageFactory imageFactory;
    //
    
    private int defaultZoomStartX;
    private int defaultZoomStartY;
    
    private int defaultZoomEndX;
    private int defaultZoomEndY;
    
    private Image image;
    
    private int defaultZoomX;
    private int defaultZoomY;
    
    private int defaultZoomMaxX;
    private int defaultZoomMaxY;
    
    private boolean visible;
    
    private final int ANIMATION_SPEED = 2;
    
    
    public Animation(AbstractImageFactory imageFactory, ZoomManager zoomManager, 
            Point defaultZoomSource, Point defaultZoomDest){
        this.zoomManager = zoomManager;
        this.imageFactory = imageFactory;
        
        //ImageIcon ii = new ImageIcon(this.getClass().getResource("/resources/toolbarIcons/editor_toolbar/cursor_hand_mod_2.png"));
        //image = ii.getImage();
        image = imageFactory.getImage(HwTypeEnum.END_DEVICE_PC, zoomManager.getIconWidth(), false);
        
        Random r = new Random();
        
        defaultZoomStartX = defaultZoomSource.x;
        defaultZoomStartY = defaultZoomSource.y;
    
        defaultZoomEndX = defaultZoomDest.x;
        defaultZoomEndY = defaultZoomDest.y;
        
        
        defaultZoomX = r.nextInt(300);
        defaultZoomY = defaultZoomX;
        
        defaultZoomMaxX = r.nextInt(500)+300;
        defaultZoomMaxY = defaultZoomMaxX;
    }
    
    
    public Image getImage() {
        image = imageFactory.getImage(HwTypeEnum.END_DEVICE_PC, zoomManager.getIconWidth(), false);
        return image;
    }

    public int getX() {
        return zoomManager.doScaleToActual(defaultZoomX);
        //return x;
    }

    public int getY() {
        return zoomManager.doScaleToActual(defaultZoomY);
        //return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void move() {
        defaultZoomX += ANIMATION_SPEED;
        defaultZoomY += ANIMATION_SPEED;

        if (defaultZoomY > defaultZoomMaxY) {
            defaultZoomY = -45;
            defaultZoomX = -45;
        }
    }
    
}
