package vis.vjit.tweeflow.data;

import davinci.data.elem.IElement;

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
public interface IWhisperElem extends IElement {

	public void active(long time);
	
	public long getLastActiveTime();
	
	public void setSentiment(double s);
	
	public double getSentiment();
	
	public boolean isActive();
	
}
