package psimulator.userInterface;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Network.Components.NetworkModel;
import psimulator.dataLayer.Network.Serializer.SaveLoadException;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder.GraphBuilderFacade;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SaveLoadManagerNetworkModel extends AbstractSaveLoadManager {

    public SaveLoadManagerNetworkModel(Component parentComponent, DataLayerFacade dataLayer) {
        super(parentComponent, dataLayer);
    }

    /**
     *
     * @return true if data can be lost, false if cant be lost
     */
    public boolean doCheckIfPossibleDataLoss(Graph graph) {
        if (graph == null) {
            return false;
        }

        // if timestamps say graph was not modified
        if (graph.getLastEditTimestamp() <= getLastSavedTimestamp()) {
            return false;
        }

        return true;
    }

    /**
     * asks user what to do
     *
     * @param graph
     * @return
     */
    public SaveLoadManagerUserReaction doAskUserIfSave(Graph graph) {
        //save config data
        int i = showWarningPossibleDataLossDialog(dataLayer.getString("WINDOW_TITLE"), dataLayer.getString("CLOSING_NOT_SAVED_PROJECT"));

        // if canceled
        if (i == 2 || i == -1) {
            // do nothing
            //return false;
            return SaveLoadManagerUserReaction.CANCEL;
        }
        
        // if do not save
        if( i == 1 ){
            return SaveLoadManagerUserReaction.DO_NOT_SAVE;
        }

        // if YES -> save
        if (i == 0) {
            //boolean result = doSaveGraphAction();
            return SaveLoadManagerUserReaction.DO_SAVE;
        }

        // should never happen
        return SaveLoadManagerUserReaction.CANCEL;
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
     * saves without dialog Returns true if succesfull
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

    public NetworkModel doLoadNetworkModel() {
        try {
            return open();
        } catch (SaveLoadException ex) {
            showWarningSaveLoadError(ex.getParametersWrapper());
            return null;
        }
    }

    public Graph buildGraphFromNetworkModel(NetworkModel networkModel) {
        GraphBuilderFacade graphBuilderFacade = new GraphBuilderFacade();
        Graph graph = graphBuilderFacade.buildGraph(networkModel);

        return graph;
    }

    private NetworkModel open() throws SaveLoadException {
        int returnVal = fileChooser.showOpenDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selctedFile = fileChooser.getSelectedFile();

            // load network model
            NetworkModel networkModel = dataLayer.loadNetworkModelFromFile(selctedFile);

            // set saved timestamp and file name
            setLastSavedFile(selctedFile);

            return networkModel;
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

            // check if overwrite
            if (selctedFile.exists()) {
                int i = showWarningPossibleOverwriteDialog(dataLayer.getString("WINDOW_TITLE"), dataLayer.getString("DO_YOU_WANT_TO_OVERWRITE"));

                // if OK, save dialog
                if (i == JOptionPane.OK_OPTION) {
                    // save
                    save(selctedFile);
                    return true;
                }

                // if CANCEL, show dialog again
                if (i == JOptionPane.NO_OPTION) {
                    return saveAs();
                }

                // cancel or quit dialog
                return false;
            } else {
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
