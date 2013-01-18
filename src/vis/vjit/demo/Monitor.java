package vis.vjit.demo;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import vis.vjit.tweeflow.Config;
import vis.vjit.tweeflow.io.Dispatcher;
import vis.vjit.tweeflow.io.TweetInfo;
import vis.vjit.tweeflow.io.TwitterDBSaver;
import vis.vjit.tweeflow.io.TwitterFileSaver;
import vis.vjit.tweeflow.io.TwitterProxy;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;

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
public class Monitor extends Dispatcher {

	int iteration = 0;
	
	public Monitor() {
		
		TwitterProxy.shutdown();
		TwitterProxy.addStatusListener(new StatusAdapter() {
			public void onStatus(Status s) {
				try {
					String loc = s.getUser().getLocation();
					if(loc != null && !"".equals(loc.trim())) {
						GeoInfoV3 info = null;
//						GeoInfoV3 info = GoogleGeoLocator.locateV2(loc);
//						if (info != null && !"".equals(info.country)) {
//							double value = SentimentAnalyzer.sentiment(s.getText());
//							long time = System.currentTimeMillis();
//							fireStatusPosted(s, info, time, value);
//						}
						if(iteration % 50 == 0) {
							System.out.println("----> Saving [" + s.getCreatedAt() + "|" + s.getId() +  "|" + info + "]");
							iteration = 0;
						}
						iteration ++;
						output(new TweetInfo(s, info));
						flush();
					}
					//Thread.sleep(30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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

	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 3) {
			
			for(int i = 0; i < args.length; ++i) {
				System.out.println(args[i]);
			}
			
			System.out
					.println("Usage : java -jar monitor.jar <-file|db> <keywords> <path|dbname>");
			System.out.println("-- Save into a file : java -jar monitor.jar -file \"twitter\", ./data/");
			System.out.println("-- Save into a database : java -jar monitor.jar -db \"twitter\", ./data/results.db");
			return;
		}
		
		Config.load("./conf-yuru.ini");
		if("-file".equalsIgnoreCase(args[0])) {
			TwitterProxy.connectTW();
			Monitor gather = new Monitor();
			gather.setSaver(new TwitterFileSaver(args[2]));
			gather.monitor(args[1]);
		} else if ("-db".equalsIgnoreCase(args[0])) {
			TwitterProxy.connectTW();
			TwitterProxy.connectDB(args[2]);
			Monitor gather = new Monitor();
			gather.setSaver(new TwitterDBSaver());
			gather.monitor(args[1]);
		}
	}
}
