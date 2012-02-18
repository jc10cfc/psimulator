package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.*;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
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
    //
    private List<Animation> animations;
    //
    private Random random = new Random();

    public AnimationPanel(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface editorPanel,
            AbstractImageFactory imageFactory, DataLayerFacade dataLayer, ZoomManager zoomManager,
            DrawPanelOuterInterface drawPanel) {

        super();
        Animator.setDefaultTimingSource(f_repaintTimer);

        this.imageFactory = imageFactory;
        this.zoomManager = zoomManager;

        this.setOpaque(false);

        animations = Collections.synchronizedList(new ArrayList<Animation>());


        f_repaintTimer.init();
        f_repaintTimer.addPostTickListener(new TimingSource.PostTickListener() {

            @Override
            public void timingSourcePostTick(TimingSource source, long nanoTime) {
                repaint();
            }
        });
    }

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

    private void removeAllAnimations() {
        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation animation = it.next(); // convert X and Yto actual using zoom manager 
            animation.stopAnimator();
            it.remove();
        }

        animations.clear();
    }

    @Override
    public void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    @Override
    public Graph removeGraph() {
        removeAllAnimations();

        Graph tmp = graph;
        graph = null;
        return tmp;
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        this.setBounds(0, 0, graph.getPreferredSize().width - 1, graph.getPreferredSize().height - 1);
    }

    @Override
    public void createAnimation(int timeInMiliseconds, int idSource, int idDestination) {
        // for now it is Random, the ids in parameter not valid
        List<AbstractHwComponent> list = new ArrayList<AbstractHwComponent>(graph.getHwComponents());
        int componentCount = graph.getAbstractHwComponentsCount();
        int i1 = random.nextInt(componentCount);
        int i2 = random.nextInt(componentCount);

        Point src = list.get(i1).getCenterLocationDefaultZoom();
        Point dest = list.get(i2).getCenterLocationDefaultZoom();


        // points in Default zoom
        //Point src = graph.getAbstractHwComponent(idSource).getCenterLocationDefaultZoom();
        //Point dest = graph.getAbstractHwComponent(idDestination).getCenterLocationDefaultZoom();

        Animation animation = new Animation(this, imageFactory, zoomManager, src, dest, timeInMiliseconds);
        animations.add(animation);
    }
}
