package vis.vjit.tweeflow.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vis.vjit.demo.UIConfig;
import vis.vjit.demo.test.Frame;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.action.DefaultTooltip;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.IWhisperElem;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.util.time.TimeHelper;
import davinci.data.elem.IVisualNode;
import davinci.rendering.DisplayRender;
import davinci.rendering.IElemRender;
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
public class WhisperVjitRender extends DisplayRender {

	private Ellipse2D m_ellipse = null;
	private Line2D m_line = null;
	private AffineTransform m_at = null;

	private DefaultTooltip m_tooltip = null;

	private List<IVisualNode> m_highlight = null;

	private Stroke m_stroke = null;

	private Font m_font = new Font("Arial", Font.PLAIN, 11);

	private Color m_disc_dark = new Color(255, 247, 188, (int)(0.3 * 255));//new Color(255, 255, 204, (int) (0.55 * 255));
	
	private Color m_disc_light = new Color(245, 245, 245, (int)(0.25 * 255));

	private TweeFlowVjit m_vjit = null;

	public WhisperVjitRender() {
		m_ellipse = new Ellipse2D.Double();
		m_line = new Line2D.Double();
		m_at = new AffineTransform();
		m_highlight = new ArrayList<IVisualNode>();
//		m_stroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
//				BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
//		m_sdf.applyPattern("yyyy-MM-dd, HH:mm:ss z");
		m_stroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	}

	public synchronized void render(Graphics2D g) {

		try {
			TwitterFlower flower = (TwitterFlower) m_owner.getData("flower");
			if (null == flower) {
				return;
			}
			
			long t1 = System.currentTimeMillis();
			
			m_vjit = (TweeFlowVjit)m_owner;
			double cx = m_owner.getWidth() / 2;
			double cy = m_owner.getHeight() / 2;
			double rr = Math.min(m_owner.getWidth(), m_owner.getHeight());
			double radii = 0.8 * rr / 2;
			m_ellipse.setFrameFromCenter(cx, cy, cx + radii, cy + radii);
			if(UIConfig.isWhite()) {
				g.setColor(m_disc_dark);
			} else {
				g.setColor(m_disc_light);
			}
			g.fill(m_ellipse);
			if(UIConfig.isWhite()) {
				g.setColor(Color.gray);
			} else {
				g.setColor(Color.lightGray);
			}
			g.draw(m_ellipse);

			VisTopic topic = flower.focus();
			renderTimeRing(g, flower, radii);

			if (((TweeFlowVjit) m_owner).isEncodeLongitude) {
				g.setColor(Color.gray);
				radii = 0.9 * Math.min(m_owner.getWidth() / 2,
						m_owner.getHeight() / 2);
				m_ellipse.setFrameFromCenter(cx, cy, cx + radii, cy + radii);
				g.draw(m_ellipse);
				int n = 8;
				double step = ExtMath.DOUBLE_PI / 8;
				double angle = 0;
				double x1 = 0, x2 = 0;
				double y1 = 0, y2 = 0;
				for (int i = 0; i < n; ++i) {
					angle = ExtMath.HALF_PI - i * step;
					x1 = cx + (radii - 10) * Math.cos(angle);
					y1 = cy + (radii - 10) * Math.sin(angle);
					x2 = cx + radii * Math.cos(angle);
					y2 = cy + radii * Math.sin(angle);
					m_line.setLine(x1, y1, x2, y2);
					if(UIConfig.isWhite()) {
						g.setColor(Color.lightGray);
					} else {
						g.setColor(Color.gray);
					}
					g.draw(m_line);

					double rx = 5;
					int label = (int) ((ExtMath.HALF_PI - angle) * 180 / Math.PI);
					if (label > 180) {
						label = label - 360;
					}

					String name = label == 180 ? "+/-" + label : "" + label;
					Graphics2D g2d = (Graphics2D) g.create();
					int sh = 0;
					FontMetrics fm = g2d.getFontMetrics();
					sh = fm.getHeight() / 2;
					if (-angle > Math.PI / 2 && -angle <= 1.5 * Math.PI) {
						int sw = fm.stringWidth(name);
						angle = Math.PI + angle;
						rx = rx - sw - 10;
					}
					m_at.setToIdentity();
					m_at.translate(x2, y2);
					m_at.rotate(angle);
					g2d.setTransform(m_at);
					if(UIConfig.isWhite()) {
						g2d.setColor(Color.lightGray);
					} else {
						g2d.setColor(Color.gray);
					}
					g2d.drawString(name, (int) rx, (int) sh);
					g2d.dispose();
				}
			}

			render(g, topic);
			
			IElemRender r = m_owner.getElemRender("edges");
			IElemTheme t = m_owner.getElemTheme("edges");

			VisTweet[] tweets = topic.active();
			for (int i = 0; i < tweets.length; ++i) {
				int size = tweets[i].getTubeSize();
				for (int j = 0; j < size; ++j) {
					VisTube tube = tweets[i].getTube(j);
					if (!tube.isVisible()) {
						continue;
					}
					r.render(g, tube, t, tube.isHighlight() || tube.isFocused());
					VisTweet glyph = null;
					Iterator<VisTweet> it = tube.iterator();
					while (it.hasNext()) {
						glyph = it.next();
						render(g, glyph);
					}
				}
			}

			FlowerNode[] groups = flower.getChildren(flower.focus());
			if (null != groups) {
				for (int i = 0; i < groups.length; ++i) {
					render(g, groups[i]);
				}
			}
			FlowerNode[] users = flower.leaves(flower.focus(),
					new FlowerNode[0]);
			if (null != users) {
				for (int i = 0; i < users.length; ++i) {
					render(g, users[i]);
				}
			}
			
			tweets = topic.toArray();
			for (int i = 0; i < tweets.length; ++i) {
				render(g, tweets[i]);
			}

			if (m_tooltip == null) {
				m_tooltip = new DefaultTooltip(m_owner);
			}

			int size = m_highlight.size();
			IVisualNode node = null;
			for (int i = 0; i < size; ++i) {
				node = m_highlight.get(i);
				if(node instanceof IWhisperElem) {
					m_tooltip.setElem(node);
					m_tooltip.setAnchor(node);
					m_tooltip.render(g);
				}
			}
			m_highlight.clear();
			
			Frame.render_cost += (System.currentTimeMillis() - t1);
			Frame.render_count ++;
			
		} catch (Exception e) {
			System.err.println("unsychronized rendering occurs");
		}
	}

