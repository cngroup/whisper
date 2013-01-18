package vis.vjit.tweeflow.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/***
 * 
 * This piece of code is a joint research with Harvard University. 
 * It is based on the MIP opensource license. Please check the 
 * term before using.
 * 
 * The paper is published in InfoVIs 2013: Nan Cao, Yu-Ru Lin, Xiaohua Sun, 
 * David Lazer, Shixia Liu and Huamin Qu. 
 * "Whisper: Tracing the Spatiotemporal Process of Information Diffusion in Real Time"
 * 
 * @author NanCao(nancao@gmail.com)
 *
 */

public class SentimentAnalyzer {
	
	public static Map<String, Integer> m_dict = null;
	
	static {
		try {
			m_dict = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./data/AFINN-111.txt")));
			String line = br.readLine();
			String[] token = null;
			while(line != null) {
				line = line.trim();
				if(!"".equals(line)) {
					token = line.split("\\t");
					m_dict.put(token[0], Integer.parseInt(token[1]));
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static double sentiment(String text) {
		String[] tokens = text.toLowerCase().split("\\W+");
		double sent = 0;
		int lens = 0;
		for(int i = 0; i < tokens.length; ++i) {
			Integer value = m_dict.get(tokens[i]);
			if(value != null) {
				sent += value;
				lens ++;
			}
		}
		sent = lens == 0 ? 0 : sent / Math.sqrt(lens);
		sent = (sent + 5) / 10.0;
		return sent;
	}
	
}
