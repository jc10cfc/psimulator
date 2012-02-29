package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.dataLayer.Network.Components.CableModel;
import psimulator.dataLayer.Network.Components.HwComponentModel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractGraphBuilder {
    
    public abstract Graph getResult();
    
    public abstract void buildGraph();
    
    public abstract void buildHwComponent(HwComponentModel hwComponentModel);
    
    public abstract void buildCable(CableModel cable);

}