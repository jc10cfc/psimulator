package psimulator.userInterface.imageFactories;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.Tools;

/**
 *
 * @author Martin
 */
public abstract class AbstractImageFactory {
    protected ImageBuffer imageBuffer;
    
    public static final int ICON_SIZE_MENU_BAR = 48;
    public static final int ICON_SIZE_MENU_BAR_POPUP = 30;
    
    public AbstractImageFactory(){
        this.imageBuffer = new ImageBuffer();
    }
    
    public abstract BufferedImage getBufferedImage(HwTypeEnum hwComponent, Integer size, boolean marked);
    
    public ImageIcon getImageIconForToolbar(Tools tool){
        String name = "";
        
        switch(tool){
            case HAND:
                name = "cursor_arrow";
                break;
            case END_DEVICE:
                name = "modern/PC";
                break;
            case ROUTER:
                name = "router";
                break;
            case SWITCH:
                name = "modern/PC";
                break;    
            case CABLE:
                name = "network-wired";
                break;
            case REAL_PC:
                name = "desktop";
                break;
            case FIT_TO_SIZE:
                return null;
                //break;
        }
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/toolbarIcons/editor/"+name+".png"));
        
        return (new ImageIcon(icon.getImage().getScaledInstance(ICON_SIZE_MENU_BAR, ICON_SIZE_MENU_BAR, Image.SCALE_SMOOTH)));
    }
    
       
    public void clearBuffer(){
        imageBuffer.clearBuffer();
    }
}
