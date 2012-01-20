package psimulator.dataLayer;

import java.util.Observer;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.Simulator.SimulatorManager;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.dataLayer.language.LanguageManager;
import psimulator.dataLayer.preferences.PreferencesManager;

/**
 *
 * @author Martin
 */
public class DataLayer extends DataLayerFacade{
    private LanguageManager languageManager;
    private PreferencesManager preferencesManager;
    private HardwareDevicesManager hwDeviceManager;
    private SimulatorManager simulatorManager;
    
    
    public DataLayer(){
        preferencesManager = new PreferencesManager();
        languageManager = new LanguageManager();
        hwDeviceManager =  new HardwareDevicesManager();
        simulatorManager = new SimulatorManager();
    }

    @Override
    public ToolbarIconSizeEnum getToolbarIconSize() {
        return preferencesManager.getToolbarIconSize();
    }

    @Override
    public void setToolbarIconSize(ToolbarIconSizeEnum size) {
        preferencesManager.setToolbarIconSize(size);
        savePreferences();
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
    public void addSimulatorObserver(Observer observer) {
        simulatorManager.addObserver(observer);
    }

    @Override
    public SimulatorManagerInterface getSimulatorInterface() {
        return simulatorManager;
    }
    
    
   

    

    
    
}
