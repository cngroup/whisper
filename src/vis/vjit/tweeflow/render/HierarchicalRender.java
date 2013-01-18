package vis.vjit.tweeflow.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import davinci.data.elem.IVisualNode;
import davinci.rendering.DisplayRender;
import davinci.rendering.IElemRender;
import davinci.rendering.IElemTheme;

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
public class HierarchicalRender extends DisplayRender {

	private Ellipse2D m_ellipse = null;
	
	public HierarchicalRender() {
		m_ellipse = new Ellipse2D.Double();
	}
	
	public void render(Graphics2D g) {
		TwitterFlower flower = (TwitterFlower) m_owner.getData("flower");
		if (null == flower) {
			return;
		}
		IElemRender r = m_owner.getElemRender("edges");
		IElemTheme t = m_owner.getElemTheme("edges");
		render(g, flower, flower.topic(), r, t);
	}
	
	private void render(Graphics2D g, TwitterFlower flower, FlowerNode node, IElemRender r, IElemTheme t) {
		
		if(!node.isCollapsed()) {
			
			VisTopic topic = (VisTopic)node;
			
			m_ellipse.setFrameFromCenter(topic.getX(), topic.getY(), topic.getX() + topic.getRadii(), topic.getRadii() + topic.getY());
			g.setColor(new Color(1, 1, 0.6f, 0.5f));
			g.fill(m_ellipse);
			
						
			VisTweet[] tweets = topic.active();
			for (int i = 0; i < tweets.length; ++i) {
				int size = tweets[i].getTubeSize();
				for (int j = 0; j < size; ++j) {
					VisTube tube = tweets[i].getTube(j);
					r.render(g, tube, t, tube.isHighlight() || tube.isFocused());
					VisTweet glyph = null;
					Iterator<VisTweet> it = tube.iterator();
					while (it.hasNext()) {
						glyph = it.next();
						render(g, glyph);
					}
				}
			}
			
			render(g, topic);
			
			tweets = topic.toArray();
			for (int i = 0; i < tweets.length; ++i) {
				render(g, tweets[i]);
			}
			
			FlowerNode[] groups = flower.getChildren(topic);
			if(groups != null) {
				for (int i = 0; i < groups.length; ++i) {
					render(g, flower, groups[i], r, t);
				}
			}
		} else {
			render(g, node);
			FlowerNode[] users = flower.leaves(node, new FlowerNode[0]);
			if (null != users) {
				for (int i = 0; i < users.length; ++i) {
					render(g, users[i]);
				}
			}
		}
	}
	
	private void render(Graphics2D g, IVisualNode node) {
		IElemRender r = m_owner.getElemRender(node.getID());
		if (r == null) {
			r = m_owner.getElemRender("nodes");
		}
		IElemTheme t = m_owner.getElemTheme(node.getID());
		if (t == null) {
			t = m_owner.getElemTheme("nodes");
		}
		r.render(g, node, t, node.isHighlight() || node.isFocused());
	}

}
