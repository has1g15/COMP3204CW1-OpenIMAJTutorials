package uk.ac.soton.ecs.has1g15.ch4;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;

public class GlobalImageFeatures {

	public static void main (String[] args) throws IOException
	{
		URL[] imageURLs = new URL[] 
		{
				new URL( "http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist1.jpg" ),
				new URL( "http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist2.jpg" ), 
				new  URL( "http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist3.jpg" ) 
		};

		ArrayList<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
		//Constructed using required number of bins in each dimension 
		HistogramModel model = new HistogramModel(4, 4, 4);

		//Generate and store histograms for the three images 
		for( URL u : imageURLs ) 
		{
			model.estimateModel(ImageUtilities.readMBF(u));
		    histograms.add( model.histogram.clone() );
		}
		
		HashMap<Double,String> distances = new HashMap<>();
		
		/*Exercise 1: Finding and Displaying Similar Images
		 *Initially, the images that are returned as most similar are two of the same image, to be expected
		 *Structure of for loops ensure that the same image is not displayed twice 
		 */
		for( int i = 0; i < histograms.size(); i++ ) 
		{
		    for( int j = i + 1; j < histograms.size(); j++ ) 
		    {
		    	/*Exercise 2: Exploring distance measures 
		    	 *When EUCLIDEAN is used for comparison, the two sunset images are displayed
		    	 *These are the two images you would expect to be displayed
		    	 *double distance = histograms.get(i).compare( histograms.get(j), DoubleFVComparison.EUCLIDEAN );
		    	 *When INTERSECTION is used for comparison, the second sunset image and the moon image are displayed
		    	 */
		        double distance = histograms.get(i).compare( histograms.get(j), DoubleFVComparison.INTERSECTION );
		        distances.put(distance, i + " " + j);
		    }
		}
		ArrayList<Double> order = new ArrayList<>();
		
		//Compare all histograms to each other - Euclidean distance measure is symmetric 
		for (Double distance: distances.keySet())
		{
			order.add(distance);
		}
		
		//Order distances
		Collections.sort(order);
		//Label of 2 most similar image obtained by getting smallest distance from list 
		String sim = distances.get(order.get(0));
		
		//Display two most alike images 
		DisplayUtilities.display(ImageUtilities.readMBF(imageURLs[Integer.parseInt(sim.split(" ")[0])]));
		DisplayUtilities.display(ImageUtilities.readMBF(imageURLs[Integer.parseInt(sim.split(" ")[1])]));
	}
}
