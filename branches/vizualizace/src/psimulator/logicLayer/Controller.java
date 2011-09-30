package psimulator.logicLayer;

import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayer;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.PreferencesManager;
import psimulator.dataLayer.language.LanguageManager;
import psimulator.userInterface.MainWindow;
import psimulator.userInterface.MainWindowInterface;

/**
 *
 * @author Martin
 */
public class Controller {

    private DataLayer model;
    private MainWindowInterface view;
    
    private UndoManager undoManager;

    public Controller(DataLayer model, MainWindowInterface view) {
        this.model = model;
        this.view = view;

        view.initView(this);
    }



    public LanguageManager getLanguageManager(){
        return model.getLanguageManager();
    }
    
    public ToolbarIconSizeEnum getToolbarIconSize(){
       return model.getPreferencesManager().getToolbarIconSize();
    }

    public void setToolbarIconSize(ToolbarIconSizeEnum size){
        // set change in model
       model.getPreferencesManager().setToolbarIconSize(size);
       model.getPreferencesManager().savePreferences();
       
       // udpate view
       view.updateToolBarIconsSize(size);
    } 
}
