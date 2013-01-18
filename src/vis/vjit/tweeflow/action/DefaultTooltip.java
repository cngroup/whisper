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
package vis.vjit.tweeflow.action;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.Icon;

import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.VisUser;
import vis.vjit.tweeflow.util.IconUtil;
import vis.vjit.tweeflow.util.InfoVisUtil;
import vis.vjit.tweeflow.util.time.TimeHelper;
import davinci.Display;
import davinci.data.elem.IElement;
import davinci.data.elem.IVisualNode;
import davinci.rendering.DisplayRender;

public class DefaultTooltip extends DisplayRender {

	protected String m_text = "";

	protected AttributedString m_textAttr;

	protected Display m_vjit = null;

	protected boolean bVisible = true;

	protected Icon m_icon = null;

	protected Point2D.Float m_pen = null;

	protected Rectangle2D.Double m_bounds = null;

	protected IVisualNode m_elem = null;

	protected boolean bDataMapping = true;

	Paint background = null;

	Paint foreground = null;

	Paint bordercolor = null;

	Stroke borderthiness = null;

	Font font = null;

	protected StringBuffer m_buff = null;

	private boolean btop = false;

	public DefaultTooltip(Display vjit) {
		m_vjit = vjit;
		m_pen = new Point2D.Float();
		m_bounds = new Rectangle2D.Double();
		m_bounds.width = 100;

		background = new Color(0.98f, 0.98f, 0.98f, 1f);
		foreground = Color.black;
		font = TweeFlowVjit.isJapan ? new Font("GulimChe", Font.PLAIN, 12) : new Font("Verdana", Font.PLAIN, 12);
		borderthiness = new BasicStroke(1.0f);
		bordercolor = new Color(0.5f, 0.5f, 0.5f);
		m_buff = new StringBuffer();
	}

	public IElement getAnchor() {
		return m_elem;
	}

	public void enableDataMapping(boolean mapping) {
		bDataMapping = mapping;
	}

	public boolean isDataMappingEnabled() {
		return bDataMapping;
	}

	public void setBounds(double x, double y, double w, double h) {
		m_bounds.setFrame(x, y, w, h);
	}

	public Rectangle2D getBounds() {
		return m_bounds;
	}

	public void setIcon(Icon icon) {
		m_icon = icon;
		if (m_icon == null) {
			return;
		}

		m_bounds.width = icon.getIconWidth() + 4;

		if (m_bounds.height < icon.getIconHeight()) {
			m_bounds.height = icon.getIconHeight() + 4;
		}
	}

	public void setElem(IVisualNode node) {
		if (m_elem != node) {
			m_elem = node;
			initialize(InfoVisUtil.DEFAULT_GRAPHICS);
		}
	}

	public void setAnchor(IVisualNode node) {
		m_bounds.x = node.getX() + node.getWidth() / 2.0f;
		m_bounds.y = node.getY() - node.getHeight() / 2.0f;
		int cw = m_vjit.getWidth();
		int ch = m_vjit.getHeight();
		if (m_bounds.x + m_bounds.width > cw) {
			m_bounds.x -= m_bounds.width;
			m_bounds.x -= node.getWidth() + 5;
		}
		if (m_bounds.y + m_bounds.height > ch) {
			m_bounds.y -= m_bounds.height;
			m_bounds.y -= node.getHeight() + 5;
		}
	}

	public void setAnchor(double x, double y, double w, double h) {
		m_bounds.x = x;
		m_bounds.y = y;
		int cw = m_vjit.getWidth();
		int ch = m_vjit.getHeight();
		if (m_bounds.x + m_bounds.width > cw) {
			m_bounds.x -= m_bounds.width;
			m_bounds.x -= w;
		}
		if (m_bounds.y + m_bounds.height > ch) {
			m_bounds.y -= m_bounds.height;
			m_bounds.y -= h;
		}
	}

