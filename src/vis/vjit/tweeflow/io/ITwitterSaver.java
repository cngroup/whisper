package vis.vjit.tweeflow.io;

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

public interface ITwitterSaver {

	public void save(String topic);
	
	public void output(TweetInfo info);
	
	public void close();
	
	public void flush();
	
}
