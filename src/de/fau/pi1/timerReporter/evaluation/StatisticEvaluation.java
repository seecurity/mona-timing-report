package de.fau.pi1.timerReporter.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import de.fau.pi1.timerReporter.dataset.Dataset;
import de.fau.pi1.timerReporter.dataset.Secret;
import de.fau.pi1.timerReporter.dataset.Time;
import de.fau.pi1.timerReporter.plots.PlotPool;
import de.fau.pi1.timerReporter.tools.FileId;
import de.fau.pi1.timerReporter.tools.Folder;

/**
 * An object of this class performs the statistic evaluation. Therefore, it starts
 * the calibration phase. This phase searches the optimal percentile boxes of the 
 * measurement. Then, it approximates to the smallest possible size of the measurement.
 * 
 * The validation phase validates the results by splitting the measurement in 
 * smallest ranges and starting the box test over the ranges.
 * 
 * If one range isn't significantly different, the validation phase sets the smallest
 * size up and begins again.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @16.08.2012
 *
 */
public class StatisticEvaluation {

	private Dataset dataSet;
	private PlotPool plotPool;
	double[] optimalBox;
	private ArrayList<Timeline> timelines = new ArrayList<Timeline>();
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");
	private ArrayList<BoxTestResults> boxTestResults = new ArrayList<BoxTestResults>();

	public StatisticEvaluation(Dataset dataSet, PlotPool plotPool) {
		this.dataSet = dataSet;
		this.plotPool = plotPool;

		this.optimalBox =  new double[2];
		this.optimalBox[0] = 0.0;
		this.optimalBox[1] = 0.0;
	}

	/**
	 * This method stores the time lines and returns the output
	 * paths.
	 * 
	 * @param outputPath
	 * @return ArrayList<String> output paths of the time lines
	 */
	public ArrayList<String> storeTimelines(String outputPath) {
		ArrayList<String> timelineNames = new ArrayList<String>();

		for (Timeline timeline : this.timelines) {
			String timelineName = outputPath + timeline.getName();
			timelineNames.add(timeline.getName());
			timeline.store(700, 105, timelineName);
		}
		return timelineNames;
	}

	/**
	 * This method tests each combination of secret pair.
	 * 
	 * Firstly, it searches a optimal box. If a optimal box
	 * is found, the measurement has a measurable significant
	 * difference.
	 * 
	 * Secondly, it searches the smallest size with a significant
	 * different result. The smallest size is the size of the 
	 * times in a measurement necessary to find out a significant 
	 * differences. The smallest size is necessary for the next 
	 * step, the validation phase. 
	 * 
	 * This method starts the validation phase automatically for
	 * each secret pair.
	 * 
	 */
	public void calibrationPhase() {
		// iterate over all secret combination
		for (Secret secretA : this.dataSet.getSecrets()) {
			for (Secret secretB : this.dataSet.getSecrets()) {
				if(secretA != secretB) {
					if(this.optimalBox[0] == 0.0 && this.optimalBox[1] == 0.0) {
						if(searchOptimalBox(secretA, secretB)) {
							int smallestSize = searchSmallestSize(secretA, secretB);
							this.openValidationPhase(secretA, secretB, smallestSize, this.optimalBox);
						}

						this.optimalBox =  new double[2];
						this.optimalBox[0] = 0.0;
						this.optimalBox[1] = 0.0;

					} else {
						int smallestSize = searchSmallestSize(secretA, secretB);
						if(smallestSize != 0) {
							plotPool.plot("Filtered Measurments: User Input Optimal Box (" + secretA.getName() + "-" + secretB.getName() + ")", this.optimalBox[0], this.optimalBox[1]);

							this.openValidationPhase(secretA, secretB, smallestSize, this.optimalBox);
						} else {
							logger.warning(secretA.getName() + " < " + secretB.getName() + ": no significant different result found! You need to measure more times.");

						}
					}
				}

			}
		}
	}

