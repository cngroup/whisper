package vis.vjit.tweeflow.data.filter;

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
public class ImportanceFilter implements IFlowerFilter<VisTweet> {

	private double m_threshold = 0;
	
	public ImportanceFilter() {
	}
	
	public ImportanceFilter(double f) {
		m_threshold = f;
	}
	
	public void setThreshold(double t) {
		m_threshold = t;
	}
	
	public double getThreshold() {
		return m_threshold;
	}
	
	public boolean accept(VisTweet node) {
		
		if(m_threshold == 0) {
			return true;
		}
		
		return node.getWeight() >= m_threshold;
	}
}
