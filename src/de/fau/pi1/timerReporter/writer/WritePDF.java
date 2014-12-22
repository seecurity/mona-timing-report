package de.fau.pi1.timerReporter.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.plots.PlotPaths;
import de.fau.pi1.timerReporter.plots.PlotPool;
import de.fau.pi1.timerReporter.tools.Conf;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;
import de.fau.pi1.timerReporter.tools.Replacer;

/**
 * An object of this class can write a pdf file.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @04.10.2012
 *
 */
public class WritePDF extends Writer {
	private Dataset dataSet;
	private String report;
	private PlotPool plotPool;
	private ArrayList<String> timelineNames;
	private String sep = Folder.getFileSep();

	public WritePDF(Dataset dataSet, String report, PlotPool plotPool, ArrayList<String> timelineNames){
		this.dataSet = dataSet;
		this.report = report;
		this.plotPool = plotPool;
		this.timelineNames = timelineNames;

		Folder.checkDir(report + sep);
		Folder.checkDir("reportingTool_tmp" + sep);

		//check png files exists
		for (PlotPaths plotPaths : this.plotPool.getPlotPaths()) {
			checkImages(this.report + "/images/" , plotPaths.getPngPaths());
		}

		//check png files exists
		checkImages(this.report + "/images/" , timelineNames);
	}

