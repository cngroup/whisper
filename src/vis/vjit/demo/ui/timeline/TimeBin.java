package vis.vjit.demo.ui.timeline;

import java.util.ArrayList;
import java.util.List;

import davinci.data.elem.VisualNode;

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
public class TimeBin extends VisualNode {
	
	private static final long serialVersionUID = 790442216232068403L;

	private long m_start = 0;
	
	private long m_duration = 0;
	
	private double m_sentiments = 0;
	
	private List<Long> m_tweets = null;
	
	public TimeBin(long start, long duration) {
		m_start = start;
		m_duration = duration;
		m_tweets = new ArrayList<Long>();
	}
		
	public long duration() {
		return m_duration;
	}
	
	public void setStartTime(long s) {
		m_start = s;
	}
	
	public long start() {
		return m_start;
	}
	
	public long end() {
		return m_start + m_duration;
	}
	
	public boolean contains(long time) {
		return (time >= m_start && time < m_start + m_duration);
	}
	
	public int size() {
		return m_tweets.size();
	}
		
	public void add(long id, double s) {
		m_sentiments += s;
		m_tweets.add(id);
	}
	
	public double sentiments() {
		return m_sentiments / m_tweets.size();
	}
	
	public Long[] toArray() {
		return m_tweets.toArray(new Long[0]);
	}
	
	public void clear() {
		m_tweets.clear();
		m_sentiments = 0;
	}
		
	public void reset() {
		m_tweets.clear();
		m_sentiments = 0;
		m_start = 0;
		m_duration = 0;
	}
}
