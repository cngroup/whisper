package vis.vjit.tweeflow.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
public class FlowerNode extends VisualNode {

	private static final long serialVersionUID = 8567952796572291424L;

	protected float m_charge = 0;
	protected boolean bValid = true;	
	protected double m_longitude = 0;
	protected boolean isCollapsed = false;
	protected double m_sentiment = 0;
	
	protected int m_order = -1;
	
	private Vector<VisTube> m_tubes = null;

	public FlowerNode() {
		m_tubes = new Vector<VisTube>();
		isCollapsed = false;
	}
	
	public void setOrder(int order) {
		m_order = order;
	}
	
	public int getOrder() {
		return m_order;
	}
	
	public void setLongitude(double longitude) {
		m_longitude= longitude;
	}
	
	public double getLongitude() {
		return m_longitude;
	}
	
	public void setCharge(float q) {
		m_charge = q;
	}
	
	public float getCharge() {
		return m_charge;
	}
	
	public void invalidate() {
		bValid = false;
	}
	
	public void setValid(boolean bvalid) {
		this.bValid = bvalid;
	}
	
	public boolean isValid() {
		return bValid;
	}
	
	public void clear() {
		m_tubes.clear();
	}
	
	public synchronized VisTube tube(FlowerNode group) {
		VisTube tube = null;
		int size = m_tubes.size();
		for(int i = 0; i < size; ++i) {
			tube = m_tubes.get(i);
			if(tube.sink() == group) {
				return tube;
			}
		}
		return null;
	}
	
	public void setSentiment(double s) {
		m_sentiment = s;
	}
	
	public double getSentiment() {
		return m_sentiment;
	}
	
	public synchronized void addTube(VisTube tube) {
		m_tubes.add(tube);
	}
	
	public synchronized void removeTube(VisTube tube) {
		m_tubes.remove(tube);
	}
	
	public synchronized VisTube[] getTubes() {
		TwitterFlower flower = (TwitterFlower)m_owner;
		List<VisTube> tubes = new ArrayList<VisTube>();
		VisTube[] tbs = m_tubes.toArray(new VisTube[]{});
		for(int i = 0; i < tbs.length; ++i) {
			if(flower.accept(tbs[i].source())) {
				tubes.add(tbs[i]);
			}
		}
		return tubes.toArray(new VisTube[0]);
	}
	
	synchronized VisTube getTube(int idx) {
		return m_tubes.get(idx);
	}
	
	synchronized int getTubeCnt() {
		return m_tubes.size();
	}
	
	public boolean isCollapsed() {
		return isCollapsed;
	}
	
	public void setCollapse(boolean c) {
		isCollapsed = c;
	}
}
