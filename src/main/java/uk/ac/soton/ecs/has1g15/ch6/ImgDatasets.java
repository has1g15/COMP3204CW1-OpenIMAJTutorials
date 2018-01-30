package uk.ac.soton.ecs.has1g15.ch6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.dataset.BingImageDataset;
import org.openimaj.image.dataset.FlickrImageDataset;
import org.openimaj.util.api.auth.DefaultTokenFactory;
import org.openimaj.util.api.auth.common.BingAPIToken;
import org.openimaj.util.api.auth.common.FlickrAPIToken;

public class ImgDatasets {

	public static void main(String[] args) throws Exception 
	{
		
		VFSListDataset<FImage> images = 
				new VFSListDataset<FImage>("C://Users/Hannah/COMP3204Coursework2/Coursework2/src/hybrid-images", 
						ImageUtilities.FIMAGE_READER);
		
		System.out.println(images.size());
		
		DisplayUtilities.display(images.getRandomInstance(), "A random image from the dataset");
		
		DisplayUtilities.display("My images", images);
		
		//Creates image dataset from images in zip file hosted on a web-server
		VFSListDataset<FImage> faces = 
				new VFSListDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
		DisplayUtilities.display("ATT faces", faces);
		
		//Maintain associations between images of each individual
		VFSGroupDataset<FImage> groupedFaces = 
				new VFSGroupDataset<FImage>( "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
		
		/*for (final Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet()) 
		{
			DisplayUtilities.display(entry.getKey(), entry.getValue());
		}*/
		
		//Exercise 1: Displaying an image that shows a randomly selected photo of each person from the dataset
		FImage randomFaces = new FImage(725, 600);
		int x = 0;
		int y = 0;
		//For every person/image set, get random image of their face 
		for (Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet()) 
		{
			FImage img = entry.getValue().getRandomInstance();
			//Draw randomly selected image onto accumulative image 
			randomFaces.drawImage(img, x, y);
			if ((x + img.getWidth()) > randomFaces.getWidth())
			{
				x = 0;
				y = y + img.getHeight();
			}
			else
			{
			x = x + img.getWidth();
			}
		}
		
		DisplayUtilities.display(randomFaces, "Random Faces");
		
		/*Exercise 2: Exploring Apache Common VFS Documentation
		 *The other kinds of sources that are supported for building datasets are BZIP2, compressed, 
		 *GZIP, HDFS, JAR and SFTP
		 */
		
		//Dynamically construct dataset of images from Flickr search
		FlickrAPIToken flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);
		FlickrImageDataset<FImage> cats = 
				FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
		DisplayUtilities.display("Cats", cats);
		
		/*Exercise 3: Experimenting with BingImageDataset 
		 *Due to Bing API issues, the code for this exercise cannot be executed
		BingAPIToken bingToken = DefaultTokenFactory.get(BingAPIToken.class);
		BingImageDataset<FImage> fruit =
				BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, "fruit", 10);
		DisplayUtilities.display("Fruit", fruit);
		 */
		
		/*Exercise 4: Using MapBackedDataset to create grouped dataset of celebrity images
		 *As Bing API could not be used, Flickr API was used again to obtain images 
		 *MapBackedset provides concrete implementation of GroupedDataset
		 */
		MapBackedDataset mbDataset = new MapBackedDataset();
		FlickrImageDataset<FImage> celeb =
				FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "Arnold Schwarznegger", 2);
		mbDataset.add("Arnold", celeb);
		celeb = FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "Harrison Ford", 2);
		mbDataset.add("Harrison", celeb);
		celeb = FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "Nicholas Cage", 2);
		mbDataset.add("Nicholas", celeb);
	}
}
