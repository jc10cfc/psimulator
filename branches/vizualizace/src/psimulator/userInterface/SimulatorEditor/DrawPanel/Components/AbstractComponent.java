package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GeneratorSingleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public abstract class AbstractComponent extends JComponent implements Markable, Identifiable {

    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    //
    protected AbstractImageFactory imageFactory;
    protected ZoomManager zoomManager;
    //
    private Integer id;
    private boolean marked = false;

    public AbstractComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager) {
        this.imageFactory = imageFactory;
        this.zoomManager = zoomManager;
        id = new Integer(GeneratorSingleton.getInstance().getNextId());
    }

    @Override
    public boolean isMarked() {
        return marked;
    }

    @Override
    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public abstract boolean intersects(Point p);

    public abstract boolean intersects(Rectangle r);

    public abstract void doUpdateImages();

    public abstract void initialize();

    @Override
    public Integer getId() {
        return id;
    }

    /**
     * Creates images for givent texts
     *
     * @param texts
     * @return
     */
    protected List<BufferedImage> getTextsImages(List<String> texts, int fontSize) {
        Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();

        // create font
        Font font = new Font("SanSerif", Font.PLAIN, fontSize); //zoomManager.getCurrentFontSize()

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
    protected BufferedImage getImageForText(FontMetrics fm, String text, Font font) {
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent() + fm.getDescent();

        return imageFactory.getImageWithText(text, font, textWidth, textHeight, fm.getMaxAscent());
    }

    /**
     * Creates images for givent texts
     *
     * @param texts
     * @param g2
     * @return
     */
    protected BufferedImage getTextImage(String text, int fontSize) {
        Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();

        // create font
        Font font = new Font("SanSerif", Font.PLAIN, fontSize); //zoomManager.getCurrentFontSize()

        //
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        return getImageForText(fm, text, font);
    }
}
