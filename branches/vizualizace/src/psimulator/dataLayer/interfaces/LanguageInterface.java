package psimulator.dataLayer.interfaces;

import java.util.Observer;

/**
 *
 * @author Martin
 */
public interface LanguageInterface {
    public void setCurrentLanguage(int languagePosition);
    public Object[] getAvaiableLanguageNames();
    public int getCurrentLanguagePosition();
    public String getString(String string);
    
    public void addLanguageObserver(Observer observer);
    public void deleteLanguageObserver(Observer observer);
}
