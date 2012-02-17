package psimulator;

import javax.swing.SwingUtilities;
import psimulator.dataLayer.DataLayer;
import psimulator.dataLayer.DataLayerFacade;
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DataLayerFacade model = new DataLayer();
                MainWindow view = new MainWindow(model);
                ControllerFacade controller = new Controller(model, view);
            }
        });

    }
}
