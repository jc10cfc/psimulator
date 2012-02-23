package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Simulator.PacketType;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.PacketImageType;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelInnerInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class AnimationPanel extends AnimationPanelOuterInterface implements AnimationPanelInnerInterface {
    //

    private static final TimingSource f_repaintTimer = new SwingTimerTimingSource();
    //
    private DataLayerFacade dataLayer;
    private Graph graph;
    //
    private List<Animation> animations;
    //

    public AnimationPanel(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface editorPanel,
            DataLayerFacade dataLayer, DrawPanelOuterInterface drawPanel) {

        super();
        // set timing sourcce to Animator
        Animator.setDefaultTimingSource(f_repaintTimer);

        this.dataLayer = dataLayer;
        
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
        
        // add jPanelAnimation as observer to preferences manager
        dataLayer.addPreferencesObserver((Observer)this);
        
        // add jPanelAnimation as observer to simulator manager
        dataLayer.addSimulatorObserver((Observer)this);
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
        
        g2.setColor(Color.BLACK);
        g2.drawRect(1, 1, getWidth()-3, getHeight()-3);
        
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case VIEW_DETAILS:
                // no need to react, 
                break;
            case ZOOM_CHANGE:
                // no need to react, will change size from UserInterfaceLayeredPane
                break;
            case PACKET_IMAGE_TYPE_CHANGE:
                // no need to react
                break;
            case SIMULATOR_PLAYER_STOP:
                removeAllAnimations();
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
    
    @Override
    public PacketImageType getPacketImageType() {
        return dataLayer.getPackageImageType();
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
    public void createAnimation(PacketType packetType, int timeInMiliseconds, int idSource, int idDestination){
        // points in Default zoom
        Point src = graph.getAbstractHwComponent(idSource).getCenterLocationDefaultZoom();
        Point dest = graph.getAbstractHwComponent(idDestination).getCenterLocationDefaultZoom();

        //System.out.println("Src= "+src+", dest= "+dest);
        
        // create new animation
        Animation anim = new Animation(this, dataLayer, 
                packetType, src, dest, timeInMiliseconds);
        
        // add animation to animations list
        animations.add(anim);
    }
    
    /**
     * Counts the duration of animation im milliseconds
     * @param cableId
     * @param speedCoeficient
     * @return 
     */
    @Override
    public int getAnimationDuration(int cableId, int speedCoeficient){
        Cable cable = graph.getCable(cableId);
        
        int delay = cable.getDelay();
        
        speedCoeficient = speedCoeficient * 50;
        
        int speed = delay * speedCoeficient;
        
        return speed;
    }

    // do not take cursor
    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}
