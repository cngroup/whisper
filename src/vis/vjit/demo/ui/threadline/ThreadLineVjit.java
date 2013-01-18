package vis.vjit.demo.ui.threadline;

import twitter4j.Status;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.io.IMonitorListener;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import davinci.Display;

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
public class ThreadLineVjit extends Display implements IMonitorListener {

	private static final long serialVersionUID = 5106730075437850416L;
	private ThreadLineLayout m_layout = null;
	private VisTube[] m_tube = null;
	
	public ThreadLineVjit() {
		m_layout = new ThreadLineLayout();
		this.addData(new ThreadLine());
		this.addLayout(m_layout);
		this.setDisplayRender(new ThreadLineRender());
		this.addElemRender("nodes", new TNodeRender());
		this.addElemRender("edges", new TEdgeRender());
		this.addElemTheme("nodes", new TNodeTheme());
		this.addElemTheme("edges", new TEdgeTheme());
	}
	
	public void update(VisTube[] tube) {
		ThreadLine line = (ThreadLine)getData("flow");
		if(line == null) {			
			return;
		}
		m_tube = tube;
		//line.clear();
		line.setTube(tube);
		doLayout();
		repaint();
	}
	
	public void update() {
	}

	public void statusPosted(Status s, GeoInfoV3 info, long time, double sentiments) {
//		long t1 = System.currentTimeMillis();
		ThreadLine line = (ThreadLine)getData("flow");
		line.current = time;
		//line.start = Math.min(line.start, line.current - Constant.ACTIVE_TWEET_LIFE * Constant.TIME_INTERVAL);
		update(m_tube);
	}
	
	public void clear() {
		ThreadLine line = (ThreadLine)getData("flow");
		if(line == null) {			
			return;
		}
		line.clear();
	}
}
