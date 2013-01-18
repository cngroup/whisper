package vis.vjit.demo.io;

import java.io.Serializable;

import twitter4j.Status;
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
public class TweetInfo implements Serializable {

	private static final long serialVersionUID = 5793816245482732463L;
	
	public Status status = null;
	public GeoInfoV3 geoinfo = null;
	
	public TweetInfo(Status s, GeoInfoV3 info) {
		status = s;
		geoinfo = info;
	}
	
}
