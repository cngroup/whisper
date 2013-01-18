package vis.vjit.tweeflow.io;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterException;
import vis.vjit.tweeflow.Config;
import vis.vjit.tweeflow.util.SentimentAnalyzer;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import vis.vjit.tweeflow.util.geo.GoogleGeoLocator;

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
public class TwitterSearcher extends Dispatcher {

	private boolean bdebug = false;

	public TwitterSearcher() {
		this(null);
	}
	
	public TwitterSearcher(ITwitterSaver s) {
		super(s);
	}

	public String query(final String topic, final int npage, final long sleep) {
		
		final String fname = "q_" + topic + "_" + System.currentTimeMillis();
		
		new Thread() {
			public void run() {
				try {
					bshutdown = false;
					Set<Long> visited = new HashSet<Long>();
					save(fname);
					Query query = new Query(topic);
					query.setRpp(100);
					query.setGeoCode(new GeoLocation(0, 0), 6378, "km");
					for (int i = 0; i <= npage && !bshutdown; ++i) {
						query.setPage(i);
						query.setResultType(Query.RECENT);
						QueryResult result = TwitterProxy.query(query);
						List<Tweet> rt = result.getTweets();
						for (Tweet tweet : rt) {
							if (bshutdown) {
								break;
							}
							String tloc = tweet.getLocation();
							if (tloc != null
									&& !visited.contains(tweet.getId())) {
								visited.add(tweet.getId());
								Status s = new TweetStatus(tweet);
								if (s.isRetweet()
										&& s.getRetweetedStatus() == null) {
									Thread.sleep(sleep);
									continue;
								}

								GeoInfoV3 info = GoogleGeoLocator.locateV2(tweet.getLocation());
								// GeoInfoV3 info =
								// GoogleGeoLocator.locateV3(tweet.getLocation());
								// String[] token =
								// tloc.split("\\s*\\,\\s*");
								// GeoInfoV3 info = new GeoInfoV3();
								// //GoogleGeoLocator.locateV3(loc);
								// info.country = token[0];
								// info.state = token.length >= 2 ? token[1]
								// : "Unknown";
								// info.latitude = loc.getLatitude();
								// info.logitude = loc.getLongitude();
								if (info != null) {
									double value = SentimentAnalyzer
											.sentiment(s.getText());
																		
									output(new TweetInfo(s, info));
									
									fireStatusPosted(s, info, s.getCreatedAt().getTime(), value);
									if (bdebug) {
										System.out.println(tweet.getFromUser());
										System.out.println(tweet.getToUser());
										System.out.println(tweet.getText());
										System.out.println(tweet.getLocation());
										System.out.println("--------------");
									}
								}
							}
							Thread.sleep(sleep);
						}
					}
					shutdown();
					visited.clear();
					visited = null;
					query = null;
				} catch (TwitterException te) {
					System.err.println("Failed to search tweets: "
							+ te.getMessage());
					shutdown();
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
					shutdown();
				}
			}
		}.start();
		
		return fname;
	}

	public synchronized void shutdown() {
		if(null != m_saver) {
			m_saver.flush();
			m_saver.close();
		}
		bshutdown = true;
	}

	public static void main(String[] args) {
		Config.load("./conf.ini");
		TwitterProxy.connectTW();
		TwitterSearcher engine = new TwitterSearcher();
		engine.query("twitter", 10, 100);
	}
}
