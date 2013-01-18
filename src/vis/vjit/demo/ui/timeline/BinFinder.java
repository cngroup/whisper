package vis.vjit.demo.ui.timeline;

import java.awt.Shape;

import davinci.Display;
import davinci.IElemFinder;
import davinci.data.elem.IElement;

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
public class BinFinder implements IElemFinder {

	private Display m_disp = null;

	private boolean m_bcursor = true;

	public BinFinder() {
	}
	
	public void setFindCursor(boolean bcursor) {
		m_bcursor = bcursor;
	}

	public void setOwner(Display disp) {
		m_disp = disp;
	}

	public Display getOwner() {
		return m_disp;
	}

	public IElement find(double x, double y) {

		TimeSeries series = (TimeSeries) m_disp.getData("timeseries");
		if (null == series || series.isEmpty()) {
			return null;
		}
		
		double xx = 0;
		double yy = 0;

		if (m_bcursor) {
			Cursor c = series.cursor();
			xx = c.getMinX();
			yy = c.getMinY();
			if (xx < x && yy < y && xx + c.getWidth() > x
					&& yy + c.getHeight() > y) {
				return c;
			}
		}

		TimeBin[] bins = series.toArray();
		for (int i = 0; i < bins.length; ++i) {
			xx = bins[i].getMinX();
			yy = bins[i].getMinY();
			if (xx < x && yy < y && xx + bins[i].getWidth() > x
					&& yy + bins[i].getHeight() > y) {
				return bins[i];
			}
		}
		return null;
	}
}
