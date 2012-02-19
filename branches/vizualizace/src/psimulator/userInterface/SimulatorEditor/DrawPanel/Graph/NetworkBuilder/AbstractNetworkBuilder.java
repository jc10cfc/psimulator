package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.NetworkBuilder;

import psimulator.AbstractNetwork.Network;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.dataLayer.Singletons.GeneratorSingleton;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public abstract class AbstractNetworkBuilder {

    public abstract Network getResult();

    public abstract void buildNetworkDevice(AbstractHwComponent abstractHwComponent);

    public abstract void buildNetworkCable(Cable cable);
    
    public abstract void buildCounter(GeneratorSingleton counter);
}
