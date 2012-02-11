package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.AbstractNetwork.NetworkCable;
import psimulator.AbstractNetwork.NetworkDevice;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public abstract class AbstractGraphBuilder {
    
    public abstract Graph getResult();
    
    public abstract void buildDevice(NetworkDevice device);
    
    public abstract void buildCable(NetworkCable cable);

}