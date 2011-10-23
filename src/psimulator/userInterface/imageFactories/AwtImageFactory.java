package psimulator.userInterface.imageFactories;


/**
 *
 * @author Martin
 */
public class AwtImageFactory extends AbstractImageFactory {


    /**
     * Loads image from file
     * @param hwComponent What component to load
     * @param size Size of returned BufferedImage
     * @param marked if icon marked
     * @return BufferedImage with result image
     
    private BufferedImage loadImage(HwTypeEnum hwComponent, Integer size, boolean marked) {
        BufferedImage bi = null;
        BufferedImage temp = null;
        
        String tail="";
        if(marked){
            tail ="_on";
        }
        
        try {
            switch (hwComponent) {
                case END_DEVICE:
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
    }*/
}
