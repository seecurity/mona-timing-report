package de.fau.pi1.timerReporter.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;


import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

/**
 * This class reads in the configuration file and 
 * sets all default values. 
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.1
 * @since 16.08.2012
 */
public class Conf {

	private static Conf instance = new Conf();
	private HashMap<String, String> conf = new HashMap<String, String>();
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");

	private Conf() {
		setDefaultValues();
	}

	/**
	 * This method returns the instance of conf.
	 * 
	 * @return Conf
	 */
	public static Conf getInstance() {
		return Conf.instance;
	}

	/**
	 * This method returns a boolean value,
	 * if the key is in the hash map.
	 * 
	 * @param key
	 * @return boolean
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(String key) {
		return conf.containsKey(key);
	}

	/**
	 * This method returns string with the entered key or null if key is not
	 * set.
	 * 
	 * @param key
	 * @return String
	 */
	public static String get(String key) {
		return Conf.getInstance().conf.get(key);
	}

	/**
	 * This method puts a new key and value into
	 * the hash map conf.<br />
	 * 
	 * Possible keys, that can be put:<br />
	 * 
	 * -pdflatexPath: Path to the directory where pdflatex is located.<br />
	 * -makeindexPath: Path to the directory where makeindex is located.<br />
	 * -gnuplot: Path to the directory where gnuplot is located.<br />
	 * -inputFile: Path and name of the input file containing the measurements.<br />
	 * -name: Name of the final PDF report.<br />
	 * -numBin: Number of bins at the x-axis of histograms.<br />
	 * -scale: Set the scale of some plots to logarithmic (l) or (q). Leave this blank to use a normal scale.<br />
	 * -lowerBound: Lower bound of the user input measurement filter (default is 0.05).<br />
	 * -upperBound: Upper bound of the user input measurement filter (default is 0.10).<br />
	 * -lowerOptimalBound: Lower bound of the optimal box. The lower optimal box must be smaller than the upper optimal box. [0.0-0.99] (no default value).<br />
	 * -upperOptimalBound: Upper bound of the optimal box. The upper optimal box must be bigger than the lower optimal box. [0.01-1.0] (no default value).<br />
	 * -verbose: Option to start a finer logging.<br />
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public static String put(String key, String value) {
		return Conf.instance.conf.put(key, value);
	}

	/**
	 * This method returns a string of the conf parameter.
	 * 
	 * @return
	 * @see java.util.AbstractMap#toString()
	 */
	public String toString() {
		return conf.toString();
	}

