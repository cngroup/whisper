package vis.vjit.demo.ui.wordle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
public class WordleVjit extends JPanel {

	private static final long serialVersionUID = 1969511432974038220L;
	private Image m_image = null;
	
	public WordleVjit() {
		setPreferredSize(new Dimension(120, 240));
		this.setBackground(Color.white);
	}
	
	public void setImage(String file) {
		try {
			m_image = ImageIO.read(new File(file));
		} catch (IOException e) {
			m_image = null;
		}
	}
	
	public void setImage(Image image) {
		m_image = image;
	}
	
	public Image getImage() {
		return m_image;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (m_image == null) {
			return;
		}
		
		int width = m_image.getWidth(this);
		int height = m_image.getHeight(this);
		
		int offx = (getWidth() - width) / 2;
		int offy = (getHeight() - height) / 2;
		
		g.drawImage(m_image, offx, offy, this);
	}
	
}
