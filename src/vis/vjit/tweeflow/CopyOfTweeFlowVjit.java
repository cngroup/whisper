package vis.vjit.tweeflow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import twitter4j.Status;
import twitter4j.User;
import vis.vjit.tweeflow.action.TweeFlowAction;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.VisUser;
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
public class CopyOfTweeFlowVjit extends Display implements IMonitorListener {

	private static final long serialVersionUID = 5759118473946263864L;

	private TweeFlowerLayout m_vlayout = null;

	private MapRender m_maprender = null;

	public boolean isEncodeLongitude = false;

	public boolean isDiffusion = true;

	public int visual_style = 0;

	private Thread m_monitor = null;

	private Lock m_lock = null;
	
	private String id = "";

	public boolean isTracing = false;

	public CopyOfTweeFlowVjit() {

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
				flower.lock();
				System.out.println("zoom : lock");
				flower.zoom(topic);
				flower.unlock();
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

		VisTopic topic = flower.topic();
		flower.lock();
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
		flower.clean();
		flower.unlock();
		doLayout();
		// System.out.println("vjit update : " + (System.currentTimeMillis() -
		// t1));
	}

	public synchronized void delight(VisTweet tweet) {

		tweet.setHighlight(false);
		if (!tweet.isActive()) {
			return;
		}
		TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}
		long t1 = System.currentTimeMillis();
		isTracing = false;
		VisTopic topic = null;
		LinkedList<VisTopic> queue = new LinkedList<VisTopic>();
		queue.add(flower.topic());
		Set<VisTopic> visited = new HashSet<VisTopic>();
		VisTube[] tubes = null;
		while (!queue.isEmpty()) {
			topic = queue.poll();
			topic.update(1.0f);
			topic.setHighlight(false);
			VisTweet ctweet = topic.getElement(tweet.getID());
			VisTweet[] tweets = topic.active();
			for (int i = 0; i < tweets.length; ++i) {
				tubes = tweets[i].getTubes();
				tweets[i].update(1f);
				tweets[i].setHighlight(false);
				for (int j = 0; j < tubes.length; ++j) {
					tubes[j].update(1f);
					tubes[j].setHighlight(false);
					FlowerNode sink = tubes[j].sink();
					if (ctweet == tweets[i] && (sink instanceof VisTopic)
							&& !visited.contains(sink)) {
						visited.add((VisTopic) sink);
						queue.add((VisTopic) sink);
					}
				}
			}
		}
		repaint();
	}

	public synchronized void highlight(final VisTweet tweet) {
		tweet.setHighlight(true);
		if (!tweet.isActive()) {
			return;
		}
		final TwitterFlower flower = (TwitterFlower) getData("flower");
		if (flower == null) {
			return;
		}
		isTracing = true;
		new Thread() {
			public void run() {
				if (flower.tryLock()) {
					long t1 = System.currentTimeMillis();
					VisTube[] tubes = null;
					VisTopic topic = null;
					LinkedList<VisTopic> queue = new LinkedList<VisTopic>();
					queue.add(flower.topic());
					Set<VisTopic> visited = new HashSet<VisTopic>();
					while (!queue.isEmpty()) {
						topic = queue.poll();
						VisTweet ctweet = topic.getTweet(tweet.getID());

						topic.update(0.1f);
						topic.setHighlight(true);
						ctweet.setHighlight(true);

						VisTweet[] tweets = topic.active();
						for (int i = 0; i < tweets.length; ++i) {
							tubes = tweets[i].getTubes();
							if (ctweet == tweets[i]) {
								tweets[i].update(1f);
								tweets[i].setHighlight(true);
								for (int j = 0; j < tubes.length; ++j) {
									tubes[j].update(1f);
									tubes[j].setHighlight(true);
									FlowerNode ntopic = tubes[j].sink();
									if (!visited.contains(ntopic)
											&& ntopic instanceof VisTopic) {
										visited.add((VisTopic) ntopic);
										queue.add((VisTopic) ntopic);
									}
								}
							} else {
								tweets[i].update(0.1f);
								tweets[i].setHighlight(false);
								for (int j = 0; j < tubes.length; ++j) {
									tubes[j].update(0.1f);
									tubes[j].setHighlight(false);
									tubes[j].sink().setHighlight(false);
									tubes[j].sink().update(1.0f);
								}
							}
						}
					}
					flower.unlock();
					System.out.println("time = " + (System.currentTimeMillis() - t1));
					repaint();
				}
			}
		}.start();
	}
}