	/**
	 * This method parses the console parameter.
	 * 
	 * @param args
	 */
	public void parseParameter(String[] args) {
		JSAP jsap = getJsap();

		JSAPResult result = jsap.parse(args);

		if (result.getBoolean("help")) {
			printHelp(jsap);
		}

		if (result.contains("pdflatexPath")) {
			this.conf.put("pdflatexPath", result.getString("pdflatexPath"));
		}

		if (result.contains("makeindexPath")) {
			this.conf.put("makeindexPath", result.getString("makeindexPath"));
		}

		if (result.contains("gnuplot")) {
			this.conf.put("gnuplot", result.getString("gnuplot"));
		}

		if (result.contains("inputFile")) {
			this.conf.put("inputFile", result.getString("inputFile"));
		}

		if (result.contains("name")) {
			this.conf.put("name", result.getString("name"));
		}

		if (result.contains("numBin")) {
			try {
				Integer.parseInt(result.getString("numBin"));
				this.conf.put("numBin", result.getString("numBin"));
			} catch (NumberFormatException e) {
				logger.warning("The number of bins must be an int.");
				System.exit(1);
			}
		}

		if (result.contains("scale")) {
			if (result.getString("scale").equals("l")
					|| result.getString("scale").equals("q") || result.getString("scale").equals("n")) {
				this.conf.put("scale", result.getString("scale"));
			} else {
				logger.warning("No valide input for the value of scale.");
				System.exit(1);
			}
		}

		if (result.contains("lowerBound")) {
			try {
				Double.parseDouble(result.getString("lowerBound"));
				this.conf.put("lowerBound", result.getString("lowerBound"));
				
				if(Double.parseDouble(result.getString("lowerBound")) < 0.00 || Double.parseDouble(result.getString("lowerBound")) > 0.99) {
					logger.warning("The value of lower bound has not the right range. [0.00-0.99]");
					System.exit(1);
				}
				
				if(result.contains("upperBound")) {
					if (Double.parseDouble(result.getString("lowerBound")) >= Double.parseDouble(result.getString("upperBound"))) {
						logger.warning("The value of lower bound must be lower than the value of the upper bound. lower < upper");
						System.exit(1);
					} 
				} else {
					logger.warning("If you would choose an user input box, please add a lower bound and an upper bound.");
					System.exit(1);
				}

			} catch (NumberFormatException e) {
				logger.warning("The value of lower bound must be a double.");
				System.exit(1);
			}
		}

		if (result.contains("upperBound")) {
			try {
				Double.parseDouble(result.getString("upperBound"));
				this.conf.put("upperBound", result.getString("upperBound"));
				
				if(Double.parseDouble(result.getString("upperBound")) < 0.01 || Double.parseDouble(result.getString("upperBound")) > 1.0) {
					logger.warning("The value of upper bound has not the right range. [0.01-1.0]");
					System.exit(1);
				}
				
				if(result.contains("lowerBound")) {
					if (Double.parseDouble(result.getString("lowerBound")) >= Double.parseDouble(result.getString("upperBound"))) {
						logger.warning("The value of upper bound must be higher than the value of the lower bound. lower < upper");
						System.exit(1);
					} 
				} else {
					logger.warning("If you would choose an user input box, please add a lower bound and an upper bound.");
					System.exit(1);
				}
				
			} catch (NumberFormatException e) {
				logger.warning("The value of upper bound must be a double.");
				System.exit(1);
			}
		}
		
		if (result.contains("lowerOptimalBound")) {
			try {
				Double.parseDouble(result.getString("lowerOptimalBound"));
				this.conf.put("lowerOptimalBound", result.getString("lowerOptimalBound"));
				
				if(Double.parseDouble(result.getString("lowerOptimalBound")) < 0.00 || Double.parseDouble(result.getString("lowerOptimalBound")) > 0.99) {
					logger.warning("The value of lower optimal bound has not the right range. [0.00-0.99]");
					System.exit(1);
				}
				
				if(result.contains("upperOptimalBound")) {
					if (Double.parseDouble(result.getString("lowerOptimalBound")) >= Double.parseDouble(result.getString("upperOptimalBound"))) {
						logger.warning("The value of the optimal upper bound must be higher than the value of the lower bound. lower < upper");
						System.exit(1);
					} 
				} else {
					logger.warning("If you would choose an optimal box, please add an optimal lower bound and an optimal upper bound.");
					System.exit(1);
				}

			} catch (NumberFormatException e) {
				logger.warning("The value of lower optimal bound must be a double.");
				System.exit(1);
			}
		}
		
		if (result.contains("upperOptimalBound")) {
			try {
				Double.parseDouble(result.getString("upperOptimalBound"));
				this.conf.put("upperOptimalBound", result.getString("upperOptimalBound"));
				
				if(Double.parseDouble(result.getString("upperOptimalBound")) < 0.01 || Double.parseDouble(result.getString("upperOptimalBound")) > 1.0) {
					logger.warning("The value of upper optimal bound has not the right range. [0.01-1.0]");
					System.exit(1);
				}
				
				if(result.contains("lowerOptimalBound")) {
					if (Double.parseDouble(result.getString("lowerOptimalBound")) >= Double.parseDouble(result.getString("upperOptimalBound"))) {
						logger.warning("The value of the optimal upper bound must be higher than the value of the lower bound. lower < upper");
						System.exit(1);
					} 
				} else {
					logger.warning("If you would choose an optimal box, please add an optimal lower bound and an optimal upper bound.");
					System.exit(1);
				}

			} catch (NumberFormatException e) {
				logger.warning("The value of upper optimal bound must be a double.");
				System.exit(1);
			}
		}
		
		if (result.contains("verbose")) {
			if(result.getBoolean("verbose")) {
				this.conf.put("verbose", "true");
			} else {
				this.conf.put("verbose", "false");
			}
		}
	}
	
