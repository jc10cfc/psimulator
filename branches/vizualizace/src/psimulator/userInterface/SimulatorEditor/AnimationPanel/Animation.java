package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Image;
import java.awt.Point;
import java.util.concurrent.TimeUnit;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTarget;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Singletons.ImageFactory.ImageFactorySingleton;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import shared.SimulatorEvents.SerializedComponents.EventType;
import shared.SimulatorEvents.SerializedComponents.PacketType;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class Animation implements TimingTarget {

    //
    private Animator animator;
    //
    private AnimationPanelInnerInterface animationPanelInnerInterface;
    private DataLayerFacade dataLayer;
    //
    private int defaultZoomStartX;
    private int defaultZoomStartY;
    private int defaultZoomEndX;
    private int defaultZoomEndY;
    //
    private Image image;
    private PacketType packetType;
    //
    private boolean visible;
    //
    private double defautlZoomWidthDifference =0.0;
    private double defautlZoomHeightDifference =0.0;
    //
    private  EventType eventType;
    //
    private double fraction;

    public Animation(final AnimationPanelInnerInterface animationPanelInnerInterface,
            DataLayerFacade dataLayer,
            PacketType packetType, Point defaultZoomSource, Point defaultZoomDest, 
            int durationInMilliseconds,
             EventType eventType) {
        
        this.dataLayer = dataLayer;
        this.animationPanelInnerInterface = animationPanelInnerInterface;
        //
        this.packetType = packetType;
        this.eventType = eventType;
        
        // get image
        image = ImageFactorySingleton.getInstance().getPacketImage(packetType, animationPanelInnerInterface.getPacketImageType(), 
                ZoomManagerSingleton.getInstance().getPackageIconWidth());

        // get start coordinates in default zoom
        defaultZoomStartX = defaultZoomSource.x;
        defaultZoomStartY = defaultZoomSource.y;

        // get end coordinates in default zoom
        defaultZoomEndX = defaultZoomDest.x;
        defaultZoomEndY = defaultZoomDest.y;

        // create animator      
        animator = new Animator.Builder().
                setDuration(durationInMilliseconds, TimeUnit.MILLISECONDS).
                setStartDirection(Animator.Direction.FORWARD).
                addTarget((TimingTarget)this).build();

        animator.start();
    }

    /**
     * Stops the animation
     */
    public void stopAnimator() {
        animator.stop();
    }

    /**
     * Returns image of animation in actual zoom sizes
     * @return 
     */
    public Image getImage() {
        image = ImageFactorySingleton.getInstance().getPacketImage(packetType, animationPanelInnerInterface.getPacketImageType(), 
                ZoomManagerSingleton.getInstance().getPackageIconWidth());
        return image;
    }

    /**
     * Gets X position of animated image in actual zoom.
     * @return 
     */
    public int getX() {
        return (int)ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomStartX + defautlZoomWidthDifference - (ZoomManagerSingleton.getInstance().getPackageIconWidthDefaultZoom()/2.0));
    }

    /**
     * Gets Y position of animated image in actual zoom.
     * @return 
     */
    public int getY() {
        return (int)ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomStartY + defautlZoomHeightDifference - (ZoomManagerSingleton.getInstance().getPackageIconWidthDefaultZoom()/2.0));
    }

    /**
     * Finds if visible
     * @return 
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Gets current fraction of animation <0,1>
     */
    public double getFraction() {
        return fraction;
    }

    

    /**
     * Gets if this animation should end normally, or has to end in the
     * middle because the pacekt was lost
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Moves image coordinates according to elapsed fraction of time.
     * @param fraction 
     */
    private void move(double fraction) {
        defautlZoomWidthDifference = (defaultZoomEndX - defaultZoomStartX) * fraction;
        defautlZoomHeightDifference = (defaultZoomEndY - defaultZoomStartY) * fraction;
        this.fraction = fraction;
        
        /*
        if(!successful && fraction > 0.5){
            defautlZoomWidthDifference = (defaultZoomEndX - defaultZoomStartX) * 0.5;
            defautlZoomHeightDifference = (defaultZoomEndY - defaultZoomStartY) * 0.5;
        }*/
    }

    // ---------- TimingTarget implementation ------------------- //
    @Override
    public void begin(Animator source) {
        // nothing to do
    }

    @Override
    public void end(Animator source) {
        // at the end of animation remove itself from animation pane
        animationPanelInnerInterface.removeAnimation(this);
    }

    @Override
    public void repeat(Animator source) {
        // nothing to do
    }

    @Override
    public void reverse(Animator source) {
        // nothing to do
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
        move(fraction);
    }
}
