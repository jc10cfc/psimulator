package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;
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
public class AnimationPanel extends AnimationPanelOuterInterface implements ActionListener{

    private AbstractImageFactory imageFactory;
    private ZoomManager zoomManager;
    
    private Graph graph;
    //
    //private Thread animator;
    private Timer timer;
    private final int DELAY = 25;
    //
    private List<Animation> animations;
    //
    private Random random = new Random();

    public AnimationPanel(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface editorPanel,
            AbstractImageFactory imageFactory, DataLayerFacade dataLayer, ZoomManager zoomManager,
            DrawPanelOuterInterface drawPanel) {

        this.imageFactory = imageFactory;
        this.zoomManager = zoomManager;
        
        this.setOpaque(false);
        
        animations = Collections.synchronizedList(new ArrayList<Animation>());

        timer = new Timer(DELAY, this);
        
        setDoubleBuffered(true);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        
        Iterator<Animation> it= animations.iterator();
        
        while(it.hasNext()){
            Animation animation = it.next();
            // convert X and Y to actual using zoom manager
            g2d.drawImage(animation.getImage(), animation.getX(), animation.getY(), this);
        }
 
        g2d.drawRect(0, 0, getWidth(), getHeight());
        
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
    

    @Override
    public Graph removeGraph() {
        timer.stop();
        
        Graph tmp = graph;
        graph = null;
        return tmp;
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        this.setBounds(0, 0, graph.getPreferredSize().width-1, graph.getPreferredSize().height-1);

        List<AbstractHwComponent> list = new ArrayList<AbstractHwComponent>(graph.getHwComponents());
        
        for (int i = 0; i < 10; i++) {
            
            int componentCount = graph.getAbstractHwComponentsCount();
            int i1 = random.nextInt(componentCount);
            int i2 = random.nextInt(componentCount);
            
            Point p1 = list.get(i1).getCenterLocationDefaultZoom();
            Point p2 = list.get(i2).getCenterLocationDefaultZoom();
            
            animations.add(new Animation(imageFactory, zoomManager, p1, p2));          
        }
        
        timer.start();
    }

    public void cycle() {
        Iterator<Animation> it= animations.iterator();
        
        while(it.hasNext()){
            it.next().move();
        }
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        cycle();
        repaint();  
    }
}
