package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.EthInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.Validator;

/**
 *
 * @author Martin
 */
public class HwComponentProperties extends JDialog {

    private DataLayerFacade dataLayer;
    private AbstractHwComponent abstractHwComponent;
    private JDialog hwComponentsProperties;
    /*
     * window componenets
     */
    private JButton jButtonOk;
    private JButton jButtonCancel;
    private JFormattedTextField jTextFieldDeviceName;
    private JComboBox jComboBoxInterface;
    private JLabel jLabelInterfaceNameValue;
    private JLabel jLabelConnectedValue;
    private JLabel jLabelConnectedToValue;
    private JFormattedTextField jTextFieldIpAddress;
    private JFormattedTextField jTextFieldMacAddress;
    /*
     * END of window components
     */
    private Font fontBold;
    private boolean showAddresses = true;
    private boolean showInterfaces = true;
    private boolean viewUniqueId = true;
    //
    private String deviceName;
    private HashMap<String, String> ipMap;
    private HashMap<String, String> macMap;
    // 

    public HwComponentProperties(Component mainWindow, DataLayerFacade dataLayer, AbstractHwComponent abstractHwComponent) {
        this.dataLayer = dataLayer;
        this.abstractHwComponent = abstractHwComponent;

        // copy values to local
        saveValuesLocally();

        this.setTitle(abstractHwComponent.getDeviceName());

        // set of JDialog parameters
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        this.setMinimumSize(new Dimension(250, 100));
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Component parentComponent = (Component) mainWindow;

        // place in middle of parent window
        int y = parentComponent.getY() + (parentComponent.getHeight() / 2) - (this.getHeight() / 2);
        int x = parentComponent.getX() + (parentComponent.getWidth() / 2) - (this.getWidth() / 2);
        this.setLocation(x, y);
        // end of set of JDialog parameters

        initialize();
    }

