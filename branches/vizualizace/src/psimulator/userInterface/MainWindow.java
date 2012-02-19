package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.logicLayer.ControllerFacade;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.UndoRedo;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.Zoom;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanel;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelState;
import psimulator.userInterface.actionListerners.PreferencesActionListener;
import psimulator.userInterface.imageFactories.AbstractImageFactory;
import psimulator.userInterface.imageFactories.AwtImageFactory;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MainWindow extends JFrame implements MainWindowInnerInterface, UserInterfaceOuterFacade, Observer {

    private DataLayerFacade dataLayer;
    private ControllerFacade controller;
    private AbstractImageFactory imageFactory;
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
    private JFileChooser fileChooser;
    
    private GlassPanelPainter glassPanelPainter;
    private MainWindowGlassPane glassPane;
    //private Component originalGlassPane;
    private long lastSavedTimestamp;

    public MainWindow(DataLayerFacade dataLayer) {
        this.dataLayer = dataLayer;

        try {
            //if OS is Windows we set the windows look and feel
            if (isWindows()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else { // otherwise we set metal look and feel
                //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }

        } catch (Exception e) {
        }

        jMenuBar = new MenuBar(dataLayer);
        jToolBar = new ToolBar(dataLayer);

        this.setTitle(dataLayer.getString("WINDOW_TITLE"));

        this.setJMenuBar(jMenuBar);
        this.add(jToolBar, BorderLayout.PAGE_START);

        this.mainWindow = (JFrame) this;

        this.imageFactory = new AwtImageFactory();

        jPanelUserInterfaceMain = new UserInterfaceMainPanel(this, dataLayer, imageFactory, UserInterfaceMainPanelState.WELCOME);
        this.add(jPanelUserInterfaceMain, BorderLayout.CENTER);


        // set this as Observer to LanguageManager
        dataLayer.addLanguageObserver((Observer) this);

        this.setIconImage(imageFactory.getImageIconForToolbar(MainTool.ADD_REAL_PC).getImage());
        
        
        // create glass pane and glass pane painter
        glassPane = new MainWindowGlassPane((UserInterfaceMainPanel)jPanelUserInterfaceMain);
        
        glassPanelPainter = new GlassPanelPainter(glassPane, jPanelUserInterfaceMain);
        //originalGlassPane = this.getGlassPane();
        
        this.setGlassPane(glassPane);
        glassPane.setOpaque(false);
        getGlassPane().setVisible(true);
        
    }

    @Override
    public void initView(ControllerFacade controller) {
        this.controller = controller;

        fileChooser = new JFileChooser();

        // set translated texts to file chooser
        setTextsToFileChooser();

        updateProjectRelatedButtons();

        updateToolBarIconsSize(dataLayer.getToolbarIconSize());

        addActionListenersToViewComponents();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                doCloseAction();
            }
        });
        
        // set of window properties
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(new Dimension(1024, 768));
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
     * reaction to Language Observable update
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        setTextsToFileChooser();
    }

    @Override
    public GlassPanelPainter getGlassPanelPainter() {
        return glassPanelPainter;
    }

    @Override
    public AnimationPanelOuterInterface getAnimationPanelOuterInterface(){
        return jPanelUserInterfaceMain.getAnimationPanelOuterInterface();
    }
    
    @Override
    public Component getMainWindowComponent() {
        return this;
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
     * Action Listener for Zoom buttons
     */
    class JMenuItemSimulatorEditorListener implements ActionListener {

        /**
         * calls zoom operation on jPanelEditor according to actionCommand
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
                    refreshUserInterfaceMainPanel(null, UserInterfaceMainPanelState.EDITOR, true);

                    break;
                case SIMULATOR:
                    // if nothing changed, do nothing
                    if (jPanelUserInterfaceMain.getUserInterfaceState() == UserInterfaceMainPanelState.SIMULATOR) {
                        return;
                    }
                    
                    // change state to editor without changing or removing the graph
                    refreshUserInterfaceMainPanel(null, UserInterfaceMainPanelState.SIMULATOR, true);

                    break;
            }
        }
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
            // if data can be lost
            if (doCheckPossibleDataLoss()) {
                return;
            }

            refreshUserInterfaceMainPanel(new Graph(), UserInterfaceMainPanelState.EDITOR, false);
            
            // set saved timestamp
            setLastSavedTimestampNow();
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
            // if data can be lost
            if (doCheckPossibleDataLoss()) {
                return;
            }

            refreshUserInterfaceMainPanel(null, UserInterfaceMainPanelState.WELCOME, false);
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
            // if data can be lost
            if (doCheckPossibleDataLoss()) {
                return;
            }

            doOpenAction();
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
            System.out.println("LISTENER Save");

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

            // if save successfull
            if (doSaveAsAction()) {
                System.out.println("save ok");
            } else {
                System.out.println("save problem");
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
            doCloseAction();
        }
    }
////////------------ PRIVATE------------///////////

    /**
     * checks possible data loss and closes window
     */
    private void doCloseAction() {
        if (doCheckPossibleDataLoss()) {
            return;
        }
        // exit
        System.exit(0);
    }

    /**
     *
     * @return true if data can be lost, false if cant be lost
     */
    private boolean doCheckPossibleDataLoss() {
        // if no mofifications made
        if (!(jPanelUserInterfaceMain.hasGraph() && (jPanelUserInterfaceMain.canUndo() || jPanelUserInterfaceMain.canRedo()))) {
            return false;
        }
        
        // if timestamps say graph was not modified
        if(jPanelUserInterfaceMain.getGraph().getLastEditTimestamp() <= lastSavedTimestamp){
            return false;
        }

        //save config data
        int i = showWarningPossibleDataLossDialog(dataLayer.getString("WINDOW_TITLE"), dataLayer.getString("CLOSING_NOT_SAVED_PROJECT"));

        // if canceled
        if (i == 2 || i == -1) {
            // do nothing
            return true;
        }

        // if YES -> save
        if (i == 0) {
            // if save not successfull
            if (!doSaveAsAction()) {
                // do nothing
                System.out.println("ukladani se nepovedlo");
                return true;
            }
        }

        return false;
    }

    /**
     * Shows save dialog.
     *
     * @return true if succesfully saved, false if not
     */
    private boolean doSaveAsAction() {
        int returnVal = fileChooser.showSaveDialog(mainWindow);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Saving file: " + file);

            // only get, not remove, we want to keep the graph inside editor
            Graph graph = jPanelUserInterfaceMain.getGraph();
            
            // save graph
            dataLayer.saveGraphToFile(graph, file);
            
            // set saved timestamp
            setLastSavedTimestampNow();
            
            return true;
        }
        return false;
    }

    /**
     * Shows open dialog
     */
    private void doOpenAction() {
        int returnVal = fileChooser.showOpenDialog(mainWindow);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening file: " + file);

            //Graph graph = new Graph();
            // load graph
            Graph graph = dataLayer.loadGraphFromFile(file);
            
            // init graph (set edit timestamp)
            refreshUserInterfaceMainPanel(graph, UserInterfaceMainPanelState.EDITOR, false);
            
            // set saved timestamp
            setLastSavedTimestampNow();
        }
    }

    private void setLastSavedTimestampNow(){
        lastSavedTimestamp = System.currentTimeMillis();
    }
    
    /**
     * Updates jPanelUserInterfaceMain according to userInterfaceState. If
     * changing to SIMULATOR or EDITOR state, graph cannot be null.
     *
     * @param graph Graph to set into jPanelUserInterfaceMain, can be null if
     * userInterfaceState will be WELCOME
     * @param userInterfaceState State to change to.
     * @param changingSimulatorEditor if true, the graph is kept untouched
     */
    private void refreshUserInterfaceMainPanel(Graph graph, UserInterfaceMainPanelState userInterfaceState, boolean changingSimulatorEditor) {
        switch (userInterfaceState) {
            case WELCOME:
                // remove graph
                jPanelUserInterfaceMain.removeGraph();
                break;
            case EDITOR:
                // if not changing from simulator to editor or back
                if (!changingSimulatorEditor) {
                    // delete events from simulator
                    jPanelUserInterfaceMain.init();
                    // remove graph
                    jPanelUserInterfaceMain.removeGraph();
                    // set another graph
                    jPanelUserInterfaceMain.setGraph(graph);
                }

                // set Editor selected in tool bar
                jToolBar.setEditorSelected(true);
                break;
            case SIMULATOR:
                // if not changing from simulator to editor or back
                if (!changingSimulatorEditor) {
                    // delete events from simulator
                    jPanelUserInterfaceMain.init();
                    // remove graph
                    jPanelUserInterfaceMain.removeGraph();
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

    private int showWarningPossibleDataLossDialog(String title, String message) {
        Object[] options = {dataLayer.getString("SAVE"), dataLayer.getString("DONT_SAVE"), dataLayer.getString("CANCEL")};
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

        //jMenuBar.addDeleteListener();
        //jMenuBar.addSelectAllListener();
        // END add listeners to Menu Bar - EDIT

        // add listeners to Menu Bar - VIEW
        ActionListener zoomListener = new JMenuItemZoomListener();
        jMenuBar.addZoomActionListener(zoomListener);
        jToolBar.addZoomActionListener(zoomListener);
        // END add listeners to Menu Bar - VIEW

        // add listeners to Menu Bar - OPTIONS
        ActionListener preferencesListener = new PreferencesActionListener(this, dataLayer);
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

    /**
     * Sets internationalized texts to file chooser
     */
    private void setTextsToFileChooser() {
        UIManager.put("FileChooser.lookInLabelText", dataLayer.getString("FILE_CHOOSER_LOOK_IN"));
        UIManager.put("FileChooser.filesOfTypeLabelText", dataLayer.getString("FILE_CHOOSER_FILES_OF_TYPE"));
        UIManager.put("FileChooser.upFolderToolTipText", dataLayer.getString("FILE_CHOOSER_UP_FOLDER"));

        UIManager.put("FileChooser.fileNameLabelText", dataLayer.getString("FILE_CHOOSER_FILE_NAME"));
        UIManager.put("FileChooser.homeFolderToolTipText", dataLayer.getString("FILE_CHOOSER_HOME_FOLDER"));
        UIManager.put("FileChooser.newFolderToolTipText", dataLayer.getString("FILE_CHOOSER_NEW_FOLDER"));
        UIManager.put("FileChooser.listViewButtonToolTipTextlist", dataLayer.getString("FILE_CHOOSER_LIST_VIEW"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", dataLayer.getString("FILE_CHOOSER_DETAILS_VIEW"));
        UIManager.put("FileChooser.saveButtonText", dataLayer.getString("FILE_CHOOSER_SAVE"));
        UIManager.put("FileChooser.openButtonText", dataLayer.getString("FILE_CHOOSER_OPEN"));
        UIManager.put("FileChooser.cancelButtonText", dataLayer.getString("FILE_CHOOSER_CANCEL"));
        UIManager.put("FileChooser.updateButtonText=", dataLayer.getString("FILE_CHOOSER_UPDATE"));
        UIManager.put("FileChooser.helpButtonText", dataLayer.getString("FILE_CHOOSER_HELP"));
        UIManager.put("FileChooser.saveButtonToolTipText", dataLayer.getString("FILE_CHOOSER_SAVE"));
        UIManager.put("FileChooser.openButtonToolTipText", dataLayer.getString("FILE_CHOOSER_OPEN"));
        UIManager.put("FileChooser.cancelButtonToolTipText", dataLayer.getString("FILE_CHOOSER_CANCEL"));
        UIManager.put("FileChooser.updateButtonToolTipText", dataLayer.getString("FILE_CHOOSER_UPDATE"));
        UIManager.put("FileChooser.helpButtonToolTipText", dataLayer.getString("FILE_CHOOSER_HELP"));


        UIManager.put("FileChooser.openDialogTitleText", dataLayer.getString("FILE_CHOOSER_OPEN"));
        UIManager.put("FileChooser.saveDialogTitleText", dataLayer.getString("FILE_CHOOSER_SAVE"));
        UIManager.put("FileChooser.fileNameHeaderText", dataLayer.getString("FILE_CHOOSER_FILE_NAME"));
        UIManager.put("FileChooser.newFolderButtonText", dataLayer.getString("FILE_CHOOSER_NEW_FOLDER"));

        UIManager.put("FileChooser.renameFileButtonText", dataLayer.getString("FILE_CHOOSER_RENAME_FILE"));
        UIManager.put("FileChooser.deleteFileButtonText", dataLayer.getString("FILE_CHOOSER_DELETE_FILE"));
        UIManager.put("FileChooser.filterLabelText", dataLayer.getString("FILE_CHOOSER_FILE_TYPES"));
        UIManager.put("FileChooser.fileSizeHeaderText", dataLayer.getString("FILE_CHOOSER_SIZE"));
        UIManager.put("FileChooser.fileDateHeaderText", dataLayer.getString("FILE_CHOOSER_DATE_MODIFIED"));

        UIManager.put("FileChooser.saveInLabelText", dataLayer.getString("FILE_CHOOSER_LOOK_IN"));
        UIManager.put("FileChooser.acceptAllFileFilterText", dataLayer.getString("FILE_CHOOSER_ACCEPT_FILES"));

        // let fileChooser to update according to current look and feel = it loads texts againt
        fileChooser.updateUI();
    }
}
