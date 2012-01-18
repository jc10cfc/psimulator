package psimulator.userInterface.Simulator;

import java.awt.*;
import java.util.Hashtable;
import javax.swing.*;

/**
 *
 * @author Martin
 */
public class SimulatorControlPanel extends JPanel {

    // Connect / Save / Load panel
    private JPanel jPanelConnectSaveLoad;
    private JPanel jPanelConnectSaveLoadButtons;
    private JButton jButtonSaveListToFile;
    private JButton jButtonLoadListFromFile;
    private JButton jButtonConnectToServer;
    private JPanel jPanelConnectSaveLoadStatus;
    private JLabel jLabelConnectionStatusName;
    private JLabel jLabelConnectionStatusValue;
    // Play controls panel
    private JPanel jPanelPlayControls;
    private JPanel jPanelPlayControlsPlayButtons;
    private JPanel jPanelPlayControlsSlider;
    private JPanel jPanelPlayControlsRecordButtons;
    private JLabel jLabelSpeedName;
    private JSlider jSliderPlayerSpeed;
    private JLabel jLabelSliderSlow;
    private JLabel jLabelSliderMedium;
    private JLabel jLabelSliderFast;
    private JButton jButtonFirst;
    private JButton jButtonLast;
    private JButton jButtonNext;
    private JButton jButtonPrevious;
    private JToggleButton jToggleButtonCapture;
    private JToggleButton jToggleButtonPlay;
    // Event list panel
    private JPanel jPanelEventList;
    private JTable jTableEventList;
    private JScrollPane jScrollPaneTableEventList;
    private JButton jButtonDeleteEvents;
    // Details panel
    private JPanel jPanelDetails;
    private JPanel jPanelLeftColumn;
    private JPanel jPanelRightColumn;
    private JCheckBox jCheckBoxPacketDetails;
    private JCheckBox jCheckBoxNamesOfDevices;
    //
    static final int SPEED_MIN = 10;
    static final int SPEED_MAX = 100;
    static final int SPEED_INIT = 50;

    public SimulatorControlPanel() {
        initComponents();
    }

    private void initComponents() {
        this.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        this.setLayout(new GridBagLayout());
        
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL; // natural height maximum width
        
        cons.gridx = 0;
	cons.gridy = 0;
        this.add(Box.createRigidArea(new Dimension(0, 6)),cons);
        cons.gridx = 0;
	cons.gridy = 1;
        this.add(createConnectSaveLoadPanel(),cons);
        cons.gridx = 0;
	cons.gridy = 2;
        this.add(Box.createRigidArea(new Dimension(0, 6)),cons);
        cons.gridx = 0;
	cons.gridy = 3;
        this.add(createPlayControlsPanel(),cons);
        cons.gridx = 0;
	cons.gridy = 4;
        this.add(Box.createRigidArea(new Dimension(0, 6)),cons);
        cons.gridx = 0;
	cons.gridy = 5;
        this.add(createEventListPanel(), cons);
        cons.gridx = 0;
	cons.gridy = 6;
        this.add(Box.createRigidArea(new Dimension(0, 6)), cons);
        cons.gridx = 0;
	cons.gridy = 7;
        this.add(createDetailsPanel(),cons);
        cons.gridx = 0;
	cons.gridy = 8;
        cons.weighty = 1.0; // if set to 1, the content in panel is in the top, not in the middle
        this.add(Box.createRigidArea(new Dimension(0, 6)),cons);
        
        
        // end Connect / Save / Load panel
        setTextsToComponents();
    }

