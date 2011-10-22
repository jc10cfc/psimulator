package psimulator.dataLayer.interfaces;

import java.util.Observer;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;

/**
 *
 * @author Martin
 */
public interface PreferencesInterface {
    public ToolbarIconSizeEnum getToolbarIconSize();
    public void setToolbarIconSize(ToolbarIconSizeEnum size);   
    
    public void savePreferences();
    
    public void addPreferencesObserver(Observer observer);
}
