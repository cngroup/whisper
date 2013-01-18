package vis.vjit.demo.ui.timeline;

import java.awt.event.MouseEvent;

import davinci.data.elem.IElement;
import davinci.interaction.ActionAdapter;

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
public class TimelineAction extends ActionAdapter {

	public TimelineAction() {
	}
	
	public void elemEntered(IElement e, MouseEvent evn) {
		e.setHighlight(true);
		m_owner.repaint();
	}
	
	public void elemExited(IElement e, MouseEvent evn) {
		e.setHighlight(false);
		m_owner.repaint();
		BinFinder finder = (BinFinder)m_owner.getElemFinder();
		finder.setFindCursor(true);
	}
	
	public void elemDragged(IElement e, MouseEvent evn) {
		if(!(e instanceof Cursor)) {
			return;
		}
		TimelineVjit vjit = (TimelineVjit)m_owner;
		TimeSeries s = (TimeSeries)m_owner.getData("timeseries");
		BinFinder finder = (BinFinder)m_owner.getElemFinder();
		finder.setFindCursor(false);
		TimeBin ee = (TimeBin)finder.find(evn.getX(), evn.getY());
		if(ee == null) {
			return;
		}
		finder.setFindCursor(true);
		Cursor c = (Cursor)e;
		if(c.start() >= ee.end()) {
			vjit.backward();
		} else if(c.end() <= ee.start()){
			vjit.foreward();
		}
		vjit.update();
	}
	
}
