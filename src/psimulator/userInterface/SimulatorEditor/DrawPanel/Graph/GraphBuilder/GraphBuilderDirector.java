package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import java.util.List;
import psimulator.AbstractNetwork.NetworkCable;
import psimulator.AbstractNetwork.NetworkDevice;
import psimulator.AbstractNetwork.Network;

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

        for (NetworkDevice device : devices) {
            abstractGraphBuilder.buildDevice(device);
        }

        List<NetworkCable> cables = network.getCables();

        for(NetworkCable cable : cables){
            abstractGraphBuilder.buildCable(cable);
        }
    }
}

