package vis.vjit.demo.ui.timeline;

import java.awt.Graphics2D;

import davinci.data.elem.IVisualNode;
import davinci.rendering.DisplayRender;
import davinci.rendering.IElemRender;
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
public class TimelineRender extends DisplayRender {
	

	public TimelineRender() {		
	}

	public void render(Graphics2D g) {
		if (m_owner == null) {
			return;
		}
		TimeSeries series = (TimeSeries) m_owner.getData("timeseries");
		if (null == series || series.isEmpty()) {
			return;
		}
		IElemRender r = m_owner.getElemRender("elems");
		IElemTheme t = m_owner.getElemTheme("elems");
		IVisualNode[] tweets = series.toArray();
		for (int i = 0; i < tweets.length; ++i) {
			r.render(g, tweets[i], t,
					tweets[i].isHighlight() || tweets[i].isFocused());
		}
		
		r.render(g, series.cursor(), t, series.cursor().isHighlight());
	}
}
