package psimulator.dataLayer.Network.Serializer;

import java.io.File;
import psimulator.dataLayer.Network.NetworkModel;
import psimulator.dataLayer.SaveLoadException;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface AbstractNetworkSerializer {
    public void saveNetworkModelToFile(NetworkModel networkModel, File file) throws SaveLoadException;
    public NetworkModel loadNetworkModelFromFile(File file) throws SaveLoadException;
}
