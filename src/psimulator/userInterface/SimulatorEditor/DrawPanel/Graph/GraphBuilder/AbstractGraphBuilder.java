package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.dataLayer.Network.CableModel;
import psimulator.dataLayer.Network.HwComponentModel;
import psimulator.dataLayer.Network.NetworkFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractGraphBuilder {
    
    public abstract Graph getResult();
    
    public abstract void buildGraph(NetworkFacade networkFacade);
    
    public abstract void buildHwComponent(HwComponentModel hwComponentModel);
    
    public abstract void buildCable(CableModel cable);

}