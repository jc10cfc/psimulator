package psimulator.dataLayer.interfaces;

import java.io.File;
import java.util.List;
import java.util.Observer;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.dataLayer.Enums.RecentlyOpenedDirectoryType;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.Enums.ViewDetailsType;
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
    
    public boolean isViewDetails(ViewDetailsType viewDetailsType);
    public void setViewDetails(ViewDetailsType viewDetailsType, boolean value);

    
    public PacketImageType getPackageImageType();
    public void setPackageImageType(PacketImageType packageImageType);
    
    public String getConnectionIpAddress();
    public void setConnectionIpAddress(String connectionIpAddress);
    public String getConnectionPort();
    public void setConnectionPort(String connectionPort);
    
    public void savePreferences();
    
    public void addPreferencesObserver(Observer observer);
    public void deletePreferencesObserver(Observer observer);
    
    public List<File> getRecentOpenedFiles();
    public void addRecentOpenedFile(File file);
    
    public void setRecentDirectory(RecentlyOpenedDirectoryType directoryType, File file);
    public File getRecentDirectory(RecentlyOpenedDirectoryType directoryType);
}
