package psimulator.userInterface;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.userInterface.Editor.Enums.Zoom;
import psimulator.dataLayer.language.LanguageManager;
import psimulator.userInterface.Editor.Enums.UndoRedo;

/**
 *
 * @author Martin
 */
public class ToolBar extends JToolBar implements Observer {
    
    private LanguageManager languageManager;
    
    private JButton jButtonNew;
    private JButton jButtonClose;
    private JButton jButtonOpen;
    private JButton jButtonSave;
    private JButton jButtonSaveAs;
    private JButton jButtonUndo;
    private JButton jButtonRedo;
    private JButton jButtonZoomIn;
    private JButton jButtonZoomOut;
    private JButton jButtonZoomReset;
    
    //private Color backgroundColor = new Color(198, 83, 83);
    
    public ToolBar(LanguageManager languageManager){
        super();
        this.languageManager = languageManager;
        
                
        // add this ToolBar as observer to languageManager
        languageManager.addObserver(this);
        
        // tool bar is not possible to move
        this.setFloatable(false);
        
        
        jButtonNew = new JButton();
        jButtonNew.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/filenew.png")));
        
        jButtonClose = new JButton();
        jButtonClose.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/fileclose.png")));
        
        jButtonOpen = new JButton();
        jButtonOpen.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/folder_green_open.png")));
        
        jButtonSave = new JButton();
        jButtonSave.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/filesave.png")));
        
        jButtonSaveAs = new JButton();
        jButtonSaveAs.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/filesaveas.png")));
        
        jButtonUndo = new JButton();
        jButtonUndo.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/undo.png")));
        jButtonUndo.setActionCommand(UndoRedo.UNDO.toString());
        
        jButtonRedo = new JButton();
        jButtonRedo.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/redo.png")));
        jButtonRedo.setActionCommand(UndoRedo.REDO.toString());
        
        jButtonZoomIn = new JButton();
        jButtonZoomIn.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/viewmag+.png")));
        jButtonZoomIn.setActionCommand(Zoom.IN.toString());
        
        jButtonZoomOut = new JButton();
        jButtonZoomOut.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/viewmag-.png")));
        jButtonZoomOut.setActionCommand(Zoom.OUT.toString());
                
        jButtonZoomReset = new JButton();
        jButtonZoomReset.setIcon(new ImageIcon(getClass().getResource("/resources/toolbarIcons/32/viewmag1.png")));
        jButtonZoomReset.setActionCommand(Zoom.RESET.toString());
                
        this.add(jButtonNew);
        this.add(jButtonClose);
        this.addSeparator();
        
        this.add(jButtonOpen);
        this.add(jButtonSave);
        this.add(jButtonSaveAs);
        this.addSeparator();
        
        this.add(jButtonUndo);
        this.add(jButtonRedo);
        this.addSeparator();
        
        this.add(jButtonZoomIn);
        this.add(jButtonZoomOut);
        this.add(jButtonZoomReset);
        this.addSeparator();
        
        //jButtonSave.setEnabled(false);
        
        setTextsToComponents();
        
        // apply background color
        this.setBackground(ColorMixerSignleton.mainToolbarColor);
        Component [] comp = this.getComponents();
        for(Component c : comp){
            c.setBackground(ColorMixerSignleton.mainToolbarColor);
        }
    }

    @Override
    public void update(Observable o, Object o1) {
        setTextsToComponents();
    }
    
    public void updateIconSize(ToolbarIconSizeEnum size){
        String path = "/resources/toolbarIcons/";
        
        switch(size){
            case SMALL:
                path += "16";
                break;
            case MEDIUM:
                path += "32";
                break;
            case LARGE:
                path += "48";
                break;
        }
        path += "/";
        
        jButtonNew.setIcon(new ImageIcon(getClass().getResource(path+"filenew.png")));
        jButtonClose.setIcon(new ImageIcon(getClass().getResource(path+"fileclose.png")));
        jButtonOpen.setIcon(new ImageIcon(getClass().getResource(path+"folder_green_open.png")));
        jButtonSave.setIcon(new ImageIcon(getClass().getResource(path+"filesave.png")));
        jButtonSaveAs.setIcon(new ImageIcon(getClass().getResource(path+"filesaveas.png")));
        jButtonUndo.setIcon(new ImageIcon(getClass().getResource(path+"undo.png")));
        jButtonRedo.setIcon(new ImageIcon(getClass().getResource(path+"redo.png")));
        jButtonZoomIn.setIcon(new ImageIcon(getClass().getResource(path+"viewmag+.png")));
        jButtonZoomOut.setIcon(new ImageIcon(getClass().getResource(path+"viewmag-.png")));
        jButtonZoomReset.setIcon(new ImageIcon(getClass().getResource(path+"viewmag1.png")));
    }
    
    
    public void setUndoEnabled(boolean enabled){
        jButtonUndo.setEnabled(enabled);
    }
    
    public void setRedoEnabled(boolean enabled){
        jButtonRedo.setEnabled(enabled);
    }
    
    public void setZoomInEnabled(boolean enabled){
        jButtonZoomIn.setEnabled(enabled);
    }
    
    public void setZoomOutEnabled(boolean enabled){
        jButtonZoomOut.setEnabled(enabled);
    }

    /**
     * Adds action listener to jButtonNew
     * @param listener Action listener
     */
    public void addNewProjectActionListener(ActionListener listener){
        jButtonNew.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jButtonClose
     * @param listener Action listener
     */
    public void addCloseActionListener(ActionListener listener){
        jButtonClose.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jButtonOpen
     * @param listener Action listener
     */
    public void addOpenActionListener(ActionListener listener){
        jButtonOpen.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jButtonSave
     * @param listener Action listener
     */
    public void addSaveActionListener(ActionListener listener){
        jButtonSave.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jButtonSaveAs
     * @param listener Action listener
     */
    public void addSaveAsActionListener(ActionListener listener){
        jButtonSaveAs.addActionListener(listener);
    }
    
    /**
     * Adds action listener to jButtonUndo and jButtonRedo
     * @param listener Action listener
     */
    public void addUndoRedoActionListener(ActionListener listener){
        jButtonUndo.addActionListener(listener);
        jButtonRedo.addActionListener(listener);
    }
    
    /**
     * Adds action listener to Zoom Buttons
     * @param listener Action listener
     */
    public void addZoomActionListener(ActionListener listener){
        jButtonZoomIn.addActionListener(listener);
        jButtonZoomOut.addActionListener(listener);
        jButtonZoomReset.addActionListener(listener);
    }


    
    ////////------------ PRIVATE------------///////////
    
    private void setTextsToComponents() {
        jButtonNew.setToolTipText(languageManager.getString("NEW_PROJECT"));
        jButtonClose.setToolTipText(languageManager.getString("CLOSE"));
        jButtonOpen.setToolTipText(languageManager.getString("OPEN"));
        jButtonSave.setToolTipText(languageManager.getString("SAVE"));
        jButtonSaveAs.setToolTipText(languageManager.getString("SAVE_AS"));
        jButtonUndo.setToolTipText(languageManager.getString("UNDO"));
        jButtonRedo.setToolTipText(languageManager.getString("REDO"));
        jButtonZoomIn.setToolTipText(languageManager.getString("ZOOM_IN"));
        jButtonZoomOut.setToolTipText(languageManager.getString("ZOOM_OUT"));
        jButtonZoomReset.setToolTipText(languageManager.getString("ZOOM_RESET"));
    }
    
}
