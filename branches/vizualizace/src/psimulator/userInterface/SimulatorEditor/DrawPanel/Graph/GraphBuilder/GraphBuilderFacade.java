package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.AbstractNetwork.Network;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class GraphBuilderFacade {
    
    public GraphBuilderFacade(){
    }
    
    public Graph buildGraph(Network network){
        AbstractGraphBuilder graphBuilder = new GraphBuilder();
        
        GraphBuilderDirector graphBuilderDirector = new GraphBuilderDirector(graphBuilder, network);
        graphBuilderDirector.construct();
        
        return graphBuilder.getResult();
    }
}
