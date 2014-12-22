package de.fau.pi1.timerReporter.dataset;

import java.util.ArrayList;

import de.fau.pi1.timerReporter.reader.Reader;

/**
 * An object of this class represents one data set. This data set has a list 
 * of all secrets. The data set can be constructed with a list of secrets or a 
 * reader to read in the secrets.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @19.07.2012
 *
 */
public class Dataset {
	private ArrayList<Secret> secrets = new ArrayList<Secret>();
	private String name = "no name";
	private Reader reader = null;

	public Dataset(ArrayList<Secret> secrets) {
		this.secrets = secrets;
	}
	
	public Dataset(Reader reader) {
		this.reader = reader;
		this.reader.read(this.secrets);
	}
	
	/**
	 * This method iterates over all secrets and finds the
	 * maximum of all timings. This method is used in the 
	 * histogram class.
	 * 
	 * @return long value of the time
	 */
	public static Long findRangeMax(ArrayList<Secret> secrets) {
		Long max = new Long(0);
		for (Secret secret : secrets) {
			if(secret.getHighestTime().compareTo(max) > 0) {
				max = secret.getHighestTime();
			}
		}
		return max;
	}
	
	/**
	 * This method iterates over all secrets and finds the
	 * minimum of all timings. This method is used in the 
	 * histogram class.
	 * 
	 * @return long value containing the timing
	 */
	public static Long findRangeMin(ArrayList<Secret> secrets) {
		Long min = new Long(0);
		
		for (Secret secret : secrets) {
			if(secret.getLowestTime().compareTo(min) < 0 || min.equals(new Long(0))) {
				min = secret.getLowestTime();
			}
		}
		return min;
	}

	public ArrayList<Secret> getSecrets() {
		return secrets;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getInputFile() {
		return this.reader.getInputFile();
	}
}
