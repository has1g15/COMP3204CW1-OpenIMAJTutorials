package uk.ac.soton.ecs.has1g15.ch7;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processing.edges.SUSANEdgeDetector;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.xuggle.XuggleVideo;

public class ProcessingVideo {

	public static void main(String[] args) throws IOException 
	{
		//Create video holding coloured frames
		Video<MBFImage> video;
		/*Load video from file
		 *video = new XuggleVideo(new URL("http://static.openimaj.org/media/tutorial/keyboardcat.flv"));
		 *Supports live video:
		 */
		video = new VideoCapture(600, 600);
		
		//Display video
		VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
		
		//Iterate through every frame in video to process it (apply edge detector)
		for (MBFImage mbfImage : video) 
		{
			/*Exercise 1: Apply different types of image processing to the video 
		     *DisplayUtilities.displayName(mbfImage.process(new CannyEdgeDetector()), "videoFrames");
		     *DisplayUtilities.displayName(mbfImage.process(new FGaussianConvolve(2f)), "videoFrames");
		     */
		    DisplayUtilities.displayName(mbfImage.process(new SUSANEdgeDetector()), "videoFrames");
		}
		
		//Using event-driven technique 
		display.addVideoListener(
		  new VideoDisplayListener<MBFImage>() {
		    public void beforeUpdate(MBFImage frame) {
		        frame.processInplace(new CannyEdgeDetector());
		    }

		    public void afterUpdate(VideoDisplay<MBFImage> display) {
		    }
		  });
	}
}
