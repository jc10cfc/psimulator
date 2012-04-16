package filesystem;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsEntryNotFoundException;
import de.schlichtherle.truezip.fs.FsSyncException;
import de.schlichtherle.truezip.fs.archive.zip.JarDriver;
import de.schlichtherle.truezip.nio.file.TPath;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;
import filesystem.dataStructures.Node;
import filesystem.dataStructures.jobs.InputFileJob;
import filesystem.dataStructures.jobs.OutputFileJob;
import filesystem.exceptions.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import logging.Logger;
import logging.LoggingCategory;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class ArchiveFileSystem implements FileSystem {

	String pathToFileSystem;
	TPath archive;

	public static String getFileSystemExtension() {
		return "fsm";
	}

	public ArchiveFileSystem(String pathToFileSystem) {

		this.pathToFileSystem = pathToFileSystem;
		TConfig config = TConfig.get();
		config.setArchiveDetector(new TArchiveDetector(
				getFileSystemExtension(), // file system file extension
				new JarDriver(IOPoolLocator.SINGLETON)));

		TConfig.push();

		archive = new TPath(pathToFileSystem);

		TFile archiveFile = archive.toFile();

		if (!archiveFile.exists() && !archiveFile.mkdirs()) // if archive doesnt exist and cannot create empty one
		{
			System.err.println("mkdir failed");
		}


		if (!archiveFile.isArchive() || !archiveFile.isDirectory()) {
			System.err.println("file: " + pathToFileSystem + " is not compatible archive ");
		}

	}

	@Override
	public boolean rm_r(String path) throws FileNotFoundException {
		TFile file = getRelativeTFile(path);

		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		try {
			file.rm_r();
		} catch (IOException ex) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isFile(String path) {
		TFile file = new TFile(pathToFileSystem + path);

		if (!file.exists()) {
			return false;
		}

		return file.isFile();
	}

	@Override
	public boolean isDir(String path) {
		TFile file = new TFile(pathToFileSystem + path);

		if (!file.exists()) {
			return false;
		}

		return file.isDirectory();
	}

	@Override
	public boolean exists(String path) {

		return new TFile(pathToFileSystem + path).exists();
	}

	@Override
	public void umount() {
		try {
			TVFS.umount(archive.toFile());
		} catch (FsSyncException ex) {
			System.err.println("FsSyncException occured when umounting filesystem");
			Logger.log(Logger.WARNING, LoggingCategory.FILE_SYSTEM, "FsSyncException occured when umounting filesystem");
		}
	}

	@Override
	public Node[] listDir(String path) throws FileNotFoundException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int runInputFileJob(String path, InputFileJob job) throws FileNotFoundException{

		while (Thread.interrupted()) {  // clear threat interrupted status
		}
		
		InputStream input = null;

		try {
			
			TPath pat = new TPath(this.pathToFileSystem, path);
			
			input = Files.newInputStream(pat);
			job.workOnFile(input);
			return 0;
		}catch (FsEntryNotFoundException ex) {
			throw new FileNotFoundException();
		} catch (Exception ex) {
			Logger.log(Logger.WARNING, LoggingCategory.FILE_SYSTEM, "Exception occured when running inputFileJob: " + ex.toString());
		} finally {

			try {
				input.close();
			} catch (Exception ex) {
			}
		}

		return -1;

	}

	@Override
	public int runOutputFileJob(String path, OutputFileJob job) {

		while (Thread.interrupted()) {  // clear threat interrupted status
		}

		OutputStream output = null;

		try {
			TPath pat = new TPath(this.pathToFileSystem, path);

			output = Files.newOutputStream(pat);
			job.workOnFile(output);
			return 0;
		} catch (Exception ex) {
			Logger.log(Logger.WARNING, LoggingCategory.FILE_SYSTEM, "Exception occured when running outputFileJob: " + ex.toString());
		} finally {

			try {
				output.close();
			} catch (Exception ex) {
			}
		}

		return -1;
	}

	@Override
	public boolean createNewFile(String path) throws FileNotFoundException {
		
		if(path.endsWith("/"))
			return false;
		try {
			
			TFile file = getRelativeTFile(path);
			
			if(!file.getParentFile().isDirectory())
				throw  new FileNotFoundException();
			
			return file.createNewFile();
		} catch (IOException ex) {
			return false;
		}
		
	}
	
	private TFile getRelativeTFile(String path){
		return new TFile(pathToFileSystem + path);
	}
}
