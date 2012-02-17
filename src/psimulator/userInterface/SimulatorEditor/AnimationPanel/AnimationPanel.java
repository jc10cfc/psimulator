package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.JComponent;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.rendering.JRendererFactory;
import org.jdesktop.swing.animation.rendering.JRendererPanel;
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
public class AnimationPanel extends AnimationPanelOuterInterface {
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

    private void removeAllAnimations(){
        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation animation = it.next(); // convert X and Yto actual using zoom manager 
            animation.stopAnimator();
            it.remove();
        }
        
        animations.clear();
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

        List<AbstractHwComponent> list = new ArrayList<AbstractHwComponent>(graph.getHwComponents());

        for (int i = 0; i < 1000; i++) {

            int componentCount = graph.getAbstractHwComponentsCount();
            int i1 = random.nextInt(componentCount);
            int i2 = random.nextInt(componentCount);

            Point p1 = list.get(i1).getCenterLocationDefaultZoom();
            Point p2 = list.get(i2).getCenterLocationDefaultZoom();

            animations.add(new Animation(imageFactory, zoomManager, p1, p2));
        }
    }
}
