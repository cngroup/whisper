package vis.vjit.tweeflow.io;

import java.util.Vector;

import twitter4j.Status;
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

public class Dispatcher implements ITwitterDispatcher{

	protected boolean bshutdown = false;
	protected Vector<IMonitorListener> m_listeners = null;
	
	protected ITwitterSaver m_saver = null;
	
	public Dispatcher() {
		this(null);
	}
	
	public Dispatcher(ITwitterSaver s) {
		m_saver = s;
		m_listeners = new Vector<IMonitorListener>();
	}
	
	public void setSaver(ITwitterSaver saver) {
		m_saver = saver;
	}
	
	public ITwitterSaver getSaver() {
		return m_saver;
	}
	
	protected void save(String topic) {
		if(m_saver != null) {
			m_saver.save(topic);
		}
	}
	
	protected void output(TweetInfo info) {
		if(m_saver != null) {
			m_saver.output(info);
		}
	}
	
	protected void close() {
		if(m_saver != null) {
			m_saver.close();
		}
	}
	
	protected void flush() {
		if(m_saver != null) {
			m_saver.flush();
		}
	}
	
	public void addListener(IMonitorListener l) {
		m_listeners.add(l);
	}

	public void removeListener(IMonitorListener l) {
		m_listeners.remove(l);
	}

	public void fireStatusPosted(Status s, GeoInfoV3 info, long time, double sentiments) {
		for (IMonitorListener l : m_listeners) {
			l.statusPosted(s, info, time, sentiments);
		}
	}

	public synchronized boolean isDispatching() {
		return !bshutdown;
	}

	public void shutdown() {
		bshutdown = true;
		if(m_saver != null) {
			m_saver.flush();
			m_saver.close();
		}
	}
}
