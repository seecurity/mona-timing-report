package de.fau.pi1.timerReporter.plots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.dataset.Time;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;
import de.fau.pi1.timerReporter.tools.Replacer;

/**
 *  An object of this class creates a scatterplot of the measurement. 
 *  
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @24.07.2012
 *
 */
public class Scatterplot extends Plot {
	private String uniqueName = null;
	private Dataset dataset;

	protected Scatterplot(Dataset dataset, double lowerBound, double upperBound) {
		this.dataset = dataset;
		Folder.checkDir("reportingTool_tmp" + sep);
		this.writeMetadata(lowerBound, upperBound);
	}

	@Override
	protected File replacedFile(String terminal, String outputFile) {
		File template = new File("templates" + sep + "plots" + sep + "scatterplot-plt.txt");
		File replacedTemplate = new File("reportingTool_tmp" + sep + FileId.getUniqueName() + "-" + "scatterplot" + "-plt.txt");

		HashMap<String, String> replacer = new HashMap<String, String>();
		replacer.put("terminal", terminal);
		replacer.put("output", outputFile);

		StringBuilder secrets = new StringBuilder();
		for(int i = 0; i < dataset.getSecrets().size(); i++){
			if(i == 0) {
				secrets.append("\"" + "reportingTool_tmp" + gSep + uniqueName + "-" + "scatterplot_" + dataset.getSecrets().get(i).getFileName() + ".txt" + "\"" +
						" using 1:2 title \"Secret " + dataset.getSecrets().get(i).getName().replaceAll("([\\\\{}_\\^#&$%~\"])", "") + "\" with points pt 2");
			} else if(i == 1) {
				secrets.append("\"" + "reportingTool_tmp" + gSep + uniqueName + "-" + "scatterplot_" + dataset.getSecrets().get(i).getFileName() + ".txt" + "\"" +
						" using 1:2 title \"Secret " + dataset.getSecrets().get(i).getName().replaceAll("([\\\\{}_\\^#&$%~\"])", "") + "\" with points pt 6"); //or circles lt 3
			}
			if((i + 1) < dataset.getSecrets().size()) {
				secrets.append(",\\\n");
			}
		}
		replacer.put("plot", secrets.toString());
		Replacer.replace(template, replacedTemplate, replacer);

		return replacedTemplate;
	}

	/**
	 * This method writes the scatterplot metadata file of all secrets.
	 * 
	 * @throws IOException
	 */
	public void writeMetadata(double lowerBound, double upperBound) {
		uniqueName = FileId.getUniqueName();

		for (Secret secret : this.dataset.getSecrets()) {

			int lowerPos = getPercentile((int)(lowerBound * 100), secret.getTimes().size());
			Time lowerTime = secret.getTimes().get(lowerPos);

			int upperPos = getPercentile((int)(upperBound * 100), secret.getTimes().size());
			Time upperTime = secret.getTimes().get(upperPos);

			try {
				File file = new File("reportingTool_tmp" + sep + uniqueName + "-" + "scatterplot_" + secret.getFileName() + ".txt");
				FileWriter writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);

				int timeCounter = 0;

				Time time = secret.getFirstTime();

				while(time != null) {
					if(time.compareTo(lowerTime) >= 0 && time.compareTo(upperTime) <= 0) {
						bw.write(++timeCounter + "\t"
								+ time.getTime() + "\n");
					}
						time = time.getSuccessor();
				}
				
				if(timeCounter == 0) {
					logger.warning(lowerBound + "-" + upperBound + ": Error the user input lower (optimal) and upper (optimal) bound create a too small box for the size of the measurement. In any case you should use about 100 time measurements per secret.");
					System.exit(1);
				}

				bw.close();
			} catch (IOException e){
				logger.warning("Error writing file of scatterplot.");
				System.exit(1);
			}
		}
	}

	/**
	 * This method returns the position of the percentile in 
	 * the time list. 
	 * 
	 * @param p
	 * @param size
	 * @return int position in the time list
	 */
	private static int getPercentile(int p, int size) {
		if(p != 100) {
			return (p * size) / 100;
		} else {
			return size - 1;
		}
	}
}