package psimulator.userInterface.ActionListeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import psimulator.logicLayer.Controller;
import psimulator.userInterface.SettingsDialog;

/**
 * Action Listener for Preferences Button
 * @author Martin
 */
public class PreferencesActionListener implements ActionListener {

    private SettingsDialog dialog;
    private Component parentComponent;
    private Controller controller;

    public PreferencesActionListener(Component parentComponent, Controller controller) {
        super();
        
        this.parentComponent = parentComponent;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog = new SettingsDialog(parentComponent, controller.getLanguageManager(), new OkButtonListener(), new CancelButtonListener());
        dialog.setVisible(true);
    }

    class OkButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // get index of selected language
            int index = dialog.getSelectedLanguagePosition();

            // set current language to language at index
            controller.getLanguageManager().setCurrentLanguage(index);

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
