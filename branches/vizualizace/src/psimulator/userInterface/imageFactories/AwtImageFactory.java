package psimulator.userInterface.imageFactories;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import psimulator.dataLayer.Enums.HwComponentEnum;

/**
 *
 * @author Martin
 */
public class AwtImageFactory extends AbstractImageFactory {

    @Override
    public BufferedImage getBufferedImage(HwComponentEnum hwComponent, Integer size, boolean marked) {
        BufferedImage bufferedImage;

        bufferedImage = imageBuffer.getBufferedImage(hwComponent, size, marked);

        if (bufferedImage == null) {
            // load image from file
            bufferedImage = loadImage(hwComponent, size, marked);
            // put image into buffer
            imageBuffer.putBufferedImage(hwComponent, size, bufferedImage, marked);
        }

        return bufferedImage;
    }

    /**
     * Loads image from file
     * @param hwComponent What component to load
     * @param size Size of returned BufferedImage
     * @param marked if icon marked
     * @return BufferedImage with result image
     */
    private BufferedImage loadImage(HwComponentEnum hwComponent, Integer size, boolean marked) {
        BufferedImage bi = null;
        BufferedImage temp = null;
        
        String tail="";
        if(marked){
            tail ="_on";
        }
        
        try {
            switch (hwComponent) {
                case PC:
                    temp = ImageIO.read(getClass().getResource("/resources/toolbarIcons/editor/modern/iMac"+tail+".png"));
                    break;
            }
            
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
}
