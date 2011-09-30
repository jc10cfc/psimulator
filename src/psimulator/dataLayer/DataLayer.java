package psimulator.dataLayer;

import psimulator.dataLayer.language.LanguageManager;

/**
 *
 * @author Martin
 */
public class DataLayer {
    private LanguageManager languageManager;
    private PreferencesManager preferencesManager;
    
    public DataLayer(){
        languageManager = new LanguageManager();
        preferencesManager = new PreferencesManager();
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
    
    public PreferencesManager getPreferencesManager(){
        return preferencesManager;
    }

}
