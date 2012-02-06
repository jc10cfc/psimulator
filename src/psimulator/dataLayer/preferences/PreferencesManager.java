package psimulator.dataLayer.preferences;

import java.util.Observable;
import java.util.prefs.Preferences;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.interfaces.SaveableInterface;

/**
 *
 * @author Martin
 */
public final class PreferencesManager extends Observable implements SaveableInterface{

    // strings for saving
    private static final String TOOLBAR_ICON_SIZE = "TOOLBAR_ICON_SIZE";
    private static final String VIEW_DEVICE_NAMES = "VIEW_DEVICE_NAMES";
    private static final String VIEW_DEVICE_TYPES = "VIEW_DEVICE_TYPES";
    private static final String VIEW_INTERFACE_NAMES = "VIEW_INTERFACE_NAMES";
    private static final String AUTO_LEVEL_OF_DETAILS = "AUTO_LEVEL_OF_DETAILS";
    // 
    private Preferences prefs;
    
    // set to default
    private ToolbarIconSizeEnum toolbarIconSize = ToolbarIconSizeEnum.MEDIUM;
    private boolean viewDeviceNames = true;
    private boolean viewDeviceTypes = false;
    private boolean viewInterfaceNames = false;
    private LevelOfDetailsMode levelOfDetails = LevelOfDetailsMode.MANUAL;
            
    public PreferencesManager(){
        // initialize preferences store
        prefs = Preferences.userNodeForPackage(this.getClass());
        
        loadPreferences();
    }
 
    
    @Override
    public void savePreferences() {
        // save toolbar icon size
        prefs.put(TOOLBAR_ICON_SIZE, toolbarIconSize.toString());
        
        prefs.putBoolean(VIEW_DEVICE_NAMES, viewDeviceNames);
        prefs.putBoolean(VIEW_DEVICE_TYPES, viewDeviceTypes);
        prefs.putBoolean(VIEW_INTERFACE_NAMES, viewInterfaceNames);
        
        prefs.put(AUTO_LEVEL_OF_DETAILS, levelOfDetails.toString());
    }

    @Override
    public void loadPreferences() {
        // load toolbar icon size
        toolbarIconSize = ToolbarIconSizeEnum.valueOf(prefs.get(TOOLBAR_ICON_SIZE, toolbarIconSize.toString()));
        
        viewDeviceNames = prefs.getBoolean(VIEW_DEVICE_NAMES, viewDeviceNames);
        viewDeviceTypes = prefs.getBoolean(VIEW_DEVICE_TYPES, viewDeviceTypes);
        viewInterfaceNames = prefs.getBoolean(VIEW_INTERFACE_NAMES, viewInterfaceNames);
        
        levelOfDetails = LevelOfDetailsMode.valueOf(prefs.get(AUTO_LEVEL_OF_DETAILS, levelOfDetails.toString()));
    }
    
    // GETTERS AND SETTERS
    public ToolbarIconSizeEnum getToolbarIconSize(){
        return toolbarIconSize;
    }
    
    public void setToolbarIconSize(ToolbarIconSizeEnum toolbarIconSize){
        this.toolbarIconSize = toolbarIconSize;
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.ICON_SIZE);  
    }

    public boolean isViewDeviceNames() {
        return viewDeviceNames;
    }

    public void setViewDeviceNames(boolean viewDeviceNames) {
        this.viewDeviceNames = viewDeviceNames;
    }

    public boolean isViewDeviceTypes() {
        return viewDeviceTypes;
    }

    public void setViewDeviceTypes(boolean viewDeviceTypes) {
        this.viewDeviceTypes = viewDeviceTypes;
    }

    public boolean isViewInterfaceNames() {
        return viewInterfaceNames;
    }

    public void setViewInterfaceNames(boolean viewInterfaceNames) {
        this.viewInterfaceNames = viewInterfaceNames;
    }

    public LevelOfDetailsMode getLevelOfDetails() {
        return levelOfDetails;
    }

    public void setLevelOfDetails(LevelOfDetailsMode levelOfDetails) {
        this.levelOfDetails = levelOfDetails;
    }
    
    
}
