package vis.vjit.demo.ui.timeline;

import java.util.Vector;

import twitter4j.Status;
import vis.vjit.tweeflow.io.IMonitorListener;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import davinci.Display;

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
public class TimelineVjit extends Display implements IMonitorListener {

	private static final long serialVersionUID = -5636638898358867440L;
	private TimelineLayout m_layout = null;
	
	private TimeSeries m_tseries = null;
	
	private Vector<ITimelineListener> m_listener = null;
		
	public TimelineVjit() {
		m_layout = new TimelineLayout();
		
		this.addLayout(m_layout);
		this.setElemFinder(new BinFinder());
		this.addAction(new TimelineAction());
		
		m_tseries = new TimeSeries();
		m_tseries.setID("timeseries");
		
		m_listener = new Vector<ITimelineListener>();
		
		this.addData(m_tseries);
		this.setDisplayRender(new TimelineRender());
		this.addElemRender("elems", new ItemRender());
		this.addElemTheme("elems", new ItemTheme());
	}
	
	public void addListener(ITimelineListener l) {
		m_listener.add(l);
	}
	
	public synchronized void fireStop() {
		for(ITimelineListener l : m_listener) {
			l.stop();
		}
	}
	
	public synchronized boolean fireSteping(long start, long duration) {
		for(ITimelineListener l : m_listener) {
			if(!l.step(start, duration)) {
				return false;
			}
		}
		return true;
	}

	public void statusPosted(Status s, GeoInfoV3 info, long time, double value) {
		m_tseries.add(s.getId(), time, value);
		doLayout();
		repaint();
	}
	
	public void update() {
		m_layout.updateCursor();
		repaint();
	}
	
	public void foreward() {
		Cursor c = m_tseries.cursor();
		m_tseries.foreward();
		if(!fireSteping(c.start(), c.duration())) {
			m_tseries.backward();
			return;
		}
		m_layout.updateCursor();
		repaint();
	}
	
	public void backward() {
		Cursor c = m_tseries.cursor();
		m_tseries.backward();
		if(!fireSteping(c.start(), c.duration())) {
			m_tseries.foreward();
			return;
		}
		m_layout.updateCursor();
		repaint();
	}
	
	public void setWindowSize(int size) {
		Cursor c = m_tseries.cursor();
		int ss = c.size();
		m_tseries.setWindowSize(size);
		if(!fireSteping(c.start(), c.duration())) {
			m_tseries.setWindowSize(ss);
			return;
		}
		m_layout.updateCursor();
		repaint();
	}
	
	public int getWindowSize() {
		Cursor c = m_tseries.cursor();
		return c.size();
	}
	
	public void clear() {
		m_tseries.clear();
		m_tseries.reset();
	}
}
