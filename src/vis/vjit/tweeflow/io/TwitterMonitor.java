package vis.vjit.tweeflow.io;

import java.util.Vector;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import vis.vjit.tweeflow.util.SentimentAnalyzer;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import vis.vjit.tweeflow.util.geo.GoogleGeoLocator;

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
public class TwitterMonitor extends Dispatcher {

	private boolean bPause = false;
	private String m_topic = "";

	public TwitterMonitor() {
		this(null);
	}
	
	public TwitterMonitor(ITwitterSaver saver) {
		super(saver);
		m_listeners = new Vector<IMonitorListener>();
		TwitterProxy.shutdown();
		TwitterProxy.addStatusListener(new StatusAdapter() {
			public void onStatus(Status s) {
				try {
					String loc = s.getUser().getLocation();
					GeoInfoV3 info = GoogleGeoLocator.locateV3(loc);
					if (info != null && !"".equals(info.country)) {
						double value = SentimentAnalyzer.sentiment(s.getText());
						long time = System.currentTimeMillis();
						fireStatusPosted(s, info, time, value);
					}
					output(new TweetInfo(s, info));
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		m_saver = saver;
	}
	
	public synchronized void shutdown() {
		TwitterProxy.shutdown();
		super.shutdown();
	}
	
	public synchronized String monitor(String strtopic) {
		String name = strtopic.replaceAll("\\s*\\,\\s*", "_");
		name = name.replaceAll("\\s+", "0");
		name = "m_" + name + "_" + System.currentTimeMillis();
		name = name.toLowerCase();
		System.out.println("-> Monitoring on [" + strtopic + "]");
		save(name);
		String[] str = strtopic.split("\\s*\\,\\s*");
		TwitterProxy.filter(str);
		return name;
	}

	public synchronized boolean isPaused() {
		return bPause;
	}

	public synchronized void pause() {
		if (bPause) {
			TwitterProxy.shutdown();
			bPause = false;
		} else {
			String[] str = m_topic.split("\\s*\\,\\s*");
			TwitterProxy.filter(str);
			bPause = true;
		}
	}
}
