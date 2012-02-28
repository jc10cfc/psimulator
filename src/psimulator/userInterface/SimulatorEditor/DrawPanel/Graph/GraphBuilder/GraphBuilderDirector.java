package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import java.util.Collection;
import java.util.Iterator;
import psimulator.dataLayer.Network.CableModel;
import psimulator.dataLayer.Network.HwComponentModel;
import psimulator.dataLayer.Network.NetworkFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class GraphBuilderDirector {

    private AbstractGraphBuilder abstractGraphBuilder;
    private NetworkFacade networkFacade;

    public GraphBuilderDirector(AbstractGraphBuilder abstractGraphBuilder, NetworkFacade networkFacade) {
        this.abstractGraphBuilder = abstractGraphBuilder;
        this.networkFacade = networkFacade;
    }

    public void construct() {
        // create graph
        abstractGraphBuilder.buildGraph(networkFacade);

        Collection <HwComponentModel> devices = networkFacade.getHwComponents();
        Iterator<HwComponentModel> ith = devices.iterator();
        
        // build all hw components
        while(ith.hasNext()){
            HwComponentModel hwComp = ith.next();
            abstractGraphBuilder.buildHwComponent(hwComp);
        }

        Collection <CableModel> cables = networkFacade.getCables();
        Iterator<CableModel> itc = cables.iterator();
        
        // build all cables
        while(itc.hasNext()){
            CableModel cableModel = itc.next();
            abstractGraphBuilder.buildCable(cableModel);
        }
    }
}