	private void renderTimeRing(Graphics2D g, TwitterFlower flower, double max) {
		VisTopic topic = flower.topic();
		double min = topic.getWidth() / 2.0 + topic.getLevel() * 10 + 10;
		long duration = (Constant.ACTIVE_TWEET_LIFE * Constant.TIME_INTERVAL) / 1000;
//		long duration = 24 * 3600;
		double step = 0;
		int size = 6;
		String unit = "6";
		String label = "";
		double lunite = 0;
		
		lunite = step = (int)(duration / size);
		
		if(duration <= 60) {
			unit = "secs";
		} else if (duration <= 3600){
			unit = "mins";
			lunite = lunite / 60;
		} else if (duration <= 24 * 3600) {
			unit = "hours";
			lunite = lunite / 3600;
		} else {
			unit = "days";
			lunite = lunite / (24 * 3600);
		}
		
		TimeHelper.applyPattern(TimeHelper.PATTERN_HHMMSSZ);
		label = TimeHelper.format(topic.getTime());

		double cx = topic.getX(), cy = topic.getY();
		double radii = 0;
		if(UIConfig.isWhite()) {
//			g.setColor(new Color(200, 200, 200));
			g.setColor(new Color(210, 210, 210));
		} else {
			g.setColor(Color.gray);
		}
		for (int i = 0; i <= size; ++i) {
			radii = min + ((max - min) * i * step) / duration;
			m_ellipse.setFrameFromCenter(cx, cy, cx + radii, cy + radii);
			Stroke bs = g.getStroke();
			g.setStroke(m_stroke);
			g.draw(m_ellipse);
			g.setStroke(bs);
			if(i > 0) {
				label = String.format("%s%3.1f %s", "+", i * lunite, unit);
			}
			g.setFont(m_font);
			FontMetrics fm = g.getFontMetrics();
			int sh = fm.getHeight() / 2;
			int sw = fm.stringWidth(label);
			double xx = cx;
			double yy = cy - radii + sh;
			xx = cx - sw / 2;
			g.drawString(label, (int) xx, (int) yy);
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
		if (node.isFocused()) {
			m_highlight.add(node);
		}

		r.render(g, node, t, node.isHighlight() || node.isFocused());
	}
}
