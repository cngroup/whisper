package vis.vjit.tweeflow.layout;

import java.util.Comparator;

import davinci.util.math.ExtMath;

import twitter4j.GeoLocation;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;

/***
 * 
 * This piece of code is a joint research with Harvard University. 
 * It is based on the CPL opensource license. Please check the 
 * term before using.
 * 
 * The paper is published in InfoVIs 2013: 
 * "Whisper: Tracing the Spatiotemporal Process of Information Diffusion in Real Time"
 * 
 * Visit Whisper's main website here : whipserseer.com
 * 
 * @author NanCao(nancao@gmail.com)
 *
 */
public class TweetComparator implements Comparator<VisTweet> {

	public TweetComparator() {
	}
	
	public int compare(VisTweet t1, VisTweet t2) {
		double longi1 = -1;
		double longi2 = -1;
		VisTube target1 = null, target2 = null;
		GeoLocation loc = t1.status().getGeoLocation();
		if(loc != null) {
			longi1 = loc.getLongitude();
		}
		if(longi1 == -1) {
			double max = -1;
			VisTube[] tubes = t1.getTubes();
			for(int i = 0; i < tubes.length; ++i) {
				if(max < tubes[i].getWeight()) {
					target1 = tubes[i];
					max = tubes[i].getWeight();
				}
			}
			if(target1 != null) {
				longi1 = target1.sink().getLongitude();
			}
		}
		
		loc = t2.status().getGeoLocation();
		if(loc != null) {
			longi2 = loc.getLongitude();
		}
		if(longi2 == -1) {
			double max = -1;
			VisTube[]tubes = t2.getTubes();
			for(int i = 0; i < tubes.length; ++i) {
				if(max < tubes[i].getWeight()) {
					target2 = tubes[i];
					max = tubes[i].getWeight();
				}
			}
			if(target2 != null) {
				longi2 = target2.sink().getLongitude();
			}
		}
		
		if(longi1 == -1 || longi2 == -1) {
			return 0;
		}
		
		//(180 - longi1) - (180 - longi2)
		
		double diff = (longi2 - longi1);
		if(diff > 0) {
			return 1;
		} else if(diff < 0){
			return -1;
		} else {
			return 0;
		}
	}
}
