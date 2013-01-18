
package vis.vjit.tweeflow.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import vis.vjit.demo.MediaType;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.VisUser;
import vis.vjit.tweeflow.util.GraphicsLib;
import vis.vjit.tweeflow.util.IconUtil;
import davinci.data.elem.IElement;
import davinci.data.elem.IVisualNode;
import davinci.rendering.ElemRender;
import davinci.rendering.IElemTheme;
import davinci.util.math.ExtMath;

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
public class WhisperNodeRender extends ElemRender {

	private Ellipse2D m_brush = null;

	private ImageIcon m_icon = null;

	private Ellipse2D m_leaves = null;

	private Rectangle2D m_rect = null;

	private Line2D m_line = null;

	private AffineTransform tx = null;

	private double ARROW = ExtMath.HALF_PI + Math.PI / 10;

	private Color m_ring = new Color(237, 207, 114);

	private BasicStroke m_sglyph = new BasicStroke(2.0f);

	public WhisperNodeRender() {
		m_brush = new Ellipse2D.Double();
		m_leaves = new Ellipse2D.Double();
		tx = new AffineTransform();
		m_line = new Line2D.Double();
		m_rect = new Rectangle2D.Double();
	}

	public Shape getRawShape(IElement elem) {
		IVisualNode node = (IVisualNode) elem;
		Shape s = (Shape) node.get("boundary");
		if (s == null) {
			double x = node.getX();
			double y = node.getY();
			double rx = node.getWidth() / 2f;
			double ry = node.getHeight() / 2f;
			if (node instanceof VisTweet
					&& (MediaType.isJournalists((VisTweet) node) 
						|| MediaType.isMedia((VisTweet) node))) {
				m_rect.setFrameFromCenter(x, y, x - rx, y - ry);
				s = m_rect;
			} else {
				m_leaves.setFrameFromCenter(x, y, x - rx, y - ry);
				s = m_leaves;
			}
		}
		return s;
	}

