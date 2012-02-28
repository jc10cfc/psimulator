//package psimulator.dataLayer.AbstractNetwork;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.xml.bind.JAXBException;
//import psimulator.AbstractNetwork.Network;
//import psimulator.dataLayer.Enums.SaveLoadExceptionType;
//import psimulator.dataLayer.SaveLoadException;
//import psimulator.dataLayer.SaveLoadExceptionParametersWrapper;
//import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
//import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder.GraphBuilderFacade;
//import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder.NetworkBuilderFacade;
//
///**
// *
// * @author Martin Švihlík <svihlma1 at fit.cvut.cz> Lukáš <lukasma1@fit.cvut.cz>
// */
//public class AbstractNetworkAdapterXML {
//
//    public void saveGraphToFile(Graph graph, File file) throws SaveLoadException{
//        // create Network builder
//        NetworkBuilderFacade networkBuilderFacade = new NetworkBuilderFacade();
//        // build Network
//        Network network = networkBuilderFacade.buildNetwork(graph);
//        // save network to file
//        String fileName = file.getPath();
//        
//        
//        // try if file exists
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException ex) {
//                throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.ERROR_WHILE_CREATING, fileName, true));
//            }
//        }
//        
//        // try if file readable
//        if (!file.canWrite()) {
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.CANT_WRITE_TO_FILE, fileName, true));
//        }
//        
//        try {
//            network.save(fileName);
//        } catch (JAXBException ex) {
//            // if needed, uncomment this line:
//            //Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
//            
//            // throw exception
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.ERROR_WHILE_WRITING, fileName, true));
//        }
//    }
//
//    public Graph loadGraphFromFile(File file) throws SaveLoadException{
//        String fileName = file.getPath();
//        Network network = null;
//
//        // try if file exists
//        if(!file.exists()){
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.FILE_DOES_NOT_EXIST, fileName, false));
//        }
//        
//        // try if file readable
//        if (!file.canRead()) {
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.CANT_READ_FROM_FILE, fileName, false));
//        }
//        
//        // try read
//        try {
//            network = Network.load(fileName);
//        } catch (JAXBException ex) {
//            // if needed, uncomment this line:
//            //Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
//            
//            // throw exception
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.ERROR_WHILE_READING, fileName, false));
//        }
//
//
//        GraphBuilderFacade graphBuilderFacade = new GraphBuilderFacade();
//        Graph graph = graphBuilderFacade.buildGraph(network);
//
//        return graph;
//
//
//    }
//
//}
