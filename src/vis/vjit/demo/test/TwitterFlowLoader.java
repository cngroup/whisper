package vis.vjit.demo.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.io.TweetInfo;
import vis.vjit.tweeflow.io.TwitterDBLoader;
import vis.vjit.tweeflow.io.TwitterProxy;
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
public class TwitterFlowLoader extends TwitterDBLoader {

	public TwitterFlowLoader() {		
	}
	
	public void load(String table, final long interval) {
		if (table == null || "".equals(table)) {
			return;
		}
		
		bshutdown = false;
		final String sql = String.format("select * from %s where status is not null order by time", table);
		new Thread() {
			public void run() {
				try {
					m_status.setStateText(" Playing...");
					ResultSet rs = null;
					try {
						TweetInfo tinfo = null;
						PreparedStatement input = TwitterProxy
								.prepareStatement(sql);
						double TPS = 500.0;
						double value = 0;
						long duration = 5 * 60 * 1000;
						long origin = 0;
						long time = System.currentTimeMillis();
						double inv = 1000 / TPS;
						double tt = time;
						int retweet = 0;
						int tweet = 0;
						origin = time;
						long t1 = 0, t2 = 0;
						for(origin = time; (time - origin) < duration;) {
							input.clearParameters();
							rs = input.executeQuery();
							while (rs.next() && !bshutdown) {
								if((time - origin) >= duration) {
									break;
								}
								byte[] obj = rs.getBytes(2);
								ObjectInputStream in = new ObjectInputStream(
										new ByteArrayInputStream(obj));
								tinfo = (TweetInfo) in.readObject();
								if(tinfo.geoinfo == null) {
									continue;
								}
								in.close();
								tinfo.status = new MyStatus(tinfo.status, time);
								
								t1 = System.currentTimeMillis();
								value = SentimentAnalyzer.sentiment(tinfo.status.getText());
								fireStatusPosted(tinfo.status, tinfo.geoinfo, time, value);
								t2 = System.currentTimeMillis();
								
								if(tinfo.status.isRetweet()) {
									retweet ++;
								}
								tweet ++;
								tt += inv;
								time = (long)tt;
//								time += interval;
								
								if(t2 - t1 < inv) {
								} else {
									Frame.delay += ((t2 - t1) - (long)inv);
								}
							}
						}
						bshutdown = true;
						
						System.out.println("---------------------------------");
						double alcost = Frame.layout_cost / (double)Frame.layout_count;
						double arcost = Frame.render_cost / (double)Frame.render_count;
						System.out.println("- Flow Rate : " + 1000 / inv + "/sec");
						System.out.println("- Time Window : " + Constant.TIME_BIN_INTEVAL * Constant.TIME_WINDOW_SIZE / 1000 + "sec" + ", duration : " + duration + "sec");
						System.out.println("- Retweet : " + retweet + ", Tweet = " + tweet + ", Retweet Rate : " + (double)retweet / tweet);
						System.out.println("- Max Active Tweet: " + Frame.max_act_tweets + ", Max InActive Tweet : " + Frame.max_inact_tweets + ", Max Retweet : " + Frame.max_re_tweets);
						System.out.println("- Avg Active Tweet: " + Frame.avg_act_tweets / (double)Frame.layout_count + ", Avg InActive Tweet : " + Frame.avg_inact_tweets / (double)Frame.layout_count + ", Avg Retweet : " + Frame.avg_re_tweets / (double)Frame.layout_count);
						System.out.println("- Max Users : " + Frame.max_users + ", Max Groups : " + Frame.max_groups);
						System.out.println("- Avg Users : " + Frame.avg_users / (double)Frame.layout_count + ", Avg Groups : " + Frame.avg_groups / (double)Frame.layout_count);
						System.out.println("- Average Layout Cost : " + alcost);
						System.out.println("- Layout Pre Sec : " + 1000 / alcost);
						System.out.println("- Average Render Cost : " + arcost);
						System.out.println("- Render Pre Sec : " + 1000 / arcost);
						System.out.println("- Delay : " + Frame.delay);
						System.out.println("---------------------------------");
						
					} catch (IOException e) {
						bshutdown = true;
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						bshutdown = true;
						e.printStackTrace();
					} finally {
						m_status.setStateText(" Ready");
						bshutdown = true;
						rs.close();
					}
				} catch (SQLException e) {
					m_status.setStateText(" Ready");
					bshutdown = true;
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
