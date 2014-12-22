package de.fau.pi1.timerReporter.evaluation;

import java.util.ArrayList;

import de.fau.pi1.timerReporter.dataset.Time;

/**
 * An object of this class represents the box test. It holds methods
 * to evaluate the measurements. 
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @17.08.2012
 *
 */
public class BoxTest {

	/**
	 * The method gives a boolean value, if it found one 
	 * significant smaller box. Also it tests if the time 
	 * array itself overlaps. Therefore, it iterates about
	 * all possible boxes.
	 * 
	 * @param timesA
	 * @param timesB
	 * @return boolean, if one box test (if significant smaller) is successful
	 */
	public static boolean boxTest(ArrayList<Time> timesA, ArrayList<Time> timesB) {

		for (int i = 0; i < 100; ++i) { // percentile lower bound
			for (int j = (i + 1); j <= 100; ++j) { // percentile upper bound

				int lowerPosA = getPercentile(i, timesA.size());
				int upperPosA = getPercentile(j, timesA.size());
				if(lowerPosA == upperPosA) {
					continue;
				}	

				int lowerPosB = getPercentile(i, timesB.size());
				int upperPosB =  getPercentile(j, timesB.size());
				if(lowerPosB == upperPosB) {
					continue;
				}

				Long upperTimeA = timesA.get(upperPosA).getTime();
				Long lowerTimeB = timesB.get(lowerPosB).getTime();

				if(isSignificantlySmaller(upperTimeA, lowerTimeB)) {
					return true;
				}
			}
		} 
		return false;
	}

	/**
	 * This method starts the box test only for the optimal box range. 
	 * 
	 * @param timesA
	 * @param timesB
	 * @param optimalBox
	 * @return 
	 */
	public static boolean boxTestSmaller(ArrayList<Time> timesA, ArrayList<Time> timesB, double[] optimalBox) {

		int lowerPosA = getPercentile((int) (optimalBox[0] * 100), timesA.size());
		int upperPosA = getPercentile((int) (optimalBox[1] * 100), timesA.size());
		if(lowerPosA == upperPosA) {
			return false;
		}

		int lowerPosB = getPercentile((int) (optimalBox[0] * 100), timesB.size());
		int upperPosB =  getPercentile((int) (optimalBox[1] * 100), timesB.size());
		if(lowerPosB == upperPosB) {
			return false;
		}

		Long upperTimeA = timesA.get(upperPosA).getTime();
		Long lowerTimeB = timesB.get(lowerPosB).getTime();

		if(isSignificantlySmaller(upperTimeA, lowerTimeB)) {
			return true;
		} 
		return false;
	}

	/**
	 * This method starts the box test only for the optimal box.
	 * 
	 * @param timesA
	 * @param timesB
	 * @param optimalBox
	 * @return
	 */
	public static boolean boxTestOverlap(ArrayList<Time> timesA, ArrayList<Time> timesB, double[] optimalBox) {

		int lowerPosA = getPercentile((int) (optimalBox[0] * 100), timesA.size());
		int upperPosA = getPercentile((int) (optimalBox[1] * 100), timesA.size());
		if(lowerPosA == upperPosA) {
			return false;
		}

		int lowerPosB = getPercentile((int) (optimalBox[0] * 100), timesB.size());
		int upperPosB =  getPercentile((int) (optimalBox[1] * 100), timesB.size());
		if(lowerPosB == upperPosB) {
			return false;
		}

		Long lowerTimeA = timesA.get(lowerPosA).getTime();
		Long upperTimeA = timesA.get(upperPosA).getTime();
		Long lowerTimeB = timesB.get(lowerPosB).getTime();
		Long upperTimeB = timesB.get(upperPosB).getTime();

		if(!isSignificantlyDifferent(lowerTimeA, upperTimeA, lowerTimeB, upperTimeB)) {
			return true;
		} 

		return false;
	}

	/**
	 * This method searches the optimal box of the two time lists. The optimal
	 * box holds most of all times and has a valid box test.
	 * 
	 * @param timesA
	 * @param timesB
	 * @return ArrayList<Integer> lower and upper bound of the optimal box
	 */
	public static double[] optimalBox(ArrayList<Time> timesA, ArrayList<Time> timesB, Timeline timeline) {
		double[] optimalBox = new double[2];
		int lowerBound = 0;
		int upperBound = 0;

		for (int i = 0; i < 100; ++i) { // percentile lower bound
			for (int j = (i + 1); j <= 100; ++j) { // percentile upper bound

				int lowerPosA = getPercentile(i, timesA.size());
				int upperPosA = getPercentile(j, timesA.size());
				if(lowerPosA == upperPosA) {
					continue;
				}

				int lowerPosB = getPercentile(i, timesB.size());
				int upperPosB =  getPercentile(j, timesB.size());
				if(lowerPosB == upperPosB) {
					continue;
				}

				Long upperTimeA = timesA.get(upperPosA).getTime();
				Long lowerTimeB = timesB.get(lowerPosB).getTime();

				if(isSignificantlySmaller(upperTimeA, lowerTimeB)) {
					incrementTimeline(i, j, timeline);
					if ((upperBound - lowerBound) < (j - i)) {
						lowerBound = i;
						upperBound = j;
					}
				} 
			}
		}

		optimalBox[0] = lowerBound / 100.0;
		optimalBox[1] = upperBound / 100.0;
		return optimalBox;
	}

	/**
	 * This method returns the significant differences value. If the 
	 * boxes are significant different, the result is true, else the
	 * boxes aren't significant different, the result is false. 
	 * 
	 * @param lowerTimeA
	 * @param upperTimeA
	 * @param lowerTimeB
	 * @param upperTimeB
	 * @return boolean, if the boxes significant different
	 */
	private static boolean isSignificantlyDifferent(Long lowerTimeA, Long upperTimeA, Long lowerTimeB, Long upperTimeB) {
		return ( (upperTimeA.compareTo(lowerTimeB) < 0) || (upperTimeB.compareTo(lowerTimeA) < 0) );
	}

	/**
	 * This method returns the significant differences value. If the
	 * boxes are significant smaller, the result is true, otherwise the 
	 * boxes aren't significant or bigger. 
	 * 
	 * @param lowerTimeA
	 * @param upperTimeA
	 * @param lowerTimeB
	 * @param upperTimeB
	 * @return boolean, if the boxes significant smaller
	 */
	private static boolean isSignificantlySmaller(Long upperTimeA, Long lowerTimeB) {
		return (upperTimeA.compareTo(lowerTimeB) < 0);
	}

	/**
	 * This method increments the time line counter. Every significantly smaller
	 * box of the measurement increases the related boxes in the time line. 
	 * 
	 * @param lowerBound
	 * @param upperBound
	 * @param timeline
	 */
	private static void incrementTimeline(int lowerBound, int upperBound, Timeline timeline) {
		for (int i = lowerBound; i <= upperBound; ++i) {
			timeline.incrementBox(i);
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