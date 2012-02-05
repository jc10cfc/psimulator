package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
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
    private static Font sanSerifFont = new Font("SanSerif", Font.PLAIN, 12);

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

        g2.drawImage(bi, getX(), getY(), null);

        paintTexts(g2);
    }

    private void paintTexts(Graphics2D g2) {
        boolean paintType = false;
        boolean paintName = false;

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

        if (paintName == false && paintType == false) {
            return;
        }

        // create font
        Font smallerFont = new Font("SanSerif", Font.PLAIN, zoomManager.getCurrentFontSize());

        // set font and get font metrics
        g2.setFont(smallerFont);
        FontMetrics fm = g2.getFontMetrics();

        // list for texts
        List<String> texts = new ArrayList<String>();

        if (paintType) {
            texts.add(dataLayer.getString(getHwComponentType().toString()));
        }

        if (paintName) {
            texts.add(getDeviceName());
        }

        int textWidth, textHeight;
        int margin = (int) (5 * zoomManager.getCurrentScale());

        int x;
        int y = getY() + getHeight() + margin;// + textHeight;

        for (String text : texts) {
            textWidth = fm.stringWidth(text);
            textHeight = fm.getAscent();

            x = (int) (getX() - ((textWidth - getWidth()) / 2.0));
            y = y + textHeight;

            // paint white border
            g2.setColor(Color.WHITE);
            g2.drawString(text, x+1, y+1);
            g2.drawString(text, x+1, y-1);
            g2.drawString(text, x-1, y+1);
            g2.drawString(text, x-1, y-1);
            g2.setColor(Color.BLACK);
            
            g2.drawString(text, x, y);

            y += margin;
        }
        
    }

    @Override
    public int getTextHeight() {
        return 30;
    }
}

/*
Graphics2D g;                     // Initialized elsewhere
Font f;                           // Initialized elsewhere
String message = "Hello World!";  // The text to measure and display
Rectangle2D box;                  // The display box: initialized elsewhere

// Measure the font and the message
FontRenderContext frc = g.getFontRenderContext();
Rectangle2D bounds = f.getStringBounds(message, frc);
LineMetrics metrics = f.getLineMetrics(message, frc);
float width = (float) bounds.getWidth();     // The width of our text
float lineheight = metrics.getHeight();      // Total line height
float ascent = metrics.getAscent();          // Top of text to baseline

// Now display the message centered horizontally and vertically in box
float x0 = (float) (box.getX() + (box.getWidth() - width)/2);
float y0 = (float) (box.getY() + (box.getHeight() - lineheight)/2 + ascent);
g.setFont(f);
g.drawString(message, x0, y0);
 */