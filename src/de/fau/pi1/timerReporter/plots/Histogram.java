package de.fau.pi1.timerReporter.plots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.dataset.Time;
import de.fau.pi1.timerReporter.tools.Conf;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;
import de.fau.pi1.timerReporter.tools.Replacer;

/**
 * An object of this class creates an histogram of the measuremnt.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @20.08.2012
 */
public class Histogram extends Plot {
	final String uniqueName = FileId.getUniqueName();
	private Dataset dataset;
	private static final int numBin = Integer.parseInt(Conf.get("numBin"));

	protected Histogram(Dataset dataset, double lowerBound, double upperBound) {
		this.dataset = dataset;
		Folder.checkDir("reportingTool_tmp" + sep);
		this.writeMetadata(lowerBound, upperBound);
	}

	@Override
	protected File replacedFile(String terminal, String outputFile) {

		File template = new File("templates" + sep + "plots" + sep + "histogram-plt.txt");
		File replacedTemplate = new File("reportingTool_tmp" + sep + FileId.getUniqueName() + "-" + "histogram" + "-plt.txt");

		HashMap<String, String> replacer = new HashMap<String, String>();
		replacer.put("terminal", terminal);
		replacer.put("output", outputFile);

		StringBuilder secrets = new StringBuilder();
		for(int i = 0; i < dataset.getSecrets().size(); i++){
			secrets.append("\"" + "reportingTool_tmp" + gSep + this.uniqueName + "-" + "histogram_" + dataset.getSecrets().get(i).getFileName() + ".txt" + "\"" +
					" using 3:xtic(2) title \"Secret " + dataset.getSecrets().get(i).getName().replaceAll("([\\\\{}_\\^#&$%~\"])", "") + "\" with histogram");
			if((i + 1) < dataset.getSecrets().size()) {
				secrets.append(",\\\n");
			}
		}
		replacer.put("plot", secrets.toString());
		Replacer.replace(template, replacedTemplate, replacer);

		return replacedTemplate;
	}

	/**
	 * This method writes the histogram meta data file.
	 * 
	 * @param lowerBound
	 * @param upperBound
	 */
	private void writeMetadata(double lowerBound, double upperBound) {
		ArrayList<Secret> secrets = new ArrayList<Secret>();

		if(lowerBound == 0.0 && upperBound == 1.0) {
			secrets = this.dataset.getSecrets();
		} else {
			for (Secret secret : this.dataset.getSecrets()) {
				final int lowerPos = (int)((secret.getTimes().size()-1) * lowerBound);
				final Long lowerTime = secret.getTimes().get(lowerPos).getTime();
				final int upperPos = (int)((secret.getTimes().size()-1) * upperBound);
				final Long upperTime = secret.getTimes().get(upperPos).getTime();

				ArrayList<Time> selectedTimes = new ArrayList<Time>();

				Time time = secret.getFirstTime();
				while(time != null) {
					if(time.getTime().compareTo(lowerTime) > 0 && time.getTime().compareTo(upperTime) < 0) {
						selectedTimes.add(time);
					}

					time = time.getSuccessor();
				}

				Collections.sort(selectedTimes);
				if(!selectedTimes.isEmpty()) {
					secrets.add(new Secret(secret.getName(), secret.getFileName(), selectedTimes));
				}
			}
		}

		Long biggestTime = Dataset.findRangeMax(secrets);
		Long smallestTime = Dataset.findRangeMin(secrets);

		try {
			for (int secretPos = 0; secretPos < secrets.size(); secretPos++) {
				if(Conf.get("scale").equals("q")) {
					quadraticMetadata(biggestTime, smallestTime, secretPos, secrets);
				} else if (Conf.get("scale").equals("l")) {
					logarithmicMetadata(biggestTime, smallestTime, secretPos, secrets);
				} else if (Conf.get("scale").equals("n")) {
					normalMetadata(biggestTime, smallestTime, secretPos, secrets);
				} else {
					logger.warning("No valide input for the value of scale.");	
					System.exit(1);
				}
			}
		} catch(IOException e){
			logger.warning("Error writing file for histogram.");
			System.exit(1);
		}
	}

	/**
	 * This method plots the histogram in normal mod.
	 * 
	 * @param biggestTime
	 * @param smallestTime
	 * @param secretPos
	 * @throws IOException 
	 */
	private void normalMetadata(final Long biggestTime, final Long smallestTime, final int secretPos, final ArrayList<Secret> secrets) throws IOException {
		final int numIntervals = numBin;
		final BigDecimal interval = new BigDecimal(biggestTime-smallestTime).divide(new BigDecimal(numIntervals), 10, BigDecimal.ROUND_HALF_UP);
		BufferedWriter bw = getWriter(secretPos);

		BigDecimal probability = new BigDecimal(1).divide(new BigDecimal(secrets.get(secretPos).getTimes().size()), 5, BigDecimal.ROUND_HALF_UP); // 1/times.size

		int intervalPos = 0;
		int elementCounter = 0;
		int labelCounter = (int) Math.ceil(numBin / 10.0);

		for (int k = 1; k <= numBin; k++) {
			//count elements in interval k
			while(intervalPos < secrets.get(secretPos).getTimes().size() && secrets.get(secretPos).getTimes().get(intervalPos).getTime() < (smallestTime+interval.multiply(new BigDecimal(k)).doubleValue())) {
				elementCounter++;
				intervalPos++;
			}
			--labelCounter;
			if(labelCounter != 0) {
				bw.write("-" + "\t" + " " + "\t" + probability.multiply(new BigDecimal(elementCounter)) + "\n");

			} else {
				bw.write("-" + "\t" + (smallestTime+interval.multiply(new BigDecimal(k-1)).doubleValue()) + "-" + (smallestTime+interval.multiply(new BigDecimal(k)).doubleValue()-1) + "\t" + probability.multiply(new BigDecimal(elementCounter)) + "\n");
				labelCounter = (int) Math.ceil(numBin / 10.0);
			}

			elementCounter = 0;
		}

		bw.close();
	}

