package psimulator.userInterface.imageFactories;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
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
    protected BufferedImageLoader bufferedImageLoader;
    
    // scales 3 je alpha
    private float[] scales = {1f, 1f, 1f, 1f};
    private float[] offsets = {50f, 50f, 50f, 1f};

    //protected RescaleOp rescaleOp = new RescaleOp(scales, offsets, null);
    protected RescaleOp rescaleOp = new RescaleOp(scales, offsets, null);


    public AbstractImageFactory() {
        this.imageBuffer = new ImageBuffer();
        this.bufferedImageLoader = new BufferedImageLoader();
    }

    /**
     * Returns BufferedImage  for hwComponentType at path of size. If marked,
     * the result image is brighter by 50%.
     * @param hwComponentType
     * @param path
     * @param size
     * @param marked
     * @return 
     */
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

    /**
     * Creates BufferedImage  for hwComponentType at path of size. If marked,
     * the result image is brighter by 50%.
     * @param hwComponentType
     * @param path
     * @param size
     * @param marked
     * @return 
     */
    protected BufferedImage createImage(HwTypeEnum hwComponentType, String path, Integer size, boolean marked) {

        BufferedImage bi = null;
    
        Image tmp = null;
        
        try {
            // loads image from path and scales it to desired size
            tmp = getScaledImage(path, size);
        } catch (IOException ex) {
            try {
                // load default image
                tmp = getScaledImage(getPath(hwComponentType), size);
            } catch (IOException ex1) {
                // should never happen, all hwComponentType default icons are in .jar as a resource
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
        graphics2D.drawImage(tmp, 0, 0, null);

        // if marked, draw transparent white rectangle over
        if (marked) {
            rescaleOp.filter(bi, bi);
        }

        graphics2D.dispose();

        return bi;
    }

    /**
     * Returns default image for MainTool tool.
     * @param tool
     * @return ImageIcon with ICON_SIZE_MENU_BAR size
     */
    public ImageIcon getImageIconForToolbar(MainTool tool) {
        String path ;

        switch (tool) {
            case HAND:
                path = HAND_PATH;
                break;
            case ADD_END_DEVICE:
                path = END_DEVICE_PC_PATH;
                break;
            case ADD_ROUTER:
                path = ROUTER_PATH;
                break;
            case ADD_SWITCH:
                path = SWITCH_PATH;
                break;
            case ADD_CABLE:
                path = CABLE_PATH;
                break;
            case ADD_REAL_PC:
            default:
                path = REAL_PC_PATH;
                break;
        }
        
        ImageIcon tmp = null;
        
        // load image
        try {
            tmp = new ImageIcon(getScaledImage(path, ICON_SIZE_MENU_BAR));
        } catch (IOException ex) {
            // should never happen, all hwComponentType default icons are in .jar as a resource
        }
        
        // return scaled image
        return tmp;
    }
 

    /**
     * Returns image for MainTool tool at path. If image at path could
     * not be loaded, the default image for tool is returned.
     * @param tool
     * @param path
     * @return ImageIcon with ICON_SIZE_MENU_BAR size.
     */
    public ImageIcon getImageIconForToolbar(MainTool tool, String path) {
        ImageIcon icon = null;
        if (path == null) {
            icon = getImageIconForToolbar(tool);
        } else {
            try {
                icon = new ImageIcon(getScaledImage(path, ICON_SIZE_MENU_BAR));
            } catch (Exception e) {
                System.out.println("chyba pri nacitani obrazku");
                icon = getImageIconForToolbar(tool);
            }
        }

        return icon;
    }

    /**
     * Creates scaled image of image at path. Size will be size x size.
     * @param path
     * @param size
     * @return scaled image
     * @throws IOException 
     */
    private Image getScaledImage(String path, int size) throws IOException{
        Image tmp = bufferedImageLoader.getImage(path);
        tmp = tmp.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return tmp;
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
