package vis.vjit.demo.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import vis.vjit.tweeflow.io.TweetInfo;

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
public class FlowTest {
	
	public static int flow_test_tweets = 50;
	public static int flow_test_timespan = 1000;
	
	private static Connection m_conn = null;
	private static Statement m_stat = null;

	public static boolean connectDB(String dbname) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			m_conn = DriverManager.getConnection(String.format(
					"jdbc:sqlite:%s", dbname));
			m_stat = m_conn.createStatement();
			m_stat.executeUpdate("create table if not exists 'catelog' (time int, name text);");
			m_stat.executeUpdate("create table if not exists 'copyright' (copyright text);");
			m_stat.executeUpdate("insert into copyright values('WhisperFlowerV1.0 (C)2011-2012 Nan Cao (nancao@cse.ust.hk) all rights reserved.')");
			return true;
		} catch (ClassNotFoundException e) {
			System.err.println("fatal error: can not find sqlite driver!");
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	public static ResultSet executeQuery(String sql) throws SQLException {
		if (m_stat != null) {
			return m_stat.executeQuery(sql);
		}
		return null;
	}
	
	public static void statistic(String table) throws SQLException, IOException, ClassNotFoundException {
		
		final String sql = String.format("select * from %s where status is not null order by time", table);
		
		ResultSet rs = null;
		long amount = 0, pretime = 0;
		int freqcnt = 0;
		double maxfreq = 0, minfreq = Double.MAX_VALUE, freq = 0, avgfreq = 0;
		double stdfreq = 0;
		int n = 0;
		long orgin = 0, end = 0;
		int located = 0;
		int retweeted = 0;
		
		TweetInfo tinfo = null;
		rs = executeQuery(sql);
		while (rs.next()) {
			long time = rs.getLong(1);
			
			byte[] obj = rs.getBytes(2);
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(obj));
			tinfo = (TweetInfo) in.readObject();
			in.close();
			if(tinfo.geoinfo != null) {
				located ++;
			}
			if(tinfo.status.isRetweet()) {
				retweeted ++;
			}
			if(pretime == 0) {
				orgin = time;
				pretime = time;
				freqcnt = 1;
			} else if(pretime != time) {
				freq = 1000.0 * freqcnt / (time - pretime);
				maxfreq = Math.max(freq, maxfreq);
				minfreq = Math.min(freq, minfreq);
				System.out.println((time - pretime) + "," + freq + "," + freqcnt);
				pretime = time;
				freqcnt = 1;
			} else {
				freqcnt ++;
			}
			end = time;
			amount++;
		}
		rs.close();
		avgfreq = 1000 * amount / (end - orgin);
		
		rs = executeQuery(sql);
		while (rs.next()) {
			long time = rs.getLong(1);
			if(pretime == 0) {
				orgin = time;
				pretime = time;
				freqcnt = 1;
			} else if(pretime != time) {
				freq = 1000.0 * freqcnt / (time - pretime);
				stdfreq += (freq - avgfreq) * (freq - avgfreq);
				System.out.println((time - pretime) + "," + freq + "," + freqcnt);
				pretime = time;
				freqcnt = 1;
				n ++;
			} else {
				freqcnt ++;
			}
			end = time;
			amount++;
		}
		
		stdfreq = Math.sqrt(stdfreq / n);
		System.out.println("----------------------");
		System.out.println("amount = " + amount);
		System.out.println("maxfreq = " + maxfreq + "/sec, minfreq = " + minfreq + "/sec" + ", avgfreq = " + avgfreq + "/sec, std = " + stdfreq);
		System.out.println("timespan = " + (end - orgin) / 1000 + "sec");
		System.out.println("retweet = " + retweeted  + ", ratio = " + (double)retweeted / amount);
		System.out.println("located = " + located + ", ratio = " + (double)located / amount);
		rs.close();
	}
	
	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		FlowTest.connectDB("./data/cases/all-20120307.db");
		FlowTest.statistic("m_romney_santorum_gingrich_paul_1331097604696");
		
//		FlowTest.connectDB("./data/ipad_7M.db");
//		FlowTest.statistic("m_ipad_1331143612562");
		
//		FlowTest.connectDB("./data/cadidates_20120314.db");
//		FlowTest.statistic("m_romney_santorum_gingrich_paul_1331708302828");
	}
}
