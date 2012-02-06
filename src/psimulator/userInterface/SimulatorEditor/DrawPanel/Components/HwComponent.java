package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GeneratorSingleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class HwComponent extends AbstractHwComponent {

    //private HwTypeEnum hwComponentType;
    //private String imagePath;
    /*
     * public HwComponent(AbstractImageFactory imageFactory, ZoomManager
     * zoomManager, HwTypeEnum hwComponentType, int interfacesCount, String
     * imagePath, String name) {
     */
    int textX;
    int textY;
    int textW;
    int textH;

    public HwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, DataLayerFacade dataLayer,
            HwTypeEnum hwComponentType, int interfacesCount) {
        super(imageFactory, zoomManager, dataLayer, interfacesCount);

        this.hwComponentType = hwComponentType;
        //this.imagePath = imagePath;

        // generate device name for HwComponent
        deviceName = GeneratorSingleton.getInstance().getNextDeviceName(hwComponentType);

        // generate names for interface
        List<String> ethInterfaceNames = GeneratorSingleton.getInstance().getInterfaceNames(hwComponentType, interfacesCount);

        // create interfaces
        for (int i = 0; i < interfacesCount; i++) {
            interfaces.add(new EthInterface(ethInterfaceNames.get(i), null));
        }

        // if custom image needed, imagePath is required
        //bi = imageFactory.getImage(hwComponentType, imagePath, zoomManager.getIconWidthDefaultZoom(), true);


        //create image in default zoom
        bi = imageFactory.getImage(hwComponentType, zoomManager.getIconWidth(), false);

        // set image width and height in default zoom
        defaultZoomWidth = zoomManager.doScaleToDefault(bi.getWidth());
        defaultZoomHeight = zoomManager.doScaleToDefault(bi.getHeight());
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isMarked()) {
            bi = imageFactory.getImage(hwComponentType, zoomManager.getIconWidth(), true);
        } else {
            bi = imageFactory.getImage(hwComponentType, zoomManager.getIconWidth(), false);
        }

        //g2.drawImage(bi, getX(), getY(), null);
        g2.drawImage(bi, getImageX(), getImageY(), null);

        paintTextsUnderImage(g2);
    }

    private void paintTextsUnderImage(Graphics2D g2) {
        // get texts that have to be painted
        List<String> texts = getTexts();
        // create images from texts
        List<BufferedImage> images = getTextsImages(texts, g2);
        // paint images
        paintTexts(g2, images);
        
        //
        /*
        g2.setColor(Color.yellow);
        
        g2.drawRect(textX, textY, textW, textH);
        
        g2.setColor(Color.BLACK);
        */
    }

    /**
     * Paint imagess of texts centered in Y_axes under component image
     * @param g2
     * @param images 
     */
    private void paintTexts(Graphics2D g2, List<BufferedImage> images) {
        int x;
        //int y = getY() + getHeight()+1;
        int y = getImageY() + getImageHeight()+1;

        textX = Integer.MAX_VALUE;
        textY = y;
        textW = 0;
        textH = 0;

        for (BufferedImage image : images) {
            //x = (int) (getX() - ((image.getWidth() - getWidth()) / 2.0));
            x = (int) (getImageX() - ((image.getWidth() - getImageWidth()) / 2.0));
            
            g2.drawImage(image, x, y, null);

            y = y + image.getHeight();// + margin;

            // update sizes
            if (x<textX) {
                textX = x;
            }
            
            if(image.getWidth()> textW){
                textW = image.getWidth();
            }
            
            textH = textH + image.getHeight();
        }
        
        actualTextImageWidth = textW;
        actualTextImageHeight = textH;
    }

    /**
     * Creates images for givent texts
     *
     * @param texts
     * @param g2
     * @return
     */
    private List<BufferedImage> getTextsImages(List<String> texts, Graphics2D g2) {
        // create font
        Font font = new Font("SanSerif", Font.PLAIN, zoomManager.getCurrentFontSize());

        //
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        List<BufferedImage> images = new ArrayList<BufferedImage>();

        for (String text : texts) {
            images.add(getImageForText(fm, text, font));
        }

        return images;
    }

    /**
     * Creates image for text in font with given FontMetrics
     *
     * @param fm
     * @param text
     * @param font
     * @return
     */
    private BufferedImage getImageForText(FontMetrics fm, String text, Font font) {
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent() + fm.getDescent();

        return imageFactory.getImageWithText(text, font, textWidth, textHeight, fm.getMaxAscent());
    }

    /**
     * Gets text that have to be displayed with this component.
     *
     * @return
     */
    private List<String> getTexts() {
        boolean paintType = false;
        boolean paintName = false;

        // if LOD active
        if (dataLayer.getLevelOfDetails() == LevelOfDetailsMode.AUTO) {
            switch (zoomManager.getCurrentLevelOfDetails()) {
                case LEVEL_1:
                    break;
                case LEVEL_2:
                    paintName = true;
                    break;
                case LEVEL_3:
                default:
                    paintType = true;
                    paintName = true;
                    break;
            }
        } else { // if LOD not active
            paintName = dataLayer.isViewDeviceNames();
            paintType = dataLayer.isViewDeviceTypes();
        }

        /*
         * if (paintName == false && paintType == false) { return null; }
         */

        // list for texts
        List<String> texts = new ArrayList<String>();

        if (paintType) {
            texts.add(dataLayer.getString(getHwComponentType().toString()));
        }

        if (paintName) {
            texts.add(getDeviceName());
        }


        return texts;
    }

}
