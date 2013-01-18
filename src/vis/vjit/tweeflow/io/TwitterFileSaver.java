package vis.vjit.tweeflow.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import twitter4j.Status;
import vis.vjit.tweeflow.util.SentimentAnalyzer;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;

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
public class TwitterFileSaver implements ITwitterSaver {

	private Vector<IMonitorListener> m_listeners = null;

	private boolean bshutdown = false;

	private ObjectOutputStream m_out = null;
	private String m_path = "";

	public TwitterFileSaver(String path) {
		m_path = path;
		m_listeners = new Vector<IMonitorListener>();
	}

	public void save(String topic) {
		try {
			String file = m_path + File.separator + topic + ".wsp";
			m_out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			m_out.flush();
			m_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void flush() {
		try {
			m_out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void output(TweetInfo s) {
		try {
			m_out.writeObject(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
