package vis.vjit.demo;

import java.io.FileInputStream;
import java.util.Properties;

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
public class UIConfig {

	public static final int STYLE_WHITE = 0;
	
	public static final int STYLE_BLACK = 1;
	
	public static int STYLE = 0;
	
	public static boolean isWhite() {
		return STYLE == STYLE_WHITE;
	}
	
	public static boolean isBlack() {
		return STYLE == STYLE_BLACK;
	}
	
	public static void setStyle(int style) {
		STYLE = style;
	}
	
	public static final int ENHANCE_NORMAL = 0;
	public static final int ENHANCE_LINE = 1;
	public static final int ENHANCE_CURVE = 2;
	public static int ENHANCE = 2;
	
	public static void load(String file) {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(file));
			if("LIGHT".equalsIgnoreCase(p.getProperty("STYLE"))) {
				STYLE = 0;
			} else if("DARK".equalsIgnoreCase(p.getProperty("STYLE"))) {
				STYLE = 1;
			}
		} catch (Exception e) {
			STYLE = 0;
		}
	}
}
