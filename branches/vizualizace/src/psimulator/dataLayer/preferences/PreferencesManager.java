package psimulator.dataLayer.preferences;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.prefs.Preferences;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.interfaces.SaveableInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.PacketImageType;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public final class PreferencesManager extends Observable implements SaveableInterface {

    // strings for saving
    private static final String TOOLBAR_ICON_SIZE = "TOOLBAR_ICON_SIZE";
    private static final String PACKAGE_IMAGE_TYPE = "PACKAGE_IMAGE_TYPE";
    private static final String VIEW_DEVICE_NAMES = "VIEW_DEVICE_NAMES";
    private static final String VIEW_DEVICE_TYPES = "VIEW_DEVICE_TYPES";
    private static final String VIEW_INTERFACE_NAMES = "VIEW_INTERFACE_NAMES";
    private static final String VIEW_CABLE_DELAY = "VIEW_CABLE_DELAY";
    private static final String VIEW_IP_ADDRESSES = "VIEW_IP_ADDRESSES";
    private static final String VIEW_MAC_ADDRESSES = "VIEW_MAC_ADDRESSES";
    private static final String AUTO_LEVEL_OF_DETAILS = "AUTO_LEVEL_OF_DETAILS";
    //
    private static final String CONNECTION_IP_ADDRESS = "CONNECTION_IP_ADDRESS";
    private static final String CONNECTION_PORT = "CONNECTION_PORT";
    //
    private static final String RECENTLY_OPENED_FILES = "RECENTLY_OPENED_FILES";
    // 
    private Preferences prefs;
    // set to default
    private ToolbarIconSizeEnum toolbarIconSize = ToolbarIconSizeEnum.MEDIUM;
    private PacketImageType packageImageType = PacketImageType.CLASSIC;
    private LevelOfDetailsMode levelOfDetails = LevelOfDetailsMode.MANUAL;
    //
    private boolean viewDeviceNames = true;
    private boolean viewDeviceTypes = true;
    private boolean viewInterfaceNames = true;
    private boolean viewCableDelay = false;
    private boolean viewIpAddresses = true;
    private boolean viewMacAddresses = true;
    //
    private String connectionIpAddress = "";
    private String connectionPort = "";
    private String recentlyOpenedFiles = "";
    //
    private RecentOpenedFilesManager recentOpenedFilesManager;
    //
    public PreferencesManager() {
        // initialize preferences store
        prefs = Preferences.userNodeForPackage(this.getClass());
        
        // create recent opened files manager
        recentOpenedFilesManager = new RecentOpenedFilesManager();
        
        // load preferences
        loadPreferences();    
    }

    @Override
    public void savePreferences() {
        // save toolbar icon size
        prefs.put(TOOLBAR_ICON_SIZE, toolbarIconSize.toString());
        prefs.put(PACKAGE_IMAGE_TYPE, packageImageType.toString());

        prefs.putBoolean(VIEW_DEVICE_NAMES, viewDeviceNames);
        prefs.putBoolean(VIEW_DEVICE_TYPES, viewDeviceTypes);
        prefs.putBoolean(VIEW_INTERFACE_NAMES, viewInterfaceNames);
        prefs.putBoolean(VIEW_CABLE_DELAY, viewCableDelay);
        prefs.putBoolean(VIEW_IP_ADDRESSES, viewIpAddresses);
        prefs.putBoolean(VIEW_MAC_ADDRESSES, viewMacAddresses);

        prefs.put(AUTO_LEVEL_OF_DETAILS, levelOfDetails.toString());

        prefs.put(CONNECTION_IP_ADDRESS, connectionIpAddress);
        prefs.put(CONNECTION_PORT, connectionPort);
        
        // get string with filenames from recentOpenedFilesManager
        recentlyOpenedFiles = recentOpenedFilesManager.createStringFromFiles();
        
        prefs.put(RECENTLY_OPENED_FILES, recentlyOpenedFiles);
    }

    @Override
    public void loadPreferences() {
        // load toolbar icon size
        toolbarIconSize = ToolbarIconSizeEnum.valueOf(prefs.get(TOOLBAR_ICON_SIZE, toolbarIconSize.toString()));
        packageImageType = PacketImageType.valueOf(prefs.get(PACKAGE_IMAGE_TYPE, packageImageType.toString()));

        viewDeviceNames = prefs.getBoolean(VIEW_DEVICE_NAMES, viewDeviceNames);
        viewDeviceTypes = prefs.getBoolean(VIEW_DEVICE_TYPES, viewDeviceTypes);
        viewInterfaceNames = prefs.getBoolean(VIEW_INTERFACE_NAMES, viewInterfaceNames);
        viewCableDelay = prefs.getBoolean(VIEW_CABLE_DELAY, viewCableDelay);
        viewIpAddresses = prefs.getBoolean(VIEW_IP_ADDRESSES, viewIpAddresses);
        viewMacAddresses = prefs.getBoolean(VIEW_MAC_ADDRESSES, viewMacAddresses);

        levelOfDetails = LevelOfDetailsMode.valueOf(prefs.get(AUTO_LEVEL_OF_DETAILS, levelOfDetails.toString()));
        
        connectionIpAddress = prefs.get(CONNECTION_IP_ADDRESS, connectionIpAddress);
        connectionPort = prefs.get(CONNECTION_PORT, connectionPort);
        
        recentlyOpenedFiles = prefs.get(RECENTLY_OPENED_FILES, recentlyOpenedFiles);
        
        // let Recent opened files manager parse the saved files
        recentOpenedFilesManager.parseFilesFromString(recentlyOpenedFiles);
        
        // clear non existing files
        recentOpenedFilesManager.clearNotExistingFiles();
    }

    // GETTERS AND SETTERS
    public ToolbarIconSizeEnum getToolbarIconSize() {
        return toolbarIconSize;
    }

    public void setToolbarIconSize(ToolbarIconSizeEnum toolbarIconSize) {
        this.toolbarIconSize = toolbarIconSize;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.ICON_SIZE);
    }

    public PacketImageType getPackageImageType() {
        return packageImageType;
    }

    public void setPackageImageType(PacketImageType packageImageType) {
        this.packageImageType = packageImageType;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.PACKET_IMAGE_TYPE_CHANGE);
    }

    public boolean isViewDeviceNames() {
        return viewDeviceNames;
    }

    public void setViewDeviceNames(boolean viewDeviceNames) {
        this.viewDeviceNames = viewDeviceNames;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public boolean isViewDeviceTypes() {
        return viewDeviceTypes;
    }

    public void setViewDeviceTypes(boolean viewDeviceTypes) {
        this.viewDeviceTypes = viewDeviceTypes;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public boolean isViewInterfaceNames() {
        return viewInterfaceNames;
    }

    public void setViewInterfaceNames(boolean viewInterfaceNames) {
        this.viewInterfaceNames = viewInterfaceNames;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public LevelOfDetailsMode getLevelOfDetails() {
        return levelOfDetails;
    }

    public void setLevelOfDetails(LevelOfDetailsMode levelOfDetails) {
        this.levelOfDetails = levelOfDetails;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public boolean isViewCableDelay() {
        return viewCableDelay;
    }

    public void setViewCableDelay(boolean viewCableDelay) {
        this.viewCableDelay = viewCableDelay;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public boolean isViewIpAddresses() {
        return viewIpAddresses;
    }

    public void setViewIpAddresses(boolean viewIpAddresses) {
        this.viewIpAddresses = viewIpAddresses;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public boolean isViewMacAddresses() {
        return viewMacAddresses;
    }

    public void setViewMacAddresses(boolean viewMacAddresses) {
        this.viewMacAddresses = viewMacAddresses;

        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.VIEW_DETAILS);
    }

    public String getConnectionIpAddress() {
        return connectionIpAddress;
    }

    public void setConnectionIpAddress(String connectionIpAddress) {
        this.connectionIpAddress = connectionIpAddress;
    }

    public String getConnectionPort() {
        return connectionPort;
    }

    public void setConnectionPort(String connectionPort) {
        this.connectionPort = connectionPort;
    }
    
    public List<File> getRecentOpenedFiles(){
        return recentOpenedFilesManager.getRecentOpenedFiles();
    }
    
    public void addRecentOpenedFile(File file){
        recentOpenedFilesManager.addFile(file);
        
        // notify all observers
        setChanged();
        notifyObservers(ObserverUpdateEventType.RECENT_OPENED_FILES_CHANGED);
    }
}
