package uk.ac.soton.ecs.has1g15.ch5;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.BasicMatcher;
import org.openimaj.feature.local.matcher.BasicTwoWayMatcher;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.LMedS;
import org.openimaj.math.model.fit.RANSAC;

public class SIFTfeatureMatching {

	public static void main (String[] args) throws MalformedURLException, IOException
	{
		MBFImage query = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/query.jpg"));
		MBFImage target = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/target.jpg"));
		
		DoGSIFTEngine engine = new DoGSIFTEngine();	
		LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
		LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());
		
		/*Exercise 1: Experimenting with different matchers 
		 *BasicTwoWayMatcher uses minimum Euclidean distance to find matches 
		 */
		LocalFeatureMatcher<Keypoint> matcher = new BasicTwoWayMatcher<Keypoint>();
		/*Construct and set up basic matcher 
		 *LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(80);
		 */
		matcher.setModelFeatures(queryKeypoints);
		matcher.findMatches(targetKeypoints);
		
		//Draw matches between two images 
		MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
		DisplayUtilities.display(basicMatches);
		
		/*Set up RANSAC model fitter configured to find Affine transforms and consistent matcher
		 *RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
				  new RANSAC.PercentageInliersStoppingCondition(0.5));*/
		
		/*Exercise 2: Different Models
		 *Homography model gave better results than affine transform 
		 */
		RobustHomographyEstimator modelFitter = new RobustHomographyEstimator(5.0, 1500,
				  new RANSAC.PercentageInliersStoppingCondition(0.5), HomographyRefinement.NONE);
		
		matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(new FastBasicKeypointMatcher<Keypoint>(8), 
				  modelFitter);

		matcher.setModelFeatures(queryKeypoints);
		matcher.findMatches(targetKeypoints);

		MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), 
				  RGBColour.RED);

		DisplayUtilities.display(consistentMatches);
		
		/*.getModel() returns internal Affine transform 
		 * Transforming bounding box of query with tranform estimated in AffineTransformModel
		 */
		target.drawShape(query.getBounds().transform(modelFitter.getModel().getTransform().inverse()), 
				  3,RGBColour.BLUE);
		DisplayUtilities.display(target); 
	}
}
