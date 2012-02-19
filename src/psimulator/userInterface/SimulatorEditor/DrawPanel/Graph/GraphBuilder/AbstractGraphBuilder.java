package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.AbstractNetwork.NetworkCable;
import psimulator.AbstractNetwork.NetworkCounter;
import psimulator.AbstractNetwork.NetworkDevice;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractGraphBuilder {
    
    public abstract Graph getResult();
    
    public abstract void buildDevice(NetworkDevice device);
    
    public abstract void buildCable(NetworkCable cable);
    
    public abstract void buildCounter(NetworkCounter counter);

}