	/**
	 * This method provides only the validation phase with
	 * an inputed smallest size.
	 * 
	 * @param int smallest size 
	 */
	public void onlyValidationPhase(int smallestSize) {
		// iterate over all secret combination
		for (Secret secretA : this.dataSet.getSecrets()) {
			for (Secret secretB : this.dataSet.getSecrets()) {
				if(secretA != secretB) {
					if(this.optimalBox[0] == 0.0 && this.optimalBox[1] == 0.0) {
						if(searchOptimalBox(secretA, secretB)) {
							this.openValidationPhase(secretA, secretB, smallestSize, this.optimalBox);
						}

						this.optimalBox =  new double[2];
						this.optimalBox[0] = 0.0;
						this.optimalBox[1] = 0.0;

					} else {
						if(smallestSize != 0) {
							plotPool.plot("Filtered Measurments: User Input Optimal Box (" + secretA.getName() + "-" + secretB.getName() + ")", this.optimalBox[0], this.optimalBox[1]);

							this.openValidationPhase(secretA, secretB, smallestSize, this.optimalBox);
						} else {
							logger.warning(secretA.getName() + " < " + secretB.getName() + ": no significant different result found! You need to measure more times.");
						}
					}
				}
			}
		}
	}

	/**
	 * This method searchs only the optimal box and returns
	 * a boolean if an optimal box found. If no optimal box 
	 * is found, there are no significant differences. The
	 * user should try to measure more timings to find a 
	 * significant differences.
	 * 
	 * @param secretA
	 * @param secretB
	 * @return boolean
	 */
	private boolean searchOptimalBox(Secret secretA, Secret secretB) {

		ArrayList<Time> timesA = secretA.getTimes();
		ArrayList<Time> timesB = secretB.getTimes();

		// first step: search optimal box
		Timeline timeline = new Timeline("timeline-" + FileId.getId() + "-" + secretA.getFileName() + "-smaller-" +secretB.getFileName() + ".png");
		this.timelines.add(timeline);
		this.optimalBox = BoxTest.optimalBox(timesA, timesB, timeline);

		if(this.optimalBox[0] != 0 || this.optimalBox[1] != 0) { 	
			// if a optimal box found, there are significant different results!
			logger.info(secretA.getName() + " < " + secretB.getName() + ": optimal box " + optimalBox[0] + "-" + optimalBox[1]);
			plotPool.plot("Filtered Measurments: Optimal Box (" + secretA.getName() + "-" + secretB.getName() + ")", this.optimalBox[0], this.optimalBox[1]);
			return true;
		} else {
			// there is no significant different result found! The user needs
			// to measure more times.
			logger.warning(secretA.getName() + " < " + secretB.getName() + ": no significant different result found! You need to measure more times.");
			return false;
		}
	}

	/**
	 * This method searchs the smallest size of the boxes per secret. 
	 * The smallest size is the smallest range that contains
	 * significant different times.
	 * 
	 * @param secretA
	 * @param secretB
	 */
	private int searchSmallestSize(Secret secretA, Secret secretB) {

		int smallestSize = 0;

		ArrayList<Time> timesA = secretA.getTimes();
		ArrayList<Time> timesB = secretB.getTimes();

		float bisector = 100; 
		int isSmallestSizeBisected = 0;

		do {

			// in the validation phase the measurement is split
			// into smallest subsets, so the smallest subset 
			// must be smaller or equal than the size of the 
			// smallest time lists
			if(timesA.size() <= timesB.size()) {
				smallestSize = timesA.size();
			} else {
				smallestSize = timesB.size();
			}

			++isSmallestSizeBisected;

			bisector -= (bisector / 2);

			timesA = secretA.getBisectedTimes((int)(secretA.getTimes().size() - (secretA.getTimes().size() * bisector / 100.0)));
			timesB = secretB.getBisectedTimes((int)(secretB.getTimes().size() - (secretB.getTimes().size() * bisector / 100.0)));

			Collections.sort(timesA);
			Collections.sort(timesB);
			
			if(timesA.size() <= 10 || timesB.size() <= 10) {
				break;
			}

		} while(BoxTest.boxTest(timesA, timesB)); // tests if a box is significant different


		// because both secrets contains the smallest size
		if(isSmallestSizeBisected != 1) {
			logger.info(secretA.getName() + " < " + secretB.getName() + ": amount of minimal measures per secret: " + smallestSize); 
		}

		return smallestSize;
	}

