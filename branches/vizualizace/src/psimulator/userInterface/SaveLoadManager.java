package psimulator.userInterface;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.SaveLoadExceptionType;
import psimulator.dataLayer.SaveLoadException;
import psimulator.dataLayer.SaveLoadExceptionParametersWrapper;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelState;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SaveLoadManager {

    private DataLayerFacade dataLayer;
    private Component parentComponent;
    private JFileChooser fileChooser;
    //
    private File file;
    private long lastSavedTimestamp;

    public SaveLoadManager(Component parentComponent, DataLayerFacade dataLayer) {
        this.parentComponent = parentComponent;
        this.dataLayer = dataLayer;

        fileChooser = new JFileChooser();

        setTextsToFileChooser();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getLastSavedTimestamp() {
        return lastSavedTimestamp;
    }

    public void setLastSavedTimestamp(long lastSavedTimestamp) {
        this.lastSavedTimestamp = lastSavedTimestamp;
    }

    public void updateTextsOnFileChooser() {
        setTextsToFileChooser();
    }

    public void setLastSavedTimestamp() {
        setLastSavedTimestamp(System.currentTimeMillis());
    }

    public void setLastSavedFile(File file) {
        setFile(file);
    }

    /**
     * true if data can be lost
     *
     * @return
     */
    public boolean doCloseAction(Graph graph) {
        return doCheckIfPossibleDataLoss(graph);
    }


    /**
     *
     * @return true if data can be lost, false if cant be lost
     */
    public boolean doCheckIfPossibleDataLoss(Graph graph) {
        // if no mofifications made
        //if (!(jPanelUserInterfaceMain.hasGraph() && (jPanelUserInterfaceMain.canUndo() || jPanelUserInterfaceMain.canRedo()))) {
        if (graph == null) {
            return false;
        }

        // if timestamps say graph was not modified
        if (graph.getLastEditTimestamp() <= getLastSavedTimestamp()) {
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
            boolean result = doSaveAction(graph);
            
            // if not success
            if(result == false){
                // data can be lost
                return true;
            }else{
                // data cant be lost
                return false;
            }
        }

        return false;
    }

    
    /**
     * Shows save dialog.
     *
     */
    public boolean doSaveAsAction(Graph graph) {
        try {
            // save as
            return saveAs(graph);
        } catch (SaveLoadException ex) {
            showWarningSaveLoadError(ex.getParametersWrapper());
            return false;
        }
    }
    
    /**
     * saves without dialog
     * Returns true if succesfull
     *
     * @throws SaveLoadException
     */
    public boolean doSaveAction(Graph graph) {
        File file = getFile();

        try {
            // same as save as but do not ask the user
            if (file != null) {
                // save
                save(file, graph);
            } else { // same as save as
                // save as
                return saveAs(graph);
            }
        } catch (SaveLoadException ex) {
            showWarningSaveLoadError(ex.getParametersWrapper());
            return false;
        }
        return true;
    }

    /**
     * Shows open dialog
     */
    public Graph doOpenAction(){
        try {
            return open();
        } catch (SaveLoadException ex) {
            showWarningSaveLoadError(ex.getParametersWrapper());
            return null;
        }
    }
    
    private Graph open() throws SaveLoadException {
        int returnVal = fileChooser.showOpenDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening file: " + file);

            //Graph graph = new Graph();
            // load graph
            Graph graph = dataLayer.loadGraphFromFile(file);

            // init graph (set edit timestamp)
            //refreshUserInterfaceMainPanel(graph, UserInterfaceMainPanelState.EDITOR, false);

            // set saved timestamp and file name
            setLastSavedFile(file);

            return graph;
        }
        return null;
    }
    
    /**
     * Returns true if success
     * 
     * @param graph
     * @return
     * @throws SaveLoadException 
     */
    private boolean saveAs(Graph graph) throws SaveLoadException {
        int returnVal = fileChooser.showSaveDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Saving as file: " + file);

            // check if overwrite
            if (file.exists()) {
                int i = showWarningPossibleOverwriteDialog(dataLayer.getString("WINDOW_TITLE"), dataLayer.getString("DO_YOU_WANT_TO_OVERWRITE"));
                
                // if OK, save dialog
                if(i == JOptionPane.OK_OPTION){
                    // save
                    save(file, graph);
                    return true;
                }
                
                // if CANCEL, show dialog again
                if(i == JOptionPane.NO_OPTION){
                    return saveAs(graph);
                }
                
                // cancel or quit dialog
                return false;
            }else{
                // save
                save(file, graph);
                return true;
            }
            /*
            // overwrite file if true than yes
            if (doCheckIfOverwrite(file)) {
                // save
                save(file, graph);
                
                return true;
            } else {
                return false;
            }*/

        }
        return false;
    }

    private void save(File file, Graph graph) throws SaveLoadException {
        // only get, not remove, we want to keep the graph inside editor
        //Graph graph = jPanelUserInterfaceMain.getGraph();

        // save graph
        dataLayer.saveGraphToFile(graph, file);

        // set saved timestamp
        setLastSavedFile(file);
        setLastSavedTimestamp();
    }

    /**
     * Returs true if overwrite, false if do not
     *
     * @param file
     * @return
     */
    private boolean doCheckIfOverwrite(File file) {
        if (file.exists()) {
            if (JOptionPane.OK_OPTION == showWarningPossibleOverwriteDialog(dataLayer.getString("WINDOW_TITLE"), dataLayer.getString("DO_YOU_WANT_TO_OVERWRITE"))) {
                return true;
            }
        }
        return false;
    }

    private int showWarningPossibleDataLossDialog(String title, String message) {
        Object[] options = {dataLayer.getString("SAVE"), dataLayer.getString("DONT_SAVE"), dataLayer.getString("CANCEL")};
        int n = JOptionPane.showOptionDialog(parentComponent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        return n;
    }

    private int showWarningPossibleOverwriteDialog(String title, String message) {
        Object[] options = {dataLayer.getString("YES"), dataLayer.getString("NO"), dataLayer.getString("CANCEL")};
        int n = JOptionPane.showOptionDialog(parentComponent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        return n;
    }

    private void showWarningSaveLoadError(SaveLoadExceptionParametersWrapper parametersWrapper) {

        String title;

        if (parametersWrapper.isSaving()) {
            title = dataLayer.getString("ERROR_WHILE_SAVING");
        } else {
            title = dataLayer.getString("ERROR_WHILE_LOADING");
        }

        String message;

        switch (parametersWrapper.getSaveLoadExceptionType()) {
            case FILE_DOES_NOT_EXIST:
                message = dataLayer.getString("FILE_DOES_NOT_EXIST");
                break;
            case CANT_READ_FROM_FILE:
                message = dataLayer.getString("FILE_IS_LOCKED_FOR_READ");
                break;
            case ERROR_WHILE_READING:
                message = dataLayer.getString("ERROR_OCCOURED_WHILE_READING_FROM_FILE");
                break;
            case CANT_WRITE_TO_FILE:
                message = dataLayer.getString("FILE_IS_LOCKED_FOR_WRITE");
                break;
            case ERROR_WHILE_WRITING:
                message = dataLayer.getString("ERROR_OCCOURED_WHILE_WRITING_TO_FILE");
                break;
            case ERROR_WHILE_CREATING:
                message = dataLayer.getString("ERROR_OCCOURED_WHILE_CREATING_NEW_FILE");
                break;
            case CANNOT_OVERWRITE:
                return;
            default:
                message = "default in main window in method show warning save load error";
                break;
        }

        //message += ": " + parametersWrapper.getFileName();


        JOptionPane.showMessageDialog(parentComponent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
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
