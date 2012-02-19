package psimulator.dataLayer.interfaces;

import java.util.Observer;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.PacketImageType;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
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
    public boolean isViewIpAddresses();
    public void setViewIpAddresses(boolean viewIpAddresses);
    public boolean isViewMacAddresses();
    public void setViewMacAddresses(boolean viewMacAddresses);
    
    
    public PacketImageType getPackageImageType();
    public void setPackageImageType(PacketImageType packageImageType);
    
    public String getConnectionIpAddress();
    public void setConnectionIpAddress(String connectionIpAddress);
    public String getConnectionPort();
    public void setConnectionPort(String connectionPort);
    
    public void savePreferences();
    
    public void addPreferencesObserver(Observer observer);
    public void deletePreferencesObserver(Observer observer);
}
