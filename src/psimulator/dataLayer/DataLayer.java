package psimulator.dataLayer;

import psimulator.dataLayer.language.LanguageManager;

/**
 *
 * @author Martin
 */
public class DataLayer {
    private LanguageManager languageManager;
    
    public DataLayer(){
        languageManager = new LanguageManager();
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

}
