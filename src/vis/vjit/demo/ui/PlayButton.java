package vis.vjit.demo.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

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
public class PlayButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = -8813429624318130605L;
	private ImageIcon m_play = null;
	private ImageIcon m_pause = null;
	private boolean bplay = false;

	public PlayButton() {
		m_play = new ImageIcon("./play.png");
		m_pause = new ImageIcon("./pause.png");
		setIcon(m_play);
		this.addActionListener(this);
		this.setBackground(Color.white);
		this.setOpaque(false);
		this.setFocusPainted(false);
		this.setBorderPainted(false);
		this.setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); 
	}

	public void actionPerformed(ActionEvent arg0) {
		if(!bplay) {
			bplay = true;
			setIcon(m_pause);
		} else {
			bplay = false;
			setIcon(m_play);
		}
		this.updateUI();
	}
	
	public boolean isPlaying() {
		return bplay;
	}
	
	public void reset() {
		setIcon(m_play);
		this.updateUI();
	}
}
