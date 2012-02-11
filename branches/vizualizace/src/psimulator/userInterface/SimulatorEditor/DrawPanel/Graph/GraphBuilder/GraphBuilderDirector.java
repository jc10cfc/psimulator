package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import java.util.List;
import psimulator.AbstractNetwork.Network;
import psimulator.AbstractNetwork.NetworkCable;
import psimulator.AbstractNetwork.NetworkDevice;

/**
 *
 * @author Martin
 */
public class GraphBuilderDirector {

    private AbstractGraphBuilder abstractGraphBuilder;
    private Network network;

    public GraphBuilderDirector(AbstractGraphBuilder abstractGraphBuilder, Network network) {
        this.abstractGraphBuilder = abstractGraphBuilder;
        this.network = network;
    }

    public void construct() {
        List<NetworkDevice> devices = network.getDevices();

        // build all devices
        for (NetworkDevice device : devices) {
            abstractGraphBuilder.buildDevice(device);
        }

        List<NetworkCable> cables = network.getCables();

        // build all cables
        for(NetworkCable cable : cables){
            abstractGraphBuilder.buildCable(cable);
        }
        
        // build counter
        abstractGraphBuilder.buildCounter(network.getCounter());
    }
}

