package de.fau.pi1.timerReporter.plots;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import de.fau.pi1.timerReporter.main.Main;
import de.fau.pi1.timerReporter.tools.Folder;

/**
 * This is the abstract class of the plots.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @07.08.2012
 *
 */
public abstract class Plot {
	protected static Logger logger = Logger.getLogger("Fau-Timer Reporter");
	protected String sep = Folder.getFileSep();
	protected String gSep = Folder.getFileGSep();

	abstract protected File replacedFile(String terminal, String outputFile);

	/**
	 * This method starts to plot a new graph. Therefore, it needs
	 * a output file of the graph and a terminal (pdf, png,..).
	 * 
	 * @param outputFile
	 * @param terminal
	 * @return boolean, if the plot is done successfully
	 */
	protected boolean newPlot(String outputFile, String terminal) {
		File replacedTemplate = this.replacedFile(terminal, outputFile);
		if( doPlot(replacedTemplate) ) {
			return true;
		}
		return false;
	}

	/**
	 * This method plots the graphs. Therefore, it uses gnuplot and
	 * needs a template of the gnuplot file.
	 * 
	 * @param replacedTemplate
	 * @return boolean, if the plot is done successfully
	 */
	private boolean doPlot(File replacedTemplate) {
		try{
			String cmdarray[] = new String[2];
			cmdarray[0] = Main.getGnuplotExecutable();
			cmdarray[1] = replacedTemplate.getAbsolutePath();
			
			Process process = Runtime.getRuntime().exec(cmdarray);
			
			Thread.sleep(1000);
			process.waitFor();
			if(process.exitValue() != 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				StringBuffer str = new StringBuffer();
				String temp;
				while ((temp = br.readLine()) != null)
					str.append(temp);
				br.close();

				throw new RuntimeException("Error while running gnuplot: " + process.exitValue() +
						".\nMessage: " + str);

			}
			return true;
		} catch (IOException e) {
			logger.warning(e.getMessage());
			return false;
		} catch (InterruptedException e) {
			logger.warning(e.getMessage());
			return false;
		}
	}
}