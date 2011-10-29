package psimulator.userInterface;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.dataLayer.Enums.ToolbarIconSizeEnum;
import psimulator.dataLayer.Enums.UpdateEventType;
import psimulator.userInterface.Editor.DrawPanel.Enums.Zoom;
import psimulator.userInterface.Editor.DrawPanel.Enums.UndoRedo;

/**
 *
 * @author Martin
 */
public final class ToolBar extends JToolBar implements Observer {
    
    private DataLayerFacade dataLayer;
    
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
    
    public ToolBar(DataLayerFacade dataLayer){
        super();
        this.dataLayer = dataLayer;
        
                
        // add this ToolBar as observer to languageManager
        dataLayer.addLanguageObserver((Observer)this);
        
        // add this as observer to preferences manager
        dataLayer.addPreferencesObserver((Observer)this);
       
        // tool bar is not possible to move
        this.setFloatable(false);
        
        jButtonNew = new JButton();
        
        jButtonClose = new JButton();
        jButtonOpen = new JButton();
        jButtonSave = new JButton();
        jButtonSaveAs = new JButton();
        
        jButtonUndo = new JButton();
        jButtonUndo.setActionCommand(UndoRedo.UNDO.toString());
        
        jButtonRedo = new JButton();
        jButtonRedo.setActionCommand(UndoRedo.REDO.toString());
        
        jButtonZoomIn = new JButton();
        jButtonZoomIn.setActionCommand(Zoom.IN.toString());
        
        jButtonZoomOut = new JButton();
        jButtonZoomOut.setActionCommand(Zoom.OUT.toString());
                
        jButtonZoomReset = new JButton();
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
            // tool icon cannot be marked (ugly frame)
            c.setFocusable(false);
        }
        
        updateIconSize(dataLayer.getToolbarIconSize());
    }

    @Override
    public void update(Observable o, Object o1) {
        // find out what event type occured
        switch((UpdateEventType)o1){
            case LANGUAGE:
                this.setTextsToComponents();
                break;
            case ICON_SIZE:
                this.updateIconSize(dataLayer.getToolbarIconSize());
                break;
        }
    }
    
    /**
     * Updates images on toolbar buttons according to size
     * @param size 
     */
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
        jButtonNew.setToolTipText(dataLayer.getString("NEW_PROJECT"));
        jButtonClose.setToolTipText(dataLayer.getString("CLOSE"));
        jButtonOpen.setToolTipText(dataLayer.getString("OPEN"));
        jButtonSave.setToolTipText(dataLayer.getString("SAVE"));
        jButtonSaveAs.setToolTipText(dataLayer.getString("SAVE_AS"));
        jButtonUndo.setToolTipText(dataLayer.getString("UNDO"));
        jButtonRedo.setToolTipText(dataLayer.getString("REDO"));
        jButtonZoomIn.setToolTipText(dataLayer.getString("ZOOM_IN"));
        jButtonZoomOut.setToolTipText(dataLayer.getString("ZOOM_OUT"));
        jButtonZoomReset.setToolTipText(dataLayer.getString("ZOOM_RESET"));
    }
    
}
