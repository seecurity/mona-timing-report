package de.fau.pi1.timerReporter.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An object of this class has methods to get identifier and an
 * unique timestamp.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @17.08.2012
 *
 */
public class FileId {
	private static String timeStamp = null;
	private static int id = 0;
	
	/**
	 * This method returns a synchronized time stamp.
	 * 
	 * @return String
	 */
	public static synchronized String getTimestamp() {
		if(FileId.timeStamp == null) {
			FileId.timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		}
		
		return FileId.timeStamp;
	}
	
	/**
	 * This method returns a synchronized file id.
	 * 
	 * @return int 
	 */
	public static synchronized int getId() {
		return ++FileId.id;
	}
	
	/**
	 * This method returns a synchronized unique name containing 
	 * a time stamp and a id.
	 * 
	 * @return String
	 */
	public static synchronized String getUniqueName() {
		return FileId.getTimestamp() + "-" + FileId.getId();
	}
}
