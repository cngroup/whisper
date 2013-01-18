package vis.vjit.demo.ui.timeline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import davinci.Display;
import davinci.ILayout;

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
public class TimelineLayout implements ILayout {

	private Rectangle2D m_rect = null;
	
	private TimelineVjit m_vjit = null;
	
	public TimelineLayout() {
		m_rect = new Rectangle2D.Float();
	}

	public String getName() {
		return "TimelineLayout";
	}

	public void layout(Display disp) {
		if (null == disp) {
			return;
		}
		
		m_vjit = (TimelineVjit)disp;
		TimeSeries series = (TimeSeries) disp.getData("timeseries");
		if (null == series) {
			return;
		}

		double width = disp.getWidth();
		double height = disp.getHeight();

		double margin_x = (width * 0.02) / 2;
		double range = width * 0.98;

		long duration = series.duration();

		double ww = series.interval() * range / duration;

		series.reset();

		double xx = margin_x + range;
		double yy = height / 2.0;
		double hh = 0.8 * height;
		Point2D.Float down = new Point2D.Float((float) xx,
				(float) (yy + hh / 2));
		Point2D.Float up = new Point2D.Float((float) xx, (float) (yy - hh / 2));
		
		// duration / range;
		TimeBin[] bin = series.toArray();
		float max = 0;
		for (int i = 0; i < bin.length; ++i) {
			max = Math.max(bin[i].size(), max);
		}
		Cursor c = series.cursor();
		xx = margin_x + c.size() * ww / 2 + ww * ((c.start() - series.start()) / series.interval());
		c.setX(xx);
		c.setY(yy);
		c.setWidth(ww * (c.end() - c.start()) / series.interval());
		c.setHeight(0.8 * height);
		c.updateSize(1);
		c.updateLocation(1);
		for (int i = 0; i < bin.length; ++i) {
			xx = margin_x + ww / 2 + ww
					* ((bin[i].start() - series.start()) / series.interval());
			hh = 0.8 * height * bin[i].size() / max;
			
			if(i == 0) {
				down = new Point2D.Float((float)(xx + ww / 2), (float) (yy + hh / 2));
				up = new Point2D.Float((float)(xx + ww / 2), (float) (yy - hh / 2));
				series.addUpperCtrl(up);
				series.addBottomCtrl(down);
			}

			down = new Point2D.Float((float) xx, (float) (yy + hh / 2));
			up = new Point2D.Float((float) xx, (float) (yy - hh / 2));
			series.addUpperCtrl(up);
			series.addBottomCtrl(down);
			
			if(i == bin.length - 1) {
				down = new Point2D.Float((float)(xx - ww / 2), (float) (yy + hh / 2));
				up = new Point2D.Float((float)(xx - ww / 2), (float) (yy - hh / 2));
				series.addUpperCtrl(up);
				series.addBottomCtrl(down);
			}
			
			bin[i].setX(xx);
			bin[i].setY(height / 2.0);
			bin[i].setWidth(ww);
			bin[i].setHeight(height);
			bin[i].updateLocation(1);
			bin[i].updateSize(1);
		}
		
		if(series.isEmpty()) {
			return;
		}
		
		Area area = null;
		Area outline = new Area(series.boundary());
		for(int i = 0; i < bin.length; ++i) {
			xx = bin[i].getX();
			yy = bin[i].getY();
			ww = bin[i].getWidth();
			hh = bin[i].getHeight();
			m_rect.setFrameFromCenter(xx, yy, xx - ww / 2, yy - hh / 2);
			area = new Area(m_rect);
			area.intersect(outline);
			bin[i].put("shape", area);
		}
	}
	
	public void updateCursor() {
		if(m_vjit == null) {
			return;
		}
		
		TimeSeries series = (TimeSeries) m_vjit.getData("timeseries");
		if (null == series) {
			return;
		}
		double width = m_vjit.getWidth();
		double height = m_vjit.getHeight();
		double margin_x = (width * 0.02) / 2;
		double range = width * 0.98;
		long duration = series.duration();
		double ww = series.interval() * range / duration;
		Cursor c = series.cursor();
		double yy = height / 2.0;
		double xx = margin_x + c.size() * ww / 2 + ww * ((c.start() - series.start()) / series.interval());
		c.setX(xx);
		c.setY(yy);
		c.setWidth(ww * (c.end() - c.start()) / series.interval());
		c.setHeight(0.8 * height);
		c.updateSize(1);
		c.updateLocation(1);
	}

	public void reset() {
	}
}
