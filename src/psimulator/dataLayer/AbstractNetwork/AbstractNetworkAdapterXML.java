package psimulator.dataLayer.AbstractNetwork;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import psimulator.AbstractNetwork.Network;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder.GraphBuilderFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder.NetworkBuilderFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz> Lukáš <lukasma1@fit.cvut.cz>
 */
public class AbstractNetworkAdapterXML {

    public void saveGraphToFile(Graph graph, File file) {
        // create Network builder
        NetworkBuilderFacade networkBuilderFacade = new NetworkBuilderFacade();
        // build Network
        Network network = networkBuilderFacade.buildNetwork(graph);
        // save network to file
        String fileName = file.getPath();
        try {
            network.save(fileName);
        } catch (JAXBException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Graph loadGraphFromFile(File file) {


        String fileName = file.getPath();
        Network network = null;

        try {
            network = Network.load(fileName);
        } catch (JAXBException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }


        GraphBuilderFacade graphBuilderFacade = new GraphBuilderFacade();
        Graph graph = graphBuilderFacade.buildGraph(network);

        return graph;


    }

}
