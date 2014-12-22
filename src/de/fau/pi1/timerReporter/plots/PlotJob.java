package de.fau.pi1.timerReporter.plots;

import java.util.concurrent.Callable;

/**
 * An object of this class represents the plot job.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @17.08.2012
 *
 */
public class PlotJob implements Callable<Boolean> {
	private Plot plot;
	private String outputFile;
	private String terminal;
	
	protected PlotJob(Plot plot, String outputFile, String terminal) {
		this.plot = plot;
		this.outputFile = outputFile;
		this.terminal = terminal;
	}

	@Override
	public Boolean call() throws Exception {
		this.plot.newPlot(this.outputFile, this.terminal);
		return true;
	}
}
