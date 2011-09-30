package psimulator.userInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.UIManager;
import psimulator.dataLayer.DataLayer;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.userInterface.Editor.Enums.Zoom;
import psimulator.dataLayer.language.LanguageManager;
import psimulator.logicLayer.Controller;
import psimulator.userInterface.ActionListeners.PreferencesActionListener;
import psimulator.userInterface.Editor.AbstractEditor;
import psimulator.userInterface.Editor.EditorPanel;
import psimulator.userInterface.Editor.Enums.UndoRedo;

/**
 *
 * @author Martin
 */
public class MainWindow extends JFrame implements MainWindowInterface {

    private DataLayer dataLayer;
    private Controller controller;
    private LanguageManager languageManager;

    /* window componenets */
    private MenuBar jMenuBar;
    private ToolBar jToolBar;
    private AbstractEditor jEditor;
    /* end of window components */

    public MainWindow(DataLayer dataLayer) {
        this.dataLayer = dataLayer;
        this.languageManager = dataLayer.getLanguageManager();

        //if OS is Windows we set the windows look and feel
        if (isWindows()) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
            }
        }

        jMenuBar = new MenuBar(languageManager);
        jToolBar = new ToolBar(languageManager);
        jEditor = new EditorPanel(this, languageManager);

        this.setTitle(languageManager.getString("WINDOW_TITLE"));

        this.setJMenuBar(jMenuBar);
        this.add(jToolBar, BorderLayout.PAGE_START);

    }

    @Override
    public void initView(Controller controller) {
        this.controller = controller;

        jEditor.init();

        updateUndoRedoButtons();
        updateZoomButtons();
        updateToolBarIconsSize(controller.getToolbarIconSize());

        addActionListenersToViewComponents();

        // set of window properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(640, 480));
        this.setVisible(true);
    }

    /**
     * Updates Undo and Redo APP buttons according to undo manager
     */
    @Override
    public void updateUndoRedoButtons() {
        jMenuBar.setUndoEnabled(jEditor.canUndo());
        jToolBar.setUndoEnabled(jEditor.canUndo());

        jMenuBar.setRedoEnabled(jEditor.canRedo());
        jToolBar.setRedoEnabled(jEditor.canRedo());

    }

    /**
     * Updates ZoomIn and ZoomOut APP buttons according to zoom manager
     */
    @Override
    public void updateZoomButtons() {
        jMenuBar.setZoomInEnabled(jEditor.canZoomIn());
        jToolBar.setZoomInEnabled(jEditor.canZoomIn());

        jMenuBar.setZoomOutEnabled(jEditor.canZoomOut());
        jToolBar.setZoomOutEnabled(jEditor.canZoomOut());
    }
    
    @Override
    public void updateToolBarIconsSize(ToolbarIconSizeEnum size){
        jToolBar.updateIconSize(size);
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
     * Action Listener for NewProject button
     */
    class JMenuItemNewListener implements ActionListener {

        /**
         * 
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("LISTENER New project");
            createJPanelEditor();
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
            //System.out.println("LISTENER Close");
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
            System.out.println("LISTENER Open");

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
            System.out.println("LISTENER Save As");
            //JOptionPane.showMessageDialog(null, "Nelze uložit jako \n\"H:\\Fotky\\2011\\dovolena\\...\\  \", protože", "Adobe Photoshop CS5", JOptionPane.ERROR_MESSAGE);
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
     * Action Listener for Exit Button
     */
    class JMenuItemExitListener implements ActionListener {

        /**
         * Saves config data and exits application.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //save config data
            System.exit(0);
        }
    }
////////------------ PRIVATE------------///////////

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
        // END add listeners to Menu Bar - EDIT

        // add listeners to Menu Bar - VIEW
        ActionListener zoomListener = new JMenuItemZoomListener();
        jMenuBar.addZoomActionListener(zoomListener);
        jToolBar.addZoomActionListener(zoomListener);
        // END add listeners to Menu Bar - VIEW

        // add listeners to Menu Bar - OPTIONS
        jMenuBar.addPreferencesActionListener(new PreferencesActionListener(this, controller));

        // END add listeners to Menu Bar - OPTIONS
    }

    /**
     * creates JPanelEditor and places it to CENTER of main window
     */
    private void createJPanelEditor() {
        this.add(jEditor, BorderLayout.CENTER);
        this.setVisible(true);
        this.repaint();
    }

    /**
     * removes JPanelEditor from main windows and deletes it
     */
    private void removeJPanelEditor() {
        this.remove(jEditor);
        this.setVisible(true);
        this.repaint();
    }

    /**
     * Finds whether OS is windows
     * @return true if windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }
}
