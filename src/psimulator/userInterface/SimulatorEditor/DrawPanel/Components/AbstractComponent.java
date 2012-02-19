package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GeneratorSingleton;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public abstract class AbstractComponent extends JComponent implements Markable, Identifiable {

    //
    protected AbstractImageFactory imageFactory;
    protected DataLayerFacade dataLayer;
    //
    protected HwTypeEnum hwType;
    protected Integer id;
    //
    private boolean marked = false;
    //
    
    
    
    /**
     * Use when creating graph by user actions.
     * @param dataLayer
     * @param imageFactory
     * @param hwType 
     */
    public AbstractComponent(DataLayerFacade dataLayer, AbstractImageFactory imageFactory, HwTypeEnum hwType){
        this.id = new Integer(GeneratorSingleton.getInstance().getNextId());
        //
        this.dataLayer = dataLayer;
        this.imageFactory = imageFactory;
        this.hwType = hwType;
    }
    
    /**
     * Use when building graph from Network.
     * 
     * @param id
     * @param hwType 
     */
    public AbstractComponent(Integer id, HwTypeEnum hwType){
        this.id = id;
        this.hwType = hwType;
    }
    
    /**
     * Use when building graph from Network.
     * @param dataLayer
     * @param imageFactory
     */
    public void setInitReferences(DataLayerFacade dataLayer, AbstractImageFactory imageFactory){
        this.dataLayer = dataLayer;
        this.imageFactory = imageFactory;
    }
    
    /**
     * Need to call it after constructor to properly setup component.
     * @param imageFactory 
     */
    public void setImageFactory(AbstractImageFactory imageFactory){
        this.imageFactory = imageFactory;
    }

    @Override
    public boolean isMarked() {
        return marked;
    }

    @Override
    public void setMarked(boolean marked) {
        this.marked = marked;
    }
    
    @Override
    public Integer getId() {
        return id;
    }
    
    public HwTypeEnum getHwType() {
        return hwType;
    }

    public abstract boolean intersects(Point p);

    public abstract boolean intersects(Rectangle r);

    public abstract void doUpdateImages();

    public abstract void initialize();

    

    /**
     * Creates images for givent texts
     *
     * @param texts
     * @return
     */
    protected List<BufferedImage> getTextsImages(List<String> texts, int fontSize) {
        // create font
        Font font = new Font("SanSerif", Font.PLAIN, fontSize); //zoomManager.getCurrentFontSize()

        // get font metrics
        FontMetrics fm = imageFactory.getFontMetrics(font);

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
        // create font
        Font font = new Font("SanSerif", Font.PLAIN, fontSize); //zoomManager.getCurrentFontSize()

        // get font metrics
        FontMetrics fm = imageFactory.getFontMetrics(font);

        return getImageForText(fm, text, font);
    }
}
