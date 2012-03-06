package psimulator.dataLayer.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public final class RecentOpenedFilesManager {

    private static final String DELIMITER = ";";
    private static final int recentOpenedFilesMaxCount = 10;
    private List<File> recentOpenedFiles;

    public RecentOpenedFilesManager(String filesInString) {

        List<String> filePathsList = parseFilesStringToList(filesInString);
        recentOpenedFiles = createFilesList(filePathsList);
    }

    public List<File> getRecentOpenedFiles() {
        return recentOpenedFiles;
    }

    public void setRecentOpenedFiles(List<File> recentOpenedFiles) {
        this.recentOpenedFiles = recentOpenedFiles;
    }

    /**
     * Adds file at begining of the list. Files over max count are removed.
     * @param file 
     */
    public void addFile(File file){
        recentOpenedFiles.add(0, file);
        
        if(recentOpenedFiles.size() > recentOpenedFilesMaxCount){
            int removeCount = recentOpenedFiles.size() - recentOpenedFilesMaxCount;
            
            for(int i = 0; i < removeCount; i++){
                recentOpenedFiles.remove(recentOpenedFilesMaxCount);
            }
        }
    }
    
    /**
     * Removes not existing files from list in parameter and returns new list
     * without them.
     * 
     * @param files
     * @return 
     */
    public List<File> cleanNotExistingFiles(List<File> files){
        List<File> newFiles = new LinkedList<>();
        for(File file : files){
            if (file.exists()) {
                newFiles.add(file);
            }
        }
        return newFiles;
    }
    
    /**
     * Checks if files in parameter exists.
     * @param files
     * @return true if exists, false if some doesnt exist.
     */
    public boolean checkFilesIfExists(List<File> files){
        for(File file : files){
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Finds out whether exists files of filepaths in parameter. The ones that 
     * exists are returned.
     * @param filePathsList
     * @return 
     */
    private List<File> createFilesList(List<String> filePathsList){
        List<File> files = new LinkedList<>();
        
        for(String str : filePathsList){
            File tmpFile = new File(str);
            
            if (tmpFile.exists()) {
                files.add(tmpFile);
            }
        }
        return files;
    }

    /**
     * Parses filenames from parameter string. Ues DELIMITER.
     * @param filesInString
     * @return 
     */
    private List<String> parseFilesStringToList(String filesInString) {
        List<String> filePathsList = new ArrayList<>();

        String[] tmp = filesInString.split(DELIMITER);
        filePathsList.addAll(Arrays.asList(tmp));

        return filePathsList;
    }
}
