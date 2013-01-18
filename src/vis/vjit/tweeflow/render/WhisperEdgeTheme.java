

package vis.vjit.tweeflow.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import vis.vjit.demo.UIConfig;
import vis.vjit.demo.ui.UIConstant;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import davinci.data.elem.IElement;
import davinci.rendering.ElemTheme;

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
public class WhisperEdgeTheme extends ElemTheme {
	
	private BasicStroke m_hthick = null;

	public WhisperEdgeTheme() {
		// m_colors = new Color[20];
		// Color[] colors = InfoVisUtil.makeColorArray(Color.red, Color.orange,
		// 10);//InfoVisUtil.makeColorArray(0, 30, 180, 240, 10);
		// System.arraycopy(colors, 0, m_colors, 0, 10);
		// colors = InfoVisUtil.makeColorArray(Color.orange, Color.green,
		// 10);//InfoVisUtil.makeColorArray(31, 60, 180, 240, 10);
		// System.arraycopy(colors, 0, m_colors, 10, 10);
		m_thick = new BasicStroke(1f);
		m_hthick = new BasicStroke(1.2f);
	}

	public Color getBorderColor(IElement e) {
		int length = UIConstant.sentiments.length;
		
		VisTube tube = ((VisTube) e);
		VisTweet tweet = tube.source();
		double s = tweet.getSentiment();
		
//		int idx = (int) (s * (length));
//		if (idx < 0) {
//			idx = 0;
//		} else if (idx > length - 1) {
//			idx = length - 1;
//		}
		
		int idx = 0;
		if(s < 0.45) {
			idx = 0;
		} else if(s > 0.55) {
			idx = 2;
		} else {
			idx = 1;
		}
		
		Double wei = (Double) e.getWeight();
		float alpha = (float)(0.4 + 0.6 * wei / 20);
		if(alpha > 1) {
			alpha = 1;
		}
//		if(!e.isHighlight()) {
//			switch (UIConfig.ENHANCE) {
//			case UIConfig.ENHANCE_NORMAL:
//				Double wei = (Double) e.getWeight();
////				alpha = (float)(0.3 + 0.7 * wei / 20);
////				if(alpha > 1) {
////					alpha = 1f;
////				}
//				alpha = 1f;
//				break;
//			case UIConfig.ENHANCE_LINE:
//			case UIConfig.ENHANCE_CURVE:
//				alpha = (float)(tube.curvature() * tube.curvature());
////				if(alpha < 0.1) alpha = 0.1f;
////				else if(alpha < 0.2) alpha = 0.2f;
//				if(alpha < 0.3) alpha = 0.3f;
////				else if(alpha < 0.4) alpha = 0.4f;
//				else if(alpha < 0.5) alpha = 0.5f;
////				else if(alpha < 0.6) alpha = 0.6f;
////				else if(alpha < 0.7) alpha = 0.7f;
////				else if(alpha < 0.8) alpha = 0.8f;
////				else if(alpha < 0.9) alpha = 0.9f;
//				else {
//					alpha = 1.0f;
//				}
//				if(UIConfig.ENHANCE == UIConfig.ENHANCE_LINE) {
//					alpha = 1 - alpha;
//				}
//				break;
//			}
//		}
		Color c = UIConstant.sentiments[idx];
		
		if(e.frac() == 1 || TweeFlowVjit.isShowAll) {
			if(e.isHighlight()) {
				return c;
			} else {
				return getColorByAlpha(c, alpha);
			}
		} else {
			return getColorByAlpha(c, e.frac());
		}
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
