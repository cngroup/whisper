package vis.vjit.demo.ui.timeline;

import java.awt.Color;

import vis.vjit.demo.ui.UIConstant;
import vis.vjit.tweeflow.util.InfoVisUtil;
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
public class ItemTheme extends ElemTheme {
	
	private Color m_cur = null;
	private Color m_hcur = null;
	
	public ItemTheme() {
		m_cur = new Color(0f, 0f, 0.8f, 0.1f);
		m_hcur = new Color(0f, 0f, 0.8f, 0.3f);
	}

	public Color getFillColor(IElement elem) {
		
		if(elem instanceof Cursor) {
			return elem.isHighlight() ? m_hcur : m_cur;
		}
		
		TimeBin tweet = (TimeBin)elem;
		double s = tweet.sentiments();
		int idx = 0;
		if(s < 0.45) {
			idx = 0;
		} else if(s > 0.55) {
			idx = 2;
		} else {
			idx = 1;
		}
		Color c = UIConstant.sentiments[idx];
		return elem.isHighlight() ? c.brighter() : c; 
	}
	
	public Color getBorderColor(IElement elem) {
		return this.getFillColor(elem).darker();
	}
	
}
