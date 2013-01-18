package vis.vjit.tweeflow.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vis.vjit.tweeflow.Constant;

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
public class VisTopic extends FlowerNode {
	
	private static final long serialVersionUID = 7618273974272038730L;
	
	private static final VisTweet[] EMPTY = new VisTweet[]{};
	
	private List<VisTweet> m_active = null;
	private List<VisTweet> m_inactive = null;
	
	private double m_angle = 0;
	
	private long m_current = 0;
	
	private Map<String, VisTweet> m_elems = null;
	
	private double m_radii;
	
	private int m_level = 0;
	
	public VisTopic() {
		m_active = new ArrayList<VisTweet>();
		m_inactive = new ArrayList<VisTweet>();
		m_elems = new HashMap<String, VisTweet>();
		setCharge(1);
	}
	
	public void setLevel(int level) {
		m_level = level;
	}
	
	public int getLevel() {
		return m_level;
	}
	
	public void setRadii(double radii) {
		this.m_radii = radii;
	}
	
	public double getRadii() {
		return m_radii;
	}
	
	public synchronized void setTime(long time) {
		m_current = time;
	}
	
	public synchronized long getTime() {
		return m_current;
	}
	
	public void setAngle(double angle) {
		this.m_angle = angle;
	}
	
	public double getAngle() {
		return m_angle;
	}
	
	public synchronized void add(VisTweet elem) {
		m_elems.put(elem.getID(), elem);
		if(elem.isActive()) {
			elem.update(frac());
			m_active.add(elem);
			m_sentiment += elem.getSentiment();
		} else {
			m_inactive.add(elem);
		}
		elem.setTopic(this);	
	}
	
	public synchronized VisTweet getTweet(String id) {
		return m_elems.get(id);
	}
	
	public synchronized VisTweet remove(VisTweet elem) {
		VisTweet tweet = m_elems.remove(elem.getID());
		m_inactive.remove(tweet);
		m_active.remove(tweet);
		tweet.setTopic(null);
		if(tweet.isActive()) {
			m_sentiment -= tweet.getSentiment();
		}
		return tweet;
	}
	
	public synchronized VisTweet[] active() {
		TwitterFlower flower = (TwitterFlower)m_owner;
		VisTweet[] tweet = m_active.toArray(EMPTY);
		List<VisTweet> tweets = new ArrayList<VisTweet>();
		for(int i = 0; i < tweet.length; ++i) {
			if(flower.accept(tweet[i])) {
				tweets.add(tweet[i]);
			}
		}
		return tweets.toArray(EMPTY);
	}
	
	public synchronized VisTweet[] inactive() {
		TwitterFlower flower = (TwitterFlower)m_owner;
		VisTweet[] tweet = m_inactive.toArray(EMPTY);
		List<VisTweet> tweets = new ArrayList<VisTweet>();
		for(int i = 0; i < tweet.length; ++i) {
			if(flower.accept(tweet[i])) {
				tweets.add(tweet[i]);
			}
		}
		return tweets.toArray(EMPTY);
	}
	
	public boolean contains(VisTweet tweet) {
		return m_elems.containsKey(tweet.getID());
	}
	
	public VisTweet getElement(String id) {
		return m_elems.get(id);
	}
	
	public VisTweet[] toArray() {
		TwitterFlower flower = (TwitterFlower)m_owner;
		VisTweet[] tt = m_elems.values().toArray(EMPTY);
		List<VisTweet> tweets = new ArrayList<VisTweet>();
		for(int i = 0; i < tt.length; ++i) {
			if(flower.accept(tt[i])) {
				tweets.add(tt[i]);
			}
		}
		return tweets.toArray(EMPTY);
	}
	
	public synchronized void active(VisTweet tweet) {
		if(!contains(tweet) || tweet.isActive()) {
			return;
		}
		tweet.update(frac());
		tweet.setActive(true);
		tweet.active(m_current);
		m_sentiment += tweet.getSentiment();
		m_active.add(tweet);
		m_inactive.remove(tweet);
	}
	
	public boolean clean() {
		long active = 0;
		long time = m_current;
		int period = 0;
		boolean flag = false;
		VisTube tube = null;
		VisTweet[] tweets = toArray();
		VisTweet tweet = null;
		for (int i = 0; i < tweets.length; ++i) {
			active = tweets[i].getLastActiveTime();
			period = (int) ((time - active) / Constant.TIME_INTERVAL);
			if (tweets[i].isActive()) {
				if (period >= Constant.ACTIVE_TWEET_LIFE) {
					// the tweet itself is invalidate
					remove(tweets[i]);
					int size = tweets[i].getTubeSize();
					for (int j = 0; j < size; ++j) {
						tube = tweets[i].getTube(0);
						tube.dettach();
						tube = null;
					}
					tweets[i] = null;
					flag = true;
				} else {
					// the tweet itself is valide
					VisTube[] tubes = tweets[i].getTubes();
					for(int j = 0; j < tubes.length; ++j) {
						tube = tubes[j];
						int gsize = tube.size();
						for (int k = 0; k < gsize; ++k) {
							tweet = tube.getTweet(0);
							active = tweet.getTime();
							if ((time - active) / Constant.TIME_INTERVAL >= Constant.ACTIVE_TWEET_LIFE) {
								tube.remove(tweet);
								tube.setWeight(tube.getWeight() - 1);
								tweet = null;
								flag = true;
							} else {
								break;
							}
						}
						if (tube.isEmpty()) {
							tube.dettach();
							tube = null;
							flag = true;
						}
					}
					int tsize = tweets[i].getTubeSize();
					if(tsize == 0) {
						remove(tweets[i]);
						tweets[i] = null;
					}
				}
			} else {
				if (period >= Constant.INACTIVE_TWEET_LIFE) {
					remove(tweets[i]);
					tweets[i] = null;
					flag = true;
				}
			}
		}
		return flag;
	}
	
	public void setSentiment(double s) {
	}
	
	public double getSentiment() {
		return m_sentiment / m_active.size();
	}
	
	public void clear() {
		this.m_elems.clear();
		this.m_active.clear();
		this.m_inactive.clear();
		this.setID("t");
		this.setLabel("");
	}
}
