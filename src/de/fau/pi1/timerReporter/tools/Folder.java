package de.fau.pi1.timerReporter.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An object of this class can check dirs, holds file separator and
 * can delete dirs.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @23.11.2012
 *
 */
public class Folder {
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");
	private static final String sep = File.separator;
	
	/**
	 * This method checks if the folder already exists. 
	 * When it doesn't exist, this method creates a new folder.
	 * 
	 * @param dir String of folder to be checked
	 */
	public static void checkDir(String dir) {
		File file = new File(dir);
		if (file.isDirectory()) {
			logger.log(Level.FINE, "Folder \"" + dir + "\" already exists.");
		} else {
			boolean success = file.mkdir();

			if (success) {
				logger.log(Level.FINE, "Folder \"" + dir + "\" was created successfully.");
			} else {
				logger.warning("Error: Folder " + dir + " wasn't created successfully.");
				System.exit(1);
			}
		}
	}
	
	/**
	 * This method deletes a folder.
	 * @param path
	 */
	private static void deleteTree(File path) {
		for (File file : path.listFiles()) {
			if ( file.isDirectory() )
				deleteTree( file );
			file.delete();
		}
		path.delete();
		logger.log(Level.FINE, "Folder \"" + path + "\" was deleted successfully.");
	}
	
	/**
	 * This method deletes the tmp dir.
	 * 
	 */
	public static void deleteTmp() {
		Folder.deleteTree(new File("reportingTool_tmp" + sep));
	}

	/**
	 * This method returns a file separator.
	 * @return
	 */
	public static String getFileSep() {
		return sep;
	}
	
	/**
	 * This method returns a gnuplot file separator.
	 * @return
	 */
	public static String getFileGSep() {
		return sep + sep;
	}
	
	/**
	 * This method converts an array list of strings into one string.
	 * 
	 * @param list
	 * @return String
	 */
	public static String convertArrayListToString(ArrayList<String> list) {
		String convertedString = "";

		for (String string : list) {
			convertedString += string;
		}

		return convertedString;
	}
	
	/**
	 * This method checks if the file exists with the inputed file path and file name.
	 * 
	 * @return boolean if the file exists
	 */
	public static boolean isImageExists(String prePath, String fileName) {
		if(new File(prePath + fileName).exists()) {
			return true;
		} 
		
		return false;
	}
}
