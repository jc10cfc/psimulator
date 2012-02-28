package psimulator.userInterface;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.SaveLoadException;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder.GraphBuilderFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SaveLoadManagerNetworkModel extends AbstractSaveLoadManager{

    
    public SaveLoadManagerNetworkModel(Component parentComponent, DataLayerFacade dataLayer) {
        super(parentComponent, dataLayer);
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
            boolean result = doSaveGraphAction();
            
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
    public boolean doSaveAsGraphAction() {
        try {
            // save as
            return saveAs();
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
    public boolean doSaveGraphAction() {
        File selectedFile = getFile();

        try {
            // same as save as but do not ask the user
            if (selectedFile != null) {
                // save
                save(selectedFile);
            } else { // same as save as
                // save as
                return saveAs();
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
    public Graph doOpenGraphAction(){
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
            File selctedFile = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening file: " + selctedFile);

            // load network model
            dataLayer.loadNetworkModelFromFile(selctedFile);
            
            // Build graph
            GraphBuilderFacade graphBuilderFacade = new GraphBuilderFacade();
            Graph graph = graphBuilderFacade.buildGraph(dataLayer.getNetworkFacade());
            

            // set saved timestamp and file name
            setLastSavedFile(selctedFile);

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
    private boolean saveAs() throws SaveLoadException {
        int returnVal = fileChooser.showSaveDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selctedFile = fileChooser.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Saving as file: " + selctedFile);

            // check if overwrite
            if (selctedFile.exists()) {
                int i = showWarningPossibleOverwriteDialog(dataLayer.getString("WINDOW_TITLE"), dataLayer.getString("DO_YOU_WANT_TO_OVERWRITE"));
                
                // if OK, save dialog
                if(i == JOptionPane.OK_OPTION){
                    // save
                    save(selctedFile);
                    return true;
                }
                
                // if CANCEL, show dialog again
                if(i == JOptionPane.NO_OPTION){
                    return saveAs();
                }
                
                // cancel or quit dialog
                return false;
            }else{
                // save
                save(selctedFile);
                return true;
            }
        }
        return false;
    }

    private void save(File file) throws SaveLoadException {
        // save graph
        //dataLayer.saveGraphToFile(graph, file);
        dataLayer.saveNetworkModelToFile(file);

        // set saved timestamp
        setLastSavedFile(file);
        setLastSavedTimestamp();
    }
}
