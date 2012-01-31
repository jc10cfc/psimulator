package psimulator.userInterface.imageFactories;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import javax.swing.ImageIcon;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.SecondaryTool;

/**
 *
 * @author Martin
 */
public abstract class AbstractImageFactory {

    public static final int ICON_SIZE_MENU_BAR = 48;
    public static final int ICON_SIZE_MENU_BAR_POPUP = 30;
    
    //public static final String TOOL_HAND_PATH = "/resources/toolbarIcons/editor_toolbar/cursor_arrow.png";
    public static final String TOOL_HAND_PATH = "/resources/toolbarIcons/editor_toolbar/cursor_hand_mod_2.png";
    public static final String TOOL_CABLE_PATH = "/resources/toolbarIcons/editor_toolbar/network-wired.png";
   
    public static final String TOOL_REAL_PC_PATH = "/resources/toolbarIcons/editor_toolbar/local_network.png";
    public static final String TOOL_ALIGN_TO_GRID_PATH = "/resources/toolbarIcons/editor_toolbar/grid.png";
    public static final String TOOL_FIT_TO_SIZE_PATH = "/resources/toolbarIcons/editor_toolbar/fit_to_size.png";
    
    //
    public static final String TOOL_END_DEVICE_WORKSTATION_PATH = "/resources/toolbarIcons/editor_toolbar/desktop.png";
    public static final String TOOL_END_DEVICE_PC_PATH = "/resources/toolbarIcons/editor_toolbar/pc.png";
    public static final String TOOL_END_DEVICE_NOTEBOOK_PATH = "/resources/toolbarIcons/editor_toolbar/notebook.png";
    public static final String TOOL_ROUTER_PATH = "/resources/toolbarIcons/editor_toolbar/router.png";
    public static final String TOOL_SWITCH_PATH = "/resources/toolbarIcons/editor_toolbar/switch.png";
    
    protected ImageBuffer imageBuffer;
    protected BufferedImageLoader bufferedImageLoader;
    
    // scales 3 je alpha
    private float[] scales = {1f, 1f, 1f, 1f};
    private float[] offsets = {50f, 50f, 50f, 1f};

    protected RescaleOp rescaleOp = new RescaleOp(scales, offsets, null);


    public AbstractImageFactory() {
        this.imageBuffer = new ImageBuffer();
        this.bufferedImageLoader = new BufferedImageLoader();
    }

    /**
     * Returns BufferedImage  for hwComponentType at path of size. If marked,
     * the result image is brighter by 50%.
     * @param hwComponentType
     * @param width
     * @param marked
     * @return 
     */
    public BufferedImage getImage(HwTypeEnum hwComponentType, Integer width, boolean marked) {
        BufferedImage image;
        String path = getPath(hwComponentType);
 
        image = imageBuffer.getBufferedImage(path, width, marked);

        if (image == null) {
            // load image from file
            image = createImage(hwComponentType, path, width, marked);
            // put image into buffer
            imageBuffer.putBufferedImage(path, width, image, marked);
        }

        return image;
    }
    
    
    /**
     * Returns BufferedImage  for hwComponentType at path of size. If marked,
     * the result image is brighter by 50%.
     * @param hwComponentType
     * @param path
     * @param width
     * @param marked
     * @return 
    */
    public BufferedImage getImage(HwTypeEnum hwComponentType, String path, Integer width, boolean marked) {
        BufferedImage image;

        image = imageBuffer.getBufferedImage(path, width, marked);

        if (image == null) {
            // load image from file
            image = createImage(hwComponentType, path, width, marked);
            // put image into buffer
            imageBuffer.putBufferedImage(path, width, image, marked);
        }

        return image;
    } 

