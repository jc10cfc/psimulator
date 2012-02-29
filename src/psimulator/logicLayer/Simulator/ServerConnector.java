package psimulator.logicLayer.Simulator;

import java.util.logging.Level;
import java.util.logging.Logger;
import psimulator.dataLayer.DataLayerFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class ServerConnector {
    
    private DataLayerFacade dataLayer;
    
    public ServerConnector(DataLayerFacade dataLayer){
        
    }
    
    public void connect(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            //Logger.getLogger(ServerConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // try to connect
        //dataLayer.getSimulatorManager().pullTriggerTmp();
    }
    
}
