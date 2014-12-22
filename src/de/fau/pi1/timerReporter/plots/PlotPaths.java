package de.fau.pi1.timerReporter.plots;

import java.util.ArrayList;

/**
 * An object of this class holds all information above one plotted setup. Therefore,
 * it holds the name of the plot with lower and upper bound and the output paths
 * of the png and pdf terminal.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @25.09.2012
 *
 */
public class PlotPaths {
	private String name = "";
	private double lowerBound;
	private double upperBound;
	private ArrayList<String> pngPaths = new ArrayList<String>();
	private ArrayList<String> pdfPaths = new ArrayList<String>();
	
	protected PlotPaths(String name, double lowerBound, double upperBound, ArrayList<String> pngPaths, ArrayList<String> pdfPaths) {
		this.name = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.pngPaths = pngPaths;
		this.pdfPaths = pdfPaths;
	}

	public String getLowerBoundAsString() {
		return lowerBound + "";
	}

	public String getUpperBoundAsString() {
		return upperBound + "";
	}

	public ArrayList<String> getPngPaths() {
		return pngPaths;
	}

	public ArrayList<String> getPdfPaths() {
		return pdfPaths;
	}

	public String getName() {
		return name;
	}
}
