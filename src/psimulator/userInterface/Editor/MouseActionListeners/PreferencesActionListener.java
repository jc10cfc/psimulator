package psimulator.userInterface.Editor.MouseActionListeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SettingsDialog;

/**
 * Action Listener for Preferences Button
 * @author Martin
 */
public class PreferencesActionListener implements ActionListener {

    private SettingsDialog dialog;
    private Component parentComponent;
    private DataLayerFacade dataLayer;

    public PreferencesActionListener(Component parentComponent, DataLayerFacade dataLayer) {
        super();
        
        this.parentComponent = parentComponent;
        this.dataLayer = dataLayer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog = new SettingsDialog(parentComponent, dataLayer, new OkButtonListener(), new CancelButtonListener());
        dialog.setVisible(true);
    }

    class OkButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // get index of selected language
            int index = dialog.getSelectedLanguagePosition();

            // set current language to language at index
            dataLayer.setCurrentLanguage(index);
            
            // get icon size selected and set it to preferences manager
            dataLayer.setToolbarIconSize(dialog.getSelectedToolbarIconSize());
            
            closeDialog();
        }
    }

    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            closeDialog();
        }
    }

    protected void closeDialog() {
        dialog.setVisible(false);
        dialog.dispose();    //closes the window
        dialog = null;
    }
}
