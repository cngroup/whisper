package vis.vjit.demo.ui.threadline;

import java.awt.Color;
import java.awt.Font;

import vis.vjit.demo.UIConfig;
import vis.vjit.demo.ui.UIConstant;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.VisUser;
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
public class TNodeTheme extends ElemTheme {
	
	public TNodeTheme() {
		m_ftext = new Font("Arial", Font.PLAIN, 15);
		m_cfill = new Color(255, 255, 220);
//		m_cfill = new Color(237, 207, 114);
		m_flabel = new Font("Arial", Font.PLAIN, 11); 
		m_ftext = new Font("Arial", Font.PLAIN, 10);
//		m_colors = new Color[20];
//		Color[] colors = InfoVisUtil.makeColorArray(0, 10, 180, 240, 8);
//		System.arraycopy(colors, 0, m_colors, 0, 8);
//		colors = InfoVisUtil.makeColorArray(11, 30, 180, 240, 2);
//		System.arraycopy(colors, 0, m_colors, 8, 2);
//		colors = InfoVisUtil.makeColorArray(31, 50, 180, 240, 2);
//		System.arraycopy(colors, 0, m_colors, 10, 2);
//		colors = InfoVisUtil.makeColorArray(51, 60, 180, 240, 8);
//		System.arraycopy(colors, 0, m_colors, 12, 8);
	}
	
	public Color getLabelColor(IElement elem) {
		if (UIConfig.isWhite()) {
			return Color.black;
		} else {
			return Color.white;
		}
	}
	
	public Color getBorderColor(IElement elem) {
		return getFillColor(elem).darker();
	}
	
	public Color getFillColor(IElement elem) {
		
		int length = UIConstant.sentiments.length;
		double s = 0;
		if(elem instanceof VisTweet) {
			s = ((VisTweet)elem).getSentiment();
		} else if(elem instanceof VisUser) {
			s = ((VisUser)elem).getSentiment();
		} else if(elem instanceof VisTweet) {
			s = ((VisTweet)elem).getSentiment();
		} else if(elem instanceof VisTopic) {
			return m_cfill;
		}
		
		int idx = (int)(s * length);
		if(idx < 0) {
			idx = 0;
		} else if(idx > length - 1) {
			idx = length - 1;
		}
		return elem.isHighlight() ? UIConstant.sentiments[idx].brighter() : UIConstant.sentiments[idx];
	}
	
	private Color getColorBySaturation(Color c0, float frac) {
		float[] hsb = Color.RGBtoHSB(c0.getRed(), c0.getGreen(), c0.getBlue(), null);
		hsb[1] *= frac;
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), true);
	}
}