    private void initialize() {
        switch (abstractHwComponent.getHwComponentType()) {
            case END_DEVICE_NOTEBOOK:
            case END_DEVICE_WORKSTATION:
            case END_DEVICE_PC:
            case LINUX_ROUTER:
            case CISCO_ROUTER:
                showAddresses = true;
                showInterfaces = true;
                break;
            case LINUX_SWITCH:
            case CISCO_SWITCH:
                showAddresses = false;
                showInterfaces = true;
                break;
            case REAL_PC:
                showAddresses = false;
                showInterfaces = false;
                break;
            default:
                System.err.println("HwComponentProperties error1");
                break;
        }

        // add Content
        this.getContentPane().add(createMainPanel());

        // update
        if (showInterfaces) {
            upadteInterfaceRelatedItems();
        }

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //saveFromFieldsLocally();
                //checkUserCloseWithoutSaving();

                /*
                saveFromFieldsLocally();
                if (changesMade()) {
                    checkUserAndSave();
                }*/

                closeAction();

            }
        });

        hwComponentsProperties = this;

        this.pack();
        this.setVisible(true);
    }

    /**
     * Copies values that can be modified to local variables.
     */
    private void saveValuesLocally() {
        // save name
        deviceName = abstractHwComponent.getDeviceName();

        // if interfaces has addreses than save addreses
        if (showAddresses) {
            ipMap = new HashMap<String, String>();
            macMap = new HashMap<String, String>();

            for (EthInterface ethInterface : abstractHwComponent.getInterfaces()) {
                ipMap.put(ethInterface.getName(), ethInterface.getIpAddress());
                macMap.put(ethInterface.getName(), ethInterface.getMacAddress());
            }

        }
    }

    /**
     * Propagates changes in name and eth interfaces addresses to the component.
     */
    private void saveChangesGlobally() {
        // save name
        abstractHwComponent.setDeviceName(deviceName);

        // if interfaces has addreses than save addreses
        if (showAddresses) {
            for (EthInterface ethInterface : abstractHwComponent.getInterfaces()) {
                ethInterface.setIpAddress(ipMap.get(ethInterface.getName()));
                ethInterface.setMacAddress(macMap.get(ethInterface.getName()));
            }
        }
    }

    /**
     * Saves device Name, IP and MAC addresses from text fields to local maps
     */
    private void saveFromFieldsLocally() {
        deviceName = jTextFieldDeviceName.getText();

        if (showAddresses) {
            String ethName = jLabelInterfaceNameValue.getText();

            String ipAddress = jTextFieldIpAddress.getText();
            if (!Validator.validateIpAddress(ipAddress)) {
                ipAddress = "";
                jTextFieldIpAddress.setText(ipAddress);
            }

            String macAddress = jTextFieldMacAddress.getText();
            if (!Validator.validateMacAddress(macAddress)) {
                macAddress = "";
                jTextFieldMacAddress.setText(macAddress);
            }

            if (macAddress.equals("  -  -  -  -  -  ")) {
                macAddress = "";
                jTextFieldMacAddress.setText(macAddress);
            }

            ipMap.put(ethName, ipAddress);
            macMap.put(ethName, macAddress);

            System.out.println("Eth name = " + ethName + ", IP=" + ipAddress + ", MAC=" + macAddress + ".");
        }
    }

    /**
     * Finds out if any changes have been made
     *
     * @return True if changes made, false if not.
     */
    private boolean changesMade() {
        if (!deviceName.equals(abstractHwComponent.getDeviceName())) {
            return true;
        }

        if (showAddresses) {
            for (EthInterface ethInterface : abstractHwComponent.getInterfaces()) {
                if (!ethInterface.getIpAddress().equals(ipMap.get(ethInterface.getName()))) {
                    return true;
                }
                if (!ethInterface.getMacAddress().equals(macMap.get(ethInterface.getName()))) {
                    return true;
                }
            }
        }

        return false;
    }

    private void upadteInterfaceRelatedItems() {
        // get selected row
        int index = jComboBoxInterface.getSelectedIndex();

        EthInterface ethInterface = abstractHwComponent.getEthInterface(index);

        // set name
        jLabelInterfaceNameValue.setText(ethInterface.getName());
        // set connected state
        if (ethInterface.hasCable()) {
            jLabelConnectedValue.setText(dataLayer.getString("YES"));
        } else {
            jLabelConnectedValue.setText(dataLayer.getString("NO"));
        }

        // if is connected
        if (ethInterface.hasCable()) {
            // set name of device connected to
            // if component1 of cable is different from abstractHwComponent
            if (ethInterface.getCable().getComponent1().getId().intValue() != abstractHwComponent.getId().intValue()) {
                // set name from component1
                jLabelConnectedToValue.setText(ethInterface.getCable().getComponent1().getDeviceName());
            } else {
                // set name from component2
                jLabelConnectedToValue.setText(ethInterface.getCable().getComponent2().getDeviceName());
            }
        } else { // set empty
            jLabelConnectedToValue.setText("");
        }

        if (showAddresses) {
            // set IP address
            jTextFieldIpAddress.setText(ipMap.get(ethInterface.getName()));
            // set mac address
            jTextFieldMacAddress.setText(macMap.get(ethInterface.getName()));
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createDevicePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        if (showInterfaces) {
            mainPanel.add(createInterfacesPanel());
        } else {
            mainPanel.add(createRealPcPanel());
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(createOkCancelPanel());

        return mainPanel;
    }

    private JPanel createDevicePanel() {
        JPanel devicePanel = new JPanel();
        devicePanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("DEVICE")));

        GridLayout devicePanelLayout = new GridLayout(0, 2);
        devicePanelLayout.setHgap(10);
        devicePanel.setLayout(devicePanelLayout);

        //
        JLabel jLabelName = new JLabel(dataLayer.getString("NAME") + " (0-15):");
        fontBold = new Font(jLabelName.getFont().getName(), Font.BOLD, jLabelName.getFont().getSize());
        jLabelName.setFont(fontBold);
        devicePanel.add(jLabelName);

        RegexFormatter deviceNameFormatter = new RegexFormatter(Validator.NAME_PATTERN);
        deviceNameFormatter.setAllowsInvalid(false);        // allow to enter invalid value for short time
        deviceNameFormatter.setCommitsOnValidEdit(true);    // value is immedeatly published to textField
        deviceNameFormatter.setOverwriteMode(false);        // do not overwrite charracters


        jTextFieldDeviceName = new JFormattedTextField(deviceNameFormatter);
        jTextFieldDeviceName.setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " 0-15 " + dataLayer.getString("CHARACTERS"));
        jTextFieldDeviceName.setText(abstractHwComponent.getDeviceName());

        devicePanel.add(jTextFieldDeviceName);
        //
        JLabel typeName = new JLabel(dataLayer.getString("TYPE") + ":");
        typeName.setFont(fontBold);
        devicePanel.add(typeName);

        JLabel typeValue = new JLabel(abstractHwComponent.getHwComponentType().toString());
        devicePanel.add(typeValue);
        //
        JLabel interfaceCountName = new JLabel(dataLayer.getString("INTERFACE_COUNT"));
        interfaceCountName.setFont(fontBold);
        devicePanel.add(interfaceCountName);

        JLabel interfaceCountValue = new JLabel("" + abstractHwComponent.getInterfaceCount());
        devicePanel.add(interfaceCountValue);
        //
        if (viewUniqueId) {
            JLabel deviceIdName = new JLabel(dataLayer.getString("DEVICE_UNIQUE_ID"));
            deviceIdName.setFont(fontBold);
            devicePanel.add(deviceIdName);

            JLabel deviceIdValue = new JLabel("" + abstractHwComponent.getId().toString());
            devicePanel.add(deviceIdValue);
        }

        return devicePanel;
    }

    private JPanel createInterfacesPanel() {
        JPanel interfacesPanel = new JPanel();
        interfacesPanel.setLayout(new BoxLayout(interfacesPanel, BoxLayout.Y_AXIS));
        interfacesPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("INTERFACES")));


        JPanel interfacesAboutPanel = new JPanel();
        GridLayout interfacesPanelLayout = new GridLayout(0, 2);
        interfacesPanelLayout.setHgap(10);
        //
        interfacesAboutPanel.setLayout(interfacesPanelLayout);
        //
        JLabel chooseInterfaceName = new JLabel(dataLayer.getString("CHOOSE_INTERFACE"));
        chooseInterfaceName.setFont(fontBold);
        interfacesAboutPanel.add(chooseInterfaceName);

        jComboBoxInterface = new JComboBox(abstractHwComponent.getInterfacesNames());
        jComboBoxInterface.addActionListener(new JComboBoxInterfaceListener());
        interfacesAboutPanel.add(jComboBoxInterface);
        //
        JLabel interfaceName = new JLabel(dataLayer.getString("INTERFACE"));
        interfaceName.setFont(fontBold);
        interfacesAboutPanel.add(interfaceName);

        jLabelInterfaceNameValue = new JLabel();
        interfacesAboutPanel.add(jLabelInterfaceNameValue);
        //
        JLabel connectedName = new JLabel(dataLayer.getString("CONNECTED"));
        connectedName.setFont(fontBold);
        interfacesAboutPanel.add(connectedName);

        jLabelConnectedValue = new JLabel();
        interfacesAboutPanel.add(jLabelConnectedValue);
        //
        JLabel connectedToName = new JLabel(dataLayer.getString("CONNECTED_TO"));
        connectedToName.setFont(fontBold);
        interfacesAboutPanel.add(connectedToName);

        jLabelConnectedToValue = new JLabel();
        interfacesAboutPanel.add(jLabelConnectedToValue);
        //
        interfacesPanel.add(interfacesAboutPanel);



        // if show addresses set to true
        if (showAddresses) {
            JPanel addressesPanel = new JPanel();
            GridLayout addressesPanelLayout = new GridLayout(0, 3);
            addressesPanelLayout.setHgap(10);
            addressesPanel.setLayout(addressesPanelLayout);

            JLabel ipAddressName = new JLabel(dataLayer.getString("IP_ADDRESS"));
            ipAddressName.setFont(fontBold);
            addressesPanel.add(ipAddressName);

            RegexFormatter ipMaskFormatter = new RegexFormatter(Validator.IP_WITH_MASK_PATTERN);
            ipMaskFormatter.setAllowsInvalid(true);         // allow to enter invalid value for short time
            ipMaskFormatter.setCommitsOnValidEdit(true);    // value is immedeatly published to textField
            ipMaskFormatter.setOverwriteMode(false);        // do notoverwrite charracters

            jTextFieldIpAddress = new JFormattedTextField(ipMaskFormatter);
            jTextFieldIpAddress.setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " 192.168.1.1/24 (IP/mask)");
            addressesPanel.add(jTextFieldIpAddress);

            JLabel ipAddressTip = new JLabel("10.0.0.1/24 (IP/mask)");
            addressesPanel.add(ipAddressTip);

            //
            JLabel macAddressName = new JLabel(dataLayer.getString("MAC_ADDRESS"));
            macAddressName.setFont(fontBold);
            addressesPanel.add(macAddressName);

            try {
                MaskFormatter macMask = new MaskFormatter("HH-HH-HH-HH-HH-HH"); // mask for MAC address
                macMask.setAllowsInvalid(false);         // allow to enter invalid value for short time
                macMask.setCommitsOnValidEdit(true);    // value is immedeatly published to textField
                macMask.setOverwriteMode(true);         // do overwrite charracters

                jTextFieldMacAddress = new JFormattedTextField(macMask);
                jTextFieldMacAddress.setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " HH-HH-HH-HH-HH-HH (H = hexadecimal n.)");
                addressesPanel.add(jTextFieldMacAddress);
            } catch (ParseException ex) {
                //should never happen
            }

            JLabel macAddressTip = new JLabel("HH-HH-HH-HH-HH-HH");
            addressesPanel.add(macAddressTip);

            //
            interfacesPanel.add(addressesPanel);
        }


        return interfacesPanel;
    }

    private JPanel createRealPcPanel() {
        JPanel realPcPanel = new JPanel();
        realPcPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("REAL_PC")));

        JLabel realPcLabel = new JLabel(dataLayer.getString("THIS_IS_REAL_PC"));
        realPcLabel.setFont(fontBold);

        realPcPanel.add(realPcLabel);

        return realPcPanel;
    }

    private JPanel createOkCancelPanel() {
        JPanel buttonPane = new JPanel();

        jButtonOk = new JButton(dataLayer.getString("OK"));
        jButtonOk.addActionListener(new JButtonOkListener());

        jButtonCancel = new JButton(dataLayer.getString("CANCEL"));
        jButtonCancel.addActionListener(new JButtonCancelListener());

        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(jButtonOk);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(jButtonCancel);

        return buttonPane;
    }

    private void closeAction() {
        boolean close = true;

        saveFromFieldsLocally();
        if (changesMade()) {
            if (checkUserAndSave() == false) {
                close = false;
            }
        }

        if (close) {
            hwComponentsProperties.setVisible(false);
            hwComponentsProperties.dispose();    //closes the window
        }
    }

    /**
     * Checks user if he wants to save changes and saves it.
     */
    private boolean checkUserAndSave() {
        // want to save?
        int i = showWarningSave(dataLayer.getString("WARNING"), dataLayer.getString("DO_YOU_WANT_TO_SAVE_CHANGES"));

        // if YES
        if (i == 0) {
            saveChangesGlobally();
        }

        if (i == -1) {
            return false;
        } else {
            return true;
        }
    }

    private int showWarningSave(String title, String message) {
        Object[] options = {dataLayer.getString("SAVE"), dataLayer.getString("DONT_SAVE")};
        int n = JOptionPane.showOptionDialog(this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        return n;
    }

    private int showWarningClose(String title, String message) {
        Object[] options = {dataLayer.getString("YES"), dataLayer.getString("NO")};
        int n = JOptionPane.showOptionDialog(this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        return n;
    }

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for JComboBoxInterface
     */
    class JButtonOkListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFromFieldsLocally();
            if (changesMade()) {
                saveChangesGlobally();
            }
            hwComponentsProperties.setVisible(false);
            hwComponentsProperties.dispose();    //closes the window
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

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for JComboBoxInterface
     */
    class JComboBoxInterfaceListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFromFieldsLocally();
            upadteInterfaceRelatedItems();
        }
    }
}
