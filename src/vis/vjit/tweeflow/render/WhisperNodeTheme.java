package vis.vjit.tweeflow.render;

import java.awt.Color;
import java.awt.Font;

import vis.vjit.demo.UIConfig;
import vis.vjit.demo.ui.UIConstant;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.IWhisperElem;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTweet;
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
public class WhisperNodeTheme extends ElemTheme {

	public WhisperNodeTheme() {
		m_ftext = new Font("Arial", Font.PLAIN, 15);
		m_cfill = new Color(255, 247, 188, (int) (0.5 * 255));
		// m_cfill = new Color(237, 207, 114);
		m_flabel = new Font("Arial", Font.BOLD, 12);
		m_ftext = new Font("Arial", Font.PLAIN, 10);
		// m_colors = new Color[20];
		// Color[] colors = InfoVisUtil.makeColorArray(0, 10, 180, 240, 8);
		// System.arraycopy(colors, 0, m_colors, 0, 8);
		// colors = InfoVisUtil.makeColorArray(11, 30, 180, 240, 2);
		// System.arraycopy(colors, 0, m_colors, 8, 2);
		// colors = InfoVisUtil.makeColorArray(31, 50, 180, 240, 2);
		// System.arraycopy(colors, 0, m_colors, 10, 2);
		// colors = InfoVisUtil.makeColorArray(51, 60, 180, 240, 8);
		// System.arraycopy(colors, 0, m_colors, 12, 8);
	}

	public Color getLabelColor(IElement elem) {
		TweeFlowVjit vjit = (TweeFlowVjit)m_owner;
		float frac = 1 - elem.frac();
		if(frac == 0) {
			frac = TweeFlowVjit.isShowAll ? 0.9f : 0.15f; 
		}
		if (UIConfig.isWhite()) {
			if(vjit.isTracing) {
				if(elem instanceof VisTopic) {
					return InfoVisUtil.getColorByAlpha(Color.black, frac);
				} else {
					return Color.black;
				}
			} else {
				return Color.black;
			}
		} else {
			if(vjit.isTracing) {
				if(elem instanceof VisTopic) {
					
					return InfoVisUtil.getColorByAlpha(Color.white, frac);
				} else {
					return Color.white;
				}
			} else {
				return Color.white;
			}
		}
	}

	public Color getBorderColor(IElement elem) {
		Color c = getFillColor(elem);
		int alpha = c.getAlpha();
		return InfoVisUtil.getColorByAlpha(c.darker(), alpha / 255f);
	}

	public Color getFillColor(IElement elem) {
		double s = 0;
		long active = 0;
		long current = 0;
		boolean isActive = false;
		if (elem instanceof IWhisperElem) {
			TwitterFlower flower = (TwitterFlower) m_owner.getData("flower");
			current = flower.focus().getTime();
			s = ((IWhisperElem) elem).getSentiment();
			active = ((IWhisperElem) elem).getLastActiveTime();
			isActive = ((IWhisperElem) elem).isActive();
		} else if (elem instanceof VisTopic) {
			if (!((VisTopic) elem).isCollapsed()) {
				return m_cfill;
			} else {
				s = ((VisTopic)elem).getSentiment();
			}
		} else {
			return super.getFillColor(elem);
		}
		
		int idx = 0;
		if(s < 0.45) {
			idx = 0;
		} else if(s > 0.55) {
			idx = 2;
		} else {
			idx = 1;
		}
		Color c = UIConstant.sentiments[idx];
		
		float alpha = (1 - (float) (current - active)
				/ ((isActive ? Constant.ACTIVE_TWEET_LIFE
						: Constant.INACTIVE_TWEET_LIFE) * Constant.TIME_INTERVAL));
		if (alpha < 0) {
			alpha = 0;
		}
		alpha = (0.3f + 0.7f * alpha);
		
		TweeFlowVjit vjit = (TweeFlowVjit)m_owner;
		if(elem instanceof VisTopic) {
			if(vjit.isTracing) {
				float frac = 1 - elem.frac();
				if(frac == 0) {
					frac = TweeFlowVjit.isShowAll ? 0.9f : 0.15f; 
				}
				return InfoVisUtil.getColorByAlpha(c, frac);
			} else {
				return InfoVisUtil.getColorByAlpha(c, 0.65f);
			}
		}
		
		if(elem.frac() == 1 || (elem instanceof VisTweet) && (((VisTweet) elem).isActive())) {
			if(elem.isHighlight()) {
				return c;
			} else {
				return InfoVisUtil.getColorByAlpha(c, alpha);
			}
		} else {
			return InfoVisUtil.getColorByAlpha(c, elem.frac());
		}
	}
}
