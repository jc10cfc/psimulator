package psimulator.userInterface.Editor.Components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Observable;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;
import psimulator.userInterface.Editor.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class HwComponent extends AbstractHwComponent {

    private HwTypeEnum hwComponentType;

    public HwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, HwTypeEnum hwComponentType) {
        super(imageFactory, zoomManager);

        //zoomManager.addObserver(this);
        this.hwComponentType = hwComponentType;
        bi = imageFactory.getBufferedImage(hwComponentType, zoomManager.getIconSize(), true);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isMarked()) {
            bi = imageFactory.getBufferedImage(HwTypeEnum.END_DEVICE, zoomManager.getIconSize(), true);
        } else {
            bi = imageFactory.getBufferedImage(HwTypeEnum.END_DEVICE, zoomManager.getIconSize(), false);
        }

        g2.drawImage(bi, xPos, yPos, null);

    }
}
