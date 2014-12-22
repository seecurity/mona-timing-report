package de.fau.pi1.timerReporter.plots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;
import de.fau.pi1.timerReporter.tools.Replacer;

/**
 * An object of this class creates a box plot of the measurement. 
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @16.08.2012
 *
 */
public class BoxPlot extends Plot{
	private String uniqueName = null;
	private Dataset dataSet;

	protected BoxPlot(Dataset dataSet, double lowerBound, double upperBound) {
		this.dataSet = dataSet;
		Folder.checkDir("reportingTool_tmp" + sep);
		this.writeMetadata(lowerBound, upperBound);
	}

	@Override
	protected File replacedFile(String terminal, String outputFile) {

		File template = new File("templates" + sep + "plots" + sep + "boxPlot-plt.txt");
		File replacedTemplate = new File("reportingTool_tmp" + sep + FileId.getUniqueName() + "-" + "boxPlot-plt.txt");

		HashMap<String, String> replacer = new HashMap<String, String>();
		replacer.put("terminal", terminal);
		replacer.put("output", outputFile);

		StringBuilder secrets = new StringBuilder();
		for(int i = 0; i < dataSet.getSecrets().size(); i++){
			secrets.append("\"" + "reportingTool_tmp" + gSep + uniqueName + "-" + "boxPlot_" + dataSet.getSecrets().get(i).getFileName() + ".txt" + "\"" +
					" using 1:2:2:4:4 title \"Secret " + dataSet.getSecrets().get(i).getName().replaceAll("([\\\\{}_\\^#&$%~\"])", "") + "\" with candlesticks,\\\n" + 
					"\"" + "reportingTool_tmp" + gSep + uniqueName + "-" + "boxPlot_" + dataSet.getSecrets().get(i).getFileName() + ".txt" + "\"" +
					" using 1:6:6:6:6 notitle with candlesticks lt -1");

			if((i + 1) < dataSet.getSecrets().size()) {
				secrets.append(",\\\n");

			}
		}
		replacer.put("plot", secrets.toString());
		Replacer.replace(template, replacedTemplate, replacer);

		return replacedTemplate;
	}

	/**
	 * This method writes the box plot meta data file.
	 * 
	 * @param lowerBound
	 * @param upperBound
	 */
	private void writeMetadata(double lowerBound, double upperBound) {
		uniqueName = FileId.getUniqueName();
		int counter = 1;
		for (Secret secret : this.dataSet.getSecrets()) {
			int lowerPos = (int)((secret.getTimes().size()-1) * lowerBound);
			int upperPos = (int)((secret.getTimes().size()-1) * upperBound);
			
			if(lowerPos == upperPos) {
				logger.info(lowerBound + "-" + upperBound + ": Error the user input lower (optimal) and upper (optimal) bound create a too small box for the size of the measurement. In any case you should use about 100 time measurements per secret.");
				System.exit(1);
			}

			try {
				File file = new File("reportingTool_tmp" + sep + uniqueName + "-" + "boxPlot_" + secret.getFileName() + ".txt");
				FileWriter writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);

				bw.write(counter + "\t" + secret.getBoxPlotLowerQuantile(lowerPos, upperPos) + "\t"
						+ secret.getTimes().get(lowerPos).getTime() + "\t"
						+ secret.getBoxPlotUpperQuantile(lowerPos, upperPos) + "\t"
						+ secret.getTimes().get(upperPos).getTime() + "\t"
						+ secret.getMedian(lowerPos, upperPos) + "\n");
				++counter;
				bw.close();
			} catch (IOException e){
				logger.warning("Error writing file of box-plot.");
				System.exit(1);
			} 
		}
	}
}
