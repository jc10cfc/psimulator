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
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class HwComponent extends AbstractHwComponent {

    /**
     * Use when creating graph by user actions.
     * @param imageFactory
     * @param dataLayer
     * @param hwType
     * @param interfacesCount 
     */
    public HwComponent(AbstractImageFactory imageFactory, DataLayerFacade dataLayer,
            HwTypeEnum hwType, int interfacesCount) {
        super(imageFactory, dataLayer, hwType);

        // generate device name for HwComponent
        deviceName = GeneratorSingleton.getInstance().getNextDeviceName(hwType);

        // generate names for interface
        List<String> ethInterfaceNames = GeneratorSingleton.getInstance().getInterfaceNames(hwType, interfacesCount);

        // create interfaces
        for (int i = 0; i < interfacesCount; i++) {
            interfaces.add(new EthInterface(ethInterfaceNames.get(i), hwType));
        }
    }
    
    /**
     * Use when building graph from Network.
     * @param id
     * @param hwComponentType
     * @param deviceName
     * @param ethInterfaces
     * @param x
     * @param y 
     */
    public HwComponent(int id, HwTypeEnum hwComponentType, String deviceName, List<EthInterface> ethInterfaces, int x, int y){
        super(id, hwComponentType);
        
        this.deviceName = deviceName;
        
        this.interfaces = ethInterfaces;
        
        // set x,y coordinates
        this.setDefaultZoomXPos(x);
        this.setDefaultZoomYPos(y);
    }

    @Override
    public void initialize() {
        doUpdateImages();

        // set image width and height in default zoom
        defaultZoomWidth = ZoomManagerSingleton.getInstance().doScaleToDefault(imageUnmarked.getWidth());
        defaultZoomHeight = ZoomManagerSingleton.getInstance().doScaleToDefault(imageUnmarked.getHeight());
    }

    @Override
    public final void doUpdateImages() {
        // get new images of icons
        imageUnmarked = imageFactory.getImage(hwType, ZoomManagerSingleton.getInstance().getIconWidth(), false);
        imageMarked = imageFactory.getImage(hwType, ZoomManagerSingleton.getInstance().getIconWidth(), true);
        
        // get texts that have to be painted
        List<String> texts = getTexts();
        textImages = getTextsImages(texts, ZoomManagerSingleton.getInstance().getCurrentFontSize());
        
        // set text images width and height
        int textW = 0;
        int textH = 0;

        for (BufferedImage image : textImages) {
            if (image.getWidth() > textW) {
                textW = image.getWidth();
            }

            textH = textH + image.getHeight();
        }

        defaultZoomTextWidth = ZoomManagerSingleton.getInstance().doScaleToDefault(textW);
        defaultZoomTextHeight = ZoomManagerSingleton.getInstance().doScaleToDefault(textH);
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
        if (textImages != null) {
            paintTextsUnderImage(g2);
        }
    }

    private void paintTextsUnderImage(Graphics2D g2) {
        paintTexts(g2, textImages);
    }

    /**
     * Paint imagess of texts centered in Y_axes under component image
     *
     * @param g2
     * @param images
     */
    private void paintTexts(Graphics2D g2, List<BufferedImage> images) {
        int x;
        int y = getY() + getHeight() + 1;

        for (BufferedImage image : images) {
            x = (int) (getX() - ((image.getWidth() - getWidth()) / 2.0));

            g2.drawImage(image, x, y, null);
            
            y = y + image.getHeight();
        }
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
            switch (ZoomManagerSingleton.getInstance().getCurrentLevelOfDetails()) {
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
            texts.add(dataLayer.getString(getHwType().toString()));
        }

        if (paintName) {
            texts.add(getDeviceName());
        }


        return texts;
    }
}
