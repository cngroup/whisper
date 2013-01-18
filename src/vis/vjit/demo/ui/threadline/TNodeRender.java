
package vis.vjit.demo.ui.threadline;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import vis.vjit.tweeflow.data.VisUser;
import davinci.data.elem.IElement;
import davinci.data.elem.IVisualNode;
import davinci.rendering.ElemRender;
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
public class TNodeRender extends ElemRender {

	private Ellipse2D m_leaves = null;
	private AffineTransform m_at = null;
	private double alpha = Math.PI / 6;
	private double cosalpha = Math.cos(alpha);
	private double sinalpha = Math.sin(alpha);

	public TNodeRender() {
		m_leaves = new Ellipse2D.Double();
		m_at = new AffineTransform();
	}

	public Shape getRawShape(IElement elem) {
		IVisualNode node = (IVisualNode) elem;
		double x = node.getX();
		double y = node.getY();
		double rx = node.getWidth() / 2f;
		double ry = node.getHeight() / 2f;
		m_leaves.setFrameFromCenter(x, y, x - rx, y - ry);
		return m_leaves;
	}

	public void render(Graphics2D g, IElement elem, IElemTheme theme,
			boolean highlight) {

		Shape s = getRawShape(elem);
		if (null == s) {
			return;
		}

		Color c = theme.getFillColor(elem);

		Stroke bs = null;
		g.setPaint(c);
		g.fill(s);
		bs = g.getStroke();
		g.setStroke(theme.getThickness(elem));
		g.setPaint(theme.getBorderColor(elem));
		g.draw(s);
		g.setStroke(bs);
		
		VisUser user = (VisUser)elem;
		
		ThreadLine graph = (ThreadLine)m_owner.getData("flow");
		if(graph == null || graph.isEmpty()) {
			return;
		}
		
		VisUser vuser = graph.getNode(0);
		if(vuser != user) {
			return;
		}
		
		String name = elem.getLabel();
		if (null != name && !"".equals(name)) {
			g.setFont(theme.getLabelFont(elem));
			FontMetrics fm = g.getFontMetrics();
			double x = user.getX();
			double y = user.getY();
			double ww = fm.stringWidth(name);
//			double r = user.getWidth() / 2.0 + 5;
//			m_at.setToIdentity();
//			m_at.translate(x, y);
//			m_at.rotate(alpha);
			g.setPaint(theme.getLabelColor(elem));
//			AffineTransform bat = g.getTransform();
//			g.setTransform(m_at);
//			g.drawString(name, (int)(r * cosalpha), (int)(r * sinalpha));
//			g.setTransform(bat);
			g.drawString(name, (int)(x - ww / 2.0), (int)(y + fm.getHeight()));
		}
	}

//	private void drawglyph(Graphics2D g, VisTweet glyph, IElemTheme t) {
//
//		TweeFlowVjit vjit = (TweeFlowVjit) m_owner;
//
//		double len = glyph.getWidth() / 2;
//		double cx = glyph.getX();
//		double cy = glyph.getY();
//		double angle = glyph.getAngle();
//		double xx = len * Math.cos(ARROW);
//		double y1 = len * Math.sin(ARROW);
//		double y2 = -len * Math.sin(ARROW);
//
//		if (!vjit.isDiffusion) {
//			angle = Math.PI + angle;
//		}
//
//		tx.setToIdentity();
//		tx.translate(cx, cy);
//		tx.rotate(angle);
//
//		AffineTransform bt = g.getTransform();
//		Stroke bs = g.getStroke();
//		g.setTransform(tx);
//		g.setColor(t.getFillColor(glyph));
//		g.setStroke(m_sglyph);
//		m_line.setLine(0, 0, xx, y1);
//		g.draw(m_line);
//		m_line.setLine(0, 0, xx, y2);
//		g.draw(m_line);
//
//		// m_line.setLine(xx, y1, xx, y2);
//		// g.draw(m_line);
//		g.setStroke(bs);
//		g.setTransform(bt);
//
//	}
}
