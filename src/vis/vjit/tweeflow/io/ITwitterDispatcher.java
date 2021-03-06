package vis.vjit.tweeflow.io;

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
public interface ITwitterDispatcher {

	public void addListener(IMonitorListener l);

	public void removeListener(IMonitorListener l);

	public void fireStatusPosted(Status s, GeoInfoV3 info, long time, double sentiments);
	
	public boolean isDispatching();
	
	public void shutdown();
	
}
