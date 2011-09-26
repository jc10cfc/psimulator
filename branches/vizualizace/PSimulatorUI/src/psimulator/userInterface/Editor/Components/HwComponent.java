package psimulator.userInterface.Editor.Components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Observable;
import psimulator.dataLayer.Enums.HwComponentEnum;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class HwComponent extends AbstractHwComponent {
    
    private HwComponentEnum hwComponentType;

    public HwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, HwComponentEnum hwComponentType) {
        super(imageFactory, zoomManager);

        zoomManager.addObserver(this);
        
        this.hwComponentType = hwComponentType;
        
        bi = imageFactory.getBufferedImage(hwComponentType, zoomManager.getIconSize(), true);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        zoomManager.deleteObserver(this);
    }

    /**
     * Updates Components position after zoom
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        xPos = zoomManager.doScaleToActual(defaultZoomXPos);
        yPos = zoomManager.doScaleToActual(defaultZoomYPos);
    }

    /**
     * Sets position to stored defaultZoomPoint
     * @param defaultZoomP 
     */
    @Override
    public void setUndoRedoPostition(Point defaultZoomP) {
        // from default original location count actual location considering zoom
        int x = zoomManager.doScaleToActual(defaultZoomP.x);
        int y = zoomManager.doScaleToActual(defaultZoomP.y);
        // set location to counted coordinates
        setLocation(x, y);
    }
    
    /**
     * Sets position with center of image in the middlePoint
     * @param middlePoint Center of image
     */
    @Override
    public void setLocationByMiddlePoint(Point middlePoint){
        setLocation(middlePoint.x - (bi.getWidth()/2), middlePoint.y - (bi.getHeight()/2));
    }

    @Override
    public void setLocation(int x, int y) {
        //System.out.println("tady");
        
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        // set new position
        this.xPos = x;
        this.yPos = y;

        // update defautl position (without zoom)
        this.defaultZoomXPos = zoomManager.doScaleToDefault(x);
        this.defaultZoomYPos = zoomManager.doScaleToDefault(y);
        
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isMarked()) {
            bi = imageFactory.getBufferedImage(HwComponentEnum.PC, zoomManager.getIconSize(), true);
        }else{
            bi = imageFactory.getBufferedImage(HwComponentEnum.PC, zoomManager.getIconSize(), false);
        }
        
        g2.drawImage(bi, xPos, yPos, null);
           
    }

    
    
        /*
    @Override
    public void translate(Point p) {
        xPos -= p.x;
        yPos -= p.y;
    }*/
}
