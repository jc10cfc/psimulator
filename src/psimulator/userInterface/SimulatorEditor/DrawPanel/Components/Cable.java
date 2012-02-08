package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GraphicUtils;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class Cable extends AbstractComponent {

    private DataLayerFacade dataLayer;
    private HwTypeEnum hwType;
    private AbstractHwComponent component1;
    private AbstractHwComponent component2;
    private EthInterface eth1;
    private EthInterface eth2;
    //
    private BufferedImage delayImage;
    //
    protected List<BufferedImage> eth1TextImages;
    protected List<BufferedImage> eth2TextImages;
    //
    private int delay;
    private Line2D line = new Line2D.Float();
    private Stroke stroke = new BasicStroke(3.5f);
    //int x1, y1, x2, y2;
    private boolean paintInterfaceNames = false;
    private boolean paintIpAddress = false;
    private boolean paintMacAddress = false;
    private boolean paintDelay = false;

    public Cable(DataLayerFacade dataLayer, AbstractImageFactory imageFactory, HwTypeEnum hwType, AbstractHwComponent component1,
            AbstractHwComponent component2, EthInterface eth1, EthInterface eth2, ZoomManager zoomManager) {
        super(imageFactory, zoomManager);
        this.dataLayer = dataLayer;
        this.hwType = hwType;
        this.component1 = component1;
        this.component2 = component2;
        this.eth1 = eth1;
        this.eth2 = eth2;
        
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

    @Override
    public void initialize() {
        doUpdateImages();
    }

    @Override
    public void doUpdateImages() {
        // get delay image
        delayImage = getTextImage("" + delay, zoomManager.getCurrentFontSize() - 2);

        // set what needs to be painted
        setWhatToPaint();

        // get texts that have to be painted
        List<String> texts = getInterfaceTexts(eth1);
        
        // get images that have to be painted
        eth1TextImages = getTextsImages(texts, zoomManager.getCurrentFontSize() - 2);
        
        texts = getInterfaceTexts(eth2);
        
        // get images that have to be painted
        eth2TextImages = getTextsImages(texts, zoomManager.getCurrentFontSize() - 2);
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

        
        paintCableLabels(g2);

        if (paintDelay) {
            // paint delay
            paintDelayLabel(g2);
        }
    }

    private void paintDelayLabel(Graphics2D g2) {
        Point middlePoint = GraphicUtils.getMiddlePoint(line.getX1(), line.getY1(), line.getX2(), line.getY2());

        g2.drawImage(delayImage, middlePoint.x, middlePoint.y, null);
    }

    private void paintCableLabels(Graphics2D g2) {
        if(eth1TextImages!=null && !eth1TextImages.isEmpty()){
            paintInterfaceLabels(g2, eth1TextImages, component1, true);
        }
        if(eth2TextImages!=null && !eth2TextImages.isEmpty()){
            paintInterfaceLabels(g2, eth2TextImages, component2, false);
        }
    }

    private void paintInterfaceLabels(Graphics2D g2, List<BufferedImage> images, AbstractHwComponent component, boolean first) {
        // get edpoints of line
        Point lineP1 = new Point((int) line.getP1().getX(), (int) line.getP1().getY());
        Point lineP2 = new Point((int) line.getP2().getX(), (int) line.getP2().getY());

        Point intersection;

        if (first) {
            // P1 inside
            intersection = component.getIntersectingPoint(lineP1, lineP2);
        } else {
            // P2 inside
            intersection = component.getIntersectingPoint(lineP2, lineP1);
        }

        // paint images
        paintTexts(g2, images, component, intersection);
    }

    /**
     * Paint imagess of texts centered in Y_axes under component image
     *
     * @param g2
     * @param images
     */
    private void paintTexts(Graphics2D g2, List<BufferedImage> images, AbstractHwComponent component, Point intersectingPoint) {
        int x;
        int y;
        
        int width = 0;
        int height = 0;
        
        boolean leftAlign;
        
        
        for (BufferedImage image : images) {
            if(image.getWidth() > width){
                width = image.getWidth();
            }
            height += image.getHeight();
        }

        // left
        if (intersectingPoint.x <= component.getCenterLocation().x) {
            if (intersectingPoint.y <= component.getCenterLocation().y) {
                // upper left
                x = intersectingPoint.x - width;
                y = intersectingPoint.y - height;
            } else {
                // lower left
                x = intersectingPoint.x - width;
                y = intersectingPoint.y;
            }
            leftAlign = false;
        } else {  // right
            if (intersectingPoint.y <= component.getCenterLocation().y) {
                // upper right
                x = intersectingPoint.x;
                y = intersectingPoint.y - height;
            } else {
                // lower right
                x = intersectingPoint.x;
                y = intersectingPoint.y;
            }
            leftAlign = true;
        }
        
        int tmpX;
        int tmpY;
        
        tmpX = x;
        tmpY = y;
        
        
        for (BufferedImage image : images) {
            //x = (int) (getX() - ((image.getWidth() - getWidth()) / 2.0));
            if(!leftAlign){ // right align
                tmpX = x + width - image.getWidth();
            }
            
            g2.drawImage(image, tmpX, tmpY, null);

            tmpY = tmpY + image.getHeight();// + margin;
        }
    }


    private void setWhatToPaint() {
        if (dataLayer.getLevelOfDetails() == LevelOfDetailsMode.AUTO) {
            switch (zoomManager.getCurrentLevelOfDetails()) {
                case LEVEL_4:
                    paintDelay = true;
                    paintInterfaceNames = true;
                    paintIpAddress = true;
                    paintMacAddress = true;
                    break;
                default:
                    paintDelay = false;
                    paintInterfaceNames = false;
                    paintIpAddress = false;
                    paintMacAddress = false;
                    break;
            }
        } else { // if LOD not active
            paintDelay = dataLayer.isViewCableDelay();
            paintInterfaceNames = dataLayer.isViewInterfaceNames();
            paintIpAddress = dataLayer.isViewIpAddresses();
            paintMacAddress = dataLayer.isViewMacAddresses();
        }
    }

    /**
     * Gets text that have to be displayed with this component.
     *
     * @return
     */
    private List<String> getInterfaceTexts(EthInterface ethInterface) {
        // list for texts
        List<String> texts = new ArrayList<String>();

        if (paintInterfaceNames) {
            texts.add(ethInterface.getName());
        }

        if (paintIpAddress) {
            if (!ethInterface.getIpAddress().isEmpty()) {
                texts.add(ethInterface.getIpAddress());
            }
        }

        if (paintMacAddress) {
            if (!ethInterface.getMacAddress().isEmpty()) {
                texts.add(ethInterface.getMacAddress());
            }
        }

        return texts;
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
    
    public void swapComponentsAndEthInterfaces(){
        AbstractHwComponent tmpComponent = component1;
        component1 = component2;
        component2 = tmpComponent;
        
        EthInterface tmpImterface = eth1;
        eth1 = eth2;
        eth2 = tmpImterface;
    }
}
