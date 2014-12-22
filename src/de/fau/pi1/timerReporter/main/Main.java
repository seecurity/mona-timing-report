package de.fau.pi1.timerReporter.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.evaluation.StatisticEvaluation;
import de.fau.pi1.timerReporter.plots.PlotPool;
import de.fau.pi1.timerReporter.reader.ReaderCsv;
import de.fau.pi1.timerReporter.tools.Conf;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;
import de.fau.pi1.timerReporter.writer.WriteHTML;
import de.fau.pi1.timerReporter.writer.WritePDF;

/**
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @since 19.07.2012
 *
 */
public class Main {

	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");
	private static String report = "";
	private static String sep = Folder.getFileSep();

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args){
		Conf.getInstance().parseIni(new File("config.ini"));
		Conf.getInstance().parseParameter(args);

		if(Boolean.parseBoolean(Conf.get("verbose"))) {
			setDebug();
		}
		report = getReport();

		// create new data set with secrets and times
		ReaderCsv reader = new ReaderCsv();
		Dataset dataset = new Dataset(reader);
		dataset.setName(Conf.get("name"));


		// create plot pool to multi threaded the plots
		PlotPool plotPool = new PlotPool(report, dataset);

		// plot the data set with the lower bound of 0.0 and the upper bound of 1.0
		plotPool.plot("Unfiltered Measurements", 0.0, 1.0);

		// plot the data set with the user input lower bound and upper bound
		plotPool.plot("Filtered Measurments: User Input", Double.parseDouble(Conf.get("lowerBound")), Double.parseDouble(Conf.get("upperBound")));

		// starts the evaluation phase
		StatisticEvaluation statisticEvaluation = new StatisticEvaluation(dataset, plotPool);
	
		if(Conf.get("upperOptimalBound") != null && Conf.get("lowerOptimalBound") != null) {
			double[] userInputOptimalBox = new double[2];
			userInputOptimalBox[0] = Double.parseDouble(Conf.get("lowerOptimalBound"));
			userInputOptimalBox[1] = Double.parseDouble(Conf.get("upperOptimalBound"));
			statisticEvaluation.setOptimalBox(userInputOptimalBox);
		}
		
		statisticEvaluation.calibrationPhase();
		
		statisticEvaluation.printBoxTestResults(new File(report + Folder.getFileSep() + FileId.getId() + "-BoxTestResult.csv"));

		// store the time lines
		ArrayList<String> timelineNames = statisticEvaluation.storeTimelines(report + sep + "images" + sep);

		// close the thread pool
		plotPool.close();

		// write results in html and pdf
		new WriteHTML(dataset, report, plotPool).write();

		try {
			new WritePDF(dataset, report, plotPool, timelineNames).write();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Error while writing the pdf.");
			System.exit(1);
		}

		//delet the folder tmp
		Folder.deleteTmp();
	}

	/**
	 * This method returns the current gnuplot path.
	 * 
	 * @return gnuplot path
	 */
	public static String getGnuplotExecutable() {
		return de.fau.pi1.timerReporter.tools.Conf.get("gnuplot");
	}

	/**
	 * This method returns a report if no report
	 * exists it creates a new report timestamp.
	 * 
	 * @return String report name
	 */
	public static String getReport() {
		if (report == "") {
			return "reports_" + FileId.getTimestamp();
		} 
		return report;
	}

	/**
	 * This method sets the logger level to finest.
	 * 
	 */
	public static void setDebug() {
		final Level level = Level.FINEST;
		Logger tempLogger = logger;
		while(tempLogger != null) {
			tempLogger.setLevel(level);
			for(Handler handler : tempLogger.getHandlers()) {
				handler.setLevel(level);
			}
			tempLogger = tempLogger.getParent();
		}
	}
}
