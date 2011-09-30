package psimulator.dataLayer;

import java.util.Observer;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.preferences.PreferencesManager;
import psimulator.dataLayer.language.LanguageManager;

/**
 *
 * @author Martin
 */
public class DataLayer extends DataLayerFacade{
    private LanguageManager languageManager;
    private PreferencesManager preferencesManager;
    
    public DataLayer(){
        preferencesManager = new PreferencesManager();
        languageManager = new LanguageManager();
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

    

    
    
}
