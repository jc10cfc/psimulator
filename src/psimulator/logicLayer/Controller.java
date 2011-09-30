package psimulator.logicLayer;

import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.UserInterfaceFacade;

/**
 *
 * @author Martin
 */
public class Controller implements ControllerFacade{

    private DataLayerFacade model;
    private UserInterfaceFacade view;
    
    private UndoManager undoManager;

    public Controller(DataLayerFacade model, UserInterfaceFacade view) {
        this.model = model;
        this.view = view;

        view.initView(this);
    }

}
