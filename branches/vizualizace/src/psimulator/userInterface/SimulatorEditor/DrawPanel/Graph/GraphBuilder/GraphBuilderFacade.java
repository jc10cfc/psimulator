package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.dataLayer.Network.Components.NetworkModel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class GraphBuilderFacade {
    
    public GraphBuilderFacade(){
    }
    
    public Graph buildGraph(NetworkModel networkModel){
        AbstractGraphBuilder graphBuilder = new GraphBuilder();
        
        GraphBuilderDirector graphBuilderDirector = new GraphBuilderDirector(graphBuilder, networkModel);
        graphBuilderDirector.construct();
        
        return graphBuilder.getResult();
    }
}