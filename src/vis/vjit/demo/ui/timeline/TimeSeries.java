package vis.vjit.demo.ui.timeline;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.util.GraphicsLib;
import davinci.data.AbstractData;

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
public class TimeSeries extends AbstractData {

	private static final long serialVersionUID = -5588572077429391317L;

	private LinkedList<TimeBin> m_bins = null;
	
	private long m_start = 0;
	
	private long m_current = 0;
	
	private long m_duration = 46 * Constant.TIME_BIN_INTEVAL;
	
	private List<Point2D.Float> u_spline = null;
	
	private List<Point2D.Float> b_spline = null;
	
	private Cursor m_cursor = null;
	
	public TimeSeries() {
		m_bins = new LinkedList<TimeBin>();
		u_spline = new LinkedList<Point2D.Float>();
		b_spline = new LinkedList<Point2D.Float>();
		m_cursor = new Cursor();
		m_cursor.setOwner(this);
		int interval = Math.max(10, Constant.TIME_WINDOW_SIZE);
		m_duration = interval * Constant.TIME_BIN_INTEVAL;
	}
	
	public void addUpperCtrl(Point2D.Float pt) {
		u_spline.add(pt);
	}
	
	public void addBottomCtrl(Point2D.Float pt) {
		b_spline.add(0, pt);
	}
	
	public Point2D.Float[] b_spline() {
		return b_spline.toArray(new Point2D.Float[0]);
	}
	
	public Point2D.Float[] u_spline() {
		return u_spline.toArray(new Point2D.Float[0]);
	}
	
	public GeneralPath boundary() {
		Point2D.Float pp = b_spline.get(0);
		GeneralPath p = GraphicsLib.getSpline(u_spline());
		p.lineTo(pp.x, pp.y);
		p.append(GraphicsLib.getSpline(b_spline()), true);
		p.closePath();
		return p;
	}
	
	public void reset() {
		b_spline.clear();
		u_spline.clear();
	}
	
	public synchronized void add(long id, long time, double s) {	
		TimeBin bin = null;
		if(m_bins.isEmpty()) {
			bin = new TimeBin(time, Constant.TIME_BIN_INTEVAL);
			bin.setOwner(this);
			m_start = time;
			m_current = time + Constant.TIME_BIN_INTEVAL;
			m_bins.addFirst(bin);
			m_cursor.setStartIdx(0);
		} else {
			if(time < m_start) {
				int dd = (int)((m_start - time) / Constant.TIME_BIN_INTEVAL);
				if(dd == 0) {
					m_start = time;
					m_bins.peekFirst().setStartTime(time);
				} else {
					bin = new TimeBin(time, Constant.TIME_BIN_INTEVAL);
					m_start = time;
					m_bins.addLast(bin);
				}
			}
			//bin = m_items.getFirst();
			Iterator<TimeBin>it = m_bins.iterator();
			while(it.hasNext()) {
				bin = it.next();
				if(!bin.contains(time)) {
					bin = null;
				} else {
					break;
				}
			}
			if(bin == null) {
				bin = m_bins.getFirst();
				long t = bin.end() + ((long)((time - bin.end()) / Constant.TIME_BIN_INTEVAL)) * Constant.TIME_BIN_INTEVAL;
				bin = new TimeBin(t, Constant.TIME_BIN_INTEVAL);
				bin.setOwner(this);
				m_bins.addFirst(bin);
				m_current = t + Constant.TIME_BIN_INTEVAL;
				int idx = (int)((m_current - m_start) / Constant.TIME_BIN_INTEVAL - m_cursor.size());
				m_cursor.setStartIdx(idx < 0 ? 0 : idx);
			}
		}
		bin.add(id, s);
	}
	
	public void setWindowSize(int size) {
		long delta = m_start + m_cursor.startIdx() * Constant.TIME_BIN_INTEVAL + size * Constant.TIME_BIN_INTEVAL - m_current;
		if(delta < 0) {
			m_cursor.setSize(size);
		} else {
			int off = (int)(delta / Constant.TIME_BIN_INTEVAL);
			if(off > 0 && (m_cursor.startIdx() - off) >= 0) {
				m_cursor.setStartIdx(m_cursor.startIdx() - off);
				m_cursor.setSize(size);
			}
		}
	}
	
	public int getWindowSize() {
		return m_cursor.size();
	}
	
	public synchronized Iterator<TimeBin> iterator() {
		return m_bins.iterator();
	}
	
	public synchronized TimeBin[] toArray() {
		return m_bins.toArray(new TimeBin[0]);
	}
	
	public synchronized boolean remove(TimeBin bin) {
		return m_bins.remove(bin);
	}
	
	public synchronized boolean isEmpty() {
		return m_bins.isEmpty();
	}

	public long start() {
		return m_start;
	}
	
	public long current() {
		return m_current;
	}
	
	public long interval() {
		return Constant.TIME_BIN_INTEVAL;
	}
	
	public long duration() {
		m_duration = Math.max(m_duration, m_current - m_start);
		return m_duration;
	}
	
	public void clear() {
		super.clear();
		m_bins.clear();
		b_spline.clear();
		u_spline.clear();
		m_start = 0;
		m_current = 0;	
		m_cursor.recet();
	}
	
	public Cursor cursor() {
		return m_cursor;
	}
	
	public void foreward() {
		int sidx = m_cursor.startIdx();
		if(m_cursor.end() + Constant.TIME_BIN_INTEVAL > m_current) {
			return;
		}
		sidx ++;
		m_cursor.setStartIdx(sidx);
	}
	
	public void backward() {
		int sidx = m_cursor.startIdx();
		if(m_cursor.start() - Constant.TIME_BIN_INTEVAL < m_start) {
			return;
		}
		sidx --;
		m_cursor.setStartIdx(sidx);
	}
}
