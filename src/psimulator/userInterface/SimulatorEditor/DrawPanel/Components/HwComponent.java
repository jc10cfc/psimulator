package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.Graphics;
import java.awt.Graphics2D;
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
            interfaces.add(new EthInterface(ethInterfaceNames.get(i), null, hwComponentType));
        }

    }
    
    @Override
    public void initialize() {
        doUpdateImages();
 
        // set image width and height in default zoom
        defaultZoomWidth = zoomManager.doScaleToDefault(imageUnmarked.getWidth());
        defaultZoomHeight = zoomManager.doScaleToDefault(imageUnmarked.getHeight());
    }
    
    @Override
    public final void doUpdateImages() {
        // get new images of icons
        imageUnmarked = imageFactory.getImage(hwComponentType, zoomManager.getIconWidth(), false);
        imageMarked = imageFactory.getImage(hwComponentType, zoomManager.getIconWidth(), true);
        
        // get texts that have to be painted
        List<String> texts = getTexts();
        textImages = getTextsImages(texts, zoomManager.getCurrentFontSize());
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isMarked()) {
            // paint image
            g2.drawImage(imageMarked, getX(), getY(), null);
        } else {
            // paint image
            g2.drawImage(imageUnmarked, getX(), getY(), null);
        }

        // paint texts
        paintTextsUnderImage(g2);
    }

    private void paintTextsUnderImage(Graphics2D g2) {
        paintTexts(g2, textImages);
    }

    /**
     * Paint imagess of texts centered in Y_axes under component image
     * @param g2
     * @param images 
     */
    private void paintTexts(Graphics2D g2, List<BufferedImage> images) {
        int x;
        int y = getY() + getHeight()+1;
        
        
        int textX = Integer.MAX_VALUE;
        int textY = y;
        int textW = 0;
        int textH = 0;

        for (BufferedImage image : images) {
            x = (int) (getX() - ((image.getWidth() - getWidth()) / 2.0));
            
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
        
        defaultZoomTextWidth = zoomManager.doScaleToDefault(textW);
        defaultZoomTextHeight = zoomManager.doScaleToDefault(textH);
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
                case LEVEL_4:
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
