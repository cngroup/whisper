package vis.vjit.demo.ui.wordle;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import davinci.data.AbstractData;

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
public class WordleImg extends AbstractData {

	private Image image = null;
	
	private void load(String file) {
		try {
			image = ImageIO.read(new File(file));
		} catch (IOException e) {
			image = null;
		}
	}
	
}
