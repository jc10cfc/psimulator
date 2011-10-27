package psimulator.userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Enums.Zoom;
import psimulator.userInterface.Editor.DrawPanel.Enums.UndoRedo;

/**
 *
 * @author Martin
 */
public class MenuBar extends JMenuBar implements Observer {

    private DataLayerFacade dataLayer;
    
    
    private JMenu jMenuFile;
    private JMenuItem jMenuItemNew;
    private JMenuItem jMenuItemClose;
    private JMenuItem jMenuItemOpen;
    private JMenuItem jMenuItemSave;        
    private JMenuItem jMenuItemSaveAs;
    private JMenuItem jMenuItemExit;
    
    private JMenu jMenuEdit;
    private JMenuItem jMenuItemUndo;        
    private JMenuItem jMenuItemRedo;
    
    private JMenu jMenuView;
    private JMenuItem jMenuItemZoomIn;
    private JMenuItem jMenuItemZoomOut;
    private JMenuItem jMenuItemZoomReset;        
    
    private JMenu jMenuOptions;
    private JMenuItem jMenuItemPreferences;

    public MenuBar(DataLayerFacade dataLayer) {
        super();
        this.dataLayer = dataLayer;

        // add this MenuBar as observer to langage manager
        dataLayer.addLanguageObserver(this);
        
        /* menu File */
        jMenuFile = new JMenu();
        
        jMenuItemNew = new JMenuItem();
        jMenuItemNew.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/filenew.png")));
        jMenuItemClose = new JMenuItem();
        jMenuItemClose.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/fileclose.png")));
        jMenuItemOpen = new JMenuItem();
        jMenuItemOpen.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/folder_green_open.png")));
        jMenuItemSave = new JMenuItem(); 
        jMenuItemSave.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/filesave.png")));
        jMenuItemSaveAs = new JMenuItem();
        jMenuItemSaveAs.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/filesaveas.png")));
        jMenuItemExit = new JMenuItem();
        
        jMenuFile.add(jMenuItemNew);
        jMenuFile.add(jMenuItemClose);
        jMenuFile.addSeparator();
        jMenuFile.add(jMenuItemOpen);
        jMenuFile.add(jMenuItemSave);
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.addSeparator();
        jMenuFile.add(jMenuItemExit);
        /* END menu File */

        /* menu Edit */
        jMenuEdit = new JMenu();
        
        jMenuItemUndo = new JMenuItem();
        jMenuItemUndo.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/undo.png")));
        jMenuItemUndo.setActionCommand(UndoRedo.UNDO.toString());
        
        jMenuItemRedo = new JMenuItem();
        jMenuItemRedo.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/redo.png")));
        jMenuItemRedo.setActionCommand(UndoRedo.REDO.toString());
        
        jMenuEdit.add(jMenuItemUndo);
        jMenuEdit.add(jMenuItemRedo);
        /* END menu Edit */
        
        /* menu View */
        jMenuView = new JMenu();
        
        jMenuItemZoomIn = new JMenuItem();
        jMenuItemZoomIn.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/viewmag+.png")));
        jMenuItemZoomIn.setActionCommand(Zoom.IN.toString());
        jMenuItemZoomOut = new JMenuItem();
        jMenuItemZoomOut.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/viewmag-.png")));
        jMenuItemZoomOut.setActionCommand(Zoom.OUT.toString());
        jMenuItemZoomReset = new JMenuItem();
        jMenuItemZoomReset.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/viewmag1.png")));
        jMenuItemZoomReset.setActionCommand(Zoom.RESET.toString());
        
        jMenuView.add(jMenuItemZoomIn);
        jMenuView.add(jMenuItemZoomOut);
        jMenuView.add(jMenuItemZoomReset);
        /* END menu View */
        
        /* menu Options */
        jMenuOptions = new JMenu();
        jMenuItemPreferences = new JMenuItem();
        jMenuItemPreferences.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/16/configure.png")));
        jMenuOptions.add(jMenuItemPreferences);
        /* END menu Options */
        
        
        /* add menus to menu bar */
        this.add(jMenuFile);
        this.add(jMenuEdit);
        this.add(jMenuView);
        this.add(jMenuOptions);

        /* set texts to menu items */
        setTextsToComponents();
    }

