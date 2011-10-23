package psimulator.userInterface.imageFactories;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.Enums.MainTool;

/**
 *
 * @author Martin
 */
public abstract class AbstractImageFactory {

    public static final int ICON_SIZE_MENU_BAR = 48;
    public static final int ICON_SIZE_MENU_BAR_POPUP = 30;
    public static final String HAND_PATH = "/resources/toolbarIcons/editor/cursor_arrow.png";
    public static final String END_DEVICE_PC_PATH = "/resources/toolbarIcons/editor/modern/PC.png";
    public static final String END_DEVICE_NOTEBOOK_PATH = "/resources/toolbarIcons/editor/notebook.png";
    public static final String ROUTER_PATH = "/resources/toolbarIcons/editor/router.png";
    public static final String SWITCH_PATH = "/resources/toolbarIcons/editor/switch.png";
    public static final String CABLE_PATH = "/resources/toolbarIcons/editor/network-wired.png";
    public static final String END_DEVICE_WORKSTATION_PATH = "/resources/toolbarIcons/editor/desktop.png";
    public static final String REAL_PC_PATH = "/resources/toolbarIcons/editor/local_network.png";
    
    protected ImageBuffer imageBuffer;
    
    // scales 3 je alpha
    private float[] scales = {1f, 1f, 1f, 1f};
    private float[] offsets = {40f, 40f, 40f, 1f};

    //protected RescaleOp rescaleOp = new RescaleOp(scales, offsets, null);
    protected RescaleOp rescaleOp = new RescaleOp(scales, offsets, null);


    public AbstractImageFactory() {
        this.imageBuffer = new ImageBuffer();
    }

    public BufferedImage getImage(HwTypeEnum hwComponentType, String path, Integer size, boolean marked) {
        BufferedImage image;

        image = imageBuffer.getBufferedImage(path, size, marked);

        if (image == null) {
            // load image from file
            image = createImage(hwComponentType, path, size, marked);
            // put image into buffer
            imageBuffer.putBufferedImage(path, size, image, marked);
        }

        return image;
    }

    protected BufferedImage createImage(HwTypeEnum hwComponentType, String path, Integer size, boolean marked) {

        BufferedImage bi = null;
    
        Image temp = null;
        
        try {
            // loads image from path and scales it to desired size
            temp = ImageIO.read(getClass().getResource(path)).getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            try {
                temp = ImageIO.read(getClass().getResource(getPath(hwComponentType))).getScaledInstance(size, size, Image.SCALE_SMOOTH);
            } catch (IOException ex1) {
                // should never happen
            }
        }
        
        // create new buffered image to paint on
        bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        // create graphics and set hints
        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // draw image
        graphics2D.drawImage(temp, 0, 0, null);

        // if marked, draw transparent white rectangle over
        if (marked) {
            rescaleOp.filter(bi, bi);
        }

        graphics2D.dispose();

        return bi;
    }

    public ImageIcon getImageIconForToolbar(MainTool tool) {
        String name = "";

        switch (tool) {
            case HAND:
                name = "cursor_arrow";
                break;
            case ADD_END_DEVICE:
                name = "modern/PC";
                break;
            case ADD_ROUTER:
                name = "router";
                break;
            case ADD_SWITCH:
                name = "switch";
                break;
            case ADD_CABLE:
                name = "network-wired";
                break;
            case ADD_REAL_PC:
                name = "desktop";
                break;
        }
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/toolbarIcons/editor/" + name + ".png"));

        return (new ImageIcon(icon.getImage().getScaledInstance(ICON_SIZE_MENU_BAR, ICON_SIZE_MENU_BAR, Image.SCALE_SMOOTH)));
    }

    public ImageIcon getImageIconForToolbar(MainTool tool, String path) {
        ImageIcon icon = null;
        if (path == null) {
            icon = getImageIconForToolbar(tool);
        } else {
            try {
                icon = new ImageIcon(getClass().getResource(path));
            } catch (Exception e) {
                System.out.println("chyba pri nacitani obrazku");
                icon = getImageIconForToolbar(tool);
            }
        }

        return (new ImageIcon(icon.getImage().getScaledInstance(ICON_SIZE_MENU_BAR, ICON_SIZE_MENU_BAR, Image.SCALE_SMOOTH)));
    }

    public void clearBuffer() {
        imageBuffer.clearBuffer();
    }
    
    private String getPath(HwTypeEnum type){
        switch(type){
            case CISCO_ROUTER:
            case LINUX_ROUTER:
                return ROUTER_PATH;
            case CISCO_SWITCH:
            case LINUX_SWITCH:
                return SWITCH_PATH;
            case END_DEVICE:
                return END_DEVICE_PC_PATH;
            case CABLE:
                return CABLE_PATH;
            case REAL_PC:
            default:
                return REAL_PC_PATH;
        }
    }
}
