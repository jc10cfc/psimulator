package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatter;
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

    /*
     * window componenets
     */
    private JButton jButtonOk;
    private JButton jButtonCancel;
    private JTextField jTextFieldDeviceName;
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
    private boolean showAddresses;
    private boolean showInterfaces;
    private boolean viewUniqueId = true;

    public HwComponentProperties(Component mainWindow, DataLayerFacade dataLayer, AbstractHwComponent abstractHwComponent) {
        this.dataLayer = dataLayer;
        this.abstractHwComponent = abstractHwComponent;

        this.setTitle(abstractHwComponent.getDeviceName());

        // set of JDialog parameters
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        this.setMinimumSize(new Dimension(250, 100));

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

        this.pack();
        this.setVisible(true);
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
            jTextFieldIpAddress.setText(ethInterface.getIpAddress());
            // set mac address
            jTextFieldMacAddress.setText(ethInterface.getMacAddress());
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
        JLabel jLabelName = new JLabel(dataLayer.getString("NAME") + ":");
        fontBold = new Font(jLabelName.getFont().getName(), Font.BOLD, jLabelName.getFont().getSize());
        jLabelName.setFont(fontBold);
        devicePanel.add(jLabelName);

        jTextFieldDeviceName = new JTextField(abstractHwComponent.getDeviceName());
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

        interfacesPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("INTERFACES")));

        GridLayout interfacesPanelLayout = new GridLayout(0, 2);
        interfacesPanelLayout.setHgap(10);
        //
        interfacesPanel.setLayout(interfacesPanelLayout);
        //
        JLabel chooseInterfaceName = new JLabel(dataLayer.getString("CHOOSE_INTERFACE"));
        chooseInterfaceName.setFont(fontBold);
        interfacesPanel.add(chooseInterfaceName);

        jComboBoxInterface = new JComboBox(abstractHwComponent.getInterfacesNames());
        jComboBoxInterface.addActionListener(new JComboBoxInterfaceListener());
        interfacesPanel.add(jComboBoxInterface);
        //
        JLabel interfaceName = new JLabel(dataLayer.getString("INTERFACE"));
        interfaceName.setFont(fontBold);
        interfacesPanel.add(interfaceName);

        jLabelInterfaceNameValue = new JLabel();
        interfacesPanel.add(jLabelInterfaceNameValue);
        //
        JLabel connectedName = new JLabel(dataLayer.getString("CONNECTED"));
        connectedName.setFont(fontBold);
        interfacesPanel.add(connectedName);

        jLabelConnectedValue = new JLabel();
        interfacesPanel.add(jLabelConnectedValue);
        //
        JLabel connectedToName = new JLabel(dataLayer.getString("CONNECTED_TO"));
        connectedToName.setFont(fontBold);
        interfacesPanel.add(connectedToName);

        jLabelConnectedToValue = new JLabel();
        interfacesPanel.add(jLabelConnectedToValue);


        // if show addresses set to true
        if (showAddresses) {
            JLabel ipAddressName = new JLabel(dataLayer.getString("IP_ADDRESS"));
            ipAddressName.setFont(fontBold);
            interfacesPanel.add(ipAddressName);

            RegexFormatter ipMaskFormatter = new RegexFormatter(Validator.IP_WITH_MASK_PATTERN);
            jTextFieldIpAddress = new JFormattedTextField(ipMaskFormatter);
            interfacesPanel.add(jTextFieldIpAddress);
            //
            JLabel macAddressName = new JLabel(dataLayer.getString("MAC_ADDRESS"));
            macAddressName.setFont(fontBold);
            interfacesPanel.add(macAddressName);

            try {
                MaskFormatter macMask = new MaskFormatter("HH-HH-HH-HH-HH-HH"); // mask for MAC address
                jTextFieldMacAddress = new JFormattedTextField(macMask);
                interfacesPanel.add(jTextFieldMacAddress);
            } catch (ParseException ex) {
                //should never happen
            }

            //
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
        jButtonCancel = new JButton(dataLayer.getString("CANCEL"));

        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(jButtonOk);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(jButtonCancel);

        return buttonPane;
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
            upadteInterfaceRelatedItems();
        }
    }
}