    private void setTextsToComponents() {
        /* menu File */
        jMenuFile.setText(dataLayer.getString("FILE"));
        jMenuFile.setMnemonic(dataLayer.getString("FILE_mnemonic").charAt(0));

        jMenuItemNew.setText(dataLayer.getString("NEW_PROJECT"));
        jMenuItemNew.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("NEW_PROJECT_mnemonic").charAt(0), InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        jMenuItemClose.setText(dataLayer.getString("CLOSE"));
        jMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("CLOSE_mnemonic").charAt(0), ActionEvent.CTRL_MASK));
        jMenuItemOpen.setText(dataLayer.getString("OPEN"));
        jMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("OPEN_mnemonic").charAt(0), InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        jMenuItemSave.setText(dataLayer.getString("SAVE"));
        jMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("SAVE_mnemonic").charAt(0), ActionEvent.CTRL_MASK));
        jMenuItemSaveAs.setText(dataLayer.getString("SAVE_AS"));
        jMenuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("SAVE_AS_mnemonic").charAt(0), InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        
        jMenuItemExit.setText(dataLayer.getString("EXIT"));
        jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("EXIT_mnemonic").charAt(0), ActionEvent.CTRL_MASK));
        /* END menu File */
        
        /* menu Edit */
        jMenuEdit.setText(dataLayer.getString("EDIT"));
        jMenuEdit.setMnemonic(dataLayer.getString("EDIT_mnemonic").charAt(0));
        
        jMenuItemUndo.setText(dataLayer.getString("UNDO"));
        jMenuItemUndo.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("UNDO_mnemonic").charAt(0), InputEvent.CTRL_DOWN_MASK));
        
        jMenuItemRedo.setText(dataLayer.getString("REDO"));
        jMenuItemRedo.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("REDO_mnemonic").charAt(0), InputEvent.CTRL_DOWN_MASK));
        /* END menu Edit */
        
        /* menu View */
        jMenuView.setText(dataLayer.getString("VIEW"));
        jMenuView.setMnemonic(dataLayer.getString("VIEW_mnemonic").charAt(0));
        
        jMenuItemZoomIn.setText(dataLayer.getString("ZOOM_IN"));
        jMenuItemZoomOut.setText(dataLayer.getString("ZOOM_OUT"));
        jMenuItemZoomReset.setText(dataLayer.getString("ZOOM_RESET"));
        /* END menu View */
        
        /* menu Options */
        jMenuOptions.setText(dataLayer.getString("OPTIONS"));
        jMenuOptions.setMnemonic(dataLayer.getString("OPTIONS_mnemonic").charAt(0));
        
        jMenuItemPreferences.setText(dataLayer.getString("PREFERENCES"));
        jMenuItemPreferences.setAccelerator(KeyStroke.getKeyStroke(dataLayer.getString("PREFERENCES_mnemonic").charAt(0), ActionEvent.CTRL_MASK));
        /* END menu Options */

        
    }

    @Override
    public void update(Observable o, Object o1) {
        this.setTextsToComponents();
    }
    
    
    public void setUndoEnabled(boolean enabled){
        jMenuItemUndo.setEnabled(enabled);
    }
    
    public void setRedoEnabled(boolean enabled){
        jMenuItemRedo.setEnabled(enabled);
    }
    
    public void setZoomInEnabled(boolean enabled){
        jMenuItemZoomIn.setEnabled(enabled);
    }
    
    public void setZoomOutEnabled(boolean enabled){
        jMenuItemZoomOut.setEnabled(enabled);
    }

    /**
     * Adds action listener to jMenuItemNew
     * @param listener Action listener
     */
    public void addNewProjectActionListener(ActionListener listener){
        jMenuItemNew.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jButtonClose
     * @param listener Action listener
     */
    public void addCloseActionListener(ActionListener listener){
        jMenuItemClose.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jMenuItemOpen
     * @param listener Action listener
     */
    public void addOpenActionListener(ActionListener listener){
        jMenuItemOpen.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jMenuItemSave
     * @param listener Action listener
     */
    public void addSaveActionListener(ActionListener listener){
        jMenuItemSave.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jMenuItemSaveAs
     * @param listener Action listener
     */
    public void addSaveAsActionListener(ActionListener listener){
        jMenuItemSaveAs.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jMenuItem Exit
     * @param listener Action listener
     */
    public void addExitActionListener(ActionListener listener){
        jMenuItemExit.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jMenuItemUndo and jMenuItemRedo
     * @param listener Action listener
     */
    public void addUndoRedoActionListener(ActionListener listener){
        jMenuItemUndo.addActionListener(listener);
        jMenuItemRedo.addActionListener(listener);
    }
    
    /**
     * Adds action listener to Zoom menu items
     * @param listener Action listener
     */
    public void addZoomActionListener(ActionListener listener){
        jMenuItemZoomIn.addActionListener(listener);
        jMenuItemZoomOut.addActionListener(listener);
        jMenuItemZoomReset.addActionListener(listener);
    }
    
    /**
     * Adds action listener to Preferences jMenuItem
     * @param listener Action listener
     */
    public void addPreferencesActionListener(ActionListener listener){
        jMenuItemPreferences.addActionListener(listener);
    }
}