	public void render(Graphics2D g, IElement elem, IElemTheme theme,
			boolean highlight) {

		Shape s = getRawShape(elem);
		if (null == s) {
			return;
		}

		Rectangle r = s.getBounds();
		int x = r.x;
		int y = r.y;
		int w = r.width;
		int h = r.height;

		boolean bflag = false;
		double radii = 0;
		Color c = theme.getFillColor(elem);
		TwitterFlower flower = (TwitterFlower) m_owner.getData("flower");
		if ((elem instanceof VisUser)
				&& (flower.focus() == flower.getParent((VisUser) elem))) {
			VisUser user = (VisUser) elem;
			m_icon = IconUtil.getICON(user.user());
			bflag = true;
		}

		Stroke bs = null;
		if (null != m_icon) {
			w = m_icon.getIconWidth();
			h = m_icon.getIconHeight();
			int xx = (int) (r.getCenterX() - w / 2);
			int yy = (int) (r.getCenterY() - h / 2);
			m_icon.paintIcon(m_owner, g, xx, yy);
			bs = g.getStroke();
			g.setStroke(theme.getThickness(elem));
			g.setColor(c);
			g.drawRect(xx, yy, w, h);
			g.setStroke(bs);
			m_icon = null;
		} else {
			if (elem == flower.focus()) {
				VisTopic topic = (VisTopic) elem;
				int level = topic.getLevel();
				m_brush.setFrameFromCenter(topic.getX(), topic.getY(),
						topic.getX() + topic.getWidth() / 2.0 + level * 10,
						topic.getY() + topic.getHeight() / 2.0 + level * 10);

				g.setPaint(m_ring);
				g.fill(m_brush);
				bs = g.getStroke();
				g.setStroke(theme.getThickness(elem));
				g.setPaint(theme.getBorderColor(elem));
				g.draw(s);
				g.setStroke(bs);
			}
			if ((elem instanceof VisTweet) && ((VisTweet) elem).isRetweet()) {
				drawglyph(g, (VisTweet) elem, theme);
			} else {
				g.setPaint(c);
				g.fill(s);
				bs = g.getStroke();
				g.setStroke(theme.getThickness(elem));
				g.setPaint(theme.getBorderColor(elem));
				g.draw(s);
				g.setStroke(bs);
			}
		}

		if (elem instanceof VisTopic || bflag) {
			FlowerNode topic = (FlowerNode) elem;
			String name = elem.getLabel();
			if (null != name && !"".equals(name)) {
				if (!topic.isCollapsed()) {
					x = (int) r.getCenterX();
					y = (int) r.getMaxY() + ((VisTopic) topic).getLevel() * 8;
					g.setFont(theme.getLabelFont(elem));
					FontMetrics fm = g.getFontMetrics();
					g.setPaint(theme.getLabelColor(elem));
					int sh = (int) (y + (fm.getLeading() + fm.getAscent()));
					int sw = (int) (x - fm.stringWidth(name) / 2.0);
					g.drawString(name, sw, sh);
				} else if(elem instanceof VisUser) { 
					x = (int) r.getCenterX();
					y = (int) (r.getCenterY() + 22 + 5);
					g.setFont(theme.getLabelFont(elem));
					FontMetrics fm = g.getFontMetrics();
					g.setPaint(theme.getLabelColor(elem));
					int sh = (int) (y + (fm.getLeading() + fm.getAscent()));
					int sw = (int) (x - fm.stringWidth(name) / 2.0);
					g.drawString(name, sw, sh);
				} else {
					
					if("United States".equalsIgnoreCase(name)) {
						name = "USA";
					} else if("United Kingdom".equalsIgnoreCase(name)) {
						name = "UK";
					}
					
					FlowerNode group = (FlowerNode) elem;

					double xx = r.getCenterX();
					double yy = r.getCenterY();

					double cx = m_owner.getWidth() / 2.0;
					double cy = m_owner.getHeight() / 2.0;

					double angle = Math.atan2((yy - cy), (xx - cx))
							% ExtMath.DOUBLE_PI;
					if (angle < 0) {
						angle += ExtMath.DOUBLE_PI;
					}

					double rx = r.getWidth() / 2.0 + 5;
					if (group instanceof VisUser) {
						rx = radii + 5;
					}

					Graphics2D g2d = (Graphics2D) g.create();
					FontMetrics fm = g2d.getFontMetrics();
					g2d.setColor(theme.getLabelColor(elem));
					g2d.setFont(theme.getLabelFont(elem));
					int sw = fm.stringWidth(name);

					int sh = 0;
					sh = fm.getHeight() / 2;
					if (angle > Math.PI / 2 && angle <= 1.5 * Math.PI) {
						angle = Math.PI + angle;
						if (group instanceof VisUser) {
							rx = rx - sw - radii * 2 - 10;
						} else {
							rx = rx - sw - group.getWidth() - 10;
						}
					}

					tx.setToIdentity();
					tx.translate(xx, yy);
					tx.rotate(angle);
					g2d.setTransform(tx);
					g2d.drawString(name, (int) rx, (int) sh);
					g2d.dispose();
				}
			}
		}
	}

	private void drawglyph(Graphics2D g, VisTweet glyph, IElemTheme t) {

		TweeFlowVjit vjit = (TweeFlowVjit) m_owner;

		double len = glyph.getWidth();
		double cx = glyph.getX();
		double cy = glyph.getY();
		double angle = glyph.getAngle();
		double xx = len * Math.cos(ARROW);
		double y1 = len * Math.sin(ARROW);
		double y2 = -len * Math.sin(ARROW);

		if (!vjit.isDiffusion) {
			angle = Math.PI + angle;
		}

		tx.setToIdentity();
		tx.translate(cx, cy);
		tx.rotate(angle);

		AffineTransform bt = g.getTransform();
		Stroke bs = g.getStroke();
		g.setTransform(tx);
		g.setColor(t.getFillColor(glyph));
		g.setStroke(m_sglyph);
		m_line.setLine(0, 0, xx, y1);
		g.draw(m_line);
		m_line.setLine(0, 0, xx, y2);
		g.draw(m_line);

		// m_line.setLine(xx, y1, xx, y2);
		// g.draw(m_line);
		g.setStroke(bs);
		g.setTransform(bt);
	}
}
