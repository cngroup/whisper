package vis.vjit.tweeflow;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import davinci.Display;
import davinci.IElemFinder;
import davinci.data.elem.IElement;
import davinci.rendering.IElemRender;

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
public class FlowerElemFinder implements IElemFinder {

	private TweeFlowVjit m_vjit = null;

	private Ellipse2D m_ellipse = null;

	public FlowerElemFinder() {
		m_ellipse = new Ellipse2D.Double();
	}

	public void setOwner(Display disp) {
		m_vjit = (TweeFlowVjit) disp;
	}

	public Display getOwner() {
		return m_vjit;
	}

	public IElement find(double x, double y) {
		TwitterFlower flower = (TwitterFlower) m_vjit.getData("flower");
		if (null == flower) {
			return null;
		}
		
		if(!flower.tryLock()) {
			return null;
		}

		IElement target = null;
		IElemRender r = m_vjit.getElemRender("nodes");
		VisTopic topic = flower.focus();
		int radii = topic.getLevel() * 10;
		m_ellipse.setFrameFromCenter(topic.getX(), topic.getY(), topic.getX()
				+ topic.getWidth() / 2.0 + radii, topic.getY() + topic.getHeight()
				/ 2.0 + radii);
		Shape s = null;
		if (m_ellipse.contains(x, y)) {
			target = topic;
			VisTweet[] tweets = topic.toArray();
			for (int i = 0; i < tweets.length; ++i) {
				s = r.getRawShape(tweets[i]);
				if (s.contains(x, y)) {
					target = tweets[i];
					break;
				}
			}
		} else {
			FlowerNode[] empty = new FlowerNode[0];
			FlowerNode[] children = flower.getChildren(topic);
			if (children != null) {
				for (int i = 0; i < children.length; ++i) {
					s = r.getRawShape(children[i]);
					if (s.contains(x, y)) {
						target = children[i];
						if (!flower.isLeaf(children[i])) {
							FlowerNode[] leaves = flower.leaves(children[i],
									empty);
							for (int j = 0; j < leaves.length; ++j) {
								s = r.getRawShape(leaves[j]);
								if (s.contains(x, y)) {
									target = leaves[j];
									break;
								}
							}
						}
						break;
					}
				}
			}
			
			if(target == null) {
				VisTube tube = null;
				VisTweet[] tweets = flower.focus().active();
				for(int i = 0; i < tweets.length; ++i) {
					int tsize = tweets[i].getTubeSize();
					for(int j = 0; j < tsize; ++j) {
						tube = tweets[i].getTube(j);
						VisTweet[] glyphs = tube.getTweets();
						for(int k = 0; k < glyphs.length; ++k) {
							s = r.getRawShape(glyphs[k]);
							if(s.contains(x, y)) {
								target = glyphs[k];
								break;
							}
						}
					}
				}
			}
		}
		flower.unlock();
		return target;
	}
}
