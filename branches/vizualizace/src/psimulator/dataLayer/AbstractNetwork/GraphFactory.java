package psimulator.dataLayer.AbstractNetwork;

import psimulator.AbstractNetwork.Device;
import psimulator.AbstractNetwork.Network;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.HwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class GraphFactory {
    
    public Graph createGraphFromNetwork(Network network){
        Graph graph = new Graph();
        
        // create and add all components to graph
        for(Device device : network.getDevices()){
            //AbstracHwComponent component = new HwComponent(null, null, device.getHwType(), device.getInterfaceCount(), component, component)
            
        }
        
        
        // create and add all connections to graph
        
        
        
        return null;
    }
    
}
