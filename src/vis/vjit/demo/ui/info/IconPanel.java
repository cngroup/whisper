package vis.vjit.demo.ui.info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

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
public class IconPanel extends JPanel {

	private static final long serialVersionUID = -3498119669954917229L;

	private ImageIcon m_icon = null;

	public IconPanel(ImageIcon icon) {
		m_icon = icon;
		this.setPreferredSize(new Dimension(48, 48));
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 5, Color.white));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (m_icon == null) {
			return;
		}
		int w = getWidth();
		int h = getHeight();
		int cx = w / 2;
		int cy = h / 2;
		m_icon.paintIcon(IconPanel.this, g, cx - 24, cy -24);
	}

	public void setIcon(ImageIcon icon) {
		m_icon = icon;
	}

	public ImageIcon getIcon() {
		return m_icon;
	}
}
