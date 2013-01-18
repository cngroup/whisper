package vis.vjit.tweeflow.data.filter;

import java.util.HashSet;
import java.util.Set;

import vis.vjit.tweeflow.data.VisTweet;

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
public class SentimentFilter implements IFlowerFilter<VisTweet> {

	private static Set<Integer> accepted = null;
	
	public SentimentFilter() {
		accepted = new HashSet<Integer>();
		accepted.add(0);
		accepted.add(1);
		accepted.add(2);
	}
	
	public static void add(Integer s) {
		accepted.add(s);
	}
	
	public static void remove(Integer s) {
		accepted.remove(s);
	}
	
	public boolean accept(VisTweet node) {
		double s = node.getSentiment();
		int idx = 0;
		if(s < 0.45) {
			idx = 0;
		} else if(s > 0.55) {
			idx = 2;
		} else {
			idx = 1;
		}
		return accepted.contains(idx);
	}
}
