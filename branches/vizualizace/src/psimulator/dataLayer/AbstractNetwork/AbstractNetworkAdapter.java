package psimulator.dataLayer.AbstractNetwork;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import psimulator.AbstractNetwork.Network;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder.GraphBuilderFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder.NetworkBuilderFacade;

/**
 *
 * @author Martin
 */
public class AbstractNetworkAdapter {

    public void saveGraphToFile(Graph graph, File file) {
        // create Network builder
        NetworkBuilderFacade networkBuilderFacade = new NetworkBuilderFacade();
        // build Network
        Network network = networkBuilderFacade.buildNetwork(graph);
        // save network to file
        saveNetwork(network, file);
    }

    public Graph loadGraphFromFile(File file) {

        // load network from file
        Network network = loadNetwork(file);
        // create Graph builder
        GraphBuilderFacade graphBuilderFacade = new GraphBuilderFacade();
        // build Graph
        Graph graph = graphBuilderFacade.buildGraph(network);

        return graph;
    }

    private void saveNetwork(Network network, File file) {


        FileOutputStream fileOuptutStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOuptutStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOuptutStream);
            objectOutputStream.writeObject(network);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                fileOuptutStream.close();
            } catch (IOException ex) {
                // nothing to do
            }
            try {
                objectOutputStream.close();
            } catch (IOException ex) {
                // nothing to do
            }
        }
    }

    private Network loadNetwork(File file) {
        Network network = null;
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            network = (Network) objectInputStream.readObject();
            return network;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException ex) {
                // nothing to do
            }
            try {
                objectInputStream.close();
            } catch (IOException ex) {
                // nothing to do
            }
        }
        return network;
    }
}
