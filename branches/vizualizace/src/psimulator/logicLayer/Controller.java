package psimulator.logicLayer;

import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.UserInterfaceOuterFacade;

/**
 *
 * @author Martin
 */
public class Controller implements ControllerFacade{

    private DataLayerFacade model;
    private UserInterfaceOuterFacade view;
    
    private UndoManager undoManager;

    public Controller(DataLayerFacade model, UserInterfaceOuterFacade view) {
        this.model = model;
        this.view = view;

        view.initView(this);
    }

}
