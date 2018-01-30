package uk.ac.soton.ecs.has1g15.ch2;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;

public class ProcessingFirstImage {
	
    public static void main( String[] args ) throws IOException 
    {
    	MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
    	System.out.println(image.colourSpace);
    	
    	//Exercise 1: Using named display to open all images in same display so only 1 window opens 
    	DisplayUtilities.displayName(image, "Displayed Image");
    	MBFImage clone = image.clone();
    	
    	//Iterating through colour image and setting all blue and green pixels to black
    	for (int y=0; y<image.getHeight(); y++) 
    	{
    	    for(int x=0; x<image.getWidth(); x++) 
    	    {
    	        clone.getBand(1).pixels[y][x] = 0;
    	        clone.getBand(2).pixels[y][x] = 0;
    	    }
    	}
    	DisplayUtilities.displayName(clone, "Displayed Image");
    	
    	//Using Canny edge detector - edge detection algorithm 
    	image.processInplace(new CannyEdgeDetector());
    	
    	/*Exercise 2: Giving speech bubbles a border
    	 *Ovals drawn behind for border effect
    	 */
    	image.drawShapeFilled(new Ellipse(700f, 450f, 30f, 20f, 0f), RGBColour.ORANGE);
    	image.drawShapeFilled(new Ellipse(650f, 425f, 35f, 22f, 0f), RGBColour.ORANGE);
    	image.drawShapeFilled(new Ellipse(600f, 380f, 40f, 25f, 0f), RGBColour.ORANGE);
    	image.drawShapeFilled(new Ellipse(500f, 300f, 110f, 80f, 0f), RGBColour.ORANGE);
    	image.drawShapeFilled(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.WHITE);
    	image.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
    	image.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE); 
    	image.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
    	image.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
    	image.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
    	
    	DisplayUtilities.displayName(image,  "Displayed Image");
    }
}

