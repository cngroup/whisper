package vis.vjit.demo.ui.timeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vis.vjit.tweeflow.Constant;

import davinci.data.elem.VisualNode;

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
public class Cursor extends VisualNode {

	private static final long serialVersionUID = -86181063846917475L;
	
	private int m_size = 3;
	
	private int m_sidx = 0;

	public Cursor() {
		m_size = Constant.TIME_WINDOW_SIZE;
	}
	
	public long start() {
		TimeSeries s = (TimeSeries)getOwner();
		return s.start() + m_sidx * s.interval();
	}
	
	public long duration() {
		TimeSeries s = (TimeSeries)getOwner();
		return s.interval() * m_size;
	}
	
	public long end() {
		//TimeSeries s = (TimeSeries)getOwner();
		//return s.start() + (m_sidx + m_size) * s.interval();
		return start() + duration();
	}
	
	public void setSize(int size) {
		m_size = size;
	}
	
	public int size() {
		return m_size;
	}
	
	int startIdx() {
		return m_sidx;
	}
	
	void setStartIdx(int idx) {
		m_sidx = idx;
	}
	
	public TimeBin[] bins() {
		List<TimeBin> bins = new ArrayList<TimeBin>();
		TimeSeries s = (TimeSeries)getOwner();
		TimeBin bin = null;
		Iterator<TimeBin> it = s.iterator();
		long start = start();
		long end = end();
		while(it.hasNext()) {
			bin = it.next();
			if(bin.start() >= end) {
				break;
			} else if(bin.start() >= start && bin.end() <= end) {
				bins.add(bin);
			}
		}
		return bins.toArray(new TimeBin[0]);
	}
	
	public Long[] tweets() {
		List<Long> bins = new ArrayList<Long>();
		TimeSeries s = (TimeSeries)getOwner();
		TimeBin bin = null;
		Iterator<TimeBin> it = s.iterator();
		long start = start();
		long end = end();
		while(it.hasNext()) {
			bin = it.next();
			if(bin.start() >= end) {
				break;
			} else if(bin.start() >= start && bin.end() <= end) {
				Long[] t = bin.toArray();
				for(int i = 0; i < t.length; ++i) {
					bins.add(t[i]);
				}
			}
		}
		return bins.toArray(new Long[0]);
	}
	
	public void recet() {
		m_sidx = 0;
	}
}