    private JPanel createConnectSaveLoadPanel() {
        // Connect / Save / Load panel
        jPanelConnectSaveLoad = new JPanel();
        jPanelConnectSaveLoad.setLayout(new BoxLayout(jPanelConnectSaveLoad, BoxLayout.Y_AXIS));
        //
        jPanelConnectSaveLoadButtons = new JPanel();
        jPanelConnectSaveLoadButtons.setLayout(new BoxLayout(jPanelConnectSaveLoadButtons, BoxLayout.X_AXIS));
        //
        jButtonSaveListToFile = new JButton();
        jButtonSaveListToFile.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/filesave.png"))); // NOI18N
        jButtonSaveListToFile.setHorizontalTextPosition(SwingConstants.CENTER);
        //jButtonSaveListToFile.setRequestFocusEnabled(false);
        jButtonSaveListToFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        //
        jButtonLoadListFromFile = new JButton();
        jButtonLoadListFromFile.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/folder_blue_open.png"))); // NOI18N
        jButtonLoadListFromFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLoadListFromFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        //
        jButtonConnectToServer = new JButton();
        jButtonConnectToServer.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/kwifimanager.png"))); // NOI18N
        jButtonConnectToServer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonConnectToServer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        //
        jPanelConnectSaveLoadButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanelConnectSaveLoadButtons.add(jButtonConnectToServer);
        jPanelConnectSaveLoadButtons.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelConnectSaveLoadButtons.add(jButtonLoadListFromFile);
        jPanelConnectSaveLoadButtons.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelConnectSaveLoadButtons.add(jButtonSaveListToFile);
        jPanelConnectSaveLoadButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        //
        jPanelConnectSaveLoadStatus = new JPanel();
        jPanelConnectSaveLoadStatus.setLayout(new BoxLayout(jPanelConnectSaveLoadStatus, BoxLayout.X_AXIS));
        jLabelConnectionStatusName = new JLabel();
        jLabelConnectionStatusValue = new JLabel();
        jLabelConnectionStatusValue.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        jLabelConnectionStatusValue.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_cancel.png"))); // NOI18N
        //
        jPanelConnectSaveLoadStatus.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanelConnectSaveLoadStatus.add(jLabelConnectionStatusName);
        jPanelConnectSaveLoadStatus.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelConnectSaveLoadStatus.add(jLabelConnectionStatusValue);
        //
        jPanelConnectSaveLoad.add(jPanelConnectSaveLoadButtons);
        jPanelConnectSaveLoad.add(Box.createRigidArea(new Dimension(0, 7)));
        jPanelConnectSaveLoad.add(jPanelConnectSaveLoadStatus);

        //
        return jPanelConnectSaveLoad;
    }

    private JPanel createPlayControlsPanel() {
        jPanelPlayControls = new JPanel();
        jPanelPlayControls.setLayout(new BoxLayout(jPanelPlayControls, BoxLayout.Y_AXIS));
        // Play buttons panel
        jPanelPlayControlsPlayButtons = new JPanel();
        jPanelPlayControlsPlayButtons.setLayout(new BoxLayout(jPanelPlayControlsPlayButtons, BoxLayout.X_AXIS));
        //
        jButtonFirst = new JButton();
        jButtonFirst.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_start.png"))); // NOI18N
        jButtonPrevious = new JButton();
        jButtonPrevious.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_rew.png"))); // NOI18N
        jButtonNext = new JButton();
        jButtonNext.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_fwd.png"))); // NOI18N
        jButtonLast = new JButton();
        jButtonLast.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_next.png"))); // NOI18N
        //
        jToggleButtonPlay = new JToggleButton();
        jToggleButtonPlay.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_play.png"))); // NOI18N
        jToggleButtonPlay.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_pause.png"))); // NOI18N
        //
        jPanelPlayControlsPlayButtons.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelPlayControlsPlayButtons.add(jButtonFirst);
        jPanelPlayControlsPlayButtons.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelPlayControlsPlayButtons.add(jButtonPrevious);
        jPanelPlayControlsPlayButtons.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelPlayControlsPlayButtons.add(jToggleButtonPlay);
        jPanelPlayControlsPlayButtons.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelPlayControlsPlayButtons.add(jButtonNext);
        jPanelPlayControlsPlayButtons.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelPlayControlsPlayButtons.add(jButtonLast);
        jPanelPlayControlsPlayButtons.add(Box.createRigidArea(new Dimension(5, 0)));
        //
        // Slider panel
        jPanelPlayControlsSlider = new JPanel();
        jPanelPlayControlsSlider.setLayout(new BoxLayout(jPanelPlayControlsSlider, BoxLayout.X_AXIS));
        //
        jLabelSpeedName = new JLabel();
        jSliderPlayerSpeed = new JSlider(JSlider.HORIZONTAL, SPEED_MIN, SPEED_MAX, SPEED_INIT);
        jSliderPlayerSpeed.setPaintTicks(true);
        jSliderPlayerSpeed.setMajorTickSpacing(10);
        //
        jLabelSliderSlow = new JLabel();
        jLabelSliderMedium = new JLabel();
        jLabelSliderFast = new JLabel();
        jSliderPlayerSpeed.setPaintLabels(true);
        //
        jPanelPlayControlsSlider.add(jLabelSpeedName);
        jPanelPlayControlsSlider.add(Box.createRigidArea(new Dimension(7, 0)));
        jPanelPlayControlsSlider.add(jSliderPlayerSpeed);
        jPanelPlayControlsSlider.add(Box.createRigidArea(new Dimension(7, 0)));
        //
        // Record panel
        jPanelPlayControlsRecordButtons = new JPanel();
        jPanelPlayControlsRecordButtons.setLayout(new BoxLayout(jPanelPlayControlsRecordButtons, BoxLayout.X_AXIS));
        //
        jToggleButtonCapture = new JToggleButton();
        jToggleButtonCapture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/Record Button.png"))); // NOI18N
        //
        jPanelPlayControlsRecordButtons.add(jToggleButtonCapture);
        //
        //Add to main panel
        jPanelPlayControls.add(jPanelPlayControlsPlayButtons);
        jPanelPlayControls.add(Box.createRigidArea(new Dimension(0, 7)));
        jPanelPlayControls.add(jPanelPlayControlsSlider);
        jPanelPlayControls.add(Box.createRigidArea(new Dimension(0, 7)));
        jPanelPlayControls.add(jPanelPlayControlsRecordButtons);
        //
        
        return jPanelPlayControls;
    }

