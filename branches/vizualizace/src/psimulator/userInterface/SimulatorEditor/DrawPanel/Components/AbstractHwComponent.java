package psimulator.userInterface.SimulatorEditor.DrawPanel.Components;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.GraphicUtils;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractHwComponent extends AbstractComponent {

    // position in 1:1 zoom
    protected int defaultZoomXPos;
    protected int defaultZoomYPos;
    protected int defaultZoomWidth;
    protected int defaultZoomHeight;
    // position of textRectangle in 1:1 zoom
    protected int defaultZoomTextWidth;
    protected int defaultZoomTextHeight;
    //
    private List<BundleOfCables> bundlesOfCables = new ArrayList<BundleOfCables>();
    
    protected List<EthInterface> interfaces = new ArrayList<EthInterface>();
    protected BufferedImage imageUnmarked;
    protected BufferedImage imageMarked;
    protected List<BufferedImage> textImages;
    protected String deviceName;
    //
    
    /**
     * Use when creating graph by user actions.
     * @param imageFactory
     * @param dataLayer
     * @param hwType 
     */
    public AbstractHwComponent(DataLayerFacade dataLayer, HwTypeEnum hwType){//, int interfacesCount) {
        super(dataLayer, hwType);
    }
    
    /**
     * Use when building graph from Network.
     * @param id
     * @param hwType 
     */
    public AbstractHwComponent(int id, HwTypeEnum hwType){
        super(id, hwType);
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
        setLocation(ZoomManagerSingleton.getInstance().doScaleToDefault(middlePoint.x) - (defaultZoomWidth / 2),
                ZoomManagerSingleton.getInstance().doScaleToDefault(middlePoint.y) - (defaultZoomHeight / 2));
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
            
            x=(int) (ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomXPos) - ((image.getWidth() - ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomWidth))/2.0));
            y= ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomYPos) + ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomHeight) + i*image.getHeight();
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

    public EthInterface getEthInterfaceAtIndex(int index) {
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
     
    public Point getLowerRightCornerLocation1() {
        return new Point(getX() + getWidth(), getY() + getHeight());
    }*/
    
    /**
     * Gets Point in actual scale of lower right corner of component including text labels
     *
     * @return Actual-scale ponint
     */
    public Point getLowerRightCornerLocation() {
        int x = getDefaultZoomXPos() + getDefaultZoomWidth();
        if(getDefaultZoomTextWidth()> getDefaultZoomWidth()){
            x += ((getDefaultZoomTextWidth() - getDefaultZoomWidth())/2.0);
        }
        
        int y = getDefaultZoomYPos() + getDefaultZoomHeight() + getDefaultZoomTextHeight();
        
        return new Point(ZoomManagerSingleton.getInstance().doScaleToActual(x), ZoomManagerSingleton.getInstance().doScaleToActual(y));
    }

    @Override
    public int getWidth() {
        return ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomWidth);
    }

    @Override
    public int getHeight() {
        return ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomHeight);// + getTextHeight();
    }

    @Override
    public int getX() {
        return ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomXPos);
    }

    @Override
    public int getY() {
        return ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomYPos);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getInterfaceCount() {
        return interfaces.size();
    }

    public void setDefaultZoomXPos(int defaultZoomXPos) {
        this.defaultZoomXPos = defaultZoomXPos;
    }

    public void setDefaultZoomYPos(int defaultZoomYPos) {
        this.defaultZoomYPos = defaultZoomYPos;
    }

    public int getDefaultZoomXPos() {
        return defaultZoomXPos;
    }

    public int getDefaultZoomYPos() {
        return defaultZoomYPos;
    }

    public int getDefaultZoomHeight() {
        return defaultZoomHeight;
    }

    public int getDefaultZoomWidth() {
        return defaultZoomWidth;
    }
    
    public int getTextsWidth(){
        return ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomTextWidth);
    }
    
    public int getTextsHeight(){
        return ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomTextHeight);
    }

    public int getDefaultZoomTextHeight() {
        return defaultZoomTextHeight;
    }

    public int getDefaultZoomTextWidth() {
        return defaultZoomTextWidth;
    }
    
    
}