	private boolean initialize(Graphics2D context) {
		try {
			if (null == m_elem) {
				return false;
			}
			m_icon = null;
			// prepare icons
			if (m_elem instanceof VisUser) {
				VisUser user = (VisUser) m_elem;
				m_icon = IconUtil.getICON(user.user());
				btop = true;
			} else if (m_elem instanceof VisTweet) {
				VisTweet tweet = (VisTweet) m_elem;
				m_icon = IconUtil.getICON(tweet.status().getUser());
				btop = false;
			}
			if (null != m_icon) {
				if (btop) {
					m_bounds.width = m_icon.getIconWidth() + 2;
					if (m_bounds.height < m_icon.getIconHeight()) {
						m_bounds.height = m_icon.getIconHeight() + 4;
					}
				} else {
					m_bounds.width = m_icon.getIconWidth() + 2 + 150;
					if (m_bounds.height < m_icon.getIconHeight()) {
						m_bounds.height = m_icon.getIconHeight() + 4;
					}
				}
			} else {
				m_bounds.width = 150;
			}

			if (m_elem instanceof VisTweet) {
				VisTweet tweet = (VisTweet) m_elem;
				TimeHelper.applyPattern(TimeHelper.PATTERN_DATE);
				String time = TimeHelper.format(tweet.status().getCreatedAt().getTime());
				m_text = String.format("%s[%s] : %s", tweet.status().getUser()
						.getName(), time,
						tweet.status().getText());
//				m_text = GraphicsLib.wraplabel(m_text, true, false, 60)[0];
			} else if (m_elem instanceof VisUser) {
				VisUser user = (VisUser) m_elem;
				m_text = user.user().getName();
			} else if (m_elem instanceof VisTweet) {
				VisTweet tweet = (VisTweet) m_elem;
				m_buff.append(String.format("-[%s : %s]\n", tweet.getTime(),
						tweet.status().getUser().getName()));
				m_text = m_buff.toString();
				m_buff.delete(0, m_buff.length());
			} else {
				m_text = "";
			}

			if (null == m_text || "".equals(m_text)) {
				return false;
			}

			m_textAttr = new AttributedString(m_text);
			context.setFont(font);

			m_bounds.width += 2;

			int cw = m_vjit.getWidth();
			int ch = m_vjit.getHeight();
			if (m_bounds.x + m_bounds.width > cw) {
				m_bounds.x -= m_bounds.width;
			}

			m_textAttr.addAttribute(TextAttribute.FONT, font);
			AttributedCharacterIterator charIterator = m_textAttr.getIterator();
			LineBreakMeasurer measurer = new LineBreakMeasurer(charIterator,
					context.getFontRenderContext());

			float strWidth = btop && m_icon != null ? (float) m_icon.getIconWidth() : 150;
			if (strWidth < 0) {
				return false;
			}

			float iconHeight = m_icon == null ? 0 : m_icon.getIconHeight();

			TextLayout layout = measurer.nextLayout(strWidth);
			if (btop) {
				m_pen.y = iconHeight + 2;
			} else {
				m_pen.y = 2;
			}

			float h = 5;
			while (null != layout
					&& measurer.getPosition() <= charIterator.getEndIndex()) {
				h = layout.getDescent() + layout.getLeading()
						+ layout.getAscent();
				m_pen.y += h;
				layout = measurer.nextLayout(strWidth);
			}
			m_bounds.height = m_pen.y + h / 2f;

			if (!btop && m_bounds.height < iconHeight) {
				m_bounds.height = iconHeight + 2;
			}
			if (m_bounds.y + m_bounds.height > ch) {
				m_bounds.y -= m_bounds.height;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void render(Graphics2D g) {

		if (m_text == null || "".equals(m_text)) {
			return;
		}

		m_bounds.x = m_bounds.x + 2;
		m_bounds.y = m_bounds.y + 2;

		if (background != null) {
			g.setPaint(background);
			g.fill(m_bounds);
		}
		if (borderthiness != null) {
			Stroke s = g.getStroke();
			g.setStroke(borderthiness);
			if (bordercolor != null) {
				g.setPaint(bordercolor);
			}
			g.draw(m_bounds);
			g.setStroke(s);
		}

		if (font != null) {
			g.setFont(font);
		}
		if (foreground != null) {
			g.setPaint(foreground);
		}

		if (m_text == null || m_text.length() == 0) {
			return;
		}

		if (m_icon != null) {
			g.draw3DRect((int) (m_bounds.x + 2), (int) (m_bounds.y + 2),
					m_icon.getIconWidth(), m_icon.getIconHeight(), true);
			m_icon.paintIcon(m_vjit, g, (int) (m_bounds.x + 2),
					(int) (m_bounds.y + 2));
		} else {
			m_bounds.width = 150;
		}

		m_textAttr.addAttribute(TextAttribute.FONT, font);
		AttributedCharacterIterator charIterator = m_textAttr.getIterator();
		LineBreakMeasurer measurer = new LineBreakMeasurer(charIterator,
				g.getFontRenderContext());

		float iconHeight = m_icon == null ? 0 : m_icon.getIconHeight();
		float iconWidth = m_icon == null ? 0 : m_icon.getIconWidth();

		float strWidth = btop ? iconWidth : 150;

		if (strWidth < 0) {
			return;
		}

		TextLayout layout = measurer.nextLayout(strWidth);
		if (btop) {
			m_pen.x = 2;
			m_pen.y = iconHeight + 2;
		} else {
			m_pen.x = iconWidth + 5;
			m_pen.y = 2;
		}
		float h = 0;
		while (null != layout
				&& measurer.getPosition() <= charIterator.getEndIndex()) {
			h = layout.getDescent() + layout.getLeading() + layout.getAscent();
			m_pen.y += h;

			float xx = (float) (m_pen.x + m_bounds.x);
			float yy = (float) (m_pen.y + m_bounds.y);

			layout.draw(g, xx, yy);
			layout = measurer.nextLayout(strWidth);
		}
		m_bounds.height = m_pen.y + h / 2f;
		if (!btop && m_bounds.height < iconHeight) {
			m_bounds.height = iconHeight + 2;
		}
	}

	public void reset() {
		this.m_text = null;
	}

	public boolean isEmpty() {
		return false;
	}
}
