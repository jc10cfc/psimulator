package psimulator.userInterface.SimulatorEditor.DrawPanel.SwingComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Support.Validator;

/**
 *
 * @author Martin
 */
public class CableProperties extends JDialog {

    private DataLayerFacade dataLayer;
    private Cable cable;
    private JDialog cableProperties;
    /*
     * window componenets
     */
    private JButton jButtonOk;
    private JButton jButtonCancel;
    private JFormattedTextField jTextFieldDelay;
    /*
     * END of window components
     */
    private Font fontBold;
    private boolean viewUniqueId = true;
    //
    private int delay;

    public CableProperties(Component mainWindow, DataLayerFacade dataLayer, Cable cable) {
        this.dataLayer = dataLayer;
        this.cable = cable;

        // copy values to local
        saveValuesLocally();

        this.setTitle(dataLayer.getString("CABLE_PROPERTIES"));

        // set of JDialog parameters
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        this.setMinimumSize(new Dimension(300, 100));
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
        this.cableProperties = this;

        // add Content
        this.getContentPane().add(createMainPanel());

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closeAction();
            }
        });

        cableProperties = this;

        this.pack();
        this.setVisible(true);
    }

    private void saveValuesLocally() {
        // save delay
        this.delay = cable.getDelay();
    }

    private void saveChangesGlobally() {
        // save delay
        cable.setDelay(delay);
    }

    private void saveFromFieldsLocally() {
        // get delay from text field
        try {
            this.delay = Integer.parseInt(jTextFieldDelay.getText());
        } catch (NumberFormatException ex) {
            
        }
    }

    private boolean changesMade() {
        if (this.delay != cable.getDelay()) {
            return true;
        }

        return false;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createInfoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(createParametersPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        mainPanel.add(createOkCancelPanel());

        return mainPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("INFO")));

        GridLayout infoPanelLayout = new GridLayout(0, 2);
        infoPanelLayout.setHgap(10);
        infoPanelLayout.setVgap(5);
        infoPanel.setLayout(infoPanelLayout);
        //
        // -- TYPE -------------------------------------------------------------
        JLabel typeName = new JLabel(dataLayer.getString("TYPE") + ":");
        fontBold = new Font(typeName.getFont().getName(), Font.BOLD, typeName.getFont().getSize());
        typeName.setFont(fontBold);
        infoPanel.add(typeName);

        JLabel typeValue = new JLabel(cable.getHwType().toString());
        infoPanel.add(typeValue);
        //
        // -- DEVICE 1 name and interface --------------------------------------
        JLabel device1Name = new JLabel(dataLayer.getString("COMPONENT") + " 1:");
        device1Name.setFont(fontBold);
        infoPanel.add(device1Name);

        JLabel device1Value = new JLabel(cable.getComponent1().getDeviceName());
        infoPanel.add(device1Value);
        //
        JLabel interface1Name = new JLabel(dataLayer.getString("INTERFACE") + " 1:");
        interface1Name.setFont(fontBold);
        infoPanel.add(interface1Name);

        JLabel interface1Value = new JLabel(cable.getEth1().getName());
        infoPanel.add(interface1Value);
        //
        // -- DEVICE 2 name and interface --------------------------------------
        JLabel device2Name = new JLabel(dataLayer.getString("COMPONENT") + " 2:");
        device2Name.setFont(fontBold);
        infoPanel.add(device2Name);

        JLabel device2Value = new JLabel(cable.getComponent2().getDeviceName());
        infoPanel.add(device2Value);
        //
        JLabel interface2Name = new JLabel(dataLayer.getString("INTERFACE") + " 2:");
        interface2Name.setFont(fontBold);
        infoPanel.add(interface2Name);

        JLabel interface2Value = new JLabel(cable.getEth2().getName());
        infoPanel.add(interface2Value);
        //
        // -- UNIQUE ID --------------------------------------------------------
        if (viewUniqueId) {
            JLabel deviceIdName = new JLabel(dataLayer.getString("DEVICE_UNIQUE_ID") + ":");
            deviceIdName.setFont(fontBold);
            infoPanel.add(deviceIdName);

            JLabel deviceIdValue = new JLabel("" + cable.getId().toString());
            infoPanel.add(deviceIdValue);
        }

        return infoPanel;
    }

    private JPanel createParametersPanel() {
        JPanel parametersPanel = new JPanel();
        parametersPanel.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("PARAMETERS")));

        GridLayout parametersPanelLayout = new GridLayout(0, 3);
        parametersPanelLayout.setHgap(10);
        parametersPanelLayout.setVgap(5);
        parametersPanel.setLayout(parametersPanelLayout);
        //
        // -- DELAY ------------------------------------------------------------
        JLabel delayName = new JLabel(dataLayer.getString("DELAY") + ":");
        delayName.setFont(fontBold);
        parametersPanel.add(delayName);

        // create formatter
        RegexFormatter delayFormatter = new RegexFormatter(Validator.DELAY_PATTERN);
        delayFormatter.setAllowsInvalid(true);        // allow to enter invalid value for short time
        delayFormatter.setCommitsOnValidEdit(true);    // value is immedeatly published to textField
        delayFormatter.setOverwriteMode(false);        // do not overwrite charracters
        
        jTextFieldDelay = new JFormattedTextField(delayFormatter);
        jTextFieldDelay.setText(""+cable.getDelay());
        jTextFieldDelay.setToolTipText(dataLayer.getString("REQUIRED_FORMAT_IS") + " 0-99999");
        parametersPanel.add(jTextFieldDelay);

        JLabel delayTip = new JLabel("0-99999");
        parametersPanel.add(delayTip);
        
        // --  ------------------------------------------------------------
        return parametersPanel;
    }

    private JPanel createOkCancelPanel() {
        JPanel buttonPane = new JPanel();

        jButtonOk = new JButton(dataLayer.getString("SAVE"));
        jButtonOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                saveFromFieldsLocally();
                if (changesMade()) {
                    saveChangesGlobally();
                }
                cableProperties.setVisible(false);
                cableProperties.dispose();    //closes the window
            }
        });

        jButtonCancel = new JButton(dataLayer.getString("CANCEL"));
        jButtonCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                closeAction();
            }
        });

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
            this.setVisible(false);
            this.dispose();    //closes the window
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
}
