package de.fau.pi1.timerReporter.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;


/**
 * This class has tools to replace and read in templates.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @16.08.2012
 *
 */
public class Replacer {
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");

	/**
	 * This method replaces the input file into the output file
	 * with the help of a replacer hash map.
	 * 
	 * @param input
	 * @param output
	 * @param replacer
	 */
	public static void replace(File input, File output,
			HashMap<String, String> replacer) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));

			StringBuilder contentBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				contentBuilder.append(line + "\n");
			}
			String content = contentBuilder.toString();

			String key;
			String value;
			for (Entry<String, String> replace : replacer.entrySet()) {
				key = replace.getKey(); //.replaceAll("\\\\", "\\\\\\\\");
				value = replace.getValue(); //.replaceAll("\\\\", "\\\\\\\\");
				content = content.replaceAll("::" + key + ":::", Matcher.quoteReplacement(value));
			}

			OutputStreamWriter outputWriter = new OutputStreamWriter(
					new FileOutputStream(output));
			outputWriter.write(content);
			outputWriter.flush();
			outputWriter.close();

			contentBuilder = null;
			content = null;
			System.gc();
		} catch (FileNotFoundException e) {
			logger.warning("Error replacer doesn't find the template " + input + ".");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			logger.warning("Error running replacer.");
			System.exit(1);
		}
	}

	/**
	 * This method reads the entered template in.
	 * 
	 * @param template
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readTemplate(String template) {
		String line = null;
		String result = "";
		BufferedReader tplReader;
		
		try {
			tplReader = new BufferedReader(new FileReader(template));

			while((line = tplReader.readLine()) != null) {
				result += line;
				result += "\n";
			}
		} catch (IOException e) {
			logger.warning("Error: The template " + template + " wasn't readed successfully.");
			System.exit(1);
		}
		return result;
	}
}