	/**
	 * This method opens the validation phase and double the smallest size, if it is needed
	 * and starts the validation phase again.
	 * 
	 * @param secretA
	 * @param secretB
	 * @param smallestSize
	 * @param optimalBox
	 */
	private void openValidationPhase(Secret secretA, Secret secretB, int smallestSize, double[] optimalBox) {
		this.boxTestResults.add(new BoxTestResults(this.dataSet.getInputFile(), secretA, secretB, this.optimalBox));
		int newSmallestSize = 0;

		if(this.validationPhase(secretA, secretB, smallestSize, optimalBox)) {
			logger.info(secretA.getName() + " < " + secretB.getName() + ": VALID amount of minimal measures per secret: " + smallestSize);
		} else {
			newSmallestSize = this.doubleSmallestSize(secretA, secretB, smallestSize);
			if(newSmallestSize != 0) {
				this.openValidationPhase(secretA, secretB, newSmallestSize, optimalBox);
			}
		}
	}

	/**
	 * This method validates the results of the calibration phase. Therefore, 
	 * it splits the measured times into subsets. Each subsets has the size 
	 * of the smallest size found in the calibration phase. Than, it tests 
	 * if all boxes are significant different. If all boxes of secretA and secretB
	 * are significant different, it starts the box test with only boxes of secretA
	 * and than with boxes of secretB. If all boxes of a secret overlaps, it has
	 * found a valid result.
	 * 
	 * @param secretA
	 * @param secretB
	 * @param smallestSize
	 * @param optimalBox
	 * @return boolean
	 */
	private boolean validationPhase(Secret secretA, Secret secretB, int smallestSize, double[] optimalBox) {

		// number of all possible subsets with a size of smallestSize
		int numberSubsets; 
		if((secretA.getTimes().size() / smallestSize) <= (secretB.getTimes().size() / smallestSize)) {
			numberSubsets = (secretA.getTimes().size() / smallestSize);
		} else {
			numberSubsets = (secretB.getTimes().size() / smallestSize);
		}

		// rest of the size modulo all subsets
		int restA = secretA.getTimes().size() % (numberSubsets * smallestSize);
		int restB = secretB.getTimes().size() % (numberSubsets * smallestSize);

		ArrayList<Time> subsetA; 
		ArrayList<Time> subsetB;

		int countWrongResults = 0;

		ArrayList<String> validateSubsetSignificantDifferent = new ArrayList<String>();
		ArrayList<String> validateSubsetOverlapA = new ArrayList<String>();
		ArrayList<String> validateSubsetOverlapB = new ArrayList<String>();
		ArrayList<Time> prevSubsetA = new ArrayList<Time>();
		ArrayList<Time> prevSubsetB = new ArrayList<Time>();


		for (int i = 0; i < numberSubsets; ++i) {

			int countInvalid = 0;
			// the rest will be deducted at the beginning
			subsetA = secretA.getBisectedTimes(restA + (smallestSize * i), smallestSize);
			subsetB = secretB.getBisectedTimes(restB + (smallestSize * i), smallestSize);

			Collections.sort(subsetA);
			Collections.sort(subsetB);

			if (BoxTest.boxTestSmaller(subsetA, subsetB, optimalBox)) {
				validateSubsetSignificantDifferent.add("o");

			} else {
				validateSubsetSignificantDifferent.add("x");
				//logger.info("subset " + i + ": wrong result");
				++countInvalid;
			}
			
			if(i != 0) {
				if (BoxTest.boxTestOverlap(prevSubsetA, subsetA, optimalBox)) {
					validateSubsetOverlapA.add("o");

				} else {
					validateSubsetOverlapA.add("x");
					//logger.info("subset " + i + ": only significant different");
					++countInvalid;
				} 

				if (BoxTest.boxTestOverlap(prevSubsetB, subsetB, optimalBox)) {
					validateSubsetOverlapB.add("o");
					//logger.info("subset " + i + ": right result");

				} else {
					validateSubsetOverlapB.add("x");
					//logger.info("subset " + i + ": only significant different, subset A overlaps");
					++countInvalid;
				}
			}

			if(countInvalid > 0) {
				++countWrongResults;
			}

			prevSubsetA = new ArrayList<Time>();
			prevSubsetB = new ArrayList<Time>();

			prevSubsetA = subsetA;
			prevSubsetB = subsetB;
		}

		double confidenceInterval = 100 - (countWrongResults * 100 / numberSubsets);

		this.boxTestResults.get(this.boxTestResults.size() - 1).saveValidation(smallestSize, confidenceInterval, validateSubsetOverlapA, validateSubsetOverlapB, validateSubsetSignificantDifferent);
		logger.finest("\n\"o\" = successful box test \n\"x\" = unsuccesful box test\n");
		logger.finest(secretA.getName() + " overlaps: " + Folder.convertArrayListToString(validateSubsetOverlapA));
		logger.finest(secretB.getName() + " overlaps: " + Folder.convertArrayListToString(validateSubsetOverlapB));
		logger.finest(secretA.getName() + " < " + secretB.getName() + ": " + Folder.convertArrayListToString(validateSubsetSignificantDifferent));

		logger.info(secretA.getName() + " < " + secretB.getName() + ": validate smallest size " + smallestSize + ": " + countWrongResults + " out of " + numberSubsets + " comparisons returned wrong results.");

		if(countWrongResults != 0) {
			return false;
		}

		return true;
	}

