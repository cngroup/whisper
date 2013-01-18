package vis.vjit.demo.ui.legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import vis.vjit.tweeflow.data.filter.SentimentFilter;

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
public class Legend extends JPanel implements MouseListener {

	private static final long serialVersionUID = 8727382900073509270L;

	private Color[] fill = new Color[3];
	
	private Stroke border = new BasicStroke(2.0f);

	private Ellipse2D ellipse = null;
	
	private float radii = 10;
	
	private int style = 0;
	
	private String label = "";
	
	private List<ILegendSelectionListener> m_listener = null;
	
	private boolean bselect = true;

	public Legend(String label, int style) {
		ellipse = new Ellipse2D.Double();
		fill[0] = new Color(140, 255, 0);
		fill[1] = new Color(255, 191, 0);
		fill[2] = new Color(244, 0, 0);
		m_listener = new ArrayList<ILegendSelectionListener>();
		this.label = label;
		this.style = style;
		this.setBackground(Color.white);
		this.addMouseListener(this);
	}
	
	public boolean isSelected() {
		return bselect;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(getBackground());
		g2d.clearRect(0, 0, getWidth(), getHeight());
		double cx = getWidth() / 2.0;
		double cy = getHeight() / 2.0;
		ellipse.setFrameFromCenter(cx, cy, cx + radii, cy + radii);
		g.setColor(fill[style]);
		g2d.fill(ellipse);
		Color c = fill[style].darker(); //this.bselect ? Color.lightGray : Color.gray; //fill[style].brighter(): fill[style].darker();
		Stroke s = this.bselect ? border : g2d.getStroke();
		Stroke bs = g2d.getStroke();
		g2d.setColor(c);
		g2d.setStroke(s);
		g2d.draw(ellipse);
		g2d.setStroke(bs);
	}

	public void setStyle(int style) {
		this.style = style;
	}
	
	public int style() {
		return this.style;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void addListener(ILegendSelectionListener l) {
		m_listener.add(l);
	}
	
	public void removeListener(ILegendSelectionListener l) {
		m_listener.remove(l);
	}
	
	public void fireSelect(boolean bselect) {
		for(ILegendSelectionListener l : m_listener) {
			l.select(bselect);
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		bselect = !bselect;
		this.fireSelect(bselect);
		this.repaint();
	}

	public void mouseEntered(MouseEvent arg0) {		
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}
}
