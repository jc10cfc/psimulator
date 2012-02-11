package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder;

import psimulator.AbstractNetwork.Network;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class NetworkBuilderFacade {

    public NetworkBuilderFacade() {
    }
    
    public Network buildNetwork(Graph graph){
        AbstractNetworkBuilder graphBuilder = new NetworkBuilder();
        
        NetworkBuilderDirector graphBuilderDirector = new NetworkBuilderDirector(graphBuilder, graph);
        graphBuilderDirector.construct();
        
        return graphBuilder.getResult();
    }
    
}
