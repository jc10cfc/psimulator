package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import java.util.Map;
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

        Map<Integer, NetworkDevice> devices = network.getDevices();

        // build all devices
        for (Map.Entry<Integer, NetworkDevice> entry : devices.entrySet()) {
            NetworkDevice networkDevice = entry.getValue();
            abstractGraphBuilder.buildDevice(networkDevice);
        }

        Map<Integer, NetworkCable> cables = network.getCables();

        // build all cables
        for (Map.Entry<Integer, NetworkCable> entry : cables.entrySet()) {
            NetworkCable networkCable = entry.getValue();
            abstractGraphBuilder.buildCable(networkCable);

        }
        // build counter
        abstractGraphBuilder.buildCounter(network.getCounter());
    }
}
