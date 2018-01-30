package uk.ac.soton.ecs.has1g15.ch14;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.sampling.GroupSampler;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.time.Timer;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;
import org.openimaj.util.parallel.partition.RangePartitioner;

public class ParallelProcessing {

	public static void main(String[] args) throws IOException 
	{
		//Parallel equivalent of for loop
		Parallel.forIndex(0, 10, 1, new Operation<Integer>() 
		{
			public void perform(Integer i) 
			{
			    System.out.println(i);
			}
		});
		
		//Loading images directly from Caltech101 dataset 
		VFSGroupDataset<MBFImage> allImages = Caltech101.getImages(ImageUtilities.MBFIMAGE_READER);
		//Restriction of using first 8 groups in the dataset 
		GroupedDataset<String, ListDataset<MBFImage>, MBFImage> images = GroupSampler.sample(allImages, 8, false);
		
		//Parallelise Inner Loop
		final List<MBFImage> output = new ArrayList<MBFImage>();
		final ResizeProcessor resize = new ResizeProcessor(200);
		
		Timer t1 = Timer.timer();
		
	    /*Loop through images in group, resample and normalise each image before drawing it in centre of white 
	     *image, add result to accumulator. Then divide accumulated image by number of samples used to create it
	     */
		for (ListDataset<MBFImage> clzImages : images.values()) 
		{
		    final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);
		    
		    /*Uses partitioned variant of for-each loop to give each thread a collection of images rather than a 
		     *single image
		     */
		    Parallel.forEachPartitioned(new RangePartitioner<MBFImage>(clzImages), new Operation<Iterator<MBFImage>>() 
		    {
		    	public void perform(Iterator<MBFImage> it) 
		    	{
		    	    //Holds intermediary results
		    	    MBFImage tmpAccum = new MBFImage(200, 200, 3);
		    	    MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);

		    	    while (it.hasNext()) 
		    	    {
		    	        final MBFImage i = it.next();
		    	        tmp.fill(RGBColour.WHITE);
		    	        final MBFImage small = i.process(resize).normalise();
		    	        final int x = (200 - small.getWidth()) / 2;
		    	        final int y = (200 - small.getHeight()) / 2;
		    	        tmp.drawImage(small, x, y);
		    	        tmpAccum.addInplace(tmp);
		    	    }
		    	    synchronized (current) 
		    	    {
		    	        current.addInplace(tmpAccum);
		    	    }
		    	}
		    });
		    current.divideInplace((float) clzImages.size());
		    output.add(current);
		}
		DisplayUtilities.display("InnerImages", output);
    	System.out.println("Time: " + t1.duration() + "ms");
		    
	    /*Exercise 1: Parallelise outer loop
		 *----------------------------------
    	 *Parallelising the outer loop makes the code run slower, with a parallelised inner loop, the code ran with 
    	 *a time of 33.2 seconds and with a parallelised outer loop, it ran with a time of 38.9 seconds. The pros of 
    	 *parallelising this loop instead of the inner loop is synchronisation is not needed as multiple threads 
    	 *are not accessing the image concurrently. On the other hand, parallelising this loop increases the time 
    	 *taken to run the code
    	 */
    	Timer t2 = Timer.timer();
    	
		Parallel.forEach(images.values(), new Operation<ListDataset<MBFImage>>()
		{
			public void perform(ListDataset<MBFImage> imgs) 
			{
				final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);
					
				for (MBFImage i:imgs) 
				{
		    	     MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
		    	     tmp.fill(RGBColour.WHITE);
		    	     MBFImage small = i.process(resize).normalise();
		    	     int x = (200 - small.getWidth()) / 2;
		    	     int y = (200 - small.getHeight()) / 2;
		    	     tmp.drawImage(small, x, y);
		    	     current.addInplace(tmp);
				}
		    	current.divideInplace((float) imgs.size());
		    	output.add(current);
			}
	    });
		DisplayUtilities.display("OuterImages", output);
		System.out.println("Time: " + t2.duration() + "ms");
	}
	
	/*List<MBFImage> output = new ArrayList<MBFImage>();
	ResizeProcessor resize = new ResizeProcessor(200);

	for (ListDataset<MBFImage> clzImages : images.values()) 
	{
    	MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);

    	for (MBFImage i : clzImages) 
    	{
        	MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
        	tmp.fill(RGBColour.WHITE);
			MBFImage small = i.process(resize).normalise();
        	int x = (200 - small.getWidth()) / 2;
        	int y = (200 - small.getHeight()) / 2;
        	tmp.drawImage(small, x, y);
			current.addInplace(tmp);
    	}
    	current.divideInplace((float) clzImages.size());
    	output.add(current);
	}*/
	
	/*Parallel.forEachPartitioned(new RangePartitioner<MBFImage>(clzImages), new Operation<Iterator<MBFImage>>() 
	  {
		public void perform(Iterator<MBFImage> it) 
		{
		    MBFImage tmpAccum = new MBFImage(200, 200, 3);
		    MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);

		    while (it.hasNext()) 
		    {
		        final MBFImage i = it.next();
		        tmp.fill(RGBColour.WHITE);
		        final MBFImage small = i.process(resize).normalise();
		        final int x = (200 - small.getWidth()) / 2;
		        final int y = (200 - small.getHeight()) / 2;
		        tmp.drawImage(small, x, y);
		        tmpAccum.addInplace(tmp);
		    }
		    synchronized (current) 
		    {
		        current.addInplace(tmpAccum);
		    }
		}
	});*/
}
