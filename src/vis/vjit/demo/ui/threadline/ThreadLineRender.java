package vis.vjit.demo.ui.threadline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import vis.vjit.demo.UIConfig;
import vis.vjit.tweeflow.data.VisUser;
import vis.vjit.tweeflow.util.time.TimeHelper;
import davinci.data.elem.IEdge;
import davinci.rendering.DisplayRender;
import davinci.rendering.IElemRender;
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
public class ThreadLineRender extends DisplayRender {

	
	private Line2D m_line = null;
	private Stroke m_primary = null;
	private Stroke m_tick = null;
	private Font m_font = null;
	private Font m_mfont = null;
	
	public ThreadLineRender() {
		m_line = new Line2D.Double();
		m_primary = new BasicStroke(1.5f);
		m_tick = new BasicStroke(1f);
		m_font = new Font("Arial", Font.PLAIN, 12);
		m_mfont = new Font("Arial", Font.PLAIN, 10);
	}
	
	public void render(Graphics2D g) {
		
		ThreadLine graph = (ThreadLine)m_owner.getData("flow");
		if(graph == null) {
			return;
		}
		
		renderScale(g, graph);
		
		IElemRender r = m_owner.getElemRender("edges");
		IElemTheme t = m_owner.getElemTheme("edges");
		int ecnt = graph.getEdgeCount();
		IEdge<VisUser> edge = null;
		for(int i = 0; i < ecnt; ++i) {
			edge = graph.getEdge(i);
			r.render(g, edge, t, edge.isHighlight() || edge.isFocused());
		}
		
		r = m_owner.getElemRender("nodes");
		t = m_owner.getElemTheme("nodes");
		VisUser[] users = graph.getNodes(new VisUser[0]);
		for(int i = 0; i < users.length; ++i) {
			r.render(g, users[i], t, users[i].isHighlight() || users[i].isFocused());
		}
	}
	
	private void renderScale(Graphics2D g, ThreadLine line) {
		
		if(line.isEmpty()) {
			return;
		}
		
		double cy = m_owner.getHeight() / 2.0;
		double minx = m_owner.getWidth() * 0.1;
		double maxx = m_owner.getWidth() * 0.9;
		double ww = m_owner.getWidth() * 0.8;
		
		long duration = line.current - line.start;
		TimeHelper.applyPattern(TimeHelper.PATTERN_HHMMSSZ);
		String start = TimeHelper.format(line.start);
		String current = TimeHelper.format(line.current);
		
		m_line.setLine(minx, cy, maxx, cy);
		if(UIConfig.isWhite()) {
			g.setColor(Color.lightGray);
		} else {
			g.setColor(Color.gray);
		}
		Stroke bs = g.getStroke();
		g.setStroke(m_primary);
		g.draw(m_line);
		long tstep = duration / 20;
		double step1 = ww / 20;
		double step2 = step1 / 5;
		for(int i = 0; i <= 20; ++i) {
			g.setStroke(m_primary);
			double x = minx + i * step1;
			m_line.setLine(x, cy - 5, x, cy + 5);
			g.draw(m_line);
			for(int j = 0; j < 5 && i < 20; ++j) {
				double xx = x + j * step2; 
				g.setStroke(m_tick);
				m_line.setLine(xx, cy - 2, xx, cy + 2);
				g.draw(m_line);
			}
			if(i % 3 == 0 && i != 0) {
				String time = TimeHelper.format(line.start + i * tstep);
				g.setFont(m_mfont);
				FontMetrics fm = g.getFontMetrics();
				int sw = fm.stringWidth(time);
				int sh = fm.getDescent() + fm.getAscent();
				g.drawString(time, (int)(x - sw / 2), (int)(cy + sh + 3));
			}
		}
		g.setStroke(bs);
		
		TimeHelper.applyPattern(TimeHelper.PATTERN_HHMMSSZ);
		g.setFont(m_font);
		FontMetrics fm = g.getFontMetrics();
		int sw = fm.stringWidth(start);
		int sh = fm.getDescent() + fm.getAscent();
		g.drawString(start, (int)(minx - sw - 5), (int)(cy + sh / 2));
		sw = fm.stringWidth(current);
		g.drawString(current, (int)(maxx + 5), (int)(cy + sh / 2));
	}

}
