package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Image;
import java.awt.Point;
import java.util.Random;
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
    private Image image;
    private int defaultZoomX;
    private int defaultZoomY;
    private int defaultZoomMaxX;
    private int defaultZoomMaxY;
    private boolean visible;
    private final int ANIMATION_SPEED = 2;
    
    private double defautlZoomWidthDifference =0.0;
    private double defautlZoomHeightDifference =0.0;

    public Animation(final AnimationPanelInnerInterface animationPanelInnerInterface,
            AbstractImageFactory imageFactory, ZoomManager zoomManager,
            Point defaultZoomSource, Point defaultZoomDest) {

        this.animationPanelInnerInterface = animationPanelInnerInterface;
        this.zoomManager = zoomManager;
        this.imageFactory = imageFactory;

        //ImageIcon ii = new ImageIcon(this.getClass().getResource("/resources/toolbarIcons/editor_toolbar/cursor_hand_mod_2.png"));
        //image = ii.getImage();
        image = imageFactory.getImage(HwTypeEnum.END_DEVICE_PC, zoomManager.getIconWidth(), false);

        Random r = new Random();

        defaultZoomStartX = defaultZoomSource.x;
        defaultZoomStartY = defaultZoomSource.y;

        defaultZoomEndX = defaultZoomDest.x;
        defaultZoomEndY = defaultZoomDest.y;


        defaultZoomX = r.nextInt(300);
        defaultZoomY = defaultZoomX;

        defaultZoomMaxX = r.nextInt(500) + 300;
        defaultZoomMaxY = defaultZoomMaxX;

        // single
        animator = new Animator.Builder().setDuration(2000, TimeUnit.MILLISECONDS).setStartDirection(Animator.Direction.FORWARD).addTarget((TimingTarget)this).build();
        // loop
        //animator = new Animator.Builder().setDuration(2000, TimeUnit.MILLISECONDS).setRepeatCount(Animator.INFINITE).setStartDirection(Animator.Direction.FORWARD).addTarget((TimingTarget)this).build();
        animator.start();
    }

    public void stopAnimator() {
        animator.stop();

    }

    public Image getImage() {
        image = imageFactory.getImage(HwTypeEnum.END_DEVICE_PC, zoomManager.getIconWidth(), false);
        return image;
    }

    public int getX() {
        return (int)zoomManager.doScaleToActual(defaultZoomStartX + defautlZoomWidthDifference);
    }

    public int getY() {
        return (int)zoomManager.doScaleToActual(defaultZoomStartY + defautlZoomHeightDifference);
    }

    public boolean isVisible() {
        return visible;
    }

    private void move(double fraction) {
        defautlZoomWidthDifference = (defaultZoomEndX - defaultZoomStartX) * fraction;
        defautlZoomHeightDifference = (defaultZoomEndY - defaultZoomStartY) * fraction;
    }

    @Override
    public void begin(Animator source) {
        //
    }

    @Override
    public void end(Animator source) {
        animationPanelInnerInterface.removeAnimation(this);
    }

    @Override
    public void repeat(Animator source) {
        //
    }

    @Override
    public void reverse(Animator source) {
        //
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
        move(fraction);
        //System.out.println("Fraction" + fraction);
    }
}
