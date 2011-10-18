package psimulator.userInterface.Editor.Components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Observable;
import psimulator.dataLayer.Enums.HwComponentEnum;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class HwComponent extends AbstractHwComponent {

    private HwComponentEnum hwComponentType;

    public HwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, HwComponentEnum hwComponentType) {
        super(imageFactory, zoomManager);

        //zoomManager.addObserver(this);
        this.hwComponentType = hwComponentType;
        bi = imageFactory.getBufferedImage(hwComponentType, zoomManager.getIconSize(), true);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isMarked()) {
            bi = imageFactory.getBufferedImage(HwComponentEnum.PC, zoomManager.getIconSize(), true);
        } else {
            bi = imageFactory.getBufferedImage(HwComponentEnum.PC, zoomManager.getIconSize(), false);
        }

        g2.drawImage(bi, xPos, yPos, null);

    }
}
