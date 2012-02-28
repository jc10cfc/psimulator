package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.dataLayer.Network.NetworkFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class GraphBuilderFacade {
    
    public GraphBuilderFacade(){
    }
    
    public Graph buildGraph(NetworkFacade networkFacade){
        AbstractGraphBuilder graphBuilder = new GraphBuilder();
        
        GraphBuilderDirector graphBuilderDirector = new GraphBuilderDirector(graphBuilder, networkFacade);
        graphBuilderDirector.construct();
        
        return graphBuilder.getResult();
    }
}
