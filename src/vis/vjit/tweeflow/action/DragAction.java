
package vis.vjit.tweeflow.action;

import java.awt.event.MouseEvent;

import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.layout.TweeFlowerLayout;
import davinci.data.elem.IElement;
import davinci.data.elem.IVisualNode;
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
public class DragAction extends ActionAdapter {

	private double off_x = 0, off_y = 0;
	
	private boolean bDrag = false;
	
	public DragAction() {
	}
	
	public void elemPressed(IElement elem, MouseEvent e) {
		
		if(e.getButton() != 3) {
			return;
		}
		
		bDrag = true;
	}
	
	public void elemDragged(IElement elem, MouseEvent e) {
		
		if(!bDrag) {
			return;
		}
		
		TwitterFlower flower = (TwitterFlower)m_owner.getData("flower");
		if(null == flower) {
			return;
		}
		VisTopic focus = flower.focus();
		if(elem == focus || !(elem instanceof VisTopic)) {
			return;
		}
		IVisualNode node = (IVisualNode)elem;
		int x = e.getX();
		int y = e.getY();
		
		double cx = m_owner.getWidth() / 2.0;
		double cy = m_owner.getHeight() / 2.0;
		double angle = Math.atan2((y - cy), (x - cx));
		double radii = 0.8 * Math.min(cx, cy);
		double xx = cx + radii * Math.cos(angle);
		double yy = cy + radii * Math.sin(angle);
		node.setX(xx);
		node.setY(yy);
		node.updateLocation(1);
		if(node instanceof FlowerNode) {
			TweeFlowerLayout layout = (TweeFlowerLayout)m_owner.getLayout("TweeFlowerLayout");
			layout.sink((FlowerNode)node);
			layout.timeline();
		}
		m_owner.repaint();
	}
	
	public void elemReleased(IElement elem, MouseEvent e) {
		bDrag = false;
	}
}
