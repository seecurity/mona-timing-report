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
 * An object of this class creates a cdf of the measurement.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @16.08.2012
 *
 */
public class Cdf extends Plot {
	private String uniqueName = null;
	private Dataset dataSet;
	
	protected Cdf(Dataset dataSet, double lowerBound, double upperBound) {
		this.dataSet = dataSet;
		Folder.checkDir("reportingTool_tmp" + sep);
		this.writeMetadata(lowerBound, upperBound);
	}
	
	@Override
	protected File replacedFile(String terminal, String outputFile) {

		File template = new File("templates" + sep + "plots" + sep + "cdf-plt.txt");
		File replacedTemplate = new File("reportingTool_tmp" + sep + FileId.getUniqueName() + "-" + "cdf" + "-plt.txt");

		HashMap<String, String> replacer = new HashMap<String, String>();
		replacer.put("terminal", terminal);
		replacer.put("output", outputFile);

		StringBuilder secrets = new StringBuilder();
		for(int i = 0; i < dataSet.getSecrets().size(); i++){
			secrets.append("\"" + "reportingTool_tmp" + gSep + uniqueName + "-" + "cdf_" + dataSet.getSecrets().get(i).getFileName() + ".txt" + "\"" +
					" using 2:3 title \"Secret " + dataSet.getSecrets().get(i).getName().replaceAll("([\\\\{}_\\^#&$%~\"])", "") + "\" with linespoints");
			if((i + 1) < dataSet.getSecrets().size()) {
				secrets.append(",\\\n");
			}
		}
		
		replacer.put("plot", secrets.toString());
		Replacer.replace(template, replacedTemplate, replacer);
		
		return replacedTemplate;
	}
	
	/**
	 * This method writes the cdf meta data file.
	 * 
	 * @param lowerBound
	 * @param upperBound
	 */
	private void writeMetadata(double lowerBound, double upperBound) {
		uniqueName = FileId.getUniqueName();

		for (Secret secret : this.dataSet.getSecrets()) {

			int lowerPos = (int)((secret.getTimes().size()-1) * lowerBound);
			int upperPos = (int)((secret.getTimes().size()-1) * upperBound);
			double prob = 0;
			Long oldTime = secret.getTimes().get(lowerPos).getTime();
			int size = ((upperPos - lowerPos) + 1);
			
			try {
				File file = new File("reportingTool_tmp" + sep + uniqueName + "-" + "cdf_" + secret.getFileName() + ".txt");
				FileWriter writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);

				for (int i = lowerPos; i <= upperPos; i++) {
					if (oldTime.equals(secret.getTimes().get(i).getTime()) == false) {
						bw.write(i + "\t" + oldTime + "\t" + prob + "\n");
						oldTime = secret.getTimes().get(i).getTime();
					}
					
					prob = prob + 1.0 / size;
				}
				
				bw.write(size + "\t" + oldTime + "\t" + prob + "\n");
				bw.close();
			} catch (IOException e){
				logger.warning("Error writing file of cdf.");
				System.exit(1);
			} 
		}
	}
}
