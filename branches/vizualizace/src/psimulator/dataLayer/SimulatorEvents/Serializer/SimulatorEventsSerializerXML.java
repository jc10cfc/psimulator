package psimulator.dataLayer.SimulatorEvents.Serializer;

import java.io.File;
import java.io.IOException;
import psimulator.dataLayer.Network.Serializer.SaveLoadExceptionType;
import psimulator.dataLayer.Network.Serializer.SaveLoadException;
import psimulator.dataLayer.Network.Serializer.SaveLoadExceptionParametersWrapper;
import psimulator.dataLayer.SimulatorEvents.SimulatorEventsWrapper;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SimulatorEventsSerializerXML implements AbstractSimulatorEventsSaveLoadInterface{

    @Override
    public void saveEventsToFile(SimulatorEventsWrapper simulatorEvents, File file) throws SaveLoadException {
         // get file name
        String fileName = file.getPath();

        // try if file exists
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.ERROR_WHILE_CREATING, fileName, true));
            }
        }
        
        // try if file readable
        if (!file.canWrite()) {
            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.CANT_WRITE_TO_FILE, fileName, true));
        }
        
        
//        // save in autoclose stream
//        try {
//            // SAVE TO XML
//        } catch (JAXBException ex) {
//            // throw exception
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.ERROR_WHILE_WRITING, fileName, true));
//        }
    }

    @Override
    public SimulatorEventsWrapper loadEventsFromFile(File file) throws SaveLoadException {
        // get file name
        String fileName = file.getPath();
        
        SimulatorEventsWrapper simulatorEvents = null;

        // try if file exists
        if(!file.exists()){
            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.FILE_DOES_NOT_EXIST, fileName, false));
        }
        
        // try if file readable
        if (!file.canRead()) {
            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.CANT_READ_FROM_FILE, fileName, false));
        }
        
//        // try read
//        try {
//            // LOAD FROM XML
//        } catch (JAXBException ex) {
//            // if needed, uncomment this line:
//            //Logger.getLogger(AbstractNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
//            
//            // throw exception
//            throw new SaveLoadException(new SaveLoadExceptionParametersWrapper(SaveLoadExceptionType.ERROR_WHILE_READING, fileName, false));
//        }
        
        return simulatorEvents;
    }
    
}
