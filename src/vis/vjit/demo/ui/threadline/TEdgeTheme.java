package vis.vjit.demo.ui.threadline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import vis.vjit.demo.ui.UIConstant;
import vis.vjit.tweeflow.data.VisUser;
import davinci.data.elem.IEdge;
import davinci.data.elem.IElement;
import davinci.rendering.ElemTheme;

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
public class TEdgeTheme extends ElemTheme {
	
	private BasicStroke m_hthick = null;

	public TEdgeTheme() {
		// m_colors = new Color[20];
		// Color[] colors = InfoVisUtil.makeColorArray(Color.red, Color.orange,
		// 10);//InfoVisUtil.makeColorArray(0, 30, 180, 240, 10);
		// System.arraycopy(colors, 0, m_colors, 0, 10);
		// colors = InfoVisUtil.makeColorArray(Color.orange, Color.green,
		// 10);//InfoVisUtil.makeColorArray(31, 60, 180, 240, 10);
		// System.arraycopy(colors, 0, m_colors, 10, 10);
		m_thick = new BasicStroke(1f);
		m_hthick = new BasicStroke(2f);
	}

	public Color getBorderColor(IElement e) {
		int length = UIConstant.sentiments.length;
		
		IEdge edge = (IEdge)e;
		
		VisUser user = (VisUser)edge.getFirstNode();
		double s = user.getSentiment();
		int idx = (int) (s * (length));
		if (idx < 0) {
			idx = 0;
		} else if (idx > length - 1) {
			idx = length - 1;
		}
		float alpha = 1;
		if(!e.isHighlight()) {
			Double wei = (Double) e.getWeight();
			alpha = (float)(0.3 + 0.7f * wei / 50);
			if(alpha > 1) {
				alpha = 1f;
			}
		}
		Color c = UIConstant.sentiments[idx];
		return e.isHighlight() ? UIConstant.sentiments[idx].brighter() : getColorByAlpha(c, alpha);
	}

	public Stroke getThickness(IElement e) {
		Double wei = (Double) e.getWeight();
		if (wei == 0) {
			wei = 1D;
		} else {
			wei = wei * wei;
		}
		// return new BasicStroke((float)(0.5 + 0.5f * Math.log(wei)));
		return e.isHighlight() ? m_hthick : m_thick;
	}

	public Color getColorByAlpha(Color c0, float frac) {
		return new Color(c0.getRed() / 255f, c0.getGreen() / 255f,
				c0.getBlue() / 255f, frac);
	}
}
