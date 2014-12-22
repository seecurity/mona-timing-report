package de.fau.pi1.timerReporter.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.tools.Folder;

/**
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @07.12.2012
 *
 */
public class BoxTestResults {
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");
	private String inputFile = null;
	private Secret secretA = null;
	private Secret secretB = null;
	private double[] optimalBox = new double[2];
	private ArrayList<Integer> smallestSize = new ArrayList<Integer>();
	private ArrayList<Double> confidenceInterval = new ArrayList<Double>();
	private ArrayList<ArrayList<String>> subsetOverlapA = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> subsetOverlapB = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> significantDifferent = new ArrayList<ArrayList<String>>();

	public BoxTestResults(String inputFile, Secret secretA, Secret secretB, double[] optimalBox) {
		this.inputFile = inputFile;
		this.secretA = secretA;
		this.secretB = secretB;
		this.optimalBox = optimalBox;
	}

	/**
	 * This method saves the data into the box test result.
	 * @param smallestSize
	 * @param confidenceInterval
	 * @param subsetOverlapA
	 * @param subsetOverlapB
	 * @param significantDifferent
	 */
	public void saveValidation(int smallestSize, double confidenceInterval, ArrayList<String> subsetOverlapA, ArrayList<String> subsetOverlapB, ArrayList<String> significantDifferent) {
		this.smallestSize.add(smallestSize);
		this.confidenceInterval.add(confidenceInterval);
		this.subsetOverlapA.add(subsetOverlapA);
		this.subsetOverlapB.add(subsetOverlapB);
		this.significantDifferent.add(significantDifferent);
	}

	/**
	 * This method prints the box test results into the
	 * output file.
	 * 
	 * @param outputFile
	 */
	public void printBoxTestResult(File outputFile) {
		String output = "";
		for(int i = 0; i < smallestSize.size(); ++i){
			output = this.inputFile + ";" + this.secretA.getFileName() + "<" + this.secretB.getFileName() + ";" + optimalBox[0] +"-" + optimalBox[1] + ";" + this.smallestSize.get(i) + ";" + this.confidenceInterval.get(i) + ";" + Folder.convertArrayListToString(subsetOverlapA.get(i)) + ";" + this.countValid(subsetOverlapA.get(i), "o") + ";" + this.countValid(subsetOverlapA.get(i), "x") + ";" + Folder.convertArrayListToString(subsetOverlapB.get(i)) + ";" + this.countValid(subsetOverlapB.get(i), "o") + ";" + this.countValid(subsetOverlapB.get(i), "x") + ";" + Folder.convertArrayListToString(significantDifferent.get(i)) + ";" + this.countValid(significantDifferent.get(i), "o") + ";"  + this.countValid(significantDifferent.get(i), "x") + ";"  ;

			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true));
				for (int j=0; j < output.length(); ++j){
					bw.write(output.charAt(j));
				}
				bw.write('\n');
				bw.close();
			} catch (IOException e) {
				logger.warning("Error while writing the box test result csv.");
				System.exit(1);
			}
		}
	}

	/**
	 * This method counts how often the string exists
	 * in the array list.
	 * 
	 * @param validationResults
	 * @param valid
	 * @return
	 */
	public int countValid(ArrayList<String> validationResults, String valid) {
		int counter = 0;
		for (String validate : validationResults) {
			if(validate.equals(valid)) {
				++counter;
			}
		}
		return counter;
	}

	public String getInputFile() {
		return inputFile;
	}

	public Secret getSecretA() {
		return secretA;
	}

	public Secret getSecretB() {
		return secretB;
	}

	public double[] getOptimalBox() {
		return optimalBox;
	}

	public ArrayList<Integer> getSmallestSize() {
		return smallestSize;
	}

	public ArrayList<Double> getConfidenceInterval() {
		return confidenceInterval;
	}

	public ArrayList<ArrayList<String>> getSubsetOverlapA() {
		return subsetOverlapA;
	}

	public ArrayList<ArrayList<String>> getSubsetOverlapB() {
		return subsetOverlapB;
	}

	public ArrayList<ArrayList<String>> getSignificantDifferent() {
		return significantDifferent;
	}
}


