package vis.vjit.tweeflow.data.filter;

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
public class OrFilter implements IFlowerFilter<VisTweet> {

	private IFlowerFilter<VisTweet>[] m_filters = null;

	public OrFilter(IFlowerFilter<VisTweet>... fs) {
		m_filters = fs;
	}

	public boolean accept(VisTweet node) {
		if (m_filters == null) {
			return true;
		}
		for (int i = 0; i < m_filters.length; ++i) {
			if (m_filters[i].accept(node)) {
				return true;
			}
		}
		return false;
	}

}
