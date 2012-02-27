package psimulator.dataLayer.SimulatorEvents;

import java.io.File;
import psimulator.dataLayer.SaveLoadException;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface AbstractSimulatorEventsSaveLoadInterface {
     public void saveEventsToFile(SimulatorEventsWrapper simulatorEvents, File file) throws SaveLoadException;
     public SimulatorEventsWrapper loadEventsFromFile(File file) throws SaveLoadException;
}
