package psimulator;

import psimulator.dataLayer.DataLayer;
import psimulator.logicLayer.Controller;
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
        DataLayer model = new DataLayer();
        MainWindow view = new MainWindow(model);
        Controller controller = new Controller(model, view);
    }

}
