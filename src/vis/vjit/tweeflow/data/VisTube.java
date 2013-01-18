package vis.vjit.tweeflow.data;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twitter4j.Status;
import davinci.data.elem.Edge;
import davinci.data.elem.IVisualNode;

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
public class VisTube extends Edge<IVisualNode> {

	private static final long serialVersionUID = -8605667000175861991L;
		
	private List<VisTweet> m_tweets = null;
	
	private GeneralPath m_path = null;
	
	private int m_lens = 0;
	
	private double m_k = 0;
	
	private boolean bdetacted = false;
	
	public VisTube() {
		this(null, null);
	}
	
	public VisTube(VisTweet tweet, FlowerNode sink) {
		m_tweets = new ArrayList<VisTweet>();
		attach(tweet, sink);
	}
	
	public void attach(VisTweet tweet, FlowerNode sink) {
		if(tweet == null || sink == null) {
			return;
		}
		this.update(tweet.frac());
		this.setFirstNode(tweet);
		this.setSecondNode(sink);
		tweet.addTube(this);
		sink.addTube(this);
		setID("t-" + tweet.getID());
		bdetacted = false;
	}
	
	public void dettach() {
		this.source().removeTube(this);
		this.sink().removeTube(this);
		this.setFirstNode(null);
		this.setSecondNode(null);
		this.m_tweets.clear();
		bdetacted = true;
	}
	
	public boolean isDetached() {
		return bdetacted;
	}
	
	public VisTweet source() {
		return (VisTweet)getFirstNode();
	}
	
	public FlowerNode sink() {
		return (FlowerNode)getSecondNode();
	}
	
	public VisTweet retweet(Status s) {
		VisTopic topic = source().getTopic();
		source().active(topic.getTime());
		VisTweet tweet = new VisTweet(s, topic.getTime());
		tweet.setActive(false);
		tweet.update(frac());
		m_tweets.add(tweet);
		setWeight(getWeight() + 1);
		return tweet;
	}
	
	public boolean remove(VisTweet tweet) {
		return m_tweets.remove(tweet);
	}
	
	public boolean isEmpty() {
		return m_tweets.isEmpty();
	}
	
	public int size() {
		return m_tweets.size();
	}
	
	public VisTweet getTweet(int i) {
		return m_tweets.get(i);
	}
	
	public VisTweet[] getTweets() {
		return m_tweets.toArray(new VisTweet[0]);
	}
	
	public Iterator<VisTweet> iterator() {
		return m_tweets.iterator();
	}
	
	public void setCurve(GeneralPath path, int lens, double k) {
		m_lens = lens;
		m_path = path;
		m_k = k; 
	}
	
	public void setCurvature(double k) {
		m_k = k;
	}
	
	public double curvature() {
		return m_k;
	}
	
	public GeneralPath curve() {
		return m_path;
	}
	
	public int curvelength() {
		return m_lens;
	}
	
	public void update(float frac) {
		super.update(frac);
		VisTweet[] glyphs = m_tweets.toArray(new VisTweet[0]);
		for(int i = 0; i < glyphs.length; ++i) {
			glyphs[i].update(frac);
		}
	}
	
	public void setHighlight(boolean bhighlight) {
		super.setHighlight(bhighlight);
		VisTweet[] glyphs = m_tweets.toArray(new VisTweet[0]);
		for(int i = 0; i < glyphs.length; ++i) {
			glyphs[i].setHighlight(bhighlight);
		}
	}
}