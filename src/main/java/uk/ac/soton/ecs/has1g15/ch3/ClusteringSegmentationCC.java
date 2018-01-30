package uk.ac.soton.ecs.has1g15.ch3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
import org.openimaj.image.segmentation.SegmentationUtilities;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.FloatKMeans;

public class ClusteringSegmentationCC {

	public static void main (String[] args) throws MalformedURLException, IOException
	{
		MBFImage input = ImageUtilities.readMBF(new URL("https://www.pets4homes.co.uk/images/articles/1646/large/kitten-emergencies-signs-to-look-out-for-537479947ec1c.jpg"));
		
		//Apply clour-space transform to image
		input = ColourSpace.convert(input, ColourSpace.CIE_Lab);
		
		MBFImage img = input;
		
		//Constructing K-means algorithm
		FloatKMeans cluster = FloatKMeans.createExact(2);
		
		//Flattening pixels of an image into required form 
		float[][] imageData = input.getPixelVectorNative(new float[input.getWidth() * input.getHeight()][3]);
		
		FloatCentroidsResult result = cluster.cluster(imageData);
		
		//Printing coordinates of each centroid
		final float[][] centroids = result.centroids;
		for (float[] fs : centroids) 
		{
		    System.out.println(Arrays.toString(fs));
		}
		
		final HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();
		
		/*Exercise 1: Reimplement loop that replaces each pixel with its class centroid using a PixelProcessor
		 *  		  Advantages and Disadvantages of using a PixelProcessor:
		 *   		  PixelProcessor is more efficient than using two for loops however it means the array passed
		 *            to processPixel() has to be converted to a primitive type and then back again when it is 
		 *            returned
		 */
		input.processInplace(new PixelProcessor<Float[]>() 
		{
		    public Float[] processPixel(Float[] pixel) 
		    {
		    	float[] p = new float[pixel.length];
		    	
				for(int i = 0; i < pixel.length; i++)
				{
					p[i] = pixel[i];
				}
				
				//Replace pixel with centroid 
				int centroid = assigner.assign(p);
				
				float[] calc = centroids[centroid];
				
				Float[] processed = new Float[calc.length];
				for(int i = 0; i < calc.length; i++)
				{
					processed[i] = calc[i];
				}
				return processed;
		    }
		});
		
		//Replacing each pixel in the input image with the centroid of its respective class
				/*for (int y = 0; y < input.getHeight(); y++) 
				{
				    for (int x = 0; x < input.getWidth(); x++) 
				    {
				        float[] pixel = input.getPixelNative(x, y);
				        int centroid = assigner.assign(pixel);
				        input.setPixelNative(x, y, centroids[centroid]);
				    }
				}*/
		
		input = ColourSpace.convert(input, ColourSpace.RGB);
		DisplayUtilities.displayName(input, "Class Number Image");
		
		//FInd connected components
		GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
		List<ConnectedComponent> components = labeler.findComponents(input.flatten());
		
		int i = 0;
		
		//Draw image with numbered components 
		for (ConnectedComponent comp : components) 
		{
		    if (comp.calculateArea() < 50) 
		        continue;
		    input.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
		}
		
		DisplayUtilities.displayName(input, "Numbered Components Image");
	
	/*Exercise 2: A real segmentation algorithm 
	 *Using the FelzenszwalbHuttenlocherSegmenter
	 */
	FelzenszwalbHuttenlocherSegmenter segmenter = new FelzenszwalbHuttenlocherSegmenter();
	DisplayUtilities.displayName(SegmentationUtilities.renderSegments(input.getWidth(), input.getHeight(), segmenter.segment(img)), "FelzenszwalbHuttenlocherSegmenter");
	}
}
