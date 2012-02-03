package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
        bi = imageFactory.getImage(hwComponentType, zoomManager.getIconWidthDefaultZoom(), true);
        //bi = imageFactory.getImage(hwComponentType, zoomManager.getIconWidthDefaultZoom(), false);

        // set image width and height in default zoom
        defaultZoomWidth = bi.getWidth();
        defaultZoomHeight = bi.getHeight();
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
        boolean paintType = true;
        boolean paintName = true;

        if (paintName == false && paintType == false) {
            return;
        }

        // create font
        Font smallerFont = new Font("SanSerif", Font.PLAIN, zoomManager.getActualFontSize());

        // set font and get font metrics
        g2.setFont(smallerFont);
        FontMetrics fm = g2.getFontMetrics();

        // CREATE CLEVER FOR CYCLE HERE!!

        String tmpDeviceType = dataLayer.getString(getHwComponentType().toString());
        String tmpDeviceName = getDeviceName();
        String [] texts = {tmpDeviceType, tmpDeviceName};
        
        String text;
        int textWidth, textHeight;
        int margin = (int) (5 * zoomManager.getCurrentScale());
        
        int x;// = (int) (getX() - ((textWidth - getWidth()) / 2.0));
        int y = getY() + getHeight() + margin;// + textHeight;
        
        for (int i = 0; i < texts.length; i++) {
            text = texts[i];
            textWidth = fm.stringWidth(text);
            textHeight = fm.getAscent();
            
            x = (int) (getX() - ((textWidth - getWidth()) / 2.0));
            y = y + textHeight;
            
            g2.drawString(text, x, y);
            
            y += margin;
        }
        
        /*
        // get text
        String text = dataLayer.getString(getHwComponentType().toString());

        // get text size
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        // count margin from icon
        int margin = (int) (5 * zoomManager.getCurrentScale());

        // get x and y
        int x = (int) (getX() - ((textWidth - getWidth()) / 2.0));
        int y = getY() + getHeight() + margin + textHeight;

        // draw text
        g2.drawString(text, x, y);

        // get text
        text = getDeviceName();

        textWidth = fm.stringWidth(text);
        textHeight = fm.getAscent();

        // get x and y
        x = (int) (getX() - ((textWidth - getWidth()) / 2.0));
        y = y + margin + textHeight;

        // draw text
        g2.drawString(text, x, y);
        */
    }

    @Override
    public int getTextHeight() {
        return 20;
    }
}
