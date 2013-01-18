package vis.vjit.demo.ui.info;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import twitter4j.Status;
import vis.vjit.tweeflow.io.IMonitorListener;
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
public class InfoList extends JList implements IMonitorListener {
	
	private static final long serialVersionUID = 613556908245548644L;
	
	private DefaultListModel m_model = null;
	
	private int capacity = 15;
	
	private class CellRender implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return (Component)m_model.get(index);
		}		
	}
	
	public InfoList() {
		this.m_model = new DefaultListModel();
		this.setModel(m_model);
		this.setBackground(Color.white);
		this.setCellRenderer(new CellRender());
		this.setPreferredSize(new Dimension(350, 250));
//		this.setBorder(BorderFactory.createEtchedBorder(1));
	}
	
	public void add(UserInfoItem item) {
		if(m_model.size() < capacity) {
			m_model.add(0, item);
		} else {
			m_model.remove(m_model.size() - 1);
			m_model.add(0, item);
		}
	}
	
	public void clear() {
		m_model.removeAllElements();
	}
	
	public void statusPosted(Status s, GeoInfoV3 info, long time, double value) {
		if(s.isRetweet()) {
			add(new UserInfoItem(s));
		}
	}
}
