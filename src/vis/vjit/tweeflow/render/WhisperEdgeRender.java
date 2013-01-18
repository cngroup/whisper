package vis.vjit.tweeflow.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import vis.vjit.tweeflow.data.VisTube;
import davinci.data.elem.IEdge;
import davinci.data.elem.IElement;
import davinci.data.elem.IVisualNode;
import davinci.rendering.ElemRender;
import davinci.rendering.ElemTheme;
import davinci.rendering.IElemTheme;

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
public class WhisperEdgeRender extends ElemRender {

	private Line2D m_line = null;
	
	public WhisperEdgeRender() {
		m_line = new Line2D.Double();
	}

	public Shape getRawShape(IElement elem) {
		VisTube edge = (VisTube) elem;
		
		GeneralPath p = edge.curve();
		if(p != null) {
			return p;
		}
		IVisualNode n1 = edge.source();
		IVisualNode n2 = edge.sink();
		m_line.setLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
		return m_line;
	}

	public void render(Graphics2D g, IElement elem, IElemTheme theme,
			boolean highlight) {
		
		VisTube tube = (VisTube) elem;
		Shape sp = getRawShape(tube);
		if(sp == null) {
			return;
		}
		ElemTheme et = (ElemTheme) theme;
		Stroke s = g.getStroke();
		g.setStroke(et.getThickness(tube));
		g.setPaint(et.getBorderColor(tube));
		g.draw(sp);
		g.setStroke(s);
	}
}
