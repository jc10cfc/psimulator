package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;
import psimulator.dataLayer.DataLayerFacade;
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
public class AnimationPanel extends AnimationPanelOuterInterface implements ActionListener {

    private Graph graph;
    //
    //private Thread animator;
    private Timer timer;
    private final int DELAY = 25;
    //
    private List<Animation> animations;

    public AnimationPanel(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface editorPanel,
            AbstractImageFactory imageFactory, DataLayerFacade dataLayer, ZoomManager zoomManager,
            DrawPanelOuterInterface drawPanel) {

        this.setOpaque(false);
        
        animations = Collections.synchronizedList(new ArrayList<Animation>());

        for (int i = 0; i < 100; i++) {
            animations.add(new Animation());          
        }

        timer = new Timer(DELAY, this);
        
        setDoubleBuffered(true);
        /*
         * Dimension dimension = new Dimension(200,200);
         * this.setPreferredSize(dimension); this.setMinimumSize(dimension);
         * this.setMaximumSize(dimension);
         */


        //this.setBounds(0, 0, 200,200);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //System.out.println("KreslimAnimPanel");

        Graphics2D g2d = (Graphics2D) g;
        //g2d.drawImage(star, x, y, this);
        
        Iterator<Animation> it= animations.iterator();
        
        while(it.hasNext()){
            Animation animation = it.next();
            // convert X and Y to actual using zoom manager
            g2d.drawImage(animation.getImage(), animation.getX(), animation.getY(), this);
        }
 
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
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
        this.setBounds(0, 0, graph.getPreferredSize().width, graph.getPreferredSize().height);

        timer.start();

        /*
         * this.setPreferredSize(graph.getPreferredSize());
         * this.setMinimumSize(graph.getPreferredSize());
         * this.setMaximumSize(graph.getPreferredSize());
         */
    }

    /*
    @Override
    public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }*/

    public void cycle() {
        Iterator<Animation> it= animations.iterator();
        
        while(it.hasNext()){
            it.next().move();
        }
    }

    /*
     * @Override public Dimension getPreferredSize() {
     * System.out.println("Zde1"); return graph.getPreferredSize(); }
     */
    /*
    @Override
    public void run() {
        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true) {

            cycle();
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }

            beforeTime = System.currentTimeMillis();
        }
    }*/

    @Override
    public void actionPerformed(ActionEvent ae) {
        cycle();
        repaint();  
    }
}
