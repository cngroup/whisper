package vis.vjit.demo.ui;

import java.awt.Color;

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
public class UIConstant {

	public static Color[] sentiments = new Color[3];
	static {
		sentiments[0] = new Color(255, 0, 0);
		sentiments[1] = new Color(255, 191, 0);
		sentiments[2] = new Color(146, 208, 80);
	}
	
	public static float[] alphas = new float[]{1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0.1f};
}
