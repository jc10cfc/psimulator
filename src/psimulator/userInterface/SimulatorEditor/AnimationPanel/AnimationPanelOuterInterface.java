package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.util.Observer;
import javax.swing.JComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public abstract class AnimationPanelOuterInterface extends JComponent implements Observer{
    
    
    
    
    /**
     * removes graph from draw panel a resets state of draw panel
     * @return 
     */
    public abstract Graph removeGraph();
    /**
     * Sets graph 
     * @param graph 
     */
    public abstract void setGraph(Graph graph);
    
    //@Override
    //public abstract Dimension getPreferredSize();
}