	/**
	 * This method parses the config.ini file.
	 * 
	 */
	public void parseIni(File iniFile) {
		try {
			Ini ini = new Ini(iniFile);
			Section section = ini.get("reportingTool");

			if (section.containsKey("pdflatexPath")) {
				this.conf.put("pdflatexPath", section.get("pdflatexPath"));
			}

			if (section.containsKey("makeindexPath")) {
				this.conf.put("makeindexPath", section.get("makeindexPath"));
			}

			if (section.containsKey("gnuplot")) {
				this.conf.put("gnuplot", section.get("gnuplot"));
			}

			if (section.containsKey("inputFile")) {
				this.conf.put("inputFile", section.get("inputFile"));
			}

			if (section.containsKey("name")) {
				this.conf.put("name", section.get("name"));
			}

			if (section.containsKey("numBin")) {
				try {
					Integer.parseInt(section.get("numBin"));
					this.conf.put("numBin", section.get("numBin"));
				} catch (NumberFormatException e) {
					logger.warning("The number of bins must be an integer.");
					System.exit(1);
				}
			}

			if (section.containsKey("scale")) {
				if (section.get("scale").equals("l")
						|| section.get("scale").equals("q") || section.get("scale").equals("n")) {
					this.conf.put("scale", section.get("scale"));
				} else {
					logger.warning("No valide input for the value of scale.");
					System.exit(1);
				}
			}

			if (section.containsKey("lowerBound")) {
				try {
					Double.parseDouble(section.get("lowerBound"));
					this.conf.put("lowerBound", section.get("lowerBound"));
				} catch (NumberFormatException e) {
					logger.warning("The value of lower bound must be a double.");
					System.exit(1);
				}
			}

			if (section.containsKey("upperBound")) {
				try {
					Double.parseDouble(section.get("upperBound"));
					this.conf.put("upperBound", section.get("upperBound"));
				} catch (NumberFormatException e) {
					logger.warning("The value of upper bound must be a double.");
					System.exit(1);
				}
			}
			
			if (section.containsKey("upperOptimalBound")) {
				try {
					Double.parseDouble(section.get("upperOptimalBound"));
					this.conf.put("upperOptimalBound", section.get("upperOptimalBound"));
				} catch (NumberFormatException e) {
					logger.warning("The value of upper optimal bound must be an double.");
					System.exit(1);
				}
			}
			
			if (section.containsKey("lowerOptimalBound")) {
				try {
					Double.parseDouble(section.get("lowerOptimalBound"));
					this.conf.put("lowerOptimalBound", section.get("lowerOptimalBound"));
				} catch (NumberFormatException e) {
					logger.warning("The value of lower optimal bound must be an double.");
					System.exit(1);
				}
			}
			
			if (section.containsKey("verbose")) {
				this.conf.put("verbose", section.get("verbose"));
			}

		} catch (InvalidFileFormatException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * This method creates a new JSAP and returns it.
	 * 
	 * @return JSAP
	 */
	private JSAP getJsap() {
		JSAP jsap = new JSAP();

		try {
			// help
			Switch help = new Switch("help").setShortFlag('h').setLongFlag(
					"help");
			help.setHelp("Prints this help message.");
			jsap.registerParameter(help);

			// pdflatexPath
			FlaggedOption pdflatexPath = new FlaggedOption("pdflatexPath")
			.setLongFlag("pdflatexPath");
			pdflatexPath
			.setHelp("Path to the directory where pdflatex is located.");
			jsap.registerParameter(pdflatexPath);

			// makeindexPath
			FlaggedOption makeindexPath = new FlaggedOption("makeindexPath")
			.setLongFlag("makeindexPath");
			makeindexPath
			.setHelp("Path to the directory where makeindex is located.");
			jsap.registerParameter(makeindexPath);

			// gnuplotPath
			FlaggedOption gnuplot = new FlaggedOption("gnuplot")
			.setLongFlag("gnuplot");
			gnuplot.setHelp("Path to the directory where gnuplot is located.");
			jsap.registerParameter(gnuplot);

			// inputFile
			FlaggedOption inputFile = new FlaggedOption("inputFile")
			.setShortFlag('i').setLongFlag("inputFile");
			inputFile
			.setHelp("Path and name of the input file containing the measurements.");
			jsap.registerParameter(inputFile);

			// name
			FlaggedOption name = new FlaggedOption("name").setShortFlag('n')
					.setLongFlag("name");
			name.setHelp("Name of the final PDF report.");
			jsap.registerParameter(name);

			// numBin
			FlaggedOption numBin = new FlaggedOption("numBin")
			.setLongFlag("numBin");
			numBin.setHelp("Number of bins at the x-axis of histograms.");
			jsap.registerParameter(numBin);

			// scale
			FlaggedOption scale = new FlaggedOption("scale")
			.setLongFlag("scale");
			scale.setHelp("Set the scale of some plots to logarithmic (l) or (q). Leave this blank to use a normal scale.");
			jsap.registerParameter(scale);

			// lowerBound
			FlaggedOption lowerBound = new FlaggedOption("lowerBound")
			.setLongFlag("lowerBound");
			lowerBound
			.setHelp("Lower bound of the user input measurement filter (default is 0.05).");
			jsap.registerParameter(lowerBound);

			// upperBound
			FlaggedOption upperBound = new FlaggedOption("upperBound")
			.setLongFlag("upperBound");
			upperBound
			.setHelp("Upper bound of the user input measurement filter (default is 0.10).");
			jsap.registerParameter(upperBound);
			
			// lowerOptimalBound
			FlaggedOption lowerOptimalBound  = new FlaggedOption("lowerOptimalBound")
			.setLongFlag("lowerOptimalBound");
			lowerOptimalBound
			.setHelp("Lower bound of the optimal box. The lower optimal box must be smaller than the upper optimal box. [0.0-0.99] (no default value).");
			jsap.registerParameter(lowerOptimalBound );
			
			// upperOptimalBound
			FlaggedOption upperOptimalBound  = new FlaggedOption("upperOptimalBound")
			.setLongFlag("upperOptimalBound");
			upperOptimalBound
			.setHelp("Upper bound of the optimal box. The upper optimal box must be bigger than the lower optimal box. [0.01-1.0] (no default value).");
			jsap.registerParameter(upperOptimalBound );
			
			// debug
			Switch verbose = new Switch("verbose").setShortFlag('V').setLongFlag(
					"verbose");
			verbose.setHelp("Option to start a finer logging.");
			jsap.registerParameter(verbose);

		} catch (JSAPException e) {
		}
		return jsap;
	}

	/**
	 * This method sets the default values of the
	 * reporting tool.
	 */
	private void setDefaultValues() {

		File pdfLatex = findPdflatex();
		if (pdfLatex != null) {
			this.conf.put("pdflatexPath", pdfLatex.getAbsolutePath());
		}

		File makeIndex = findMakeIndex();
		if (makeIndex != null) {
			this.conf.put("makeindexPath", makeIndex.getAbsolutePath());
		}

		File gnuplot = findGnuplot();
		if (gnuplot != null) {
			this.conf.put("gnuplot", gnuplot.getAbsolutePath());
		}

		this.conf.put("name", "no name");
		this.conf.put("numBin", "100");
		this.conf.put("scale", "n");
		this.conf.put("lowerBound", "0.05");
		this.conf.put("upperBound", "0.10");
		this.conf.put("lowerOptimalBound ", null);
		this.conf.put("upperOptimalBound ", null);
		this.conf.put("verbose", "0");

	}

	/**
	 * This method prints the help at the console.
	 * 
	 * @param jsap
	 */
	private void printHelp(JSAP jsap) {
		System.out.println();

		System.out.println(" -----------------------");
		System.out.println(" - FAU-Timer Reporting Tool help -");
		System.out.println(" -----------------------");

		System.out.println();
		System.out.println();

		System.out.println("  Usage: java -jar reportingTool.jar"
				+ jsap.getUsage());

		System.out.println();
		System.out.println();

		System.out.println(jsap.getHelp());

		System.exit(0);
	}

	/**
	 * Tries to find a given executable in the system path.
	 * 
	 * @param name
	 *            Executable that should be searched for
	 * @return If found the executable otherwise NULL
	 */
	private File findExecutable(String name) {
		String pathString = System.getenv("PATH");
		String separator = System.getProperty("path.separator");
		String[] path = pathString.split(separator);

		File file = null;
		for (int i = 0; i < path.length; i++) {
			file = new File(path[i], name);
			if (file.exists() && file.canExecute()) {
				return file;
			}
		}
		return null;
	}

	/**
	 * Tries to find the pdflatex executable.
	 * 
	 * @return If found the executable otherwise NULL
	 */
	private File findPdflatex() {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
			return findExecutable("pdflatex");
		} else {
			return findExecutable("pdflatex.exe");
		}
	}

	/**
	 * Tries to find the makeindex executable.
	 * 
	 * @return If found the executable otherwise NULL
	 */
	private File findMakeIndex() {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
			return findExecutable("makeindex");
		} else {
			return findExecutable("makeindex.exe");
		}
	}

	/**
	 * Tries to find the gnuplot executable.
	 * 
	 * @return If found the executable otherwise NULL
	 */
	private File findGnuplot() {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
			return findExecutable("gnuplot");
		} else {
			return findExecutable("gnuplot.exe");
		}
	}
}