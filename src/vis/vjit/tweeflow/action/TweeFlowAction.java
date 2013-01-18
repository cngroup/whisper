package vis.vjit.tweeflow.action;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.VisUser;
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
public class TweeFlowAction extends ActionAdapter {
	
	public List<VisTweet> m_highlight = null;
	
	public TweeFlowAction() {
		setGeneralAction(true);
		m_highlight = new ArrayList<VisTweet>();
	}
	
	public void elemPressed(IElement e, MouseEvent evn) {
		if(evn.getButton() != 1) {
			return;
		}
		
		TweeFlowVjit vjit = (TweeFlowVjit)m_owner;		
		TwitterFlower flower = (TwitterFlower)m_owner.getData("flower");
		if((e instanceof FlowerNode && e != flower.focus())) {
			FlowerNode pre = (FlowerNode)e;
			VisTopic p = (VisTopic)flower.getParent((FlowerNode)pre);
			VisTopic topic = flower.focus();
			while(p != topic && p != null) {
				pre = p;
				p = (VisTopic)flower.getParent(pre);
			}
			if((pre instanceof VisTopic) && ((VisTopic)pre).isCollapsed()) {
				vjit.zoom((VisTopic)pre);
			}
		} else if(e instanceof VisTweet && ((VisTweet)e).isActive()) {
			if(!m_highlight.contains(e)) {
				m_highlight.add((VisTweet)e);
				vjit.highlight(m_highlight.toArray(new VisTweet[]{}));
			} else {
				m_highlight.remove(e);
				vjit.highlight(m_highlight.toArray(new VisTweet[]{}));
			}
		}
	}
	
	public void elemReleased(IElement e, MouseEvent evn) {
	}
	
	public void elemEntered(final IElement e, MouseEvent evn) {
		if(!(e instanceof VisTweet) && !(e instanceof VisUser)) {
			return;
		}
		TweeFlowVjit vjit = (TweeFlowVjit)m_owner;
		if((e instanceof VisTweet)) {
			if(((VisTweet)e).isActive()) {
				vjit.highlight((VisTweet)e);
			}
		} else {
			e.setHighlight(true);
			vjit.repaint();
		}
	}
	
	public void elemExited(IElement e, MouseEvent evn) {
		if(e instanceof VisTweet || e instanceof VisUser ) {
			e.setHighlight(false);
			m_owner.repaint();
		}
	}
	
	public void mousePressed(final MouseEvent evn) {
		final TweeFlowVjit vjit = (TweeFlowVjit)m_owner;
		if(evn.getButton() == 3) {
			if(evn.getClickCount() == 2) {
				vjit.doLayout();
			} else {
				m_highlight.clear();
				vjit.delight();
			}
			return;
		} else if(evn.getButton() == 1) {
			vjit.zoomOut();
		}
	}
}
