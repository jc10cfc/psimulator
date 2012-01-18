package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.logicLayer.ControllerFacade;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.Enums.Zoom;
import psimulator.userInterface.actionListerners.PreferencesActionListener;
import psimulator.userInterface.Editor.EditorOuterInterface;
import psimulator.userInterface.Editor.DrawPanel.Enums.UndoRedo;
import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import psimulator.userInterface.Editor.EditorPanel;
import psimulator.userInterface.Simulator.SimulatorControlPanel;
import psimulator.userInterface.imageFactories.AbstractImageFactory;
import psimulator.userInterface.imageFactories.AwtImageFactory;

/**
 *
 * @author Martin
 */
public class MainWindow extends JFrame implements MainWindowInnerInterface, UserInterfaceOuterFacade, Observer {

    private DataLayerFacade dataLayer;
    private ControllerFacade controller;
    private AbstractImageFactory imageFactory;
    /* window componenets */
    private MenuBar jMenuBar;
    private ToolBar jToolBar;
    private EditorOuterInterface jEditor;
    /* end of window components */
    private JFrame parentForCompoents;
    private JFileChooser fileChooser;

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

        this.parentForCompoents = (JFrame) this;

        this.imageFactory = new AwtImageFactory();

        jEditor = new EditorPanel(this, dataLayer, imageFactory);
        
        //
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        JPanel simulatorPanel = new SimulatorControlPanel();
        this.add(simulatorPanel, BorderLayout.EAST);
        //
        
        // set this as Observer to LanguageManager
        dataLayer.addLanguageObserver((Observer) this);

        this.setIconImage(imageFactory.getImageIconForToolbar(MainTool.ADD_REAL_PC).getImage());
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
        this.setMinimumSize(new Dimension(640, 480));
        this.setSize(new Dimension(800, 600));
        this.setVisible(true);
    }

    @Override
    public void updateUndoRedoButtons() {
        jMenuBar.setUndoEnabled(jEditor.canUndo());
        jToolBar.setUndoEnabled(jEditor.canUndo());

        jMenuBar.setRedoEnabled(jEditor.canRedo());
        jToolBar.setRedoEnabled(jEditor.canRedo());

    }

    @Override
    public void updateZoomButtons() {
        jMenuBar.setZoomInEnabled(jEditor.canZoomIn());
        jToolBar.setZoomInEnabled(jEditor.canZoomIn());

        jMenuBar.setZoomOutEnabled(jEditor.canZoomOut());
        jToolBar.setZoomOutEnabled(jEditor.canZoomOut());

        jMenuBar.setZoomResetEnabled(true);
        jToolBar.setZoomResetEnabled(true);
    }

    @Override
    public void updateToolBarIconsSize(ToolbarIconSizeEnum size) {
        jToolBar.updateIconSize(size);
    }

    /**
     * reaction to Language Observable update
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        setTextsToFileChooser();
    }

    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for Undo and Redo button
     */
    class JMenuItemUndoRedoListener implements ActionListener {

        /**
         * calls undo or redo and repaint on jPanelEditor, updates Undo and Redo buttons
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (UndoRedo.valueOf(e.getActionCommand())) {
                case UNDO:
                    jEditor.undo();
                    break;
                case REDO:
                    jEditor.redo();
                    break;
            }
            updateUndoRedoButtons();
            jEditor.repaint();
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
                    jEditor.zoomIn();
                    break;
                case OUT:
                    jEditor.zoomOut();
                    break;
                case RESET:
                    jEditor.zoomReset();
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

            // if has graph
            if (jEditor.hasGraph()) {
                removeJPanelEditor();
            }

            initJPanelEditor(new Graph());
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

            removeJPanelEditor();
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

            // if has graph
            if (jEditor.hasGraph()) {
                removeJPanelEditor();
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
        if (!(jEditor.hasGraph() && (jEditor.canUndo() || jEditor.canRedo()))) {
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
     * @return true if succesfully saved, false if not
     */
    private boolean doSaveAsAction() {
        int returnVal = fileChooser.showSaveDialog(parentForCompoents);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Saving file: " + file);

            Graph graph = jEditor.getGraph();

            return true;
        }
        return false;
    }

    /**
     * Shows open dialog
     */
    private void doOpenAction() {
        int returnVal = fileChooser.showOpenDialog(parentForCompoents);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening file: " + file);

            Graph graph = new Graph();
            initJPanelEditor(graph);
        }
    }

    /**
     * creates JPanelEditor and places it to CENTER of main window
     */
    private void initJPanelEditor(Graph graph) {
        // set graph to editor    
        jEditor.setGraph(graph);

        // update buttons
        updateProjectRelatedButtons();

        // add editor to framve
        this.add(jEditor, BorderLayout.CENTER);
        this.setVisible(true);
        this.repaint();
    }

    /**
     * removes JPanelEditor from main windows and deletes it
     */
    private void removeJPanelEditor() {
        // remove graph from editor
        jEditor.removeGraph();

        // remove editor from frame
        this.remove(jEditor);

        // update buttons
        updateProjectRelatedButtons();

        // update main frame
        this.setVisible(true);
        this.repaint();
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
        if (!jEditor.hasGraph()) {
            jMenuBar.setProjectRelatedButtonsEnabled(false);
            jToolBar.setProjectRelatedButtonsEnabled(false);

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
        jToolBar.setProjectRelatedButtonsEnabled(true);

        updateZoomButtons();
    }

    /**
     * Adds action listeners to View Components
     */
    private void addActionListenersToViewComponents() {

        // add listeners to Menu Bar - FILE
        ActionListener newListener = new JMenuItemNewListener();
        jMenuBar.addNewProjectActionListener(newListener);
        jToolBar.addNewProjectActionListener(newListener);

        ActionListener closeListener = new JMenuItemCloseListener();
        jMenuBar.addCloseActionListener(closeListener);
        jToolBar.addCloseActionListener(closeListener);

        ActionListener openListener = new JMenuItemOpenListener();
        jMenuBar.addOpenActionListener(openListener);
        jToolBar.addOpenActionListener(openListener);

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
        jMenuBar.addPreferencesActionListener(new PreferencesActionListener(this, dataLayer));

        // END add listeners to Menu Bar - OPTIONS
    }

    /**
     * Finds whether OS is windows
     * @return true if windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }

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
