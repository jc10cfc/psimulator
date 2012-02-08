package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GraphicUtils;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public abstract class AbstractHwComponent extends AbstractComponent {

    // position in 1:1 zoom
    protected int defaultZoomXPos;
    protected int defaultZoomYPos;
    protected int defaultZoomWidth;
    protected int defaultZoomHeight;
    // position of textRectangle in 1:1 zoom
    //protected int defaultZoomTextXPos;
    //protected int defaultZoomTextYPos;
    protected int defaultZoomTextWidth;
    protected int defaultZoomTextHeight;
    //
    protected ZoomManager zoomManager;
    protected AbstractImageFactory imageFactory;
    private List<BundleOfCables> bundlesOfCables = new ArrayList<BundleOfCables>();
    protected List<EthInterface> interfaces = new ArrayList<EthInterface>();
    protected BufferedImage imageUnmarked;
    protected BufferedImage imageMarked;
    protected List<BufferedImage> textImages;
    protected String deviceName;
    protected HwTypeEnum hwComponentType;
    //
    protected DataLayerFacade dataLayer;
    //
    
    public AbstractHwComponent(AbstractImageFactory imageFactory, ZoomManager zoomManager, DataLayerFacade dataLayer, int interfacesCount) {
        super();
        this.dataLayer = dataLayer;
        this.zoomManager = zoomManager;
        this.imageFactory = imageFactory;
    }

    /**
     * Changes position by offset in parameter adding it to default zoom
     *
     * @param offsetInDefaultZoom
     */
    public void doChangePosition(Dimension offsetInDefaultZoom, boolean positive) {
        Point defaultZoomPoint;
        // count point to move component
        if (positive) {
            defaultZoomPoint = new Point(defaultZoomXPos + offsetInDefaultZoom.width,
                    defaultZoomYPos + offsetInDefaultZoom.height);
        } else {
            defaultZoomPoint = new Point(defaultZoomXPos - offsetInDefaultZoom.width,
                    defaultZoomYPos - offsetInDefaultZoom.height);
        }
        // set new postion
        setLocation(defaultZoomPoint.x, defaultZoomPoint.y);
    }

    /**
     * Sets position with center of image in the middlePoint
     *
     * @param middlePoint Center of image in actual zoom
     */
    public void setLocationByMiddlePoint(Point middlePoint) {
        setLocation(zoomManager.doScaleToDefault(middlePoint.x) - (defaultZoomWidth / 2),
                zoomManager.doScaleToDefault(middlePoint.y) - (defaultZoomHeight / 2));
    }

    @Override
    public void setLocation(int defaultZoomXPos, int defaultZoomYPos) {
        //System.out.println("tady");

        if (defaultZoomXPos < 0) {
            defaultZoomXPos = 0;
        }
        if (defaultZoomYPos < 0) {
            defaultZoomYPos = 0;
        }

        // update defautl position (without zoom)
        this.defaultZoomXPos = defaultZoomXPos;
        this.defaultZoomYPos = defaultZoomYPos;
    }

    @Override
    public boolean intersects(Point p) {
        if ((p.x >= getX() && p.x <= getX() + imageUnmarked.getWidth())
                && (p.y >= getY() && p.y <= getY() + imageUnmarked.getHeight())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean intersects(Rectangle r) {
        Rectangle rect = new Rectangle(getX(), getY(), imageUnmarked.getWidth(), imageUnmarked.getHeight());
        //Rectangle rect = new Rectangle(getX(), getY(), getWidth(), getHeight());
        return r.intersects(rect);
    }
    
    /**
     * Calculates intersecting point of this component and line made from inside and outside point.
     * Inside point is in this component. Outside point is out of the component
     * 
     * @param insidePoint Point in actual zoom
     * @param outsidePoint Point in actual zoom
     * @return Point in actual zoom
     */
    public Point getIntersectingPoint(Point insidePoint, Point outsidePoint) {
        Point intersection;
        
        Rectangle rectangle;
        Line2D line = new Line2D.Float();
        
        for(int i = textImages.size()-1;i>=0;i--){
            BufferedImage image = textImages.get(i);
            
            int x,y,w,h;
            
            x=(int) (zoomManager.doScaleToActual(defaultZoomXPos) - ((image.getWidth() - zoomManager.doScaleToActual(defaultZoomWidth))/2.0));
            y= zoomManager.doScaleToActual(defaultZoomYPos) + zoomManager.doScaleToActual(defaultZoomHeight) + i*image.getHeight();
            w = image.getWidth();
            h = image.getHeight();
            
            rectangle = new Rectangle(x,y,w,h);
            
            line.setLine(insidePoint, outsidePoint);
 
            if(line.intersects(rectangle)){
                intersection = GraphicUtils.getIntersectingPoint(rectangle, insidePoint, outsidePoint);
                return intersection;
            }
        }
        
        // no text rectangle intersects line
        
        rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
        intersection = GraphicUtils.getIntersectingPoint(rectangle, insidePoint, outsidePoint);
        
        return intersection;
    }

    //----------- GETTERS AND SETTERS
    /**
     * returns all EthInterfaces
     *
     * @return
     */
    public List<EthInterface> getInterfaces() {
        return interfaces;
    }

    public Object[] getInterfacesNames() {
        Object[] list = new Object[interfaces.size()];

        for (int i = 0; i < interfaces.size(); i++) {
            list[i] = interfaces.get(i).getName();
        }
        return list;
    }

    public EthInterface getEthInterface(Integer id) {
        for (EthInterface ei : interfaces) {
            if (ei.getId().intValue() == id.intValue()) {
                return ei;
            }
        }
        return null;
    }

    public EthInterface getEthInterface(int index) {
        return interfaces.get(index);
    }

    /**
     * gets first avaiable ethInterface, if no avaiable null is renturned
     *
     * @return
     */
    public EthInterface getFirstFreeInterface() {
        for (EthInterface ei : interfaces) {
            if (!ei.hasCable()) {
                return ei;
            }
        }
        return null;
    }

    /**
     * finds out whether component has any free EthInterface
     *
     * @return
     */
    public boolean hasFreeInterace() {
        for (EthInterface ei : interfaces) {
            if (!ei.hasCable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * gets all bundles of cables
     *
     * @return
     */
    public List<BundleOfCables> getBundleOfCableses() {
        return bundlesOfCables;
    }

    /**
     * adds bundle of cables to component
     *
     * @param boc
     */
    public void addBundleOfCables(BundleOfCables boc) {
        bundlesOfCables.add(boc);
    }

    /**
     * removes bundle of cables from this component
     *
     * @param boc
     */
    public void removeBundleOfCables(BundleOfCables boc) {
        bundlesOfCables.remove(boc);
    }

    /**
     * gets center of this component
     *
     * @return
     */
    public Point getCenterLocation() {
        return new Point(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }
    
    /**
     * gets center of this component
     *
     * @return
     */
    public Point getCenterLocationDefault() {
        return new Point(defaultZoomXPos + defaultZoomWidth / 2, defaultZoomYPos + defaultZoomTextHeight / 2);
    }

    /**
     * gets center of this component
     *
     * @return
     */
    public Point getCenterLocationDefaultZoom() {
        return new Point(defaultZoomXPos + defaultZoomWidth / 2,
                defaultZoomYPos + defaultZoomHeight / 2);
    }

    /**
     * Gets Point in actual scale of lower right corner of component
     *
     * @return Actual-scale ponint
     */
    public Point getLowerRightCornerLocation() {
        return new Point(getX() + getWidth(), getY() + getHeight());
    }

    @Override
    public int getWidth() {
        return zoomManager.doScaleToActual(defaultZoomWidth);
    }

    @Override
    public int getHeight() {
        return zoomManager.doScaleToActual(defaultZoomHeight);// + getTextHeight();
    }

    @Override
    public int getX() {
        return zoomManager.doScaleToActual(defaultZoomXPos);
    }

    @Override
    public int getY() {
        return zoomManager.doScaleToActual(defaultZoomYPos);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public HwTypeEnum getHwComponentType() {
        return hwComponentType;
    }

    public int getInterfaceCount() {
        return interfaces.size();
    }
}
