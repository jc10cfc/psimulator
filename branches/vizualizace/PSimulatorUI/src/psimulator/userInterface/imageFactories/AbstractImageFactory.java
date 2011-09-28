package psimulator.userInterface.imageFactories;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import psimulator.dataLayer.Enums.HwComponentEnum;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public abstract class AbstractImageFactory {
    protected ImageBuffer imageBuffer;
    
    protected int ICON_SIZE_MENU_BAR = 48;
    
    public AbstractImageFactory(){
        this.imageBuffer = new ImageBuffer();
    }
    
    public abstract BufferedImage getBufferedImage(HwComponentEnum hwComponent, Integer size, boolean marked);
    
    public ImageIcon getImageIconForToolbar(Tools tool){
        String name = "";
        

        
        switch(tool){
            case HAND:
                name = "cursor_arrow";
                break;
            case MAC:
                name = "modern/iMac_on";
                break;
            case PC:
                name = "modern/PC";
                break;
            case CABLE:
                name = "network-wired";
                break;
        }
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/toolbarIcons/editor/"+name+".png"));
        
        return (new ImageIcon(icon.getImage().getScaledInstance(ICON_SIZE_MENU_BAR, ICON_SIZE_MENU_BAR, Image.SCALE_SMOOTH)));
    }
    
    public void clearBuffer(){
        imageBuffer.clearBuffer();
    }
}
