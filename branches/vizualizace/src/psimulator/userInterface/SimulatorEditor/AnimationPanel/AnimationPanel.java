package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelInnerInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class AnimationPanel extends AnimationPanelOuterInterface implements AnimationPanelInnerInterface {
    //

    private static final TimingSource f_repaintTimer = new SwingTimerTimingSource();
    //
    private AbstractImageFactory imageFactory;
    private ZoomManager zoomManager;
    private Graph graph;
    //
    private List<Animation> animations;
    //

    public AnimationPanel(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface editorPanel,
            AbstractImageFactory imageFactory, DataLayerFacade dataLayer, ZoomManager zoomManager,
            DrawPanelOuterInterface drawPanel) {

        super();
        // set timing sourcce to Animator
        Animator.setDefaultTimingSource(f_repaintTimer);

        this.imageFactory = imageFactory;
        this.zoomManager = zoomManager;

        // set opacity
        this.setOpaque(false);

        // CopyOnWrite is good for:
        //  - reads hugely outnumber writes (paint component every 15ms)
        //  - the array is small (or writes are very infrequent)
        animations = new CopyOnWriteArrayList<Animation>();

        // init timer
        f_repaintTimer.init();
        
        // add listener for tick
        f_repaintTimer.addPostTickListener(new TimingSource.PostTickListener() {

            @Override
            public void timingSourcePostTick(TimingSource source, long nanoTime) {
                repaint();
            }
        });
    }

    /**
     * Paints animations on this panel.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation animation = it.next(); // convert X and Yto actual using zoom manager 
            g2.drawImage(animation.getImage(), animation.getX(), animation.getY(), null);
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case VIEW_DETAILS:
                break;
            case ZOOM_CHANGE:
                //System.out.println("Aniamtion panel zoom changed");
                break;
        }
    }

    /**
     * Removes all animations from list
     */
    private void removeAllAnimations() {
        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation animation = it.next(); // convert X and Yto actual using zoom manager 
            animation.stopAnimator();
            it.remove();
        }

        animations.clear();
    }

    /**
     * Removes concrete animation from animations list
     * @param animation 
     */
    @Override
    public void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    /**
     * Removes all animations and removes Graph.
     * @return 
     */
    @Override
    public Graph removeGraph() {
        removeAllAnimations();

        Graph tmp = graph;
        graph = null;
        return tmp;
    }

    /**
     * Sets graph and chnage bounds of this panel
     * @param graph 
     */
    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        this.setBounds(0, 0, graph.getPreferredSize().width - 1, graph.getPreferredSize().height - 1);
    }
    
    @Override
    public Graph getGraph(){
        return graph;
    }

    /**
     * Creates animation of given timeInMiliseconds from AbstractHwComponent idSource to
     * AbstractHwComponent idDestination.
     * @param timeInMiliseconds
     * @param idSource
     * @param idDestination 
     */
    @Override
    public void createAnimation(int timeInMiliseconds, int idSource, int idDestination) {
        // points in Default zoom
        Point src = graph.getAbstractHwComponent(idSource).getCenterLocationDefaultZoom();
        Point dest = graph.getAbstractHwComponent(idDestination).getCenterLocationDefaultZoom();

        // create new animation
        Animation animation = new Animation(this, imageFactory, zoomManager, src, dest, timeInMiliseconds);
        
        // add animation to animations list
        animations.add(animation);
    }
}
