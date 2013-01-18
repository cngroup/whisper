package vis.vjit.tweeflow.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.TwitterFlower;
import davinci.rendering.DisplayRender;

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
public class MapRender extends DisplayRender {

	private Image m_image = null;

	public MapRender() {

		try {
			m_image = ImageIO.read(new File("./map.png"));
		} catch (IOException e) {
			m_image = null;
			e.printStackTrace();
		}
	}

	public void render(Graphics2D g) {

		TweeFlowVjit vjit = (TweeFlowVjit) m_owner;
		TwitterFlower flower = (TwitterFlower)vjit.getData("flower");
		
		if(flower == null) {
			return;
		}
		
		if(!(flower.focus() == flower.getTreeRoot())) {
			return;
		}
		
		if (m_image != null && vjit.isEncodeLongitude) {
			double cx = m_owner.getWidth() / 2;
			double cy = m_owner.getHeight() / 2;
			double rr = Math.min(m_owner.getWidth(), m_owner.getHeight());
			double radii = 0.4 * rr;
			g.drawImage(m_image, (int) (cx - radii), (int) (cy - radii),
					(int) (2 * radii), (int) (2 * radii), m_owner);
		}
	}
}
