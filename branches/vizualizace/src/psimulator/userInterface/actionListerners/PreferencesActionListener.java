package psimulator.userInterface.actionListerners;

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

    private Component parentComponent;
    private DataLayerFacade dataLayer;

    public PreferencesActionListener(Component parentComponent, DataLayerFacade dataLayer) {
        super();
        
        this.parentComponent = parentComponent;
        this.dataLayer = dataLayer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SettingsDialog dialog = new SettingsDialog(parentComponent, dataLayer);
    }
}
