package psimulator.dataLayer;

import java.util.Observer;
import psimulator.dataLayer.interfaces.PreferencesInterface;
import psimulator.dataLayer.interfaces.LanguageInterface;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;


/**
 *
 * @author Martin
 */
public abstract class DataLayerFacade implements PreferencesInterface, LanguageInterface{
    
    public abstract SimulatorManagerInterface getSimulatorInterface();
    public abstract void addSimulatorObserver(Observer observer);
}
