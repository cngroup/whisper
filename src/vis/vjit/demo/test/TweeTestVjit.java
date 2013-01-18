package vis.vjit.demo.test;

import twitter4j.Status;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTweet;
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
public class TweeTestVjit extends TweeFlowVjit {

	private static final long serialVersionUID = 5759118473946263864L;

	public TweeTestVjit() {	
		super();
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

		flower.lock();
		VisTopic topic = flower.topic();
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
				
		if(Frame.total_tweet % 500 == 0) {
			flower.clean();			
			flower.unlock();
			doLayout();
		} else {
			flower.unlock();
		}
		
		Frame.total_tweet ++;
	}
}
