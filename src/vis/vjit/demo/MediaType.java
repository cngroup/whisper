package vis.vjit.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import vis.vjit.tweeflow.data.VisTweet;

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
public class MediaType {

	
	private static Set<String> media = new HashSet<String>();
	
	private static Set<String> journalists = new HashSet<String>();
	
	public static void load(String file) {
		
		try {
			media.clear();
			journalists.clear();
			boolean bjournalist = false;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String text = br.readLine();
			while(null != text) {
				text = text.trim();
				if("".equals(text)) {
					text = br.readLine();
					continue;
				}
				if(text.indexOf("#") == 0) {
					bjournalist = true;
					text = br.readLine();
					continue;
				}
				if(bjournalist) {
					journalists.add(text);
				} else {
					media.add(text);
				}
				text = br.readLine();
			}
		} catch (Exception e) {
			System.err.println("Warning : Fail to load media list");
		}
	}
	
	public static boolean isMedia(VisTweet tweet) {
		String s = tweet.status().getUser().getScreenName();
		return media.contains(s);
	}
	
	public static boolean isJournalists(VisTweet tweet) {
		String s = tweet.status().getUser().getScreenName();
		return journalists.contains(s);
	}
}
