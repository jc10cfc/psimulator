package psimulator;

import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.DataLayer;
import psimulator.logicLayer.Controller;
import psimulator.logicLayer.ControllerFacade;
import psimulator.userInterface.MainWindow;

/**
 *
 * @author Martin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DataLayerFacade model = new DataLayer();
        MainWindow view = new MainWindow(model);
        ControllerFacade controller = new Controller(model, view);
    }

}