    private JPanel createEventListPanel(){
        jPanelEventList = new JPanel();
        //jPanelEventList.setLayout(new BoxLayout(jPanelEventList, BoxLayout.Y_AXIS));
        jPanelEventList.setLayout(new BorderLayout());
        //
        jTableEventList = new JTable();
        jTableEventList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Time", "From", "To", "Type", "Other"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEventList.setFillsViewportHeight(true);
        
        jScrollPaneTableEventList = new JScrollPane();
        
        jScrollPaneTableEventList.setViewportView(jTableEventList);
        //
        jButtonDeleteEvents = new JButton();
        jButtonDeleteEvents.setAlignmentX(Component.LEFT_ALIGNMENT);
        //
        jPanelEventList.add(jScrollPaneTableEventList, BorderLayout.CENTER);
        //jPanelEventList.add(jButtonDeleteEvents);
        //
        return jPanelEventList;
    }
    
    private JPanel createDetailsPanel(){
        jPanelDetails = new JPanel();
        jPanelDetails.setLayout(new BoxLayout(jPanelDetails, BoxLayout.LINE_AXIS));
        //
        jPanelLeftColumn = new JPanel();
        jPanelLeftColumn.setLayout(new BoxLayout(jPanelLeftColumn, BoxLayout.PAGE_AXIS));
        //
        jPanelRightColumn = new JPanel();
        jPanelRightColumn.setLayout(new BoxLayout(jPanelRightColumn, BoxLayout.PAGE_AXIS));
        //
        jCheckBoxPacketDetails = new JCheckBox();
        jCheckBoxPacketDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        jCheckBoxNamesOfDevices = new JCheckBox();
        jCheckBoxNamesOfDevices.setAlignmentX(Component.LEFT_ALIGNMENT);
        //
        jPanelLeftColumn.add(jCheckBoxPacketDetails);
        jPanelLeftColumn.add(jCheckBoxNamesOfDevices);
        //  
        jPanelDetails.add(jPanelLeftColumn);
        jPanelDetails.add(jPanelRightColumn);
        //        
        return jPanelDetails;
    }
    
    ////////------------ PRIVATE------------///////////
    private void setTextsToComponents() {
        jPanelConnectSaveLoad.setBorder(BorderFactory.createTitledBorder("Connect / Save / Load"));
        jButtonSaveListToFile.setText("Save list to file");
        jButtonLoadListFromFile.setText("Load list from file");
        jButtonConnectToServer.setText("Connect to server");
        jLabelConnectionStatusName.setText("Connection status:");
        jLabelConnectionStatusValue.setText("Disconnected");
        //
        jPanelPlayControls.setBorder(BorderFactory.createTitledBorder("Play controls"));
        jSliderPlayerSpeed.setToolTipText("Speed controll");
        jLabelSpeedName.setText("Speed:");
        jLabelSliderSlow.setText("Slow");
        jLabelSliderMedium.setText("Medium");
        jLabelSliderFast.setText("Fast");
        //
        jToggleButtonCapture.setText("Capture");
        jToggleButtonCapture.setToolTipText("Capture packets from server");
        //
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(SPEED_MIN), jLabelSliderSlow);
        labelTable.put(new Integer(SPEED_MAX/2), jLabelSliderMedium);
        labelTable.put(new Integer(SPEED_MAX), jLabelSliderFast);
        jSliderPlayerSpeed.setLabelTable(labelTable);
        
        //
        jPanelEventList.setBorder(BorderFactory.createTitledBorder("Event list"));
        jButtonDeleteEvents.setText("Delete events");
        //
        jPanelDetails.setBorder(BorderFactory.createTitledBorder("Details"));
        jCheckBoxPacketDetails.setText("Packet details");
        jCheckBoxNamesOfDevices.setText("Names of devices");
    }
}
