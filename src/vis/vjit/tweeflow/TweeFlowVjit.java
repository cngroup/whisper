package vis.vjit.tweeflow;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import twitter4j.Status;
import vis.vjit.tweeflow.action.DragAction;
import vis.vjit.tweeflow.action.TweeFlowAction;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.filter.AndFilter;
import vis.vjit.tweeflow.data.filter.ImportanceFilter;
import vis.vjit.tweeflow.data.filter.SentimentFilter;
import vis.vjit.tweeflow.data.filter.TopicFilter;
import vis.vjit.tweeflow.io.IMonitorListener;
import vis.vjit.tweeflow.layout.TweeFlowerLayout;
import vis.vjit.tweeflow.render.MapRender;
import vis.vjit.tweeflow.render.WhisperEdgeRender;
import vis.vjit.tweeflow.render.WhisperEdgeTheme;
import vis.vjit.tweeflow.render.WhisperNodeRender;
import vis.vjit.tweeflow.render.WhisperNodeTheme;
import vis.vjit.tweeflow.render.WhisperVjitRender;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import vis.vjit.tweeflow.util.geo.GoogleGeoLocator;
import davinci.Display;
import davinci.data.IData;

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
public class TweeFlowVjit extends Display implements IMonitorListener {

	private static final long serialVersionUID = 5759118473946263864L;

	private TweeFlowerLayout m_vlayout = null;

	private MapRender m_maprender = null;

	public boolean isEncodeLongitude = false;

	public boolean isDiffusion = true;

	public int visual_style = 0;

	private Thread m_monitor = null;

	private Lock m_lock = null;
	
	private String m_focus = "";

	public boolean isTracing = false;
	
	public static boolean isShowAll = false;
	
	public static boolean isJapan = true;
	
	private TopicFilter m_tfilter = null;
	
	private ImportanceFilter m_pfilter = null;
	
	private SentimentFilter m_sfilter = null;
	
	private AndFilter m_andfilter = null;

