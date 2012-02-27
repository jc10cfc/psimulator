package psimulator.dataLayer;

import java.io.File;
import java.util.Observer;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;
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
    
    public abstract void saveGraphToFile(Graph graph, File file) throws SaveLoadException;
    public abstract Graph loadGraphFromFile(File file) throws SaveLoadException;
    
    public abstract void saveEventsToFile(SimulatorEventsWrapper simulatorEvents, File file) throws SaveLoadException;
    public abstract SimulatorEventsWrapper loadEventsFromFile(File file) throws SaveLoadException;
}
