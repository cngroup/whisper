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
public class TopicFilter implements IFlowerFilter<VisTweet> {

	private String[] m_topics = null;

	public TopicFilter() {
		this(null);
	}

	public TopicFilter(String[] topic) {
		this.setTopics(topic);
	}

	public void setTopics(String... tps) {
		if (tps != null) {
			m_topics = new String[tps.length];
			for (int i = 0; i < m_topics.length; ++i) {
				m_topics[i] = tps[i].toLowerCase();
			}
		} else {
			m_topics = null;
		}
	}

	public String[] getTopics() {
		return m_topics;
	}

	public boolean accept(VisTweet node) {
		if (null == m_topics) {
			return true;
		}
		String text = node.status().getText().toLowerCase();
		for (int i = 0; i < m_topics.length; ++i) {
			if (text.contains(m_topics[i])) {
				return true;
			}
		}
		return false;
	}
}