	/**
	 * This method doubles the smallest size only if the new smallest size
	 * is smaller than both time lists.
	 * 
	 * @param secretA
	 * @param secretB
	 * @param smallestSize
	 * @return int new smallest size
	 */
	private int doubleSmallestSize(Secret secretA, Secret secretB, int smallestSize) {
		int newSmallestSize = (smallestSize * 2);

		if(newSmallestSize <= secretA.getTimes().size() && newSmallestSize <= secretB.getTimes().size()) {
			return newSmallestSize;
		} else {
			return 0;
		}
	}

	/**
	 * This method sets the optimal box upper and lower bound.
	 * This method should called before the calibration phase,
	 * if the user want to use an inputed optimal box.
	 * 
	 * @param optimalBox
	 */
	public void setOptimalBox(double[] optimalBox) {
		if(!(optimalBox[1] > optimalBox[0] && optimalBox[0] <= 1.0 && optimalBox[1] >= 0.0)) {
			logger.warning("Error: The lower/upper optimal bound have not the right range. They must be between 0.0 and 1.0. upper > lower");
			System.exit(1);
		}

		this.optimalBox = optimalBox;

		logger.info("USER INPUT optimal box " + this.optimalBox[0] + "-" + this.optimalBox[1]);
	}

	/**
	 * This method prints the box test results into a file. 
	 * Therefore, it iterates above all box test results.
	 * 
	 * @param outputFile
	 */
	public void printBoxTestResults(File outputFile) {
		String output = "Input File;SecretA < SecretB;Optimal Box;Smallest Size;Confidence Interval;Graphic Overlaps Subset A;valid;invalid;Graphic Overlaps Subset B;valid;invalid;Graphic Significant Difference;valid;invalid;";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			for (int i=0; i < output.length(); i++){
				bw.write(output.charAt(i));
			}
			bw.write('\n');
			bw.close();
		} catch (IOException e) {
			logger.warning("Error while writing the box test result csv.");
			System.exit(1);
		}
		for (BoxTestResults boxTest : this.boxTestResults) {
			boxTest.printBoxTestResult(outputFile);
		}
	}

	public ArrayList<BoxTestResults> getBoxTestResults() {
		return boxTestResults;
	}
}
