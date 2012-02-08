package psimulator.dataLayer.interfaces;

import java.util.Observer;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;

/**
 *
 * @author Martin
 */
public interface PreferencesInterface {
    public ToolbarIconSizeEnum getToolbarIconSize();
    public void setToolbarIconSize(ToolbarIconSizeEnum size); 
    public LevelOfDetailsMode getLevelOfDetails();
    public void setLevelOfDetails(LevelOfDetailsMode levelOfDetails);
    
    public boolean isViewDeviceNames();
    public void setViewDeviceNames(boolean viewDeviceNames);
    public boolean isViewDeviceTypes();
    public void setViewDeviceTypes(boolean viewDeviceTypes);
    public boolean isViewInterfaceNames();
    public void setViewInterfaceNames(boolean viewInterfaceNames);
    public boolean isViewCableDelay();
    public void setViewCableDelay(boolean viewCableDelay);
    
    public void savePreferences();
    
    public void addPreferencesObserver(Observer observer);
    public void deletePreferencesObserver(Observer observer);
}