    /**
     * Creates BufferedImage  for hwComponentType at path of size. If marked,
     * the result image is brighter by 50%.
     * @param hwComponentType
     * @param path
     * @param width
     * @param marked
     * @return 
     */
    protected BufferedImage createImage(HwTypeEnum hwComponentType, String path, Integer width, boolean marked) {

        BufferedImage bi = null;
    
        Image tmp = null;
        
        try {
            // loads image from path and scales it to desired size
            tmp = getScaledImage(path, width);
        } catch (IOException ex) {
            try {
                // load default image
                tmp = getScaledImage(getPath(hwComponentType), width);
            } catch (IOException ex1) {
                // should never happen, all hwComponentType default icons are in .jar as a resource
            }
        }
        
        // create new buffered image to paint on
        bi = new BufferedImage(tmp.getWidth(null), tmp.getHeight(null), BufferedImage.TYPE_INT_ARGB);

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
                path = TOOL_HAND_PATH;
                break;
            case ADD_END_DEVICE:
                path = TOOL_END_DEVICE_PC_PATH;
                break;
            case ADD_ROUTER:
                path = TOOL_ROUTER_PATH;
                break;
            case ADD_SWITCH:
                path = TOOL_SWITCH_PATH;
                break;
            case ADD_CABLE:
                path = TOOL_CABLE_PATH;
                break;
            case ADD_REAL_PC:
            default:
                path = TOOL_REAL_PC_PATH;
                break;
        }
        
        ImageIcon tmp = null;
        
        // load image
        try {
            //tmp = new ImageIcon(getScaledImage(path, ICON_SIZE_MENU_BAR));
            tmp = createSquareImage(getScaledImage(path, ICON_SIZE_MENU_BAR));
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
                //icon = new ImageIcon(getScaledImage(path, ICON_SIZE_MENU_BAR));
                icon = createSquareImage(getScaledImage(path, ICON_SIZE_MENU_BAR));
            } catch (Exception e) {
                System.out.println("chyba pri nacitani obrazku");
                icon = getImageIconForToolbar(tool);
            }
        }

        return icon;
    }
    
    public ImageIcon getImageIconForToolbar(SecondaryTool tool) {
        String path ;

        switch (tool) {
            case ALIGN_TO_GRID:
                path = TOOL_ALIGN_TO_GRID_PATH;
                break;
            case FIT_TO_SIZE:
            default:
                path = TOOL_FIT_TO_SIZE_PATH;
                break;
        }
        
        ImageIcon tmp = null;
        
        // load image
        try {
            //tmp = new ImageIcon(getScaledImage(path, ICON_SIZE_MENU_BAR));
            tmp = createSquareImage(getScaledImage(path, ICON_SIZE_MENU_BAR));
        } catch (IOException ex) {
            // should never happen, all hwComponentType default icons are in .jar as a resource
        }
        
        // return scaled image
        return tmp;
    }

    private ImageIcon createSquareImage(Image image){
        int size = image.getWidth(null);
        
        int offsetHeight = (image.getWidth(null) - image.getHeight(null))/2;
        
        BufferedImage  bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        
        // create graphics and set hints
        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // draw image
        graphics2D.drawImage(image, 0, offsetHeight, null);
        
        graphics2D.dispose();
        
        return new ImageIcon(bi);
    }
    
    /**
     * Creates scaled image of image at path. Size will be size x size.
     * @param path
     * @param width
     * @return scaled image
     * @throws IOException 
     */
    private Image getScaledImage(String path, int width) throws IOException{
        Image tmp = bufferedImageLoader.getImage(path);
        int height = (int)((double)(tmp.getHeight(null) / (double)tmp.getWidth(null)) * width);
        tmp = tmp.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return tmp;
    }
    
    public void clearBuffer() {
        imageBuffer.clearBuffer();
    }
    
    private String getPath(HwTypeEnum type){
        switch(type){
            case CISCO_ROUTER:
            case LINUX_ROUTER:
                return TOOL_ROUTER_PATH;
            case CISCO_SWITCH:
            case LINUX_SWITCH:
                return TOOL_SWITCH_PATH;
            case END_DEVICE_PC:
                return TOOL_END_DEVICE_PC_PATH;
            case END_DEVICE_NOTEBOOK:
                return TOOL_END_DEVICE_NOTEBOOK_PATH;
            case END_DEVICE_WORKSTATION:
                return TOOL_END_DEVICE_WORKSTATION_PATH;
            case CABLE_ETHERNET:
            case CABLE_OPTIC:
                return TOOL_CABLE_PATH;
            case REAL_PC:
            default:
                return TOOL_REAL_PC_PATH;
        }
    }
}
