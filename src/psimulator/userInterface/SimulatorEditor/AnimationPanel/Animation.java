package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Image;
import java.awt.Point;
import java.util.concurrent.TimeUnit;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTarget;
import psimulator.AbstractNetwork.HwTypeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class Animation implements TimingTarget {

    //
    private Animator animator;
    //
    private AnimationPanelInnerInterface animationPanelInnerInterface;
    private ZoomManager zoomManager;
    private AbstractImageFactory imageFactory;
    //
    private int defaultZoomStartX;
    private int defaultZoomStartY;
    private int defaultZoomEndX;
    private int defaultZoomEndY;
    //
    private Image image;
    //
    private boolean visible;
    //
    private double defautlZoomWidthDifference =0.0;
    private double defautlZoomHeightDifference =0.0;

    public Animation(final AnimationPanelInnerInterface animationPanelInnerInterface,
            AbstractImageFactory imageFactory, ZoomManager zoomManager,
            Point defaultZoomSource, Point defaultZoomDest, int durationInMilliseconds) {

        this.animationPanelInnerInterface = animationPanelInnerInterface;
        this.zoomManager = zoomManager;
        this.imageFactory = imageFactory;

        // get image
        image = imageFactory.getImage(HwTypeEnum.END_DEVICE_PC, zoomManager.getIconWidth(), false);

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
        
        // loop
//        animator = new Animator.Builder().
//                setDuration(durationInMilliseconds, TimeUnit.MILLISECONDS).
//                setRepeatCount(Animator.INFINITE).
//                setStartDirection(Animator.Direction.FORWARD).
//                addTarget((TimingTarget)this).build();
        
        // start animation
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
        image = imageFactory.getImage(HwTypeEnum.END_DEVICE_PC, zoomManager.getIconWidth(), false);
        return image;
    }

    /**
     * Gets X position of animated image in actual zoom.
     * @return 
     */
    public int getX() {
        return (int)zoomManager.doScaleToActual(defaultZoomStartX + defautlZoomWidthDifference - (zoomManager.getIconWidthDefaultZoom()/2.0));
    }

    /**
     * Gets Y position of animated image in actual zoom.
     * @return 
     */
    public int getY() {
        return (int)zoomManager.doScaleToActual(defaultZoomStartY + defautlZoomHeightDifference - (zoomManager.getIconWidthDefaultZoom()/2.0));
    }

    /**
     * Finds if visible
     * @return 
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Moves image coordinates according to elapsed fraction of time.
     * @param fraction 
     */
    private void move(double fraction) {
        defautlZoomWidthDifference = (defaultZoomEndX - defaultZoomStartX) * fraction;
        defautlZoomHeightDifference = (defaultZoomEndY - defaultZoomStartY) * fraction;
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
