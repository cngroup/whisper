package vis.vjit.tweeflow.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import vis.vjit.tweeflow.Config;

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
public class TwitterProxy {

	// ////////////////////////////////////////////////////////
	// TODO: Database Proxy
	// ////////////////////////////////////////////////////////
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
	
	public static void setAutoCommit(boolean auto) throws SQLException {
		m_conn.setAutoCommit(auto);
	}
	
	public static void commit() throws SQLException {
		m_conn.commit();
	}

	public static void disconnect() throws SQLException {
		if (m_conn != null) {
			m_conn.close();
			m_conn = null;
			m_stat.close();
			m_stat = null;
		}
	}

	public static int executeUpdate(String sql) throws SQLException {
		if (m_stat != null) {
			return m_stat.executeUpdate(sql);
		}
		return 0;
	}

	public static boolean execute(String sql) throws SQLException {
		if (m_stat != null) {
			return m_stat.execute(sql);
		}
		return false;
	}
	
	public static ResultSet executeQuery(String sql) throws SQLException {
		if (m_stat != null) {
			return m_stat.executeQuery(sql);
		}
		return null;
	}

	public static PreparedStatement prepareStatement(String sql)
			throws SQLException {
		if (m_conn != null)
			return m_conn.prepareStatement(sql);
		else
			return null;
	}

	public static Connection getConnection() {
		return m_conn;
	}

	public static Statement getStatement() {
		return m_stat;
	}

	// ///////////////////////////////////////////////
	// TODO: Twitter Proxy
	// ///////////////////////////////////////////////
	private static Twitter m_twitter = null;
	private static TwitterStream m_tstream = null;
	
	public static void connectTW() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Config.OAUTH_CONSUMER_KEY)
				.setOAuthConsumerSecret(Config.OAUTH_CONSUMER_SECRET)
				.setOAuthAccessToken(Config.OAUTH_ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(Config.OAUTH_ACCESS_TOKEN_SECRET)
				.setIncludeEntitiesEnabled(true);
		Configuration c = cb.build();
		m_tstream = new TwitterStreamFactory(c).getInstance();
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Config.OAUTH_CONSUMER_KEY)
				.setOAuthConsumerSecret(Config.OAUTH_CONSUMER_SECRET)
				.setOAuthAccessToken(Config.OAUTH_ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(Config.OAUTH_ACCESS_TOKEN_SECRET);
		m_twitter = new TwitterFactory(cb.build()).getInstance();
	}
	
	
	public static QueryResult query(Query q) throws TwitterException {
		return m_twitter.search(q);
	}

	public static void shutdown() {
		if(m_tstream != null)
		m_tstream.shutdown();
	}

	public static void addStatusListener(StatusListener l) {
		if(m_tstream != null)
		m_tstream.addListener(l);
	}

	public static void filter(String[] terms) {
		FilterQuery q = new FilterQuery();
		q.track(terms);
		if(m_tstream != null)
		m_tstream.filter(q);
	}
}
