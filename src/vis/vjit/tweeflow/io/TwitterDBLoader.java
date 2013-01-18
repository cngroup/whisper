package vis.vjit.tweeflow.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import twitter4j.Status;
import vis.vjit.demo.ui.PStateBar;
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
public class TwitterDBLoader extends Dispatcher implements ITwitterLoader {

	protected PStateBar m_status = null;
	
	public TwitterDBLoader() {
		m_status = null;
	}
	
	public void setStateBar(PStateBar s) {
		m_status = s;
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
						GeoInfoV3 info = null;
						PreparedStatement input = TwitterProxy
								.prepareStatement(sql);
						rs = input.executeQuery();
						double value = 0;
						while (rs.next() && !bshutdown) {
							long time = rs.getLong(1);
							byte[] obj = rs.getBytes(2);
							ObjectInputStream in = new ObjectInputStream(
									new ByteArrayInputStream(obj));
							tinfo = (TweetInfo) in.readObject();
							in.close();
							s = tinfo.status;
							info = tinfo.geoinfo;
							long t1 = 0, t2 = interval;
							if(info != null) {
								value = SentimentAnalyzer.sentiment(s.getText());
								t1 = System.currentTimeMillis();
								fireStatusPosted(s, info, time, value);
								t2 = System.currentTimeMillis();
//								System.out.println("update time cost : " + (t2 - t1));
								Thread.sleep(interval);
							}
						}
						bshutdown = true;
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

	public void load(String dataset, final long start, final long end,
			final long sleep) {
		if (dataset == null || "".equals(dataset)) {
			return;
		}
		bshutdown = false;
		final String sql = String
				.format("select * from %s where where status is not null and time >= %d and time < %d order by time",
						dataset, start, end);
		new Thread() {
			public void run() {
				try {
					ResultSet rs = null;
					try {
						Status s = null;
						TweetInfo tinfo = null;
						GeoInfoV3 info = null;
						PreparedStatement input = TwitterProxy
								.prepareStatement(sql);
						rs = input.executeQuery();
						double value = 0;
						while (rs.next() && !bshutdown) {
							long time = rs.getLong(1);
							byte[] obj = rs.getBytes(2);
							ObjectInputStream in = new ObjectInputStream(
									new ByteArrayInputStream(obj));
							tinfo = (TweetInfo) in.readObject();
							in.close();
							s = tinfo.status;
							info = tinfo.geoinfo;
							value = SentimentAnalyzer.sentiment(s.getText());
							fireStatusPosted(s, info, time, value);
							Thread.sleep(sleep);
						}
						bshutdown = true;
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
						bshutdown = true;
						rs.close();
					}
				} catch (SQLException e) {
					bshutdown =true;
					e.printStackTrace();
				}
			}
		}.start();
	}
}
