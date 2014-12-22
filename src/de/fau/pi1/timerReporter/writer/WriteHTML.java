package de.fau.pi1.timerReporter.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.plots.PlotPaths;
import de.fau.pi1.timerReporter.plots.PlotPool;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;
import de.fau.pi1.timerReporter.tools.Replacer;

/**
 * An object of this class can write an html file. 
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @25.09.2012
 *
 */
public class WriteHTML extends Writer {
	private Dataset dataSet;
	private String report;
	private PlotPool plotPool;
	private String sep = Folder.getFileSep();

	public WriteHTML(Dataset dataSet, String report, PlotPool plotPool) {
		this.dataSet = dataSet;
		this.report = report;
		this.plotPool = plotPool;
		
		Folder.checkDir(report + sep);
	}

	/* (non-Javadoc)
	 * @see Writer.Writer#write(java.util.ArrayList)
	 */
	@Override
	public void write() {
		
		File input = new File("templates" + sep + "html" + sep + "index.html");
		File output = new File(report + sep + FileId.getId() + "-index.html");

		HashMap<String, String> replacer = new HashMap<String, String>();

		ArrayList<Secret> secrets = dataSet.getSecrets();	
		StringBuilder table = new StringBuilder();
		for(int i = 0; i < secrets.size(); i++){
			String secretName = StringEscapeUtils.unescapeHtml4(secrets.get(i).getName().replaceAll("[^\\x20-\\x7e]", ""));
			table.append("<tr><td>" + secretName + 
					"</td><td>" + secrets.get(i).getTimes().size() +
					"</td><td>" + secrets.get(i).getLowestTime() + 
					"</td><td>" + secrets.get(i).getHighestTime() + 
					"</td><td>" + secrets.get(i).getMedian(0, secrets.get(i).getTimes().size() - 1) + 
					"</td><td>" + secrets.get(i).getArithmeticMean());
			table.append("\n");
		}
		
		String datasetName = StringEscapeUtils.unescapeHtml4(dataSet.getName()).replaceAll("[^\\x20-\\x7e]", "");
		
		replacer.put("measurementName", datasetName);
		replacer.put("contentTable", table.toString());
		String toReplace = new String();
		for (PlotPaths plotPaths : this.plotPool.getPlotPaths()) {

			toReplace += Replacer.readTemplate("templates" + sep + "html" + sep + "plotPathes.tpl");
			toReplace = toReplace.replace("::name:::", plotPaths.getName());
			toReplace = toReplace.replace("::lowerBound:::", plotPaths.getLowerBoundAsString());
			toReplace = toReplace.replace("::upperBound:::", plotPaths.getUpperBoundAsString());
			toReplace = toReplace.replace("::scatterplot:::", "images/" + plotPaths.getPngPaths().get(0));
			toReplace = toReplace.replace("::boxPlot:::", "images/" + plotPaths.getPngPaths().get(1));
			toReplace = toReplace.replace("::cdf:::", "images/" + plotPaths.getPngPaths().get(2));
			toReplace = toReplace.replace("::histogram:::", "images/" + plotPaths.getPngPaths().get(3));
		}

		replacer.put("results", toReplace);
		
		Replacer.replace(input, output, replacer);
	}
}
