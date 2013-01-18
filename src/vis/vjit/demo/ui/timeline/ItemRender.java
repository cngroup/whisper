package vis.vjit.demo.ui.timeline;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

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
public class ItemRender extends ElemRender {

	private Rectangle2D m_rect = null;

	public ItemRender() {
		m_rect = new Rectangle2D.Double();
	}

	public Shape getRawShape(IElement elem) {
		IVisualNode node = (IVisualNode) elem;

		Shape s = (Shape) node.get("shape");
		if (s == null) {
			double xx = node.getX();
			double yy = node.getY();
			double ww = node.getWidth();
			double hh = node.getHeight();
			m_rect.setFrameFromCenter(xx, yy, xx - ww / 2, yy - hh / 2);
			s = m_rect;
		}
		return s;
	}

	public void render(Graphics2D g, IElement elem, IElemTheme theme,
			boolean highlight) {
		Shape s = getRawShape(elem);
		g.setColor(theme.getFillColor(elem));
		g.fill(s);
		g.setColor(theme.getBorderColor(elem));
		g.draw(s);
	}
}
