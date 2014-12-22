package de.fau.pi1.timerReporter.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.dataset.Time;
import de.fau.pi1.timerReporter.tools.Conf;

/**
 * An object of this class represents the csv reader. It only
 * can read an csv file. 
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @19.07.2012
 *
 */
public class ReaderCsv extends Reader{
	String inputFile = null;

	public ReaderCsv(){
		if(Conf.get("inputFile") != null) {
			this.inputFile = Conf.get("inputFile");
		} else {
			logger.warning("You have to specify an input file.");	
			System.exit(1);
		}
	}

	/* (non-Javadoc)
	 * @see de.fau.pi1.timerReporter.reader.Reader#read(java.util.ArrayList)
	 */
	public void read(ArrayList<Secret> secrets) {
		FileReader fileReader;
		String line;
		String[] splitArray;
		Long newTime = new Long(0);
		String secretName = null;

		try {
			fileReader = new FileReader(this.inputFile);
			BufferedReader data = new BufferedReader(fileReader);
			int lineCounter = 0; 

			HashMap<String, Secret> secretMap = new HashMap<String, Secret>();

			while ((line = data.readLine()) != null) {
				splitArray = line.split(";");
				if(splitArray.length == 1) {
					// If there are no ';'-separated values, try ','-separated
					splitArray = line.split(",");
				}
				for (int i = 0; i < splitArray.length; i++) {
					if(splitArray[i].startsWith("\"") && splitArray[i].endsWith("\"")) {
						splitArray[i] = splitArray[i].substring(1, splitArray[i].length()-1);
					}
				}

				try{
					if(splitArray.length == 3) {
						newTime = Long.parseLong(splitArray[2]);
						secretName = splitArray[1];
					} else if(splitArray.length == 2) {
						newTime = Long.parseLong(splitArray[1]);
						secretName = splitArray[0];
					} else {
						throw new RuntimeException("Wrong input file format. Should be: (<id>;)?<secret>;<time>");
					}
				} catch(NumberFormatException nfe) {
					logger.warning("NumberFormatException in line " + lineCounter + 1 + ": " + nfe.getMessage());	
					System.exit(1);
				}

				Secret secret = secretMap.get(secretName);

				if(secret != null) {
					Time time = new Time(lineCounter, secret.getTimes().size(), newTime);
					secret.addTime(time);
				} else {				
					Secret newSecret = new Secret(secretName, "secret" + secretMap.size());
					secrets.add(newSecret);
					secretMap.put(secretName, newSecret);
					Time time = new Time(lineCounter, newSecret.getTimes().size(), newTime);
					newSecret.addTime(time);
				}

				++lineCounter;
			}
		} catch (FileNotFoundException e) {
			logger.warning("File " + inputFile + " not found.");
			System.exit(1);
		} catch (IOException e) {
			logger.warning("E/A-Error");
			System.exit(1);
		}

		// the time list in secrets should always be sorted
		for (Secret secret : secrets) {
			secret.sortTimes();
		}
	}

	public String getInputFile() {
		return inputFile;
	}
}


