package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder;

import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.Network;
import psimulator.AbstractNetwork.NetworkCable;
import psimulator.AbstractNetwork.NetworkDevice;
import psimulator.AbstractNetwork.NetworkInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.EthInterface;

/**
 *
 * @author Martin
 */
public class NetworkBuilder extends AbstractNetworkBuilder {

    //
    private Network network;

    public NetworkBuilder() {
        //
        this.network = new Network();
    }

    @Override
    public void buildNetworkDevice(AbstractHwComponent hwComponent) {
        // crete network device
        NetworkDevice networkDevice = new NetworkDevice(hwComponent.getId().intValue(), hwComponent.getHwType(), hwComponent.getDeviceName(),
                hwComponent.getDefaultZoomXPos(), hwComponent.getDefaultZoomYPos());

        // create interfaces
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();

        for (EthInterface ethInteface : hwComponent.getInterfaces()) {
            // create interface
            NetworkInterface networkInterface = new NetworkInterface(ethInteface.getId().intValue(), networkDevice, ethInteface.getName(),
                    ethInteface.getIpAddress(), ethInteface.getMacAddress());
            
            // add interface to list
            networkInterfaces.add(networkInterface);
            
            // add interface to hashMap in network for search
            network.addNetworkInterface(networkInterface);
        }

        // add interfaces to device
        networkDevice.setInterfaces(networkInterfaces);
        
        // add network device to network
        network.addDevice(networkDevice);
    }

    @Override
    public void buildNetworkCable(Cable cable) {
        // get both interfaces
        NetworkInterface networkInterface1 = network.getNetworkInterface(cable.getEth1().getId());
        NetworkInterface networkInterface2 = network.getNetworkInterface(cable.getEth2().getId());
        
        // create network cable
        NetworkCable networkCable = new NetworkCable(cable.getId(), networkInterface1, networkInterface2, 
                cable.getHwType(),cable.getDelay());
        
        // add cable to network
        network.addCable(networkCable);
    }

    @Override
    public Network getResult() {
        return network;
    }
}
