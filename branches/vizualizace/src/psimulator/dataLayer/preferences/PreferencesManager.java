package psimulator.dataLayer.preferences;

import java.util.Observable;
import java.util.prefs.Preferences;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.Enums.UpdateEventType;
import psimulator.dataLayer.SaveableInterface;

/**
 *
 * @author Martin
 */
public final class PreferencesManager extends Observable implements SaveableInterface{

    // strings for saving
    private static final String TOOLBAR_ICON_SIZE = "TOOLBAR_ICON_SIZE";
    // 
    private Preferences prefs;
    
    // set to default
    private ToolbarIconSizeEnum toolbarIconSize = ToolbarIconSizeEnum.MEDIUM;
    
    public PreferencesManager(){
        // initialize preferences store
        prefs = Preferences.userNodeForPackage(this.getClass());
        
        loadPreferences();
    }
 
    
    @Override
    public void savePreferences() {
        // save toolbar icon size
        prefs.put(TOOLBAR_ICON_SIZE, toolbarIconSize.toString());
    }

    @Override
    public void loadPreferences() {
        // load toolbar icon size
        toolbarIconSize = ToolbarIconSizeEnum.valueOf(prefs.get(TOOLBAR_ICON_SIZE, toolbarIconSize.toString()));
    }
    
    // GETTERS AND SETTERS
    public ToolbarIconSizeEnum getToolbarIconSize(){
        return toolbarIconSize;
    }
    
    public void setToolbarIconSize(ToolbarIconSizeEnum toolbarIconSize){
        this.toolbarIconSize = toolbarIconSize;
        
        // notify all observers
        setChanged();
        notifyObservers(UpdateEventType.ICON_SIZE);  
    }   
}
