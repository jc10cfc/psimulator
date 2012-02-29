package psimulator.dataLayer.SimulatorEvents.Serializer;

import java.io.File;
import psimulator.dataLayer.Network.Serializer.SaveLoadException;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface AbstractSimulatorEventsSaveLoadInterface {
     public void saveEventsToFile(SimulatorEventsWrapper simulatorEvents, File file) throws SaveLoadException;
     public SimulatorEventsWrapper loadEventsFromFile(File file) throws SaveLoadException;
}
