package uk.ac.soton.ecs.has1g15.ch13;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.model.EigenImages;

public class Eigenfaces {

	public static void main (String[] args) throws FileSystemException
	{
		VFSGroupDataset<FImage> dataset = 
			    new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
		
	  /*Exercise 2: Experimenting with reduction of training images
	   *As number of images in training set decreases, accuracy also decreases 
	   */
		int nTraining = 5;
	  /*Training Set Size = 5, Accuracy = 0.94
	   *Training Set Size = 4, Accuracy = 0.89
	   *Training Set Size = 3, Accuracy = 0.845
       *Training Set Size = 2, Accuracy = 0.815
	   *Training Set Size = 1, Accuracy = 0.655
	   */
		int nTesting = 5;
		GroupedRandomSplitter<String, FImage> splits = 
		    new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
		GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
		GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();
		
		List<FImage> basisImages = DatasetAdaptors.asList(training);
		int nEigenvectors = 100;
		EigenImages eigen = new EigenImages(nEigenvectors);
		//Learn training images from each person 
		eigen.train(basisImages);
		
		//Draw first 12 basis vectors to visualise as images 
		List<FImage> eigenFaces = new ArrayList<FImage>();
		for (int i = 0; i < 12; i++) 
		{
		    eigenFaces.add(eigen.visualisePC(i));
		}
		DisplayUtilities.display("EigenFaces", eigenFaces);
		
		//Build database of features from training images
		Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
		for (final String person : training.getGroups()) 
		{
		    final DoubleFV[] fvs = new DoubleFV[nTraining];

		    for (int i = 0; i < nTraining; i++) 
		    {
		        final FImage face = training.get(person).get(i);
		        fvs[i] = eigen.extractFeature(face);
		    }
		    features.put(person, fvs);
		}
		
	    /*Good value for threshold appeared to be 12 as thresholds higher than this converged in number of unknowns 
	     * and started to drop increasingly in accuracy value. Lower thresholds returned significantly larger numbers
	     * of unknowns 
	     */
		int thresholdVal = 12;
	    /*To estimate identity of unknown face image, extract feature from image - find database feature with 
	     *smallest distance (Euclidean)and return identifier of corresponding person 
	     */
		double correct = 0, incorrect = 0;
		for (String truePerson : testing.getGroups()) 
		{
		    for (FImage face : testing.get(truePerson)) 
		    {
		        DoubleFV testFeature = eigen.extractFeature(face);

		        String bestPerson = null;
		        double minDistance = Double.MAX_VALUE;
		        for (final String person : features.keySet()) 
		        {
		            for (final DoubleFV fv : features.get(person)) 
		            {
		                double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);

		                if (distance < minDistance) 
		                {
		                    minDistance = distance;
		                    bestPerson = person;
		                }
		            }
		        }
		        /*Exercise 3: Applying a threhold 
		         *If distance between query face and closest database face is greater than threshold value, unknown
		         *result is returned as opposed to closest face being guessed
		         */
		        if (minDistance > thresholdVal)
		        {
		        	bestPerson = "unknown";
		        }
		        else
		        {
		        	if (truePerson.equals(bestPerson))
		        		correct++;
		        	else
		        		incorrect++;
		        }
		        System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);
		    }
		}
		//Compute accuracy of recognition 
		System.out.println("Accuracy: " + (correct / (correct + incorrect)));
		
	    /*Exercise 1: Reconstructing Faces
		 *Extracts feature of randomly selected face from test set
		 */
		ArrayList<String> people = new ArrayList<String>(training.getGroups());
    	
    	String name = people.get((int) (Math.random()*people.size()+1));
    	DoubleFV[] feats = features.get(name);
    	FImage reconstruct = null;
    	
    	for(int i=0; i<feats.length; i++)
    	{
	    	/*Convert feature back to an image 
	    	 *Normalise in case there are pixel values bigger than 1 or smaller than 0
	    	 */
    		reconstruct = eigen.reconstruct(feats[i]).normalise();
    	}
    	
    	DisplayUtilities.display(name, reconstruct);
	}
}
