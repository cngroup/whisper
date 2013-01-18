package vis.vjit.tweeflow.layout;

import java.util.Comparator;

import vis.vjit.tweeflow.data.FlowerNode;

/***
 * 
 * This piece of code is a joint research between HKUST and Harvard University. 
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
public class GroupComparator implements Comparator<FlowerNode> {

	public GroupComparator() {
	}
	
	public int compare(FlowerNode g1, FlowerNode g2) {
		double diff = g1.getLongitude() - g2.getLongitude();
		if(diff > 0) {
			return 1;
		} else if(diff < 0){
			return -1;
		} else {
			return 0;
		}
	}
}
