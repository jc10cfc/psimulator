package psimulator.dataLayer;

import java.io.File;
import java.util.Observer;
import psimulator.dataLayer.Enums.LevelOfDetailsMode;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.Network.NetworkFacade;
import psimulator.dataLayer.Network.NetworkModel;
import psimulator.dataLayer.Network.Serializer.NetworkModelSerializer;
import psimulator.dataLayer.Simulator.SimulatorManager;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsSerializer;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsSerializerXML;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;
import psimulator.dataLayer.Singletons.ImageFactory.ImageFactorySingleton;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.dataLayer.language.LanguageManager;
import psimulator.dataLayer.preferences.PreferencesManager;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.PacketImageType;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class DataLayer extends DataLayerFacade {

    private LanguageManager languageManager;
    private PreferencesManager preferencesManager;
    private SimulatorManager simulatorManager;
    
    //
    //private AbstractNetworkAdapter abstractNetworkAdapter;
    //private AbstractNetworkAdapterXML abstractNetworkAdapterXML;
    private NetworkModelSerializer networkModelSerializer;
    //
    private SimulatorEventsSerializer simulatorEventsSerializer;
    private SimulatorEventsSerializerXML simulatorEventsSerializerXML;
    //
    private NetworkFacade networkFacade;
    

    public DataLayer() {
        preferencesManager = new PreferencesManager();
        languageManager = new LanguageManager();
        simulatorManager = new SimulatorManager();
        //abstractNetworkAdapter = new AbstractNetworkAdapter();
        //abstractNetworkAdapterXML = new AbstractNetworkAdapterXML();
        networkModelSerializer = new NetworkModelSerializer();
        
        simulatorEventsSerializer = new SimulatorEventsSerializer();
        simulatorEventsSerializerXML = new SimulatorEventsSerializerXML();
        
        // get instance to init imageFactory
        ImageFactorySingleton.getInstance();
        
        //
        networkFacade = new NetworkFacade();
    }
    
    

    @Override
    public ToolbarIconSizeEnum getToolbarIconSize() {
        return preferencesManager.getToolbarIconSize();
    }

    @Override
    public void setToolbarIconSize(ToolbarIconSizeEnum size) {
        preferencesManager.setToolbarIconSize(size);
    }

    @Override
    public void savePreferences() {
        preferencesManager.savePreferences();
    }

    @Override
    public void setCurrentLanguage(int languagePosition) {
        languageManager.setCurrentLanguage(languagePosition);
    }

    @Override
    public Object[] getAvaiableLanguageNames() {
        return languageManager.getAvaiableLanguageNames();
    }

    @Override
    public int getCurrentLanguagePosition() {
        return languageManager.getCurrentLanguagePosition();
    }

    @Override
    public String getString(String string) {
        return languageManager.getString(string);
    }

    @Override
    public void addLanguageObserver(Observer observer) {
        languageManager.addObserver(observer);
    }

    @Override
    public void addPreferencesObserver(Observer observer) {
        preferencesManager.addObserver(observer);
    }

    @Override
    public void deletePreferencesObserver(Observer observer) {
        preferencesManager.deleteObserver(observer);
    }

    @Override
    public void deleteLanguageObserver(Observer observer) {
        languageManager.deleteObserver(observer);
    }

    @Override
    public void addSimulatorObserver(Observer observer) {
        simulatorManager.addObserver(observer);
    }

    @Override
    public SimulatorManagerInterface getSimulatorManager() {
        return simulatorManager;
    }

    
//    @Override
//    public void saveGraphToFile(Graph graph, File file) throws SaveLoadException{
//        //abstractNetworkAdapter.saveGraphToFile(graph, file);
//        abstractNetworkAdapterXML.saveGraphToFile(graph, file);
//    }
//
//    @Override
//    public Graph loadGraphFromFile(File file) throws SaveLoadException{
//        //return abstractNetworkAdapter.loadGraphFromFile(file);
//        return abstractNetworkAdapterXML.loadGraphFromFile(file);
//    }
    
    
    @Override
    public void saveNetworkModelToFile(File file) throws SaveLoadException {
        networkModelSerializer.saveNetworkModelToFile(networkFacade.getNetworkModel(), file);
    }

    @Override
    public void loadNetworkModelFromFile(File file) throws SaveLoadException {
        NetworkModel networkModel = networkModelSerializer.loadNetworkModelFromFile(file);
        networkFacade.setNetworkModel(networkModel);
    }
  
    @Override
    public void saveEventsToFile(SimulatorEventsWrapper simulatorEvents, File file) throws SaveLoadException {
        simulatorEventsSerializer.saveEventsToFile(simulatorEvents, file);
        //simulatorEventsSerializerXML.saveEventsToFile(simulatorEvents, file);
    }

    @Override
    public SimulatorEventsWrapper loadEventsFromFile(File file) throws SaveLoadException {
        return simulatorEventsSerializer.loadEventsFromFile(file);
        //return simulatorEventsSerializerXML.loadEventsFromFile(file);
    }

    @Override
    public boolean isViewDeviceNames() {
        return preferencesManager.isViewDeviceNames();
    }

    @Override
    public void setViewDeviceNames(boolean viewDeviceNames) {
        preferencesManager.setViewDeviceNames(viewDeviceNames);
    }

    @Override
    public boolean isViewDeviceTypes() {
        return preferencesManager.isViewDeviceTypes();
    }

    @Override
    public void setViewDeviceTypes(boolean viewDeviceTypes) {
        preferencesManager.setViewDeviceTypes(viewDeviceTypes);
    }

    @Override
    public boolean isViewInterfaceNames() {
        return preferencesManager.isViewInterfaceNames();
    }

    @Override
    public void setViewInterfaceNames(boolean viewInterfaceNames) {
        preferencesManager.setViewInterfaceNames(viewInterfaceNames);
    }

    @Override
    public LevelOfDetailsMode getLevelOfDetails() {
        return preferencesManager.getLevelOfDetails();
    }

    @Override
    public void setLevelOfDetails(LevelOfDetailsMode levelOfDetails) {
        preferencesManager.setLevelOfDetails(levelOfDetails);
    }

    @Override
    public boolean isViewCableDelay() {
        return preferencesManager.isViewCableDelay();
    }

    @Override
    public void setViewCableDelay(boolean viewCableDelay) {
        preferencesManager.setViewCableDelay(viewCableDelay);
    }

    @Override
    public boolean isViewIpAddresses() {
        return preferencesManager.isViewIpAddresses();
    }

    @Override
    public void setViewIpAddresses(boolean viewIpAddresses) {
        preferencesManager.setViewIpAddresses(viewIpAddresses);
    }

    @Override
    public boolean isViewMacAddresses() {
        return preferencesManager.isViewMacAddresses();
    }

    @Override
    public void setViewMacAddresses(boolean viewMacAddresses) {
        preferencesManager.setViewMacAddresses(viewMacAddresses);
    }

    @Override
    public PacketImageType getPackageImageType() {
        return preferencesManager.getPackageImageType();
    }

    @Override
    public void setPackageImageType(PacketImageType packageImageType) {
        preferencesManager.setPackageImageType(packageImageType);
    }

    @Override
    public String getConnectionIpAddress() {
        return preferencesManager.getConnectionIpAddress();
    }

    @Override
    public void setConnectionIpAddress(String connectionIpAddress) {
        preferencesManager.setConnectionIpAddress(connectionIpAddress);
    }

    @Override
    public String getConnectionPort() {
        return preferencesManager.getConnectionPort();
    }

    @Override
    public void setConnectionPort(String connectionPort) {
        preferencesManager.setConnectionPort(connectionPort);
    }

    @Override
    public NetworkFacade getNetworkFacade() {
        return networkFacade;
    }


}
