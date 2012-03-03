package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.Network.Components.NetworkModel;
import psimulator.dataLayer.SimulatorEvents.SerializedComponents.SimulatorEventsWrapper;
import psimulator.dataLayer.Singletons.ImageFactory.ImageFactorySingleton;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.logicLayer.ControllerFacade;
import psimulator.userInterface.GlassPane.GlassPanelPainterSingleton;
import psimulator.userInterface.GlassPane.MainWindowGlassPane;
import psimulator.userInterface.SaveLoad.SaveLoadManagerEvents;
import psimulator.userInterface.SaveLoad.SaveLoadManagerNetworkModel;
import psimulator.userInterface.SaveLoad.SaveLoadManagerUserReaction;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.UndoRedo;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.Zoom;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanel;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelState;
import psimulator.userInterface.actionListerners.PreferencesActionListener;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MainWindow extends JFrame implements MainWindowInnerInterface, UserInterfaceOuterFacade, Observer {

    private SaveLoadManagerNetworkModel saveLoadManagerGraph;
    private SaveLoadManagerEvents saveLoadManagerEvents;
    //
    private DataLayerFacade dataLayer;
    private ControllerFacade controller;
    /*
     * window componenets
     */
    private MenuBar jMenuBar;
    private ToolBar jToolBar;
    private UserInterfaceMainPanelOuterInterface jPanelUserInterfaceMain;
    //private JPanel glassPanel;
    /*
     * end of window components
     */
    private JFrame mainWindow;
    private MainWindowGlassPane glassPane;
    //private Component originalGlassPane;

    public MainWindow(DataLayerFacade dataLayer) {
        this.dataLayer = dataLayer;

        try {
            //if OS is Windows we set the windows look and feel
            if (isWindows()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else { // otherwise we set metal look and feel
                //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        saveLoadManagerGraph = new SaveLoadManagerNetworkModel((Component) this, dataLayer);
        saveLoadManagerEvents = new SaveLoadManagerEvents((Component) this, dataLayer);

        jMenuBar = new MenuBar(dataLayer);
        jToolBar = new ToolBar(dataLayer);

        this.setTitle(dataLayer.getString("WINDOW_TITLE"));

        this.setJMenuBar(jMenuBar);
        this.add(jToolBar, BorderLayout.PAGE_START);

        this.mainWindow = (JFrame) this;

        jPanelUserInterfaceMain = new UserInterfaceMainPanel(this, dataLayer, UserInterfaceMainPanelState.WELCOME);
        this.add(jPanelUserInterfaceMain, BorderLayout.CENTER);


        // set this as Observer to LanguageManager
        dataLayer.addLanguageObserver((Observer) this);

        this.setIconImage(ImageFactorySingleton.getInstance().getImageIconForToolbar(MainTool.ADD_REAL_PC).getImage());


        // create glass pane and glass pane painter
        glassPane = new MainWindowGlassPane();

        // initialize glass pane painter singleton
        GlassPanelPainterSingleton.getInstance().initialize(glassPane);

        this.setGlassPane(glassPane);
        glassPane.setOpaque(false);
        getGlassPane().setVisible(true);

    }

    @Override
    public void initView(ControllerFacade controller) {
        this.controller = controller;


        // set translated texts to file chooser
        //setTextsToFileChooser();

        updateProjectRelatedButtons();

        updateToolBarIconsSize(dataLayer.getToolbarIconSize());

        addActionListenersToViewComponents();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                // if data can be lost after check
                if (!checkDataLoss()) {
                    return;
                }
                
                refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.WELCOME, false);

                System.exit(0);
            }
        });

        // set of window properties
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //this.setMinimumSize(new Dimension(987, 740));
        this.setMinimumSize(new Dimension(933, 700));
        this.setSize(new Dimension(1024, 768));
        this.setVisible(true);

        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        this.setLocation(x, y);

        this.setVisible(true);
    }

    @Override
    public void updateUndoRedoButtons() {
        // if in simulator mode
        if (jPanelUserInterfaceMain.getUserInterfaceState() == UserInterfaceMainPanelState.EDITOR) {
            jMenuBar.setUndoEnabled(jPanelUserInterfaceMain.canUndo());
            jToolBar.setUndoEnabled(jPanelUserInterfaceMain.canUndo());

            jMenuBar.setRedoEnabled(jPanelUserInterfaceMain.canRedo());
            jToolBar.setRedoEnabled(jPanelUserInterfaceMain.canRedo());
        } else {
            jMenuBar.setUndoEnabled(false);
            jToolBar.setUndoEnabled(false);

            jMenuBar.setRedoEnabled(false);
            jToolBar.setRedoEnabled(false);
        }
    }

    @Override
    public void updateZoomButtons() {
        jMenuBar.setZoomInEnabled(ZoomManagerSingleton.getInstance().canZoomIn());
        jToolBar.setZoomInEnabled(ZoomManagerSingleton.getInstance().canZoomIn());

        jMenuBar.setZoomOutEnabled(ZoomManagerSingleton.getInstance().canZoomOut());
        jToolBar.setZoomOutEnabled(ZoomManagerSingleton.getInstance().canZoomOut());

        jMenuBar.setZoomResetEnabled(true);
        jToolBar.setZoomResetEnabled(true);
    }

    @Override
    public void updateToolBarIconsSize(ToolbarIconSizeEnum size) {
        jToolBar.updateIconSize(size);
    }

    /**
     * Reaction to Language Observable update
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        saveLoadManagerGraph.updateTextsOnFileChooser();
        saveLoadManagerEvents.updateTextsOnFileChooser();
    }

    @Override
    public AnimationPanelOuterInterface getAnimationPanelOuterInterface() {
        return jPanelUserInterfaceMain.getAnimationPanelOuterInterface();
    }

    @Override
    public Component getMainWindowComponent() {
        return this;
    }

    /**
     * Saves events. Exceptions handled inside.
     *
     * @param simulatorEventsWrapper
     */
    @Override
    public void saveEventsAction(SimulatorEventsWrapper simulatorEventsWrapper) {
        saveEventsAndInformAboutSuccess(simulatorEventsWrapper);
    }
    
    private boolean saveEventsAndInformAboutSuccess(SimulatorEventsWrapper simulatorEventsWrapper){
        boolean success = saveLoadManagerEvents.doSaveAsEventsAction(simulatorEventsWrapper);

        if (success) {
            // inform user
            String file = saveLoadManagerEvents.getFile().getPath();
            GlassPanelPainterSingleton.getInstance().
                    addAnnouncement(dataLayer.getString("EVENT_LIST_SAVE_ACTION"), dataLayer.getString("SAVED_TO"), file);
        }
        
        return success;
    }

    /**
     * Returns loaded events or null if it could not be loaded. Exceptions are
     * handled inside.
     *
     * @return
     */
    @Override
    public SimulatorEventsWrapper loadEventsAction() {
        SimulatorEventsWrapper simulatorEventsWrapper = saveLoadManagerEvents.doLoadEventsAction();

        if (simulatorEventsWrapper != null) {
            // inform user
            String file = saveLoadManagerEvents.getFile().getPath();
            GlassPanelPainterSingleton.getInstance().
                    addAnnouncement(dataLayer.getString("EVENT_LIST_OPEN_ACTION"), dataLayer.getString("OPENED_FROM"), file);
        }

        return simulatorEventsWrapper;
    }

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Undo and Redo button
     */
    class JMenuItemUndoRedoListener implements ActionListener {

        /**
         * calls undo or redo and repaint on jPanelEditor, updates Undo and Redo
         * buttons
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (UndoRedo.valueOf(e.getActionCommand())) {
                case UNDO:
                    jPanelUserInterfaceMain.undo();
                    break;
                case REDO:
                    jPanelUserInterfaceMain.redo();
                    break;
            }
            updateUndoRedoButtons();
            jPanelUserInterfaceMain.repaint();
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Zoom buttons
     */
    class JMenuItemZoomListener implements ActionListener {

        /**
         * calls zoom operation on jPanelEditor according to actionCommand
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (Zoom.valueOf(e.getActionCommand())) {
                case IN:
                    ZoomManagerSingleton.getInstance().zoomIn();
                    break;
                case OUT:
                    ZoomManagerSingleton.getInstance().zoomOut();
                    break;
                case RESET:
                    ZoomManagerSingleton.getInstance().zoomReset();
                    break;
            }
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Simulator and Editor buttons
     */
    class JMenuItemSimulatorEditorListener implements ActionListener {

        /**
         * 
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (UserInterfaceMainPanelState.valueOf(e.getActionCommand())) {
                case EDITOR:
                    // if nothing changed, do nothing
                    if (jPanelUserInterfaceMain.getUserInterfaceState() == UserInterfaceMainPanelState.EDITOR) {
                        return;
                    }

                    // change state to editor without changing or removing the graph
                    refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.EDITOR, true);

                    break;
                case SIMULATOR:
                    // if nothing changed, do nothing
                    if (jPanelUserInterfaceMain.getUserInterfaceState() == UserInterfaceMainPanelState.SIMULATOR) {
                        return;
                    }
                    
                    // check if we can change to simulator (wrong events in list)
                    if(!dataLayer.getSimulatorManager().hasAllEventsItsComponentsInModel()){
                        // if there is a problem, ask user what to do
                        int result = showWarningEventsInListHaventComponents(dataLayer.getString("WARNING"), dataLayer.getString("EVENTS_CANT_BE_APPLIED_WHAT_TO_DO"));

                        if(result == 0){    // save events and celar list
                            //System.out.println("save events and celar list");
                            // save events
                            boolean success = saveEventsAndInformAboutSuccess(dataLayer.getSimulatorManager().getSimulatorEventsCopy());
                            // if save wasnt succesfull
                            if(!success){
                                // go back to editor
                                refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.EDITOR, true);
                                return;
                            }
                            // if save succesfull clear list
                            dataLayer.getSimulatorManager().deleteAllSimulatorEvents();
                        } else if (result == 1){    // celar events
                            //System.out.println("Clear list");
                            // clear list
                            dataLayer.getSimulatorManager().deleteAllSimulatorEvents();
                        } else {    // go back to editor
                            //System.out.println("Cancel");
                            // get back to editor
                            // change state to editor without changing or removing the graph
                            refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.EDITOR, true);
                            return;
                        }     
                    }
                    

                    // change state to editor without changing or removing the graph
                    refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.SIMULATOR, true);
                    break;
            }
        }
    }
    
    private int showWarningEventsInListHaventComponents(String title, String message) {
        //Object[] options = {dataLayer.getString("SAVE"), dataLayer.getString("DONT_SAVE"), dataLayer.getString("CANCEL")};
        Object[] options = {dataLayer.getString("SAVE_EVENTS_AND_CLEAR_LIST"), 
            dataLayer.getString("DELETE_EVENTS"),dataLayer.getString("GO_BACK_TO_EDITOR")};
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
    
    /**
     * Returns true if checked and we should continue. False if do not continue.
     *
     * @return
     */
    private boolean checkDataLoss() {
        // if data can be lost
        if (saveLoadManagerGraph.doCheckIfPossibleDataLoss(jPanelUserInterfaceMain.getGraph())) {

            SaveLoadManagerUserReaction userReaction = saveLoadManagerGraph.doAskUserIfSave(jPanelUserInterfaceMain.getGraph());

            switch (userReaction) {
                case DO_NOT_SAVE:
                    // user dont want to save, we should proceed
                    return true;
                case DO_SAVE:
                    // save 
                    boolean success = saveLoadManagerGraph.doSaveGraphAction();
                    if (success) {
                        String file = saveLoadManagerGraph.getFile().getPath();
                        GlassPanelPainterSingleton.getInstance().
                                addAnnouncement(dataLayer.getString("NETWORK_SAVE_ACTION"), dataLayer.getString("SAVED_TO"), file);
                    }

                    // return true if save successfull, false if not succesfull
                    return success;
                case CANCEL:
                    return false;
            }
        }
        // data cant be lost
        return true;
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for NewProject button
     */
    class JMenuItemNewListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // if data can be lost after check
            if (!checkDataLoss()) {
                return;
            }

            // create new network model
            NetworkModel networkModel = dataLayer.getNetworkFacade().createNetworkModel();

            // create new graph
            Graph graph = new Graph();

            // refresh UI
            refreshUserInterfaceMainPanel(graph, networkModel, UserInterfaceMainPanelState.EDITOR, false);

            // set saved timestamp
            saveLoadManagerGraph.setLastSavedTimestamp();
            saveLoadManagerGraph.setLastSavedFile(null);
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for NewProject button
     */
    class JMenuItemCloseListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // if data can be lost after check
            if (!checkDataLoss()) {
                return;
            }

            // turn off playing recording and etc
            //jPanelUserInterfaceMain.stopSimulatorActivities();

            refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.WELCOME, false);
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Open button
     */
    class JMenuItemOpenListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // if data can be lost after check
            if (!checkDataLoss()) {
                return;
            }

            // turn off playing recording and etc
            jPanelUserInterfaceMain.stopSimulatorActivities();

            // load network model
            NetworkModel networkModel = saveLoadManagerGraph.doLoadNetworkModel();

            if (networkModel == null) {
                return;
            }

            // create graph from model
            Graph graph = saveLoadManagerGraph.buildGraphFromNetworkModel(networkModel);

            if (graph != null) {
                // init graph (set edit timestamp)
                refreshUserInterfaceMainPanel(graph, networkModel, UserInterfaceMainPanelState.EDITOR, false);

                // set saved timestamp
                saveLoadManagerGraph.setLastSavedTimestamp();

                // inform user
                String file = saveLoadManagerGraph.getFile().getPath();
                GlassPanelPainterSingleton.getInstance().
                        addAnnouncement(dataLayer.getString("NETWORK_OPEN_ACTION"), dataLayer.getString("OPENED_FROM"), file);
            }
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Save button
     */
    class JMenuItemSaveListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("LISTENER Save");
            boolean success = saveLoadManagerGraph.doSaveGraphAction();

            // inform user
            if (success) {
                String file = saveLoadManagerGraph.getFile().getPath();
                GlassPanelPainterSingleton.getInstance().
                        addAnnouncement(dataLayer.getString("NETWORK_SAVE_ACTION"), dataLayer.getString("SAVED_TO"), file);
            }
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for SaveAs button
     */
    class JMenuItemSaveAsListener implements ActionListener {

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("LISTENER Save As");
            boolean success = saveLoadManagerGraph.doSaveAsGraphAction();

            // inform user
            if (success) {
                String file = saveLoadManagerGraph.getFile().getPath();
                GlassPanelPainterSingleton.getInstance().
                        addAnnouncement(dataLayer.getString("NETWORK_SAVE_AS_ACTION"), dataLayer.getString("SAVED_TO"), file);
            }
        }
    }

/////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Exit Button
     */
    class JMenuItemExitListener implements ActionListener {

        /**
         * Saves config data and exits application.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            // if data can be lost after check
            if (!checkDataLoss()) {
                return;
            }

            refreshUserInterfaceMainPanel(null, null, UserInterfaceMainPanelState.WELCOME, false);

            System.exit(0);
        }
    }
////////------------ PRIVATE------------///////////

    /**
     * Updates jPanelUserInterfaceMain according to userInterfaceState. If
     * changing to SIMULATOR or EDITOR state, graph cannot be null.
     *
     * @param graph Graph to set into jPanelUserInterfaceMain, can be null if
     * userInterfaceState will be WELCOME
     * @param userInterfaceState State to change to.
     * @param changingSimulatorEditor if true, the graph is kept untouched
     */
    private void refreshUserInterfaceMainPanel(Graph graph, NetworkModel networkModel, UserInterfaceMainPanelState userInterfaceState, boolean changingSimulatorEditor) {

        // turn off playing recording and etc
        jPanelUserInterfaceMain.stopSimulatorActivities();

        if (!changingSimulatorEditor) {
            // delete events from simulator
            jPanelUserInterfaceMain.init();
        }

        switch (userInterfaceState) {
            case WELCOME:
                // remove graph
                jPanelUserInterfaceMain.removeGraph();
                break;
            case EDITOR:
                // if not only changing from simulator to editor or back
                if (!changingSimulatorEditor) {
                    // remove graph
                    jPanelUserInterfaceMain.removeGraph();
                    // set network model to network facade
                    dataLayer.getNetworkFacade().setNetworkModel(networkModel);
                    // set another graph
                    jPanelUserInterfaceMain.setGraph(graph);
                }

                // set Editor selected in tool bar
                jToolBar.setEditorSelected(true);
                break;
            case SIMULATOR:
                // if not only changing from simulator to editor or back
                if (!changingSimulatorEditor) {
                    // remove graph
                    jPanelUserInterfaceMain.removeGraph();
                    // set network model to network facade
                    dataLayer.getNetworkFacade().setNetworkModel(networkModel);
                    // set another graph
                    jPanelUserInterfaceMain.setGraph(graph);
                }

                // set Simulator selected in tool bar
                jToolBar.setSimulatorSelected(true);
                break;
        }

        jPanelUserInterfaceMain.doChangeMode(userInterfaceState);

        // update buttons
        updateProjectRelatedButtons();
    }

    private void updateProjectRelatedButtons() {
        if (!jPanelUserInterfaceMain.hasGraph()) {
            jMenuBar.setProjectRelatedButtonsEnabled(false);
            jToolBar.setProjectRelatedButtonsEnabled(false, jPanelUserInterfaceMain.getUserInterfaceState());

            jMenuBar.setUndoEnabled(false);
            jToolBar.setUndoEnabled(false);

            jMenuBar.setRedoEnabled(false);
            jToolBar.setRedoEnabled(false);

            jMenuBar.setZoomInEnabled(false);
            jToolBar.setZoomInEnabled(false);

            jMenuBar.setZoomOutEnabled(false);
            jToolBar.setZoomOutEnabled(false);

            jMenuBar.setZoomResetEnabled(false);
            jToolBar.setZoomResetEnabled(false);

            return;
        }

        jMenuBar.setProjectRelatedButtonsEnabled(true);
        jToolBar.setProjectRelatedButtonsEnabled(true, jPanelUserInterfaceMain.getUserInterfaceState());

        updateZoomButtons();
        updateUndoRedoButtons();
    }

    /**
     * Adds action listeners to View Components
     */
    private void addActionListenersToViewComponents() {

        // add listeners to Menu Bar - FILE
        ActionListener newListener = new JMenuItemNewListener();
        jMenuBar.addNewProjectActionListener(newListener);
        jToolBar.addNewProjectActionListener(newListener);
        jPanelUserInterfaceMain.addNewProjectActionListener(newListener);

        ActionListener closeListener = new JMenuItemCloseListener();
        jMenuBar.addCloseActionListener(closeListener);
        jToolBar.addCloseActionListener(closeListener);

        ActionListener openListener = new JMenuItemOpenListener();
        jMenuBar.addOpenActionListener(openListener);
        jToolBar.addOpenActionListener(openListener);
        jPanelUserInterfaceMain.addOpenProjectActionListener(openListener);

        ActionListener saveListener = new JMenuItemSaveListener();
        jMenuBar.addSaveActionListener(saveListener);
        jToolBar.addSaveActionListener(saveListener);

        ActionListener saveasListener = new JMenuItemSaveAsListener();
        jMenuBar.addSaveAsActionListener(saveasListener);
        jToolBar.addSaveAsActionListener(saveasListener);

        jMenuBar.addExitActionListener(new JMenuItemExitListener());
        // END add listeners to Menu Bar - FILE

        // add listeners to Menu Bar - EDIT
        ActionListener udnoListener = new JMenuItemUndoRedoListener();
        jMenuBar.addUndoRedoActionListener(udnoListener);
        jToolBar.addUndoRedoActionListener(udnoListener);

        // END add listeners to Menu Bar - EDIT

        // add listeners to Menu Bar - VIEW
        ActionListener zoomListener = new JMenuItemZoomListener();
        jMenuBar.addZoomActionListener(zoomListener);
        jToolBar.addZoomActionListener(zoomListener);
        // END add listeners to Menu Bar - VIEW

        // add listeners to Menu Bar - OPTIONS
        ActionListener preferencesListener = new PreferencesActionListener((MainWindowInnerInterface) this, dataLayer);
        jMenuBar.addPreferencesActionListener(preferencesListener);
        jToolBar.addPreferencesActionListener(preferencesListener);

        // END add listeners to Menu Bar - OPTIONS

        // add listeners to ToolBar editor and simulator toggle buttons
        jToolBar.addSimulatorEditorActionListener(new JMenuItemSimulatorEditorListener());
        // END add listeners to ToolBar editor and simulator toggle buttons
    }

    /**
     * Finds whether OS is windows
     *
     * @return true if windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }
}
