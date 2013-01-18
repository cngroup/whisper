package vis.vjit.tweeflow.util.geo;

import java.io.Serializable;

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
public class GeoInfoV3 implements Serializable {

	private static final long serialVersionUID = -6463225600503701943L;

	public String country = "";
	
	public String state = "Unknown";
	
	public String city = "Unknown";
	
	public double logitude = -1;
	
	public double latitude = -1;
	
	public double west = 0, east = 0, north = 0, south = 0;
	
	public double slogitude = -1;
	
	public double clogitude = -1;
	
	public int order = -1;
	
	public String toString() {
		return country + ", " + state + ", " + city;
	}
	
}
