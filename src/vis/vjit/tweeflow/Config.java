package vis.vjit.tweeflow;

import java.io.FileInputStream;
import java.util.Properties;

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
public class Config {

	public static String OAUTH_CONSUMER_KEY = "";

	public static String OAUTH_CONSUMER_SECRET = "";

	public static String OAUTH_ACCESS_TOKEN = "";

	public static String OAUTH_ACCESS_TOKEN_SECRET = "";

	public static String GOOGLE_GEO_KEY = "";

	public static void load(String file) {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(file));
			OAUTH_CONSUMER_KEY = p.getProperty("OAUTH_CONSUMER_KEY");
			OAUTH_CONSUMER_SECRET = p.getProperty("OAUTH_CONSUMER_SECRET");
			OAUTH_ACCESS_TOKEN = p.getProperty("OAUTH_ACCESS_TOKEN");
			OAUTH_ACCESS_TOKEN_SECRET = p
					.getProperty("OAUTH_ACCESS_TOKEN_SECRET");
			GOOGLE_GEO_KEY = p.getProperty("GOOGLE_GEO_KEY");
			
			String v = p.getProperty("TIME_BIN_INTEVAL");
			if(v != null && !"".equals(v)) {
				Constant.TIME_BIN_INTEVAL = Long.parseLong(v) * 1000;
			}
			v = p.getProperty("TIME_WINDOW_SIZE");
			if(v != null && !"".equals(v)) {
				Constant.TIME_WINDOW_SIZE = Integer.parseInt(v);
			}
			v = p.getProperty("TIME_SUB_WINDOW_SIZE");
			if(v != null && !"".equals(v)) {
				Constant.TIME_SUB_WINDOW_SIZE = Integer.parseInt(v);
			}
			double ratio = 0;
			v = p.getProperty("INACTIVE_TWEET_LIFE");
			if(v != null && !"".equals(v)) {
				ratio = Double.parseDouble(v);
			}
			v = p.getProperty("LOADING_TIME_INTERVAL");
			if(v != null && !"".equals(v)) {
				Constant.LOADING_TIME_INTERVAL = Long.parseLong(v);
			}
			Constant.TIME_INTERVAL = Math.round(Constant.TIME_BIN_INTEVAL * Constant.TIME_WINDOW_SIZE / Constant.TIME_SUB_WINDOW_SIZE);
			Constant.ACTIVE_TWEET_LIFE = Constant.TIME_SUB_WINDOW_SIZE;
			Constant.INACTIVE_TWEET_LIFE = (int)Math.round(Constant.ACTIVE_TWEET_LIFE * ratio);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fatal Error : Fail to load Twitter or Google Geo API access tokens or keys");
			System.exit(-1);
		}
	}
	/*
	 * Nan 's account
	 */
	// OAUTH_CONSUMER_KEY = "fs1NJhf4xBcLUYxfsPtQ";
	// OAUTH_CONSUMER_SECRET = "UkWTS4uJBsT4CJn43gegFhPRTAignV5l18S1rzwkIo";
	// OAUTH_ACCESS_TOKEN =
	// "54139486-EWlqDIaSFfFf9DedzR5kmE3NFbtpyi9PpykwEaV0l";
	// OAUTH_ACCESS_TOKEN_SECRET =
	// "oBbyrriEq4I207XRITYKdEcg9DEYJwPWMZJ6XJ7GQ";

	/*
	 * Yu-Ru's account
	 * 
	 * Consumer key Mn4ZPQRCJ7yFVJrRHGtuQ Consumer secret
	 * zm8TYwIx6taCFUYxGILzebXYD3xO4FzEhbVg7TSf4
	 * 
	 * Access Token : 83995411-qJ0CjA18F2ytlFdOLW990KrPDAjaK7D0SEkypRw4k Access
	 * Token Secret : EudsqVstZ6l5w5FCr2V5k4GYvDXJLcfEGIPNtvLmeM4
	 */
	/*
	 * OAUTH_CONSUMER_KEY = "Mn4ZPQRCJ7yFVJrRHGtuQ"; OAUTH_CONSUMER_SECRET =
	 * "zm8TYwIx6taCFUYxGILzebXYD3xO4FzEhbVg7TSf4"; OAUTH_ACCESS_TOKEN =
	 * "83995411-qJ0CjA18F2ytlFdOLW990KrPDAjaK7D0SEkypRw4k";
	 * OAUTH_ACCESS_TOKEN_SECRET =
	 * "EudsqVstZ6l5w5FCr2V5k4GYvDXJLcfEGIPNtvLmeM4";
	 */

}
