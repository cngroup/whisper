package vis.vjit.demo.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

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
public class StopButton extends JButton {

	private static final long serialVersionUID = -7797767224013143231L;
	private ImageIcon m_stop = null;
	
	public StopButton() {
		m_stop = new ImageIcon("./stop.png");
		setIcon(m_stop);
		this.setBackground(Color.white);
		this.setOpaque(false);
		this.setFocusPainted(false);
		this.setBorderPainted(false);
		this.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); 
	}
	
}
