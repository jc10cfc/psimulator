package psimulator.userInterface.imageFactories;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
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
    public static final String HAND_PATH = "/resources/toolbarIcons/editor/cursor_arrow.png";
    public static final String END_DEVICE_PATH = "/resources/toolbarIcons/editor/modern/PC.png";
    public static final String ROUTER_PATH = "/resources/toolbarIcons/editor/router.png";
    public static final String SWITCH_PATH = "/resources/toolbarIcons/editor/switch.png";
    public static final String CABLE_PATH = "/resources/toolbarIcons/editor/network-wired.png";
    public static final String REAL_PC_PATH = "/resources/toolbarIcons/editor/desktop.png";

    public AbstractImageFactory() {
        this.imageBuffer = new ImageBuffer();
    }

    public abstract BufferedImage getBufferedImage(HwTypeEnum hwComponent, Integer size, boolean marked);

    public BufferedImage getBufferedImage(String path, Integer size) {
        BufferedImage bi = null;
        BufferedImage temp = null;

        try {
            temp = ImageIO.read(getClass().getResource(path));
            

            bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bi.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(temp, 0, 0, size, size, null);

            graphics2D.dispose();

        } catch (IOException ex) {
            // should never happen
            //Logger.getLogger(PcComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bi;
    }

    public ImageIcon getImageIconForToolbar(Tools tool) {
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
            case FIT_TO_SIZE:
                return null;
            //break;
        }
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/toolbarIcons/editor/" + name + ".png"));

        return (new ImageIcon(icon.getImage().getScaledInstance(ICON_SIZE_MENU_BAR, ICON_SIZE_MENU_BAR, Image.SCALE_SMOOTH)));
    }

    public ImageIcon getImageIconForToolbar(Tools tool, String path) {
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
}