	public TweeFlowVjit() {
		
		m_tfilter = new TopicFilter();
		m_pfilter = new ImportanceFilter();
		m_sfilter = new SentimentFilter();
		m_andfilter = new AndFilter(m_tfilter, m_pfilter, m_sfilter);

		m_vlayout = new TweeFlowerLayout();

		m_lock = new ReentrantLock();

		m_maprender = new MapRender();

		addLayout(m_vlayout);

		setElemFinder(new FlowerElemFinder());
		setDisplayRender(new WhisperVjitRender());
		addBackgroundRender(m_maprender);
		// setDisplayRender(new HierarchicalRender());

		m_monitor = new Thread() {
			public void run() {
				while (true) {
					m_lock.lock();
					TwitterFlower flower = (TwitterFlower) getData("flower");
					if (flower != null) {
						flower.lock();
						System.out.println("clean up monitor : lock");
						flower.update(flower.topic(),
								System.currentTimeMillis());
						flower.clean();
						flower.unlock();
						doLayout();
						System.out.println("clean up monitor : unlock");
					}
					m_lock.unlock();
					try {
						Thread.sleep(Constant.TIME_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		addAction(new DragAction());
		addAction(new TweeFlowAction());
		addElemTheme("nodes", new WhisperNodeTheme());
		addElemRender("nodes", new WhisperNodeRender());
		addElemTheme("edges", new WhisperEdgeTheme());
		addElemRender("edges", new WhisperEdgeRender());
	}

	public synchronized void start() {
		if (m_monitor.isAlive()) {
			m_lock.unlock();
		} else {
			m_monitor.start();
		}
	}

	public synchronized void stop() {
		if (m_monitor.isAlive()) {
			m_lock.lock();
		}
	}

	public void updateTopic() {
		TwitterFlower flower = (TwitterFlower) getData("flower");
		m_vlayout.source(flower.topic());
		this.repaint();
	}

	public void zoom(final VisTopic topic) {
		new Thread() {
			public void run() {
				if (m_vlayout.isPerforming()) {
					m_vlayout.shutdown();
				}
				TwitterFlower flower = (TwitterFlower) getData("flower");
				if(isTracing) {
					delight();
				}
				delight();
				flower.lock();
				System.out.println("zoom : lock");
				flower.zoom(topic);
				flower.unlock();
				if(!"".equals(m_focus) && !(null == m_focus)) {
					VisTweet t = topic.getElement(m_focus);
					if(t != null) {
						highlight(t);
					}
				}
				System.out.println("zoom : unlock");
				// m_vlayout.layout(TweeFlowVjit.this);
				doLayout();
				repaint();
			}
		}.start();
	}

	public void zoomOut() {
		new Thread() {
			public void run() {
				if (m_vlayout.isPerforming()) {
					m_vlayout.shutdown();
				}
				TwitterFlower flower = (TwitterFlower) getData("flower");
				if (flower == null) {
					return;
				}
				flower.lock();
				System.out.println("zoom : lock");
				flower.zoomOut();
				flower.unlock();
				System.out.println("zoom : unlock");
				// m_vlayout.layout(TweeFlowVjit.this);
				doLayout();
				repaint();
			}
		}.start();
	}

	public void reLayout(boolean bAnimate) {
		if (m_vlayout.isPerforming()) {
			m_vlayout.shutdown();
		}
		m_vlayout.setEnable(bAnimate);
		doLayout();
	}
	
	public void addData(IData data) {
		super.addData(data);
		if(data instanceof TwitterFlower) {
			((TwitterFlower)data).setFilter(m_andfilter);
		}
	}

	public void clear() {
		super.clear();
		m_vlayout.reset();
	}

	public void enableLongitude(boolean enable) {
		isEncodeLongitude = enable;
		m_vlayout.setEnable(true);
		doLayout();
		repaint();
	}

	public void postUpdate(VisTopic topic, Status s, GeoInfoV3 info, long time,
			double sentiments) {
		TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}

		double longitude = GoogleGeoLocator.getLogitude(info.country);
		if (longitude != -1) {
			info.clogitude = longitude;
		} else {
			return;
		}

		if ("United States".equals(info.country)) {
			if ("Unknown".equals(info.state)) {
				info.slogitude = -155;
				info.order = 51;
			} else {
				info.slogitude = GoogleGeoLocator.getLogitude(info.state);
				if (info.slogitude == -1) {
					info.state = "Unknown";
					info.slogitude = -155;
					info.order = 51;
				}
				info.order = GoogleGeoLocator.getOrder(info.state);
			}
		} else {
			info.slogitude = (info.east + info.west) / 2.0;
		}
		if (time > topic.getTime()) {
			flower.update(topic, time);
		}
		if (s.isRetweet()) {
			flower.retweet(s, info);
		} else {
			String id = s.getId() + "";
			VisTweet tweet = topic.getElement(id);
			if (tweet == null) {
				tweet = new VisTweet(s);
				tweet.setSentiment(sentiments);
				topic.add(tweet);
			}
			tweet.active(topic.getTime());
			topic.setValid(false);
			VisTopic t = (VisTopic) flower.getNode(info.country);
			if (t != null) {
				tweet = t.getElement(id);
				if (null == tweet) {
					tweet = new VisTweet(s);
					tweet.setSentiment(sentiments);
					t.add(tweet);
				}
				tweet.active(topic.getTime());
				t.setValid(false);
			}
			t = (VisTopic) flower.getNode(info.state);
			if (t != null) {
				tweet = t.getElement(id);
				if (null == tweet) {
					tweet = new VisTweet(s);
					tweet.setSentiment(sentiments);
					t.add(tweet);
				}
				tweet.active(topic.getTime());
				t.setValid(false);
			}
		}
	}

	public void statusPosted(Status s, GeoInfoV3 info, long time,
			double sentiments) {

		TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}

		double longitude = GoogleGeoLocator.getLogitude(info.country);
		if (longitude != -1) {
			info.clogitude = longitude;
			// info.logitude = longitude;
		} else {
			return;
		}

		if ("United States".equals(info.country)) {
			if ("Unknown".equals(info.state)) {
				info.slogitude = -155;
				info.order = 51;
			} else {
				info.slogitude = GoogleGeoLocator.getLogitude(info.state);
				if (info.slogitude == -1) {
					info.state = "Unknown";
					info.slogitude = -155;
					info.order = 51;
				}
				info.order = GoogleGeoLocator.getOrder(info.state);
			}
		} else {
			info.slogitude = (info.east + info.west) / 2.0;
		}

//		long t1 = System.currentTimeMillis();
		flower.lock();
//		long t2 = System.currentTimeMillis();
//		System.out.println("lock time : " + (t2 - t1));
		VisTopic topic = flower.topic();
		if (time > topic.getTime()) {
			flower.update(topic, time);
		}
//		long t3 = System.currentTimeMillis();
//		long t4 = 0;
//		System.out.println("update time : " + (t3 - t2));
		if (s.isRetweet()) {
			flower.retweet(s, info);
//			t4 = System.currentTimeMillis();
//			System.out.println("retweet time : " + (t4 - t3));
		} else {
			String id = s.getId() + "";
			VisTweet tweet = topic.getElement(id);
			if (tweet == null) {
				tweet = new VisTweet(s);
				tweet.setSentiment(sentiments);
				topic.add(tweet);
			}
			tweet.active(topic.getTime());
			topic.setValid(false);
			VisTopic t = (VisTopic) flower.getNode(info.country);
			if (t != null) {
				tweet = t.getElement(id);
				if (null == tweet) {
					tweet = new VisTweet(s);
					tweet.setSentiment(sentiments);
					t.add(tweet);
				}
				tweet.active(topic.getTime());
				t.setValid(false);
			}
			t = (VisTopic) flower.getNode(info.state);
			if (t != null) {
				tweet = t.getElement(id);
				if (null == tweet) {
					tweet = new VisTweet(s);
					tweet.setSentiment(sentiments);
					t.add(tweet);
				}
				tweet.active(topic.getTime());
				t.setValid(false);
			}
//			t4 = System.currentTimeMillis();
//			System.out.println("post time : " + (t4 - t3));
		}
		flower.clean();
//		long t5 = System.currentTimeMillis();
//		System.out.println("clean time : " + (t5 - t4));
		flower.unlock();
//		long t6 = System.currentTimeMillis();
//		System.out.println("unloc time : " + (t6 - t5));
		doLayout();
//		long t7 = System.currentTimeMillis();
//		System.out.println("layout time : " + (t7 - t6));
//		System.out.println("-------------------------------");
		// System.out.println("vjit update : " + (System.currentTimeMillis() -
		// t1));
	}

	public void delight() {
		TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}
		isTracing = false;
		VisTopic topic = flower.focus();
		VisTube[] tubes = null;
		topic.update(1.0f);
		topic.setHighlight(false);
		VisTweet[] tweets = topic.active();
		for (int i = 0; i < tweets.length; ++i) {
			tubes = tweets[i].getTubes();
			tweets[i].update(1f);
			tweets[i].setHighlight(false);
			for (int j = 0; j < tubes.length; ++j) {
				tubes[j].update(1f);
				tubes[j].setHighlight(false);
				FlowerNode sink = tubes[j].sink();
				sink.setHighlight(false);
				sink.update(1f);
			}
		}
		repaint();
	}
	
	public void highlight(VisTweet[] vtweets) {
		
		Set<String> sidx = new HashSet<String>();
		for(int i = 0; i < vtweets.length; ++i) {
			vtweets[i].setHighlight(true);
			sidx.add(vtweets[i].getID());
		}
		
		final TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}
		isTracing = true;
		
		for(int idx = 0; idx < vtweets.length; ++idx) {
			VisTube[] tubes = null;
			VisTopic topic = flower.focus();
			VisTweet ctweet = topic.getTweet(vtweets[idx].getID());
			if(ctweet == null) {
				continue;
			}
			topic.update(0.1f);
			topic.setHighlight(true);
			ctweet.setHighlight(true);
			VisTweet[] tweets = topic.active();
			Set<FlowerNode> focused = new HashSet<FlowerNode>();
			for (int i = 0; i < tweets.length; ++i) {
				tubes = tweets[i].getTubes();
				if (ctweet == tweets[i]) {
					tweets[i].update(1f);
					tweets[i].setHighlight(true);
					for (int j = 0; j < tubes.length; ++j) {
						tubes[j].update(1f);
						tubes[j].setHighlight(true);
						focused.add(tubes[j].sink());
						tubes[j].sink().update(0.1f);
						tubes[j].sink().setHighlight(true);
					}
				} else if(!sidx.contains(tweets[i].getID())){
					tweets[i].update(0.1f);
					tweets[i].setHighlight(false);
					for (int j = 0; j < tubes.length; ++j) {
						tubes[j].update(0.1f);
						tubes[j].setHighlight(false);
						FlowerNode sink = tubes[j].sink();
						if(!focused.contains(sink)) {
							tubes[j].sink().setHighlight(false);
							tubes[j].sink().update(1.0f);
						}
					}
				}
			}
		}
		repaint();
	}

	public void highlight(final VisTweet tweet) {
		tweet.setHighlight(true);
		if (!tweet.isActive()) {
			return;
		}
		final TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}
		isTracing = true;
		m_focus = tweet.getID();
		VisTube[] tubes = null;
		VisTopic topic = flower.focus();
		VisTweet ctweet = topic.getTweet(tweet.getID());
		topic.update(0.1f);
		topic.setHighlight(true);
		ctweet.setHighlight(true);
		VisTweet[] tweets = topic.active();
		Set<FlowerNode> focused = new HashSet<FlowerNode>();
		for (int i = 0; i < tweets.length; ++i) {
			tubes = tweets[i].getTubes();
			if (ctweet == tweets[i]) {
				tweets[i].update(1f);
				tweets[i].setHighlight(true);
				for (int j = 0; j < tubes.length; ++j) {
					tubes[j].update(1f);
					tubes[j].setHighlight(true);
					focused.add(tubes[j].sink());
					tubes[j].sink().update(0.1f);
					tubes[j].sink().setHighlight(true);
				}
			} else {
				tweets[i].update(0.1f);
				tweets[i].setHighlight(false);
				for (int j = 0; j < tubes.length; ++j) {
					tubes[j].update(0.1f);
					tubes[j].setHighlight(false);
					FlowerNode sink = tubes[j].sink();
					if(!focused.contains(sink)) {
						tubes[j].sink().setHighlight(false);
						tubes[j].sink().update(1.0f);
					}
				}
			}
		}
		repaint();
	}
	
	public void filter(final double importance) {
		new Thread() {
			public void run() {
				TwitterFlower flower = (TwitterFlower)getData("flower");
				if (null == flower) {
					return;
				}
				flower.lock();
				m_pfilter.setThreshold(importance);
				flower.unlock();
				doLayout();
				repaint();
			}
		}.start();
	}
	
	public void filter (final String[] topics) {
		new Thread() {
			public void run() {
				m_tfilter.setTopics(topics);
				doLayout();
				repaint();
			}
		}.start();
	}
	
	public void filter() {
		new Thread() {
			public void run() {
				doLayout();
				repaint();
			}
		}.start();
	}
}
