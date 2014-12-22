package de.fau.pi1.timerReporter.writer;

import java.util.logging.Logger;


/**
 * This is the abstract class of the writers.
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @25.09.2012
 *
 */
public abstract class Writer {
	protected static Logger logger = Logger.getLogger("Fau-Timer Reporter");

	abstract public void write() throws Exception;

}
