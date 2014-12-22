package de.fau.pi1.timerReporter.dataset;

/**
 * An Object of this class represents a time. A time has a specific identifier
 * in relation to the secret and an imported identifier.
 *  
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @19.07.2012
 *
 */

public class Time implements Comparable<Time> {
	private Long time;
	private int rowNo;
	private int secretNo;
	private Time successor = null;

	public Time(int rowNo, int secretNo, Long time) {
		this.rowNo = rowNo;
		this.secretNo = secretNo;
		this.time = time;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public int getRowNo() {
		return rowNo;
	}

	public int getSecretNo() {
		return secretNo;
	}

	public Time getSuccessor() {
		return successor;
	}

	public void setSuccessor(Time successor) {
		this.successor = successor;
	}

	public String toString() {
		return "Time #" + rowNo + "[time=" + time + "]";
	}

	/**
	 * This method compares the times. If no difference between 
	 * two times, the method compares the id. 
	 */
	public int compareTo(Time compareTime) {
		if(this.time.equals(compareTime.time) == false) {
			return this.time.compareTo(compareTime.time);
		} else {
			return new Integer(this.rowNo).compareTo(compareTime.rowNo);
		}
	}
}
