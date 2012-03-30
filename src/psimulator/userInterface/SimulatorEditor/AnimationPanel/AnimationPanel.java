package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import shared.SimulatorEvents.SerializedComponents.PacketType;
import psimulator.dataLayer.Singletons.TimerKeeperSingleton;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.CableGraphic;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.PacketImageType;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelInnerInterface;
import shared.SimulatorEvents.SerializedComponents.EventType;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class AnimationPanel extends AnimationPanelOuterInterface implements AnimationPanelInnerInterface {
    //

    //private static final TimingSource f_repaintTimer = new SwingTimerTimingSource();
    private PostTickListener postTickListener;
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
        //Animator.setDefaultTimingSource(f_repaintTimer);

        this.dataLayer = dataLayer;

        // set opacity
        this.setOpaque(false);

        // CopyOnWrite is good for:
        //  - reads hugely outnumber writes (paint component every 15ms)
        //  - the array is small (or writes are very infrequent)
        animations = new CopyOnWriteArrayList<>();

        // create post tick listener
        postTickListener = new TimingSource.PostTickListener() {

            @Override
            public void timingSourcePostTick(TimingSource source, long nanoTime) {
                repaint();
            }
        };

        // add jPanelAnimation as observer to preferences manager
        dataLayer.addPreferencesObserver((Observer) this);

        // add jPanelAnimation as observer to simulator manager
        dataLayer.addSimulatorObserver((Observer) this);
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
            Composite tmpComposite = g2.getComposite();

            // if packet is lost and reached half-way
//            if (animation.getFraction() > 0.5 && !animation.isSuccessful()) {
//                int rule = AlphaComposite.SRC_OVER;
//                //float alpha = (float)animation.getFraction()*2;
//                float alpha = (float) (-2 * animation.getFraction() + 2);
//                if (alpha > 1f) {
//                    alpha = 1f;
//                }
//                if (alpha < 0f) {
//                    alpha = 0f;
//                }
//                
//                // set antialiasing
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//
//                int width = (int) (animation.getImage().getWidth(null) * 0.7);
//                int height = (int) (animation.getImage().getHeight(null) * 0.7);
//
//                int x = (int) (animation.getX() + animation.getImage().getWidth(null) * 0.15);
//                int y = (int) (animation.getY() + animation.getImage().getHeight(null) * 0.15);
//                
//                // create cross shape
//                GeneralPath shape = new GeneralPath();
//                shape.moveTo(x, y);
//                shape.lineTo(x + width, y + height);
//                shape.moveTo(x, y + height);
//                shape.lineTo(x + width, y);
//
//                // create stroke
//                float strokeWidth = animation.getImage().getWidth(null) / 5;
//                BasicStroke stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//
//                Composite comp = AlphaComposite.getInstance(rule, alpha);
//                // set transparency
//                g2.setComposite(comp);
//                // paint image
//                g2.drawImage(animation.getImage(), animation.getX(), animation.getY(), null);
//                // set original transparency
//                g2.setComposite(tmpComposite);
//
//                // save old stroke and color
//                Stroke tmpStroke = g2.getStroke();
//                Color tmpColor = g2.getColor();
//
//                // set stroke and color
//                g2.setStroke(stroke);
//                g2.setColor(Color.RED);
//
//                // paint red cross
//                g2.draw(shape);
//
//                // restore old stroke and color
//                g2.setColor(tmpColor);
//                g2.setStroke(tmpStroke);
//            } else {
                g2.drawImage(animation.getImage(), animation.getX(), animation.getY(), null);
//            }



        }
        /*
         * g2.setColor(Color.BLACK); g2.drawRect(1, 1, getWidth() - 3,
         * getHeight() - 3);
         */
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
            case SIMULATOR_PLAYER_PLAY:
            case SIMULATOR_REALTIME_ON:
                connectToTimer();
                break;
            case SIMULATOR_PLAYER_STOP:
            case SIMULATOR_REALTIME_OFF:
                removeAllAnimations();
                disconnectFromTimer();
                break;
        }
    }

    private void connectToTimer() {
        TimerKeeperSingleton.getInstance().getTimingSource().addPostTickListener(postTickListener);
        //System.out.println("Connected to timer");
    }

    private void disconnectFromTimer() {
        TimerKeeperSingleton.getInstance().getTimingSource().removePostTickListener(postTickListener);
        //System.out.println("Disconnected from timer");
        this.repaint();
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
     *
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
     *
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
     *
     * @param graph
     */
    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Graph getGraph() {
        return graph;
    }

    /**
     * Creates animation of given timeInMiliseconds from AbstractHwComponent
     * idSource to AbstractHwComponent idDestination.
     *
     * @param timeInMiliseconds
     * @param idSource
     * @param idDestination
     */
    @Override
    public void createAnimation(PacketType packetType, int timeInMiliseconds, int idSource, int idDestination, EventType eventType) {
        // points in Default zoom
        Point src = graph.getAbstractHwComponent(idSource).getCenterLocationDefaultZoom();
        Point dest = graph.getAbstractHwComponent(idDestination).getCenterLocationDefaultZoom();

        //System.out.println("Src= "+src+", dest= "+dest);

        // create new animation
        Animation anim = new Animation(this, dataLayer,
                packetType, src, dest, timeInMiliseconds, eventType);

        // add animation to animations list
        animations.add(anim);
    }

    /**
     * Counts the duration of animation im milliseconds
     *
     * @param cableId
     * @param speedCoeficient
     * @return
     */
    @Override
    public int getAnimationDuration(int cableId, int speedCoeficient) {
        CableGraphic cable = graph.getCable(cableId);

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
