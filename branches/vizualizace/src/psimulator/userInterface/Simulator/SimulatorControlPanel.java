package psimulator.userInterface.Simulator;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.SimulatorPlayerState;
import psimulator.dataLayer.Enums.UpdateEventType;
import psimulator.dataLayer.Simulator.SimulatorEvent;
import psimulator.dataLayer.Simulator.SimulatorManager;
import psimulator.dataLayer.interfaces.SimulatorManagerInterface;

/**
 *
 * @author Martin
 */
public class SimulatorControlPanel extends JPanel implements Observer{

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
    private JPanel jPanelEventListTable;
    private JPanel jPanelEventListButtons;
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
    //
    //
    private DataLayerFacade dataLayer;
    private SimulatorManagerInterface simulatorInterface;

    public SimulatorControlPanel(DataLayerFacade dataLayer) {
        this.dataLayer = dataLayer;
        this.simulatorInterface = dataLayer.getSimulatorInterface();
        
        // create graphic layout with components
        initComponents();
        
        // add listeners to components
        addListenersToComponents();
    }
    
    
    @Override
    public void update(Observable o, Object o1) {
        switch((UpdateEventType)o1){
            case LANGUAGE:
                setTextsToComponents();
                break;
            case SIMULATOR:
                updateComponentsAccordingToModel();
                break;
        }
    }
    
    

    
    ////////------------ PRIVATE------------///////////
    private void addListenersToComponents(){
        // jSliderPlayerSpeed state change listener
        jSliderPlayerSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                // set the speed in model
                simulatorInterface.setPlayerSpeed(jSliderPlayerSpeed.getValue());
            }
        });
        
        //
        jButtonConnectToServer.addActionListener(new ActionListener() {
            int tmpCounter = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //simulatorInterface.pullTriggerTmp();
                simulatorInterface.addSimulatorEvent(new SimulatorEvent(tmpCounter++,"Router1", "Router2", "PING", ""));
            }
        });
        
        //
        jButtonDeleteEvents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int i = showYesNoDialog(dataLayer.getString("WARNING"), dataLayer.getString("DELETING_EVENT_LIST_WARNING"));
                // if YES
                if(i==0){
                    simulatorInterface.deleteAllSimulatorEvents();
                }
            }
        });
        
        // -------------------- PLAY BUTTONS ACTIONS ---------------------
        jButtonFirst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorInterface.playerFunctionActivated(SimulatorPlayerState.FIRST);
            }
        });
        
        //
        jButtonLast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorInterface.playerFunctionActivated(SimulatorPlayerState.LAST);
            }
        });
        
        //
        jButtonNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorInterface.playerFunctionActivated(SimulatorPlayerState.NEXT);
            }
        });
        
        //
        jButtonPrevious.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulatorInterface.playerFunctionActivated(SimulatorPlayerState.PREVIOUS);
            }
        });
        
        //
        jToggleButtonPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(jToggleButtonPlay.isSelected()){
                    simulatorInterface.playerFunctionActivated(SimulatorPlayerState.PLAY);
                }else{
                    simulatorInterface.playerFunctionActivated(SimulatorPlayerState.STOP);
                }
            }
        });
        
        // -------------------- CAPTURE ACTION ---------------------
        jToggleButtonCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(jToggleButtonCapture.isSelected()){
                    simulatorInterface.recordingActivated(true);
                }else{
                    simulatorInterface.recordingActivated(false);
                }
            }
        });
        
        // -------------------- VIEW DETAILS ---------------------
        jCheckBoxPacketDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(jCheckBoxPacketDetails.isSelected()){
                    simulatorInterface.setPacketDetails(true);
                }else{
                    simulatorInterface.setPacketDetails(false);
                }
            }
        });
        
        jCheckBoxNamesOfDevices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(jCheckBoxNamesOfDevices.isSelected()){
                    simulatorInterface.setNamesOfDevices(true);
                }else{
                    simulatorInterface.setNamesOfDevices(false);
                }
            }
        });
    }
    
    
    private void initComponents(){
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
        this.add(createDetailsPanel(),cons);
        cons.gridx = 0;
	cons.gridy = 8;
        this.add(Box.createRigidArea(new Dimension(0, 6)),cons);
        
        
        // end Connect / Save / Load panel
        setTextsToComponents();
        updateComponentsAccordingToModel();
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
        jPanelEventList.setLayout(new BoxLayout(jPanelEventList, BoxLayout.Y_AXIS));

        //// link table with table model
        jTableEventList = new JTable(simulatorInterface.getEventTableModel());
        jTableEventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //
        jPanelEventListTable = new JPanel();
        jScrollPaneTableEventList = new JScrollPane();
        jScrollPaneTableEventList.setViewportView(jTableEventList);
        
        GroupLayout jPanelEventListLayout = new GroupLayout(jPanelEventListTable);
        jPanelEventListTable.setLayout(jPanelEventListLayout);
        jPanelEventListLayout.setHorizontalGroup(
            jPanelEventListLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneTableEventList, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanelEventListLayout.setVerticalGroup(
            jPanelEventListLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneTableEventList, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)//200
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
    
    private void setTextsToComponents() {
        jPanelConnectSaveLoad.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("CONNECT_SAVE_LOAD")));
        jButtonSaveListToFile.setText(dataLayer.getString("SAVE_LIST_TO_FILE"));
        jButtonSaveListToFile.setToolTipText(dataLayer.getString("SAVE_LIST_TO_FILE_TOOL_TIP"));
        jButtonLoadListFromFile.setText(dataLayer.getString("LOAD_LIST_FROM_FILE"));
        jButtonLoadListFromFile.setToolTipText(dataLayer.getString("LOAD_LIST_FROM_FILE_TOOL_TIP"));
        jButtonConnectToServer.setText(dataLayer.getString("CONNECT_TO_SERVER"));
        jButtonConnectToServer.setToolTipText(dataLayer.getString("CONNECT_TO_SERVER_TOOL_TIP"));
        jLabelConnectionStatusName.setText(dataLayer.getString("CONNECTION_STATUS"));
        
        if(simulatorInterface.isConnectedToServer()){
            jLabelConnectionStatusValue.setText(dataLayer.getString("CONNECTED"));
        }else{
            jLabelConnectionStatusValue.setText(dataLayer.getString("DISCONNECTED"));
        }
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
        jToggleButtonCapture.setText(dataLayer.getString("CAPTURE"));
        jToggleButtonCapture.setToolTipText(dataLayer.getString("CAPTURE_PACKETS_FROM_SERVER"));
        //
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(SimulatorManager.SPEED_MIN), jLabelSliderSlow);
        labelTable.put(new Integer(SimulatorManager.SPEED_MAX/2), jLabelSliderMedium);
        labelTable.put(new Integer(SimulatorManager.SPEED_MAX), jLabelSliderFast);
        jSliderPlayerSpeed.setLabelTable(labelTable);
        
        //
        jPanelEventList.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("EVENT_LIST")));
        jButtonDeleteEvents.setText(dataLayer.getString("DELETE_EVENTS"));
        jButtonDeleteEvents.setToolTipText(dataLayer.getString("DELETES_EVENTS_IN_LIST"));
        //
        jPanelDetails.setBorder(BorderFactory.createTitledBorder(dataLayer.getString("DETAILS")));
        jCheckBoxPacketDetails.setText(dataLayer.getString("PACKET_DETAILS"));
        jCheckBoxNamesOfDevices.setText(dataLayer.getString("NAMES_OF_DEVICES"));
        //
        jTableEventList.getColumnModel().getColumn(0).setHeaderValue(dataLayer.getString("TIME"));
        jTableEventList.getColumnModel().getColumn(1).setHeaderValue(dataLayer.getString("FROM"));
        jTableEventList.getColumnModel().getColumn(2).setHeaderValue(dataLayer.getString("TO"));
        jTableEventList.getColumnModel().getColumn(3).setHeaderValue(dataLayer.getString("TYPE"));
        jTableEventList.getColumnModel().getColumn(4).setHeaderValue(dataLayer.getString("INFO"));
        
        
    }
    
    private void updateComponentsAccordingToModel(){
        if(simulatorInterface.isConnectedToServer()){
            jLabelConnectionStatusValue.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_ok.png"))); // NOI18N
            jLabelConnectionStatusValue.setText(dataLayer.getString("CONNECTED"));
        }else{
            jLabelConnectionStatusValue.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/button_cancel.png"))); // NOI18N
            jLabelConnectionStatusValue.setText(dataLayer.getString("DISCONNECTED"));
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
}
