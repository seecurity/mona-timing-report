package de.fau.pi1.timerReporter.dataset;

import java.util.ArrayList;
import java.util.Collections;


/**
 * An object of this class represents one secret with a list of timings. 
 * The time list should be sorted by timings in ascending order. Every 
 * timing knows his successor time with the following secret id, 
 * therefore it is possible to order back into secret id order. Also,
 * a secret knows the first and the last time in secret id order.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @19.07.2012
 *
 */
public class Secret {
	private String name;
	private String fileName;
	private ArrayList<Time> times = new ArrayList<Time>();
	private Time firstTime;
	private Time lastTime;

	public Secret(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}
	
	public Secret(String name, String fileName, ArrayList<Time> times) {
		this.name = name;
		this.fileName = fileName;
		this.times = times;
		this.sortTimes();
		this.firstTime = times.get(0);
		this.lastTime = times.get(times.size() - 1);
	}

	/**
	 * This method adds a new timing. 
	 * @param time
	 */
	public void addTime(Time time) {
		if(this.firstTime == null) {
			this.firstTime = time;
		}

		this.times.add(time);

		if(this.lastTime != null) {
			this.lastTime.setSuccessor(time);
		}

		this.lastTime = time;
	}
	
	/**
	 * This method returns the highest time.
	 * @return highest time stored in the time list
	 */
	public Long getHighestTime() {
		return this.times.get(this.times.size()-1).getTime();

	}

	/**
	 * This method returns the lowest time.
	 * @return lowest time stored in the time list
	 */
	public Long getLowestTime() {
		return this.times.get(0).getTime();
	}

	/**
	 * This method returns the median of the time list.
	 * 
	 * @param lowerPos
	 * @param upperPos
	 * @return median of the time list
	 */
	public Long getMedian(int lowerPos, int upperPos) {

		//we need the size of the space
		int middle = ((upperPos - lowerPos) + 1)/2;
		if((upperPos - lowerPos) % 2 == 0) {
			return this.times.get(lowerPos + middle).getTime();
		} else {
			return (long) ((this.times.get(lowerPos + middle).getTime() + this.times.get(lowerPos + middle-1).getTime())/2.0);
		}
	}

	/**
	 * This method returns the arithmetic mean.
	 * 
	 * @return arithmetic mean of the time list
	 */
	public Long getArithmeticMean() {
		long arithmeticMean = 0;
		for (Time time : this.times) {
			arithmeticMean += time.getTime();
		}

		arithmeticMean = arithmeticMean / this.times.size();
		return arithmeticMean;
	}

	/**
	 * This method returns the lower quantile which is 
	 * necessary for the box plot.
	 * 
	 * @param lowerPos
	 * @param upperPos
	 * @return lower quantile of the box plot
	 */
	public Long getBoxPlotLowerQuantile(int lowerPos, int upperPos) {
		//we need the size of the space
		int size = ((upperPos - lowerPos));
		return this.times.get(lowerPos + (int) (Math.ceil(size * 0.25))).getTime();
	}

	/**
	 * This method returns the upper quantile which is
	 * necessary for the box plot.
	 * 
	 * @param lowerPos
	 * @param upperPos
	 * @return upper quantile of the box plot.
	 */
	public Long getBoxPlotUpperQuantile(int lowerPos, int upperPos) {
		//we need the size of the space
		int size = ((upperPos - lowerPos));
		return this.times.get(lowerPos + (int) (Math.ceil(size * 0.75))).getTime();
	}

	public String getName() {
		return this.name;
	}
	
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * This method returns the list of times
	 * sorted by time.
	 * 
	 * @return
	 */
	public ArrayList<Time> getTimes() {
		return this.times;
	}

	/**
	 * This method sorts the list of times by
	 * time.
	 */
	public void sortTimes() {
		Collections.sort(this.times);
	}
	
	public Time getFirstTime() {
		return this.firstTime;
	}
	
	public Time getLastTime() {
		return this.lastTime;
	}
	
	/**
	 * This method searches the time with the 
	 * matching secret number.
	 * 
	 * @param secretNo
	 * @return time
	 */
	public Time getTime(int secretNo) {
		Time time = this.firstTime;

		while(time != null && time.getSecretNo() != secretNo) {
			time = time.getSuccessor();
		}
		return time;
	}
	
	/**
	 * This method returns a bisected list of the secret times. 
	 * The list starts with the parameter start and ends if no 
	 * more successor is found. Sorted by id.
	 * 
	 * @param start
	 * @return list of times
	 */
	public ArrayList<Time> getBisectedTimes(int start) {
		ArrayList<Time> bisectedTime = new ArrayList<Time>();
		
		for(Time selectedTime = this.getTime(start); selectedTime != null; selectedTime = selectedTime.getSuccessor()) {
			bisectedTime.add(selectedTime);
		}
		return bisectedTime;
	}
	
	/**
	 * This method returns a bisected list of the secret times. The
	 * list starts with the parameter start and ends if the size is
	 * the same of the parameter size. Sorted by id.
	 * 
	 * @param start
	 * @param size
	 * @return list of times
	 */
	public ArrayList<Time> getBisectedTimes(int start, int size) {
		ArrayList<Time> bisectedTime = new ArrayList<Time>(size);
		
		Time selectedTime = this.getTime(start);
		bisectedTime.add(selectedTime);
		
		while(bisectedTime.size() < size) {
			selectedTime = selectedTime.getSuccessor();
			bisectedTime.add(selectedTime);
		}
		
		return bisectedTime;
	}
}
