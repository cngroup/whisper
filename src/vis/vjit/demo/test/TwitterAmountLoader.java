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
public class TwitterAmountLoader extends TwitterDBLoader {
	
	private GeoInfoV3 m_info = null;

	public TwitterAmountLoader() {
		
		m_info = new GeoInfoV3();
		
		m_info.country = "Unknown";
		
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
						Status s = null;
						TweetInfo tinfo = null;
						PreparedStatement input = TwitterProxy
								.prepareStatement(sql);
						double value = 0;
						List<TweetInfo> flow = new ArrayList<TweetInfo>();
						System.out.println("- preparing stream with flow rate : " + (1000 / interval) + "/sec");
						
						long duration = 1000;
						long origin = 0;
						long time = System.currentTimeMillis();
						origin = time;
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
									tinfo.geoinfo = m_info;
								}
								in.close();
								s = tinfo.status;
								tinfo.status = new MyStatus(s, time);
								flow.add(tinfo);
								time += interval;
							}
						}
						bshutdown = true;
						
						System.out.println("- start to replay with rate : " + (1000 / interval) + "/sec");
						long t1 = 0, t2 = 0;
						int size = flow.size();
						for(int i = 0; i < size; ++i) {
							tinfo = flow.get(i);
							s = tinfo.status;
							time = s.getCreatedAt().getTime();
							value = SentimentAnalyzer.sentiment(s.getText());
							t1 = System.currentTimeMillis();
							fireStatusPosted(s, tinfo.geoinfo, time, value);
							t2 = System.currentTimeMillis();
							
							if(t2 - t1 < interval) {
								Thread.sleep(interval - (t2 - t1));
							} else {
								Frame.delay += ((t2 - t1) - interval);
								continue;
							}
						}
						System.out.println("---------------------------------");
						double alcost = Frame.layout_cost / (double)Frame.layout_count;
						double arcost = Frame.render_cost / (double)Frame.render_count;
						System.out.println("- Flow Rate : " + 1000 / interval + "/sec");
						System.out.println("- Time Window : " + Constant.TIME_BIN_INTEVAL * Constant.TIME_WINDOW_SIZE / 1000 + "sec");
						System.out.println("- Max Active Tweet: " + Frame.max_act_tweets + ", Max InActive Tweet : " + Frame.max_inact_tweets);
						System.out.println("- Max Users : " + Frame.max_users + ", Max Groups : " + Frame.max_groups);
						System.out.println("- Average Layout Cost : " + alcost + "ms");
						System.out.println("- Layout Pre Sec : " + 1000 / alcost);
						System.out.println("- Average Render Cost : " + arcost + "ms");
						System.out.println("- Render Pre Sec : " + 1000 / arcost);
						System.out.println("- Delay : " + Frame.delay);
						System.out.println("---------------------------------");
						
					} catch (IOException e) {
						bshutdown = true;
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						bshutdown = true;
						e.printStackTrace();
					} catch (InterruptedException e) {
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
