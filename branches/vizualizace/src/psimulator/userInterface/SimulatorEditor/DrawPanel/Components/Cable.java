package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class Cable extends AbstractComponent {

    private DataLayerFacade dataLayer;
    private AbstractImageFactory imageFactory;
    private HwTypeEnum hwType;
    private ZoomManager zoomManager;
    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private EthInterface eth1;
    private EthInterface eth2;
    private int delay;
    private Line2D line = new Line2D.Float();
    private Stroke stroke = new BasicStroke(3.5f);
    int x1, y1, x2, y2;

    public Cable(DataLayerFacade dataLayer, AbstractImageFactory imageFactory, HwTypeEnum hwType, AbstractHwComponent component1,
            AbstractHwComponent component2, EthInterface eth1, EthInterface eth2, ZoomManager zoomManager) {
        this.dataLayer = dataLayer;
        this.imageFactory = imageFactory;
        this.hwType = hwType;
        this.component1 = component1;
        this.component2 = component2;
        this.eth1 = eth1;
        this.eth2 = eth2;
        this.zoomManager = zoomManager;

        // set delay according to type
        switch (hwType) {
            case CABLE_ETHERNET:
                delay = 10;
                break;
            case CABLE_OPTIC:
            default:
                delay = 2;
                break;
        }
    }

    public AbstractHwComponent getComponent1() {
        return component1;
    }

    public AbstractHwComponent getComponent2() {
        return component2;
    }

    public EthInterface getEth1() {
        return eth1;
    }

    public EthInterface getEth2() {
        return eth2;
    }

    public void paintComponent(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;

        stroke = new BasicStroke(zoomManager.getStrokeWidth());

        line.setLine(x1, y1, x2, y2);

        Color tmpC = g2.getColor();
        Stroke tmpS = g2.getStroke();

        if (isMarked()) {
            g2.setColor(Color.blue);
            g2.setStroke(stroke);
            g2.drawLine(x1, y1, x2, y2);
        } else {
            // set cable color
            switch (hwType) {
                case CABLE_ETHERNET:
                    g2.setColor(Color.black);
                    break;
                case CABLE_OPTIC:
                default:
                    g2.setColor(Color.gray);
                    break;
            }

            //g2.setColor(Color.black);
            g2.setStroke(stroke);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(tmpC);
        g2.setStroke(tmpS);

        // paint labels
        paintCableLabels(g2);

    }

    private void paintCableLabels(Graphics2D g2) {
        boolean paintLabels = false;

        if (dataLayer.getLevelOfDetails() == LevelOfDetailsMode.AUTO) {
            switch (zoomManager.getCurrentLevelOfDetails()) {
                case LEVEL_3:
                    paintLabels = true;
                    break;
                default:
                    paintLabels = false;
                    break;
            }
        } else {
            if (dataLayer.isViewInterfaceNames()) {
                paintLabels = true;
            } else {
                paintLabels = false;
            }
        }

        if (paintLabels) {
            paintInterfaceName(g2, eth1, component1, true);
            paintInterfaceName(g2, eth2, component2, false);
        }
    }

    private void paintInterfaceName(Graphics2D g2, EthInterface ethInterface, AbstractHwComponent component, boolean first) {
        // get text to be painted
        String text = ethInterface.getName();
        // create images from texts
        BufferedImage image = getTextImage(text, g2);
 
        Point lineP1 = new Point((int)line.getP1().getX(), (int)line.getP1().getY());
        Point lineP2 = new Point((int)line.getP2().getX(), (int)line.getP2().getY());

        Point intersection;
        
        if(first){
            // P1 inside
            intersection = component.getIntersectingPoint(lineP1, lineP2);
        }else{
            // P2 inside
            intersection = component.getIntersectingPoint(lineP2, lineP1);
        }
  
        // paint images
        paintText(g2, image, component, intersection);
    }

    /**
     * Paint imagess of texts centered in Y_axes under component image
     *
     * @param g2
     * @param images
     */
    private void paintText(Graphics2D g2, BufferedImage image, AbstractHwComponent component, Point intersectingPoint) {
        int x;
        int y;
        
        // left
        if( intersectingPoint.x <= component.getCenterLocation().x){
            if(intersectingPoint.y <= component.getCenterLocation().y){
                // upper left
                x = intersectingPoint.x - image.getWidth();
                y = intersectingPoint.y - image.getHeight();
            }else{
                // lower left
                x = intersectingPoint.x - image.getWidth();
                y = intersectingPoint.y;
            }
        }else{  // right
            if(intersectingPoint.y <= component.getCenterLocation().y){
                // upper right
                x = intersectingPoint.x;
                y = intersectingPoint.y - image.getHeight();
            }else{
                // lower right
                x = intersectingPoint.x;
                y = intersectingPoint.y;
            }
        }
        
        g2.drawImage(image, x, y, null);
    }

    /**
     * Creates images for givent texts
     *
     * @param texts
     * @param g2
     * @return
     */
    private BufferedImage getTextImage(String text, Graphics2D g2) {
        // create font
        Font font = new Font("SanSerif", Font.PLAIN, zoomManager.getCurrentFontSize());

        //
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        return getImageForText(fm, text, font);
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

    @Override
    public int getWidth() {
        //return Math.abs(getX1()-getX2());
        //return LINE_WIDTH;
        return (int) zoomManager.getStrokeWidth();
    }

    @Override
    public int getHeight() {
        //return Math.abs(getY1()-getY2());
        //return LINE_WIDTH;
        return (int) zoomManager.getStrokeWidth();
    }

    @Override
    public int getX() {
        if (getX1() <= getX2()) {
            return getX1();
        }
        return getX2();
    }

    @Override
    public int getY() {
        if (getY1() <= getY2()) {
            return getY1();
        }
        return getY2();
    }

    @Override
    public boolean intersects(Point p) {
        Rectangle r = new Rectangle(p);
        return intersects(r);
    }

    @Override
    public boolean intersects(Rectangle r) {
        return line.intersects(r);
    }

    public int getX1() {
        return component1.getCenterLocation().x;
    }

    public int getY1() {
        return component1.getCenterLocation().y;
    }

    public Point2D getP1() {
        return component1.getCenterLocation();
    }

    public int getX2() {
        return component2.getCenterLocation().x;
    }

    public int getY2() {
        return component2.getCenterLocation().y;
    }

    public Point2D getP2() {
        return component2.getCenterLocation();
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public HwTypeEnum getHwType() {
        return hwType;
    }
}
