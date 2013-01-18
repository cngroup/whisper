package vis.vjit.tweeflow.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import twitter4j.Status;
import vis.vjit.tweeflow.util.SentimentAnalyzer;
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
public class TwitterFileLoader extends Dispatcher implements ITwitterLoader {

	public TwitterFileLoader() {
	}
	
	public void load(final String context, final long interval) {
		bshutdown = false;
		new Thread() {
			public void run() {
				try {
					ObjectInputStream io = null;
					try {
						io = new ObjectInputStream(new FileInputStream(context));
						TweetInfo tinfo = null;
						GeoInfoV3 info = null;
						do {
							if (bshutdown) {
								break;
							}
							tinfo = (TweetInfo) io.readObject();
							info = tinfo.geoinfo;
							Status s = tinfo.status;
							double value = SentimentAnalyzer.sentiment(s
									.getText());
							fireStatusPosted(s, info, s.getCreatedAt()
									.getTime(), value);
							Thread.sleep(interval);
						} while (tinfo != null);
						io.close();
						shutdown();
					} catch (ClassNotFoundException e) {
						shutdown();
						e.printStackTrace();
						io.close();
					} catch (IOException ie) {
						shutdown();
						io.close();
					} catch (InterruptedException e) {
						shutdown();
						e.printStackTrace();
						io.close();
					}
				} catch (IOException ie) {
					shutdown();
					ie.printStackTrace();
				}
			}
		}.start();
	}

	public void load(String topic, long interval, long start, long end) {
		throw new RuntimeException("methods not supported in TwitterFileLoader");
	}
}