	/**
	 * This method plots the histogram in logarithmic mod.
	 * 
	 * @param biggestTime
	 * @param smallestTime
	 * @param secretPos
	 * @throws IOException 
	 */
	private void logarithmicMetadata(final Long biggestTime, final Long smallestTime, final int secretPos, final ArrayList<Secret> secrets) throws IOException {
		final int numIntervals = ((int)((1/9.0)*(Math.pow(10, numBin)-1)));
		final BigDecimal interval = new BigDecimal(biggestTime-smallestTime).divide(new BigDecimal(numIntervals), 10, BigDecimal.ROUND_HALF_UP);
		BufferedWriter bw = getWriter(secretPos);

		BigDecimal probability = new BigDecimal(1).divide(new BigDecimal(secrets.get(secretPos).getTimes().size()), 5, BigDecimal.ROUND_HALF_UP); // 1/times.size

		int intervalPos = 0;
		int elementCounter = 0;
		int labelCounter = (int) Math.ceil(numBin / 10.0);

		for (int k = 1; k <= numBin; k++) {
			//count elements in interval k																															
			while(intervalPos < secrets.get(secretPos).getTimes().size() && secrets.get(secretPos).getTimes().get(intervalPos).getTime() < (smallestTime+interval.multiply(new BigDecimal(((int)((1/9.0)*(Math.pow(10, k)-1))))).doubleValue())) {
				elementCounter++;
				intervalPos++;
			}
			--labelCounter;
			if(labelCounter != 0) {
				bw.write("-" + "\t" + " " + "\t" + probability.multiply(new BigDecimal(elementCounter)) + "\n");
			} else {
				bw.write("-" + "\t" + (smallestTime+interval.multiply(new BigDecimal(((int)((1/9.0)*(Math.pow(10, k-1)-1))))).doubleValue()) + "-" + (smallestTime+interval.multiply(new BigDecimal(((int)((1/9.0)*(Math.pow(10, k)-1))))).doubleValue()-1) + "\t" + probability.multiply(new BigDecimal(elementCounter)) + "\n");
				labelCounter = (int) Math.ceil(numBin / 10.0);
			}
			elementCounter = 0;
		}

		bw.close();
	}

	/**
	 * This method plots the histogram in quadratic mod.
	 * 
	 * @param biggestTime
	 * @param smallestTime
	 * @param secretPos
	 * @throws IOException
	 */
	private void quadraticMetadata(final Long biggestTime, final Long smallestTime, final int secretPos, final ArrayList<Secret> secrets) throws IOException {
		final int numIntervals = ((int)Math.pow(2, numBin) - 1);
		final BigDecimal interval = new BigDecimal(biggestTime-smallestTime).divide(new BigDecimal(numIntervals), 10, BigDecimal.ROUND_HALF_UP);
		BufferedWriter bw = getWriter(secretPos);

		BigDecimal probability = new BigDecimal(1).divide(new BigDecimal(secrets.get(secretPos).getTimes().size()), 5, BigDecimal.ROUND_HALF_UP); // 1/times.size

		int intervalPos = 0;
		int elementCounter = 0;
		int labelCounter = (int) Math.ceil(numBin / 10.0);

		for (int k = 1; k <= numBin; k++) {
			//count elements in interval k
			while(secrets.get(secretPos).getTimes().size() > intervalPos && intervalPos < secrets.size() && secrets.get(secretPos).getTimes().get(intervalPos).getTime() < (smallestTime+interval.multiply(new BigDecimal(((int)Math.pow(2, k) - 1))).doubleValue())) {
				elementCounter++;
				intervalPos++;
			}
			--labelCounter;
			if(labelCounter != 0) {
				bw.write("-" + "\t" + " " + "\t" + probability.multiply(new BigDecimal(elementCounter)) + "\n");				

			} else {
				bw.write("-" + "\t" + (smallestTime+interval.multiply(new BigDecimal(((int)Math.pow(2, k-1) - 1))).doubleValue()) + "-" + (smallestTime+interval.multiply(new BigDecimal(((int)Math.pow(2, k) - 1))).doubleValue()-1) + "\t" + probability.multiply(new BigDecimal(elementCounter)) + "\n");				
				labelCounter = (int) Math.ceil(numBin / 10.0);
			}

			elementCounter = 0;
		}

		bw.close();
	}

	/**
	 * This method returns the buffered writer.
	 * 
	 * @param secretPos
	 * @return
	 * @throws IOException
	 */
	private BufferedWriter getWriter(int secretPos) throws IOException {
		File file = new File("reportingTool_tmp" + sep + this.uniqueName + "-" + "histogram_" + this.dataset.getSecrets().get(secretPos).getFileName() + ".txt");
		FileWriter writer = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(writer);
		return bw;
	}
}
