package psimulator.logicLayer;

import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayer;
import psimulator.dataLayer.language.LanguageManager;
import psimulator.userInterface.MainWindow;

/**
 *
 * @author Martin
 */
public class Controller {

    private DataLayer model;
    private MainWindow view;
    
    private UndoManager undoManager;

    public Controller(DataLayer model, MainWindow view) {
        this.model = model;
        this.view = view;

        view.initView(this);
    }



    public LanguageManager getLanguageManager(){
        return model.getLanguageManager();
    }

}
