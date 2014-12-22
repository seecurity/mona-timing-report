package de.fau.pi1.timerReporter.plots;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;

/**
 * An object of this class holds the thread pool. 
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @24.08.2012
 *
 */
public class PlotPool {
	private ExecutorService threadPool = Executors.newFixedThreadPool((int) Math.ceil( Runtime.getRuntime().availableProcessors() / 2.0 ));
	private String report = "";
	private Dataset dataSet;
	private boolean closed = false;
	private String sep = Folder.getFileSep();
	private String gSep = Folder.getFileGSep();
	private ArrayList<PlotPaths> plotPaths = new ArrayList<PlotPaths>();
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");

	public PlotPool(String report, Dataset dataSet) {
		this.report = report;
		this.dataSet = dataSet;
		Folder.checkDir(report + sep);
		Folder.checkDir(report + sep + "images" + sep);
	}

	/**
	 * This method plots all possible graphs and holds a list of all output paths.
	 * 
	 * @param name
	 * @param lowerBound
	 * @param upperBound
	 */
	public void plot(String name, double lowerBound, double upperBound) {
		if(!(Double.compare(upperBound, lowerBound) > 0 && upperBound <= 1.0 && upperBound >= 0.0 && lowerBound <= 1.0 && lowerBound >= 0.0)) {
			logger.warning("Error: (" + name + ") The entered lower/upper bound (" + lowerBound + "-" + upperBound + ") have not the right range. They must be between 0.0 and 1.0. upper > lower");
			System.exit(1);
		}
		
		if(closed) {
			throw new RuntimeException("Plot pool already closed.");
		}
		
		ArrayList<String> pngPaths = new ArrayList<String>();
		ArrayList<String> pdfPaths = new ArrayList<String>();

		// create plot classes
		Scatterplot scatterplot = new Scatterplot(this.dataSet, lowerBound, upperBound);
		BoxPlot boxPlot = new BoxPlot(this.dataSet, lowerBound, upperBound);
		Cdf cdf = new Cdf(this.dataSet, lowerBound, upperBound);
		Histogram histogram = new Histogram(this.dataSet, lowerBound, upperBound);

		// add the plots to the thread pool and save the png path
		pngPaths.add(FileId.getId() +  "-scatterplot-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".png");
		threadPool.submit(new PlotJob(scatterplot, this.report + gSep + "images" + gSep + pngPaths.get(pngPaths.size()-1), "png size 1200,600"));
		pngPaths.add(FileId.getId() +  "-boxPlot-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".png");
		threadPool.submit(new PlotJob(boxPlot, this.report + gSep + "images" + gSep + pngPaths.get(pngPaths.size()-1), "png size 1200,600"));
		pngPaths.add(FileId.getId() +  "-cdf-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".png");
		threadPool.submit(new PlotJob(cdf, this.report + gSep + "images" + gSep + pngPaths.get(pngPaths.size()-1), "png size 1200,600"));
		pngPaths.add(FileId.getId() + "-histogram-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".png");
		threadPool.submit(new PlotJob(histogram, this.report + gSep + "images" + gSep + pngPaths.get(pngPaths.size()-1), "png size 1200,600"));

		// add the plots to the thread pool and save the pdf path
		pdfPaths.add(FileId.getId() +  "-scatterplot-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".pdf");
		threadPool.submit(new PlotJob(scatterplot, this.report + gSep + "images" + gSep + pdfPaths.get(pdfPaths.size()-1), "pdf"));
		pdfPaths.add(FileId.getId() +  "-boxPlot-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".pdf");
		threadPool.submit(new PlotJob(boxPlot, this.report + gSep + "images" + gSep + pdfPaths.get(pdfPaths.size()-1), "pdf"));
		pdfPaths.add(FileId.getId() +  "-cdf-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".pdf");
		threadPool.submit(new PlotJob(cdf, this.report + gSep + "images" + gSep + pdfPaths.get(pdfPaths.size()-1), "pdf"));
		pdfPaths.add(FileId.getId() + "-histogram-" + (int)(lowerBound * 100) + "-" + (int)(upperBound * 100) + ".pdf");
		threadPool.submit(new PlotJob(histogram, this.report + gSep + "images" + gSep + pdfPaths.get(pdfPaths.size()-1), "pdf"));

		// create new plot paths
		this.plotPaths.add(new PlotPaths(name, lowerBound, upperBound, pngPaths, pdfPaths));
		
	}

	/**
	 * This method closes the thread pool.
	 * 
	 */
	public void close() {
		closed = true;
		// Wait till all box tests finished
		threadPool.shutdown();
		do {
			try {
				threadPool.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!threadPool.isTerminated());
	}

	/**
	 * This method returns the output paths of all plots.
	 * 
	 * @return ArrayList<PlotPaths>
	 */
	public ArrayList<PlotPaths> getPlotPaths() {
		return plotPaths;
	}

}
