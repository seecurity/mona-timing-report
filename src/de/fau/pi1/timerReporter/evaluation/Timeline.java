package de.fau.pi1.timerReporter.evaluation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import de.fau.pi1.timerReporter.tools.Folder;

/**
 * An object of this class represents a time line. A time line contains of
 * 101 boxes. Every box displays how many significant smaller boxes fall into
 * the percentiles. If a boxes has the size of 0.1 to 0.2 it means, that 
 * the boxes 10 to boxes 20 are increment. The brighter boxes are the boxes 
 * with more significant smaller boxes than others. So if the box 0.12 to 0.2
 * is significant smaller box too, the time line increments the box 12 to box 20.
 * So the range are brighter as the range between box 10 to box 11.
 * 
 * @FauTimerReporter
 * @author Isabell Schmitt
 * @version 1.0
 * @16.08.2012
 *
 */

public class Timeline {
	private String name = "";
	private int[] timeline = new int[101];
	private Color[] colorList = new Color[6];
	private static Logger logger = Logger.getLogger("Fau-Timer Reporter");
	
	public Timeline(String name) {
		this.name = name;

		for(int i = 0; i < this.timeline.length; i++) {
			this.timeline[i] = 0;
		}
		
		colorList[0] = new Color(0, 0, 0);
		colorList[1] = new Color(102, 102, 102);
		colorList[2] = new Color(153, 153, 153);
		colorList[3] = new Color(204, 204, 204);
		colorList[4] = new Color(229, 229, 229);
		colorList[5] = new Color(255, 255, 255);
	}

	public String getName() {
		return name;
	}
	
	/**
	 * This method stores the graphic of the timeline.
	 * 
	 * @param width
	 * @param height
	 * @param imgFileName
	 * @throws Exception
	 */
	public void store(int width, int height, String outputFile) {
		Folder.checkDir(outputFile);
		BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		paint(img.createGraphics());
		try {
			ImageIO.write( img, "png", new File(outputFile) );
		} catch( Exception ex ) {
			throw new RuntimeException( "\nError: Image storing to '" + outputFile + "' failed: " + ex.getMessage() );
		}
	}
	
	/**
	 * This method increments the box, if a window overlays 
	 * at the same percentile. 
	 * 
	 * @param box position
	 */
	public void incrementBox(int i) {
		this.timeline[i]++;
	}
	
	/**
	 * This method search the box with the maximum of overlays.
	 * 
	 * @return int
	 */
	private int findMax() {
		int max = 0;
		for(int i = 0; i < this.timeline.length; i++) {
			if (this.timeline[i] > max) {
				max = timeline[i];
			}
		}
		return max;
	}
	
	/**
	 * This method logs the time line results. The symbol "=" means
	 * that the significant smaller box test on this position is 
 	 * negative and the symbol "-" means the box test  on this position
	 * is OK.
	 */
	@SuppressWarnings("unused")
	private void printTimeline() {

		String test = "";
		for(int i = 0; i < timeline.length; i++) {
			if(timeline[i] == 0) {
				test += "=";
			} else {
				test += timeline[i];
			}
		}
		logger.info(test);
	}

	/**
	 * This is the paint method of the timeline result, it creates
	 * with black and white boxes. Every box stands for a percentile, 
	 * for example box one is the box with the percentile 0, and the 
	 * last box is the box stands for the percentile of 100. If the 
	 * boxes 35-40 are white, it means that, most of the boxes in this 
	 * range are significant smaller. The maximum of significant smaller 
	 * boxes is contained in this range. 
	 * 
	 * @param g
	 */
	private void paint(Graphics g)
	{
		double var = this.findMax()/5.0;
		int i = 0;
		while(i < timeline.length) {
			
			if( i == 0 || i == 20 || i == 40 || i == 60 || i == 80 || i == 100) {
				g.setColor(this.colorList[0]);
				g.drawLine(54 + (6*i), 56 , 54 + (6*i), (50 + 10));
				g.drawString(i + "", 50+(6*i), (50+23));
			}
			
			if(timeline[i] == 0) {
				g.setColor(this.colorList[0]);
				g.fill3DRect(50+(6*i), 50, 5, 5, true);
				i++;
				
			} else {
				if(timeline[i] <= var) {
					g.setColor(this.colorList[1]);
				} else if (timeline[i] <= (var*2)) {
					g.setColor(this.colorList[2]);
				} else if (timeline[i] <= (var*3)) {
					g.setColor(this.colorList[3]);
				} else if (timeline[i] <= (var*4)) {
					g.setColor(this.colorList[4]);
				} else if (timeline[i] <= (var*5)) {
					g.setColor(this.colorList[5]);
				}

				g.fill3DRect(50+(6*i), 50, 5, 5, true);
				i++;
			}

		}
	}
}
