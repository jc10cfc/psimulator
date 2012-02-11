package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder;

import java.util.Collection;
import java.util.Iterator;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class NetworkBuilderDirector {
    private AbstractNetworkBuilder abstractNetworkBuilder;
    private Graph graph;

    public NetworkBuilderDirector(AbstractNetworkBuilder abstractNetworkBuilder, Graph graph) {
        this.abstractNetworkBuilder = abstractNetworkBuilder;
        this.graph = graph;
    }

    public void construct() {
        // get all components from graph
        Collection<AbstractHwComponent> abstractHwComponents = graph.getHwComponents();
        
        // create interator
        Iterator<AbstractHwComponent> componentIt = abstractHwComponents.iterator();

        // iterate through all components
        while (componentIt.hasNext()) {
            AbstractHwComponent hwComponent = componentIt.next();
            // build network device
            abstractNetworkBuilder.buildNetworkDevice(hwComponent);
        }
  
        
        // get all cables from graph
        Collection<Cable> cables = graph.getCables();
        
        // create interator
        Iterator<Cable> cableIt = cables.iterator();
        
        // iterate through all components
        while (cableIt.hasNext()) {
            Cable cable = cableIt.next();
            // build network device
            abstractNetworkBuilder.buildNetworkCable(cable);
        }
    }
}
