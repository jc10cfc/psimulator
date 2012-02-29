package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import psimulator.dataLayer.Network.Components.CableModel;
import psimulator.dataLayer.Network.Components.HwComponentModel;
import psimulator.dataLayer.Network.NetworkFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.HwComponentGraphic;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.CableGraphic;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class GraphBuilder extends AbstractGraphBuilder {
    //
    private Graph graph;

    public GraphBuilder() {
        
    }

    @Override
    public void buildGraph(){
        //
        this.graph = new Graph();
    }
    
    @Override
    public void buildHwComponent(HwComponentModel hwComponentModel) {
        
        // create new component
        HwComponentGraphic hwComponent = new HwComponentGraphic(hwComponentModel);

        // add component to graph
        graph.addHwComponentWithoutGraphSizeChange(hwComponent);
    }

    @Override
    public void buildCable(CableModel cableModel) {
        // get IDs
        int component1id = cableModel.getComponent1().getId();
        int component2id = cableModel.getComponent2().getId();
        
        // get components by ID from Graph
        HwComponentGraphic component1 = graph.getAbstractHwComponent(component1id);
        HwComponentGraphic component2 = graph.getAbstractHwComponent(component2id);
              
        // create new cable
        CableGraphic cable = new CableGraphic(cableModel, component1, component2);
        
        // add cable to graph
        graph.addCableOnGraphBuild(cable);
    }

    @Override
    public Graph getResult() {
        return graph;
    }

}
