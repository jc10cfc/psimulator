package psimulator.userInterface.SimulatorEditor.SimulatorControllPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
import psimulator.dataLayer.Simulator.SimulatorManager;
import psimulator.dataLayer.SimulatorEvents.SimulatorEvent;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;
import psimulator.userInterface.MainWindowInnerInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorControlPanel extends JPanel implements Observer {

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
    private JToggleButton jToggleButtonRealtime;
    // Event list panel
    private JPanel jPanelEventList;
    private JPanel jPanelEventListTable;
    private JPanel jPanelEventListButtons;
    private JTableEventList jTableEventList;
    private JScrollPane jScrollPaneTableEventList;
    private JButton jButtonDeleteEvents;
    // Packet details panel
    private JPanel jPanelPacketDetails;
    private JLabel jLabelDetailsTimeName;
    private JLabel jLabelDetailsTimeValue;
    private JLabel jLabelDetailsFromName;
    private JLabel jLabelDetailsFromValue;
    private JLabel jLabelDetailsFromInterfaceName;
    private JLabel jLabelDetailsFromInterfaceValue;
    private JLabel jLabelDetailsToName;
    private JLabel jLabelDetailsToValue;
    private JLabel jLabelDetailsToInterfaceName;
    private JLabel jLabelDetailsToInterfaceValue;
    private JLabel jLabelDetailsTypeName;
    private JLabel jLabelDetailsTypeValue;
    private JTextArea jTextAreaPacketDetails;
    //
    private DecimalFormat fmt = new DecimalFormat("0.000");
    //
    //
    private DataLayerFacade dataLayer;
    private MainWindowInnerInterface mainWindow;
    private SimulatorManagerInterface simulatorManagerInterface;
    //
    private ConnectToServerDialog connectToServerDialog;

    public SimulatorControlPanel(MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        this.dataLayer = dataLayer;
        this.simulatorManagerInterface = dataLayer.getSimulatorManager();
        this.mainWindow = mainWindow;

        // create graphic layout with components
        initComponents();

        // add listeners to components
        addListenersToComponents();
    }

    /**
     * Turns of playing, realtime and recording.
     */
    public void setTurnedOff() {
        simulatorManagerInterface.setPlayingStopped();
        simulatorManagerInterface.setRealtimeDeactivated();
        simulatorManagerInterface.setRecordingDeactivated();
        // can stay connected


    }

    public void clearEvents() {
        simulatorManagerInterface.deleteAllSimulatorEvents();
    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case LANGUAGE:
                setTextsToComponents();
                break;
            case CONNECTION_CONNECTING_FAILED:  // ony during connection establishing
                if (connectToServerDialog != null) {
                    connectToServerDialog.connectingFailed();
                }
                break;
            case SIMULATOR_CONNECTED:           // when connection established
                // dispose
                if (connectToServerDialog != null) {
                    connectToServerDialog.connected();
                }
                updateConnectionInfoAccordingToModel();
                break;
            case CONNECTION_CONNECTION_FAILED:  // when connection failed
                updateConnectionInfoAccordingToModel();
                // do inform user
                JOptionPane.showMessageDialog(mainWindow.getMainWindowComponent(),
                        dataLayer.getString("CONNECTION_BROKE_DOWN"),
                        dataLayer.getString("WARNING"),
                        JOptionPane.WARNING_MESSAGE);
                break;
            case SIMULATOR_DISCONNECTED:        // when disconnected by user
                updateConnectionInfoAccordingToModel();
                break;
            case SIMULATOR_RECORDER_ON:
            case SIMULATOR_RECORDER_OFF:
                updateRecordingInfoAccordingToModel();
                break;
            case SIMULATOR_PLAYER_STOP:
                updatePlayingInfoAccordingToModel();
                break;
            case SIMULATOR_REALTIME_ON:
            case SIMULATOR_REALTIME_OFF:
                updateRealtimeAccordingToModel();
            case SIMULATOR_PLAYER_LIST_MOVE:
            case SIMULATOR_PLAYER_NEXT:
            case SIMULATOR_PLAYER_PLAY:
                updatePositionInListAccordingToModel();
                updatePacketDetailsAccordingToModel();
                break;

        }
    }

    ////////------------ PRIVATE------------///////////
    private void addListenersToComponents() {
        jTableEventList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                
                int rowNumber = jTableEventList.getSelectedRow();
                
                // set concrete row in model
                simulatorManagerInterface.setConcreteRawSelected(rowNumber);
            }
        });

        // LOAD button action listener
        jButtonLoadListFromFile.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                // if list not empty as if rewrite
                if(simulatorManagerInterface.hasEvents()){
                    
                    int i = showYesNoDialog(dataLayer.getString("WARNING"), dataLayer.getString("REWRITE_EVENT_LIST_WARNING"));
                    // if do not rewrite
                    if (i != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
        
                // load events
                SimulatorEventsWrapper simulatorEventsWrapper = mainWindow.loadEventsAction();
                
                // set events to simulator manager
                simulatorManagerInterface.setSimulatorEvents(simulatorEventsWrapper);
            }
        });
        
        // SAVE button action listener
        jButtonSaveListToFile.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                
                // get events
                SimulatorEventsWrapper simulatorEventsWrapper = simulatorManagerInterface.getSimulatorEvents();
                
                // save events
                mainWindow.saveEventsAction(simulatorEventsWrapper);
            }
        });
        

        // jSliderPlayerSpeed state change listener
        jSliderPlayerSpeed.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                // set the speed in model
                simulatorManagerInterface.setPlayerSpeed(jSliderPlayerSpeed.getValue());
            }
        });

        //
        jButtonConnectToServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // if connected
                if (simulatorManagerInterface.isConnectedToServer()) {
                    // disconnect
                    simulatorManagerInterface.doDisconnect();
                } else {
                    // open connect dialog
                    connectToServerDialog = new ConnectToServerDialog(mainWindow.getMainWindowComponent(), dataLayer);

                    // set visible
                    connectToServerDialog.setVisible(true);
                }
            }
        });

        //
        jButtonDeleteEvents.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // if table empty
                if (!simulatorManagerInterface.hasEvents()) {
                    showWarningDialog(dataLayer.getString("WARNING"), dataLayer.getString("LIST_IS_EMPTY_WARNING"));
                } else { // if has content
                    int i = showYesNoDialog(dataLayer.getString("WARNING"), dataLayer.getString("DELETING_EVENT_LIST_WARNING"));
                    // if YES
                    if (i == 0) {
                        simulatorManagerInterface.deleteAllSimulatorEvents();

                        // update packet details
                        updatePacketDetailsAccordingToModel();
                    }
                }
            }
        });

        // -------------------- PLAY BUTTONS ACTIONS ---------------------
        jButtonFirst.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorManagerInterface.setPlayerFunctionActivated(SimulatorPlayerCommand.FIRST);
            }
        });

        //
        jButtonLast.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorManagerInterface.setPlayerFunctionActivated(SimulatorPlayerCommand.LAST);
            }
        });

        //
        jButtonNext.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorManagerInterface.setPlayerFunctionActivated(SimulatorPlayerCommand.NEXT);
            }
        });

        //
        jButtonPrevious.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorManagerInterface.setPlayerFunctionActivated(SimulatorPlayerCommand.PREVIOUS);
            }
        });

        //
        jToggleButtonPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jToggleButtonPlay.isSelected()) {
                    simulatorManagerInterface.setPlayingActivated();
                } else {
                    simulatorManagerInterface.setPlayingStopped();
                }
            }
        });

        // -------------------- CAPTURE ACTION ---------------------
        jToggleButtonCapture.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jToggleButtonCapture.isSelected()) {
                    simulatorManagerInterface.setRecordingActivated();
                } else {
                    simulatorManagerInterface.setRecordingDeactivated();
                }
            }
        });

        // -------------------- REALTIME ACTION ---------------------
        jToggleButtonRealtime.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jToggleButtonRealtime.isSelected()) {
                    simulatorManagerInterface.setRealtimeActivated();
                } else {
                    simulatorManagerInterface.setRealtimeDeactivated();
                }
            }
        });
        /*
         * // -------------------- VIEW DETAILS ---------------------
         * jCheckBoxPacketDetails.addActionListener(new ActionListener() {
         *
         * @Override public void actionPerformed(java.awt.event.ActionEvent evt)
         * { if (jCheckBoxPacketDetails.isSelected()) {
         * simulatorManagerInterface.setPacketDetails(true); } else {
         * simulatorManagerInterface.setPacketDetails(false); } } });
         *
         * jCheckBoxNamesOfDevices.addActionListener(new ActionListener() {
         *
         * @Override public void actionPerformed(java.awt.event.ActionEvent evt)
         * { if (jCheckBoxNamesOfDevices.isSelected()) {
         * simulatorManagerInterface.setNamesOfDevices(true); } else {
         * simulatorManagerInterface.setNamesOfDevices(false); } }
        });
         */
    }

    private void initComponents() {
        this.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL; // natural height maximum width

        cons.gridx = 0;
        cons.gridy = 0;
        this.add(Box.createRigidArea(new Dimension(0, 6)), cons);
        cons.gridx = 0;
        cons.gridy = 1;
        this.add(createConnectSaveLoadPanel(), cons);
        cons.gridx = 0;
        cons.gridy = 2;
        this.add(Box.createRigidArea(new Dimension(0, 6)), cons);
        cons.gridx = 0;
        cons.gridy = 3;
        this.add(createPlayControlsPanel(), cons);
        cons.gridx = 0;
        cons.gridy = 4;
        this.add(Box.createRigidArea(new Dimension(0, 6)), cons);
        cons.gridx = 0;
        cons.gridy = 5;
        cons.weighty = 1.0;
        cons.weightx = 1.0;
        cons.fill = GridBagConstraints.BOTH; // both width and height max
        this.add(createEventListPanel(), cons);
        cons.fill = GridBagConstraints.HORIZONTAL; // natural height maximum width
        cons.weighty = 0.0;
        cons.weightx = 0.0;
        cons.gridx = 0;
        cons.gridy = 6;
        this.add(Box.createRigidArea(new Dimension(0, 6)), cons);
        cons.gridx = 0;
        cons.gridy = 7;
        //createDetailsPanel();

        this.add(createPacketDetailsPanel(), cons);
        cons.gridx = 0;
        cons.gridy = 8;
        this.add(Box.createRigidArea(new Dimension(0, 6)), cons);


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
        jToggleButtonPlay.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/player_stop.png"))); // NOI18N
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
        jSliderPlayerSpeed = new JSlider(JSlider.HORIZONTAL, SimulatorManager.SPEED_MIN, SimulatorManager.SPEED_MAX, SimulatorManager.SPEED_INIT);
        jSliderPlayerSpeed.setPaintTicks(true);
        jSliderPlayerSpeed.setMajorTickSpacing(10);
        //
        jLabelSliderSlow = new JLabel();
        jLabelSliderMedium = new JLabel();
        jLabelSliderFast = new JLabel();
        jSliderPlayerSpeed.setPaintLabels(true);
        //
        jPanelPlayControlsSlider.add(Box.createRigidArea(new Dimension(7, 0)));
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
        jToggleButtonCapture.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/record_button.png"))); // NOI18N
        //
        jToggleButtonRealtime = new JToggleButton();
        jToggleButtonRealtime.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/realtime_play.png"))); // NOI18N
        jToggleButtonRealtime.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/realtime_stop.png"))); // NOI18N
        //
        jPanelPlayControlsRecordButtons.add(jToggleButtonCapture);
        jPanelPlayControlsRecordButtons.add(jToggleButtonRealtime);
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

    private JPanel createEventListPanel() {
        jPanelEventList = new JPanel();
        jPanelEventList.setLayout(new BoxLayout(jPanelEventList, BoxLayout.Y_AXIS));

        //// link table with table model
        jTableEventList = new JTableEventList(simulatorManagerInterface.getEventTableModel());
        //
        jPanelEventListTable = new JPanel();
        jScrollPaneTableEventList = new JScrollPane();
        jScrollPaneTableEventList.setViewportView(jTableEventList);             // add table to scroll pane

        GroupLayout jPanelEventListLayout = new GroupLayout(jPanelEventListTable);
        jPanelEventListTable.setLayout(jPanelEventListLayout);
        jPanelEventListLayout.setHorizontalGroup(
                jPanelEventListLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPaneTableEventList, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE));
        jPanelEventListLayout.setVerticalGroup(
                jPanelEventListLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPaneTableEventList, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)//200
                );
        //
        //
        jPanelEventListButtons = new JPanel();
        jPanelEventListButtons.setLayout(new BoxLayout(jPanelEventListButtons, BoxLayout.X_AXIS));

        jButtonDeleteEvents = new JButton();
        jButtonDeleteEvents.setAlignmentX(Component.LEFT_ALIGNMENT);
        jButtonDeleteEvents.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/trashcan_full.png"))); // NOI18N

        //jPanelEventListButtons.add(Box.createRigidArea(new Dimension(0, 7)));
        jPanelEventListButtons.add(jButtonDeleteEvents);
        //
        //
        jPanelEventList.add(jPanelEventListTable);
        jPanelEventList.add(Box.createRigidArea(new Dimension(0, 7)));
        jPanelEventList.add(jPanelEventListButtons);

        return jPanelEventList;
    }

    /*
     * private JPanel createDetailsPanel() { jPanelDetails = new JPanel();
     * jPanelDetails.setLayout(new BoxLayout(jPanelDetails,
     * BoxLayout.LINE_AXIS)); // jPanelLeftColumn = new JPanel();
     * jPanelLeftColumn.setLayout(new BoxLayout(jPanelLeftColumn,
     * BoxLayout.PAGE_AXIS)); // jPanelRightColumn = new JPanel();
     * jPanelRightColumn.setLayout(new BoxLayout(jPanelRightColumn,
     * BoxLayout.PAGE_AXIS)); // jCheckBoxPacketDetails = new JCheckBox();
     * jCheckBoxPacketDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
     * jCheckBoxNamesOfDevices = new JCheckBox();
     * jCheckBoxNamesOfDevices.setAlignmentX(Component.LEFT_ALIGNMENT); //
     * jPanelLeftColumn.add(jCheckBoxPacketDetails);
     * jPanelLeftColumn.add(jCheckBoxNamesOfDevices); //
     * jPanelDetails.add(jPanelLeftColumn);
     * jPanelDetails.add(jPanelRightColumn); // return jPanelDetails;
    }
     */
    private JPanel createPacketDetailsPanel() {
        jPanelPacketDetails = new JPanel();
        jPanelPacketDetails.setLayout(new BoxLayout(jPanelPacketDetails, BoxLayout.PAGE_AXIS));
        //
        JPanel jPanelDetails = new JPanel();
        jPanelDetails.setLayout(new GridLayout(0, 2));
        //
        JPanel jPanelLeftColumn = new JPanel();
        jPanelLeftColumn.setLayout(new BoxLayout(jPanelLeftColumn, BoxLayout.PAGE_AXIS));
        //
        JPanel jPanelRightColumn = new JPanel();
        jPanelRightColumn.setLayout(new BoxLayout(jPanelRightColumn, BoxLayout.PAGE_AXIS));
        //
        jPanelDetails.add(jPanelLeftColumn);
        jPanelDetails.add(jPanelRightColumn);
        //
        jPanelPacketDetails.add(jPanelDetails);
        //
        JPanel jPanelTime = new JPanel();
        jPanelTime.setLayout(new BoxLayout(jPanelTime, BoxLayout.LINE_AXIS));
        jPanelTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        jLabelDetailsTimeName = new JLabel();
        Font boldFont = new Font(jLabelDetailsTimeName.getFont().getName(), Font.BOLD, jLabelDetailsTimeName.getFont().getSize());
        jLabelDetailsTimeName.setFont(boldFont);
        jPanelTime.add(jLabelDetailsTimeName);
        jLabelDetailsTimeValue = new JLabel();

        jPanelTime.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelTime.add(jLabelDetailsTimeName);
        jPanelTime.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelTime.add(jLabelDetailsTimeValue);

        jPanelLeftColumn.add(jPanelTime);

        JPanel jPanelType = new JPanel();
        jPanelType.setLayout(new BoxLayout(jPanelType, BoxLayout.LINE_AXIS));
        jPanelType.setAlignmentX(Component.LEFT_ALIGNMENT);

        jLabelDetailsTypeName = new JLabel();
        jLabelDetailsTypeName.setFont(boldFont);
        jLabelDetailsTypeValue = new JLabel();

        jPanelType.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelType.add(jLabelDetailsTypeName);
        jPanelType.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelType.add(jLabelDetailsTypeValue);

        jPanelRightColumn.add(jPanelType);
        //
        //
        JPanel jPanelFrom = new JPanel();
        jPanelFrom.setLayout(new BoxLayout(jPanelFrom, BoxLayout.LINE_AXIS));
        jPanelFrom.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabelDetailsFromName = new JLabel();
        jLabelDetailsFromName.setFont(boldFont);
        jLabelDetailsFromValue = new JLabel();

        jPanelFrom.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelFrom.add(jLabelDetailsFromName);
        jPanelFrom.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelFrom.add(jLabelDetailsFromValue);

        jPanelLeftColumn.add(jPanelFrom);

        JPanel jPanelFromInterface = new JPanel();
        jPanelFromInterface.setLayout(new BoxLayout(jPanelFromInterface, BoxLayout.LINE_AXIS));
        jPanelFromInterface.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabelDetailsFromInterfaceName = new JLabel();
        jLabelDetailsFromInterfaceName.setFont(boldFont);
        jLabelDetailsFromInterfaceValue = new JLabel();

        jPanelFromInterface.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelFromInterface.add(jLabelDetailsFromInterfaceName);
        jPanelFromInterface.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelFromInterface.add(jLabelDetailsFromInterfaceValue);

        jPanelRightColumn.add(jPanelFromInterface);

        //
        JPanel jPanelTo = new JPanel();
        jPanelTo.setLayout(new BoxLayout(jPanelTo, BoxLayout.LINE_AXIS));
        jPanelTo.setAlignmentX(Component.LEFT_ALIGNMENT);

        jLabelDetailsToName = new JLabel();
        jLabelDetailsToName.setFont(boldFont);
        jLabelDetailsToValue = new JLabel();

        jPanelTo.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelTo.add(jLabelDetailsToName);
        jPanelTo.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelTo.add(jLabelDetailsToValue);

        jPanelLeftColumn.add(jPanelTo);

        JPanel jPanelToInterface = new JPanel();
        jPanelToInterface.setLayout(new BoxLayout(jPanelToInterface, BoxLayout.LINE_AXIS));
        jPanelToInterface.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabelDetailsToInterfaceName = new JLabel();
        jLabelDetailsToInterfaceName.setFont(boldFont);
        jLabelDetailsToInterfaceName.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabelDetailsToInterfaceValue = new JLabel();

        jPanelToInterface.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelToInterface.add(jLabelDetailsToInterfaceName);
        jPanelToInterface.add(Box.createRigidArea(new Dimension(5, 0)));
        jPanelToInterface.add(jLabelDetailsToInterfaceValue);

        jPanelRightColumn.add(jPanelToInterface);

        //
        JPanel jPanelTextArea = new JPanel();
        jPanelTextArea.setLayout(new BoxLayout(jPanelTextArea, BoxLayout.PAGE_AXIS));

        jTextAreaPacketDetails = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(jTextAreaPacketDetails);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setMinimumSize(new Dimension(100, 80));
        //scrollPane.setMaximumSize(new Dimension(500, 150));

        jTextAreaPacketDetails.setFont(new Font(boldFont.getName(), Font.PLAIN, boldFont.getSize()));
        jTextAreaPacketDetails.setLineWrap(true);
        jTextAreaPacketDetails.setWrapStyleWord(true);
        jTextAreaPacketDetails.setEditable(false);

        jPanelTextArea.add(scrollPane);
        jPanelPacketDetails.add(jPanelTextArea);

        return jPanelPacketDetails;
    }

    private void setTextsToComponents() {
        jPanelConnectSaveLoad.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("CONNECT_SAVE_LOAD")));
        jButtonSaveListToFile.setText(dataLayer.getString("SAVE_LIST_TO_FILE"));
        jButtonSaveListToFile.setToolTipText(dataLayer.getString("SAVE_LIST_TO_FILE_TOOL_TIP"));
        jButtonLoadListFromFile.setText(dataLayer.getString("LOAD_LIST_FROM_FILE"));
        jButtonLoadListFromFile.setToolTipText(dataLayer.getString("LOAD_LIST_FROM_FILE_TOOL_TIP"));
        jLabelConnectionStatusName.setText(dataLayer.getString("CONNECTION_STATUS"));
        //
        jPanelPlayControls.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("PLAY_CONTROLS")));
        jSliderPlayerSpeed.setToolTipText(dataLayer.getString("SPEED_CONTROL"));
        jLabelSpeedName.setText(dataLayer.getString("SPEED_COLON"));
        jLabelSliderSlow.setText(dataLayer.getString("SLOW"));
        jLabelSliderMedium.setText(dataLayer.getString("MEDIUM"));
        jLabelSliderFast.setText(dataLayer.getString("FAST"));
        jButtonFirst.setToolTipText(dataLayer.getString("SKIP_TO_FIRST_EVENT"));
        jButtonLast.setToolTipText(dataLayer.getString("SKIP_TO_LAST_EVENT"));
        jButtonNext.setToolTipText(dataLayer.getString("SKIP_TO_NEXT_EVENT"));
        jButtonPrevious.setToolTipText(dataLayer.getString("SKIP_TO_PREV_EVENT"));
        jToggleButtonPlay.setToolTipText(dataLayer.getString("START_STOP_PLAYING"));
        //

        //
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(SimulatorManager.SPEED_MIN), jLabelSliderSlow);
        labelTable.put(new Integer(SimulatorManager.SPEED_MAX / 2), jLabelSliderMedium);
        labelTable.put(new Integer(SimulatorManager.SPEED_MAX), jLabelSliderFast);
        jSliderPlayerSpeed.setLabelTable(labelTable);
        //
        jPanelEventList.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("EVENT_LIST")));
        jButtonDeleteEvents.setText(dataLayer.getString("DELETE_EVENTS"));
        jButtonDeleteEvents.setToolTipText(dataLayer.getString("DELETES_EVENTS_IN_LIST"));
        //
        jTableEventList.getColumnModel().getColumn(0).setHeaderValue(dataLayer.getString("TIME") + " [s]");
        jTableEventList.getColumnModel().getColumn(1).setHeaderValue(dataLayer.getString("FROM"));
        jTableEventList.getColumnModel().getColumn(2).setHeaderValue(dataLayer.getString("TO"));
        jTableEventList.getColumnModel().getColumn(3).setHeaderValue(dataLayer.getString("TYPE"));
        jTableEventList.getColumnModel().getColumn(4).setHeaderValue(dataLayer.getString("INFO"));
        //
        jPanelPacketDetails.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("SELECTED_PACKET_DETAILS")));
        jLabelDetailsTimeName.setText(dataLayer.getString("TIME") + ":");
        jLabelDetailsFromName.setText(dataLayer.getString("FROM") + ":");
        jLabelDetailsFromInterfaceName.setText(dataLayer.getString("INTERFACE") + ":");
        jLabelDetailsToName.setText(dataLayer.getString("TO") + ":");
        jLabelDetailsToInterfaceName.setText(dataLayer.getString("INTERFACE") + ":");
        jLabelDetailsTypeName.setText(dataLayer.getString("TYPE") + ":");
        //
        updateConnectionInfoAccordingToModel();
        updateRecordingInfoAccordingToModel();
        updateRealtimeAccordingToModel();

    }

    private void updateConnectionInfoAccordingToModel() {
        if (simulatorManagerInterface.isConnectedToServer()) {
            jLabelConnectionStatusValue.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_ok.png"))); // NOI18N
            jLabelConnectionStatusValue.setText(dataLayer.getString("CONNECTED"));
            //
            jToggleButtonCapture.setEnabled(true);
            jToggleButtonRealtime.setEnabled(true);
            //
            jButtonConnectToServer.setText(dataLayer.getString("DISCONNECT_FROM_SERVER"));
            jButtonConnectToServer.setToolTipText(dataLayer.getString("DISCONNECT_FROM_SERVER_TOOL_TIP"));
        } else {
            jLabelConnectionStatusValue.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_cancel.png"))); // NOI18N
            jLabelConnectionStatusValue.setText(dataLayer.getString("DISCONNECTED"));
            //
            jToggleButtonCapture.setEnabled(false);
            jToggleButtonRealtime.setEnabled(false);
            //
            jButtonConnectToServer.setText(dataLayer.getString("CONNECT_TO_SERVER"));
            jButtonConnectToServer.setToolTipText(dataLayer.getString("CONNECT_TO_SERVER_TOOL_TIP"));
        }
    }

    private void updateRecordingInfoAccordingToModel() {
        if (simulatorManagerInterface.isRecording()) {
            jToggleButtonCapture.setText(dataLayer.getString("CAPTURE_STOP"));
            jToggleButtonCapture.setToolTipText(dataLayer.getString("CAPTURE_PACKETS_FROM_SERVER_STOP"));
            jToggleButtonCapture.setSelected(true);
        } else {
            jToggleButtonCapture.setText(dataLayer.getString("CAPTURE"));
            jToggleButtonCapture.setToolTipText(dataLayer.getString("CAPTURE_PACKETS_FROM_SERVER"));
            jToggleButtonCapture.setSelected(false);
        }
    }

    private void updatePlayingInfoAccordingToModel() {
        if (simulatorManagerInterface.isPlaying()) {
            jToggleButtonPlay.setSelected(true);
        } else {
            jToggleButtonPlay.setSelected(false);
        }
    }

    private void updatePositionInListAccordingToModel() {
        if (simulatorManagerInterface.getListSize() > 0) {
            int row = simulatorManagerInterface.getCurrentPositionInList();
            // if some row selected
            if (row >= 0) {
                jTableEventList.setRowSelectionInterval(row, row);

                // need to do this in thread because without thread it does not repaint correctly during playing
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        // scrolls table to selected row position
                        jTableEventList.scrollRectToVisible(jTableEventList.getCellRect(simulatorManagerInterface.getCurrentPositionInList(), 0, false));
                    }
                });
            }
        } else {
            // if no content, remove selection
            jTableEventList.getSelectionModel().clearSelection();
        }
    }

    private void updatePacketDetailsAccordingToModel() {
        int row = simulatorManagerInterface.getCurrentPositionInList();

        // if some row selected
        if (simulatorManagerInterface.getListSize() > 0 && row >= 0) {
            SimulatorEvent event = simulatorManagerInterface.getSimulatorEventAtCurrentPosition();

            String seconds = fmt.format(event.getTimeStamp() / 1000.0);
            jLabelDetailsTimeValue.setText(seconds);
            jLabelDetailsTypeValue.setText(event.getPacketType().toString());
            //
            jLabelDetailsFromValue.setText(event.getComponent1().getName());
            jLabelDetailsFromInterfaceValue.setText(event.getEth1().getName());
            //
            jLabelDetailsToValue.setText(event.getComponent2().getName());
            jLabelDetailsToInterfaceValue.setText(event.getEth2().getName());
            //
            jTextAreaPacketDetails.setText(event.getDetailsText());
        } else {
            jLabelDetailsTimeValue.setText("");
            jLabelDetailsFromValue.setText("");
            jLabelDetailsFromInterfaceValue.setText("");
            jLabelDetailsToValue.setText("");
            jLabelDetailsToInterfaceValue.setText("");
            jLabelDetailsTypeValue.setText("");
            jTextAreaPacketDetails.setText("");
        }
    }

    private void updateRealtimeAccordingToModel() {
        if (simulatorManagerInterface.isRealtime()) {
            jToggleButtonRealtime.setText(dataLayer.getString("REALTIME_STOP"));
            jToggleButtonRealtime.setToolTipText(dataLayer.getString("REALTIME_STOP_TOOLTIP"));
            jToggleButtonRealtime.setSelected(true);

            // deactivate play buttons
            setPlayerButtonsEnabled(false);

        } else {
            jToggleButtonRealtime.setText(dataLayer.getString("REALTIME"));
            jToggleButtonRealtime.setToolTipText(dataLayer.getString("REALTIME_START_TOOLTIP"));
            jToggleButtonRealtime.setSelected(false);

            // activate player buttons
            setPlayerButtonsEnabled(true);
        }
    }

    private void setPlayerButtonsEnabled(boolean enabled) {
        jButtonFirst.setEnabled(enabled);
        jButtonPrevious.setEnabled(enabled);
        jButtonNext.setEnabled(enabled);
        jButtonLast.setEnabled(enabled);
        jToggleButtonPlay.setEnabled(enabled);
        //jSliderPlayerSpeed.setEnabled(enabled);

        // capture button could be enabled when not connected to server, we have to check this
        if (simulatorManagerInterface.isConnectedToServer() && enabled) {
            jToggleButtonCapture.setEnabled(enabled);
        } else {
            jToggleButtonCapture.setEnabled(false);
        }
    }

    private int showYesNoDialog(String title, String message) {
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

    private void showWarningDialog(String title, String message) {
        //custom title, warning icon
        JOptionPane.showMessageDialog(this,
                message, title, JOptionPane.WARNING_MESSAGE);
    }
}
