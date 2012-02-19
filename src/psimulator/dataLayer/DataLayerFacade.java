package psimulator.dataLayer;

import java.io.File;
import java.util.Observer;
import psimulator.dataLayer.interfaces.LanguageInterface;
import psimulator.dataLayer.interfaces.PreferencesInterface;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;


/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class DataLayerFacade implements PreferencesInterface, LanguageInterface{
    
    public abstract SimulatorManagerInterface getSimulatorManager();
    public abstract void addSimulatorObserver(Observer observer);
    
    public abstract void saveGraphToFile(Graph graph, File file);
    public abstract Graph loadGraphFromFile(File file);
}
