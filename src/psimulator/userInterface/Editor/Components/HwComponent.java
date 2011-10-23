package psimulator.userInterface.Editor.Components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class HwComponent extends AbstractHwComponent {

    private HwTypeEnum hwComponentType;
    
    private String imagePath;

    /*
    public HwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, HwTypeEnum hwComponentType) {
        super(imageFactory, zoomManager, 3);

        //zoomManager.addObserver(this);
        this.hwComponentType = hwComponentType;
        bi = imageFactory.getBufferedImage(hwComponentType, zoomManager.getIconSize(), true);
    }*/
    
    public HwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, 
            HwTypeEnum hwComponentType, int interfacesCount, String imagePath, String name) {
        super(imageFactory, zoomManager, interfacesCount);

        //zoomManager.addObserver(this);
        this.hwComponentType = hwComponentType;
        //bi = imageFactory.getBufferedImage(hwComponentType, zoomManager.getIconSize(), true);
        
        // pozor !! zapnout buffer
        //bi = imageFactory.getBufferedImage(hwComponentType, zoomManager.getIconSize(), true);
        this.imagePath = imagePath;
        
        bi = imageFactory.getBufferedImage(imagePath, zoomManager.getIconSize());
    }
    

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        /*
        if (isMarked()) {
            bi = imageFactory.getBufferedImage(HwTypeEnum.END_DEVICE, zoomManager.getIconSize(), true);
        } else {
            bi = imageFactory.getBufferedImage(HwTypeEnum.END_DEVICE, zoomManager.getIconSize(), false);
        }*/
        
        bi = imageFactory.getBufferedImage(imagePath, zoomManager.getIconSize());

        g2.drawImage(bi, xPos, yPos, null);

    }
}