	/* (non-Javadoc)
	 * @see writer.Writer#write()
	 */
	@Override
	public void write() throws IOException {
		File input = new File("templates" + sep + "latex" + sep + "Report.tex");
		File tmpDir = null;
		while (tmpDir == null || tmpDir.exists() == true) {
			tmpDir = new File("reportingTool_tmp" + sep + "latex"
					+ Long.toString(System.nanoTime()));
		}
		tmpDir.mkdir();
		logger.log(Level.FINE, "Writing tex files to " + tmpDir.getPath());

		File output = new File(this.report + sep);
		if (output.exists()) {
			output.delete();
		}

		File makeIndex = new File(Conf.get("makeindexPath"));
		File pdfLatex = new File(Conf.get("pdflatexPath"));

		HashMap<String, String> replacer = new HashMap<String, String>();
		replacer.put("name", dataSet.getName().replaceAll("([\\\\{}_\\^#&$%~])", "\\$1"));

		replacer.put("Spalte1", "Secret");
		replacer.put("Spalte2", "Amount Measurement");
		replacer.put("Spalte3", "MIN");
		replacer.put("Spalte4", "MAX");
		replacer.put("Spalte5", "Median");
		replacer.put("Spalte6", "AVG");

		StringBuilder table = new StringBuilder();
		for (int i = 0; i < this.dataSet.getSecrets().size(); i++) {
			table.append(this.dataSet.getSecrets().get(i).getName().replaceAll("([\\\\{}_\\^#&$%~])", "\\$0") + "&"
					+ this.dataSet.getSecrets().get(i).getTimes().size() + "&"
					+ this.dataSet.getSecrets().get(i).getLowestTime() + "&"
					+ this.dataSet.getSecrets().get(i).getHighestTime() + "&"
					+ this.dataSet.getSecrets().get(i).getMedian(0, this.dataSet.getSecrets().get(i).getTimes().size() - 1) + "&"
					+ this.dataSet.getSecrets().get(i).getArithmeticMean()
					+ "\\\\\n");

			table.append("\\hline\n");
		}
		replacer.put("tableContent", table.toString());

		//TIMELINE
		String timelineToReplace = new String();
		for(int i = 0; i < this.timelineNames.size(); ++i) {
			timelineToReplace += Replacer.readTemplate("templates" + sep + "latex" + sep + "timeline.tpl");
			timelineToReplace = timelineToReplace.replaceAll("::name:::", timelineNames.get(i).replaceAll("([\\\\{}_\\^#&$%~])", "\\$0"));
			timelineToReplace = timelineToReplace.replaceAll("::path:::", "../../" + this.report + "/images/" + Matcher.quoteReplacement(timelineNames.get(i)));
		}
		replacer.put("timelines", timelineToReplace);

		//Plots
		String toReplace = new String();
		for (PlotPaths plotPaths : this.plotPool.getPlotPaths()) {

			toReplace += Replacer.readTemplate("templates" + sep + "latex" + sep + "plotPathes.tpl");
			toReplace = toReplace.replaceAll("::name:::", plotPaths.getName().replaceAll("([\\\\{}_\\^#&$%~])", "\\$0"));
			toReplace = toReplace.replaceAll("::lowerBound:::", Matcher.quoteReplacement(plotPaths.getLowerBoundAsString()));
			toReplace = toReplace.replaceAll("::upperBound:::", Matcher.quoteReplacement(plotPaths.getUpperBoundAsString()));
			toReplace = toReplace.replaceAll("::scatterplot:::", "../../" + this.report + "/images/" + Matcher.quoteReplacement(plotPaths.getPngPaths().get(0)));
			toReplace = toReplace.replaceAll("::boxPlot:::", "../../" + this.report + "/images/" + Matcher.quoteReplacement(plotPaths.getPngPaths().get(1)));
			toReplace = toReplace.replaceAll("::cdf:::", "../../" + this.report + "/images/" + Matcher.quoteReplacement(plotPaths.getPngPaths().get(2)));
			toReplace = toReplace.replaceAll("::histogram:::", "../../" + this.report + "/images/" + Matcher.quoteReplacement(plotPaths.getPngPaths().get(3)));
		}
		replacer.put("results", toReplace);

		File tmpOutput = new File(tmpDir, "Report.tex");
		Replacer.replace(input, tmpOutput, replacer);

		// Prepare building pdf
		String cmdarray[] = null;
		Process p = null;
		BufferedReader localBufferedReader = null;
		StringBuilder localStringBuilder = null;
		String str = null;


		// makeindex
		cmdarray = new String[2];
		cmdarray[0] = makeIndex.getAbsolutePath();
		cmdarray[1] = tmpOutput.getName();

		logger.log(Level.FINE, "Calling makeindex: " + cmdarray[0] + " " + cmdarray[1]);

		p = Runtime.getRuntime().exec(cmdarray, null, tmpDir);
		localBufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		localStringBuilder = new StringBuilder();
		try
		{
			while ((str = localBufferedReader.readLine()) != null)
				localStringBuilder.append(str + "\n");
		}
		finally {
			localBufferedReader.close();
		}

		try {
			p.waitFor();				
			if(p.exitValue() != 0) {
				StringBuffer sb = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String temp;
				while ((temp = br.readLine()) != null)
					sb.append(temp);
				logger.warning("ERROR while calling makeindex: " + p.exitValue() + ". --> " + sb);
				throw new RuntimeException("ERROR while calling makeindex: " + p.exitValue());
			}
		} catch (InterruptedException e) {
			p.destroy();
		}

		// makepdf
		cmdarray = new String[3];
		cmdarray[0] = pdfLatex.getAbsolutePath();
		cmdarray[1] = "-interaction=nonstopmode";
		cmdarray[2] = tmpOutput.getName();
		for (int i = 0; i < 2; ++i) {
			// We have to run pdflatex twice

			logger.log(Level.FINE, "Calling pdflatex (" + i + "/2): " + cmdarray[0] + " " + cmdarray[1] + " " + cmdarray[2]);
			p = Runtime.getRuntime().exec(cmdarray, null, tmpDir);

			// It's important to read the input stream. If input stream is full pdflatex hangs
			localBufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			localStringBuilder = new StringBuilder();
			str = null;
			try
			{
				while ((str = localBufferedReader.readLine()) != null)
					localStringBuilder.append(str + "\n");
			}
			finally {
				localBufferedReader.close();
			}
			try {
				p.waitFor();
				if(p.exitValue() != 0) {
					logger.warning("ERROR while calling pdflatex: " + p.exitValue());
					throw new RuntimeException("ERROR while calling pdflatex: " + p.exitValue());
				}
			} catch (InterruptedException e) {
				p.destroy();
			}
		}

		// move generated pdf
		File src = new File(tmpDir, "Report.pdf");
		File dst = new File(output, FileId.getId() + "-report.pdf");

		if (src.renameTo(dst)) {
			logger.finest("Moved report to " + dst.getAbsolutePath());
		} else {
			logger.warning("ERROR while moving report to " + dst.getAbsolutePath() + ". Report is still in the temp folder.");
		}
	}

	/**
	 * This method checks a list of image names if they exist.
	 * 
	 * @param path
	 * @param names
	 */
	private void checkImages(String path, ArrayList<String> names) {

		for (String name : names) {
			if (!Folder.isImageExists(path, name)) {
				logger.warning("Error: image file " + path + name + " doesn't exist.");
				System.exit(1);
			}
		}
	}
}
