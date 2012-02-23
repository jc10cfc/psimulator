package psimulator.userInterface.SimulatorEditor.SimulatorControllPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Dialogs.AbstractPropertiesDialog;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.Validator;
import psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents.RegexFormatter;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public final class ConnectToServerDialog extends AbstractPropertiesDialog {

    private Font boldFont;
    //
    private JButton jButtonConnectToServer;
    private JButton jButtonCancel;
    //
    private JFormattedTextField jTextFieldPsimulatorIpAddress;
    private JFormattedTextField jTextFieldPsimulatorPort;
    //
    private String connectionIpAddress;
    private String connectionPort;
    
    public ConnectToServerDialog(Component mainWindow, DataLayerFacade dataLayer) {
        super(mainWindow, dataLayer);

        // set title
        this.setTitle(dataLayer.getString("CONNECT_TO_SERVER"));

        // set minimum size
        this.setMinimumSize(new Dimension(150, 150));

        // initialize
        initialize();
        
        // set visible
        this.setVisible(true);
    }
    
    @Override
    protected void copyValuesFromGlobalToLocal() {
        connectionIpAddress = dataLayer.getConnectionIpAddress();
        connectionPort = dataLayer.getConnectionPort();
    }

    @Override
    protected void copyValuesFromFieldsToLocal() {
        connectionIpAddress = jTextFieldPsimulatorIpAddress.getText();
        connectionPort = jTextFieldPsimulatorPort.getText();
    }

    @Override
    protected void copyValuesFromLocalToGlobal() {
        dataLayer.setConnectionIpAddress(connectionIpAddress);
        dataLayer.setConnectionPort(connectionPort);
        
        // save preferences 
        dataLayer.savePreferences();
    }

    @Override
    protected boolean hasChangesMade() {
        if(!connectionIpAddress.equals(dataLayer.getConnectionIpAddress())){
            return true;
        }
        
        if(!connectionPort.equals(dataLayer.getConnectionPort())){
            return true;
        }

        return false;
    }
    
    
    @Override
    protected void validateInputs() {
        //jTextFieldPsimulatorIpAddress.set
        //jTextFieldPsimulatorPort;
    }

    
    @Override
    protected void setDefaultJButton() {
        jButtonDefault = jButtonConnectToServer;
    }
    
    @Override
    protected JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createContentPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(createConnectCancelPanel());

        return mainPanel;
    }

    @Override
    protected JPanel createContentPanel() {
        JPanel addressesPanel = new JPanel();
        addressesPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("CONNECTION_PROPERTIES")));
        // set layout
        GridLayout addressesPanelLayout = new GridLayout(0, 3);
        addressesPanelLayout.setHgap(10);
        addressesPanel.setLayout(addressesPanelLayout);

        // IP address
        JLabel ipAddressName = new JLabel(dataLayer.getString("IP_ADDRESS"));
        boldFont = new Font(ipAddressName.getFont().getName(), Font.BOLD, ipAddressName.getFont().getSize());
        ipAddressName.setFont(boldFont);
        addressesPanel.add(ipAddressName);

        RegexFormatter ipMaskFormatter = new RegexFormatter(Validator.IP_PATTERN);
        ipMaskFormatter.setAllowsInvalid(true);         // allow to enter invalid value for short time
        ipMaskFormatter.setCommitsOnValidEdit(true);    // value is immedeatly published to textField
        ipMaskFormatter.setOverwriteMode(false);        // do notoverwrite charracters

        jTextFieldPsimulatorIpAddress = new JFormattedTextField(ipMaskFormatter);
        jTextFieldPsimulatorIpAddress.setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " 192.168.1.1 (IP)");
        jTextFieldPsimulatorIpAddress.setText(connectionIpAddress);
        // add decorator that paints wrong input icon
        addressesPanel.add(new JLayer<JFormattedTextField>(jTextFieldPsimulatorIpAddress, layerUI));

        JLabel ipAddressTip = new JLabel("10.0.0.1 (IP)");
        addressesPanel.add(ipAddressTip);
        
        // PORT
        JLabel portName = new JLabel(dataLayer.getString("PORT"));
        portName.setFont(boldFont);
        addressesPanel.add(portName);
        
        RegexFormatter portFormatter = new RegexFormatter(Validator.PORT_PATTERN);
        portFormatter.setAllowsInvalid(true);         // allow to enter invalid value for short time
        portFormatter.setCommitsOnValidEdit(true);    // value is immedeatly published to textField
        portFormatter.setOverwriteMode(false);        // do notoverwrite charracters
        
        jTextFieldPsimulatorPort = new JFormattedTextField(portFormatter);
        jTextFieldPsimulatorPort.setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " 1-49 999");
        jTextFieldPsimulatorPort.setText(connectionPort);
        addressesPanel.add(new JLayer<JFormattedTextField>(jTextFieldPsimulatorPort, layerUI));
        
        JLabel portTip = new JLabel("1-49 999");
        addressesPanel.add(portTip);
        
        return addressesPanel;
    }
    
    private JPanel createConnectCancelPanel(){
        JPanel buttonPane = new JPanel();

        jButtonConnectToServer = new JButton(dataLayer.getString("CONNECT_TO_SERVER"));
        jButtonConnectToServer.addActionListener(new JButtonConnectToServerListener());

        jButtonCancel = new JButton(dataLayer.getString("CANCEL"));
        jButtonCancel.addActionListener(new AbstractPropertiesDialog.JButtonCancelListener());

        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        buttonPane.add(jButtonConnectToServer);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(jButtonCancel);
        buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));

        return buttonPane;
    }

    //
    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for JComboBoxInterface
     */
    class JButtonConnectToServerListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            copyValuesFromFieldsToLocal();
            if (hasChangesMade()) {
                copyValuesFromLocalToGlobal();
            }
            
            thisDialog.setVisible(false);
            thisDialog.dispose();    //closes the window
        }
    }

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for JComboBoxInterface
     */
    class JButtonCancelListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            closeAction();
        }
    }
}
