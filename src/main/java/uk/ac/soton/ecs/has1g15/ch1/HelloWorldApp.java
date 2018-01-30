package uk.ac.soton.ecs.has1g15.ch1;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

/**
 * OpenIMAJ Hello world!
 *
 */
public class HelloWorldApp {
	
    public static void main( String[] args ) 
    {
    	//Create an image
        MBFImage image = new MBFImage(650,70, ColourSpace.RGB);

        //Fill the image with white
        image.fill(RGBColour.WHITE);
        		        
        //Render some text into the image
        //Exercise 1
        image.drawText("Bonjour le monde", 50, 60, HersheyFont.TIMES_BOLD, 50, RGBColour.ORANGE);

        //Apply a Gaussian blur
        image.processInplace(new FGaussianConvolve(2f));
        
        //Display the image
        DisplayUtilities.display(image);
    }
}

