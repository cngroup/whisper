package vis.vjit.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
public class DataIO {

	public static Map<String, Double> location(String path) throws IOException {

		Map<String, Double> map = new HashMap<String, Double>();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(path)));

		String[] tokens = null;
		String line = br.readLine();
		while (null != line) {
			line = line.trim();
			if (!"".equals(line)) {
				tokens = line.split("\\|");
				double longitude = "null".equals(tokens[2]) ? 0 : Double
						.parseDouble(tokens[2]);
				map.put(tokens[0].toUpperCase(), longitude);
			}
			line = br.readLine();
		}
		return map;
	}

	public static Map<String, Integer> states(String file) throws IOException {
		final Map<String, Double> states = DataIO.location(file);
		String[] s = states.keySet().toArray(new String[0]);
		Arrays.sort(s, new Comparator<String>() {
			public int compare(String s1, String s2) {
				Double l1 = states.get(s1);
				Double l2 = states.get(s2);
				if (l1 > l2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		Map<String, Integer> order = new HashMap<String, Integer>();
		for(int i = 0; i < s.length; ++i) {
			order.put(s[i], i);
		}
		order.put("Unknown", 51);
		return order;
	}
}