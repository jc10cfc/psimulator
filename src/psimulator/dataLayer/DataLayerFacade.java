package psimulator.dataLayer;

import psimulator.dataLayer.Network.Serializer.SaveLoadException;
import java.io.File;
import java.util.Observer;
import psimulator.dataLayer.Network.Components.NetworkModel;
import psimulator.dataLayer.Network.NetworkFacade;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;
import psimulator.dataLayer.interfaces.LanguageInterface;
import psimulator.dataLayer.interfaces.PreferencesInterface;
import psimulator.dataLayer.Simulator.SimulatorManagerInterface;


/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class DataLayerFacade implements PreferencesInterface, LanguageInterface{
    
    public abstract SimulatorManagerInterface getSimulatorManager();
    public abstract void addSimulatorObserver(Observer observer);

    public abstract void saveNetworkModelToFile(File file) throws SaveLoadException;
    public abstract NetworkModel loadNetworkModelFromFile(File file) throws SaveLoadException;
    
    public abstract void saveEventsToFile(SimulatorEventsWrapper simulatorEvents, File file) throws SaveLoadException;
    public abstract SimulatorEventsWrapper loadEventsFromFile(File file) throws SaveLoadException;
    
    public abstract NetworkFacade getNetworkFacade();
}
