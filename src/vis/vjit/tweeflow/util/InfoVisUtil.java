package vis.vjit.tweeflow.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Random;

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
public class InfoVisUtil {

	private static Random rand = null;
	static {
		rand = new Random(System.currentTimeMillis());
	}

	public static final Graphics2D DEFAULT_GRAPHICS = (Graphics2D) new BufferedImage(
			1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();

	public static Color getRandomColor(float alpha) {
		if (alpha > 1f) {
			alpha = 1f;
		}

		if (alpha == 1) {
			return new Color(rand.nextInt(255), rand.nextInt(255), rand
					.nextInt(255));
		} else {
			return new Color(rand.nextInt(255), rand.nextInt(255), rand
					.nextInt(255), (int) (255 * alpha));
		}
	}

	public static Color[] makeRandomColorArray(int size) {
		Color[] c = new Color[size];
		int h = 0, s = 0, b = 0;
		for (int i = 0; i < size; ++i) {
			h = rand.nextInt(255);
			s = rand.nextInt(255);
			b = rand.nextInt(255);
			c[i] = new Color(Color.HSBtoRGB(h, s, b));
		}
		return c;
	}
	
	public static Color[] makeRandomColorArray(int size, float alpha) {
		Color[] c = new Color[size];
		float r = 0, g = 0, b = 0;
		for (int i = 0; i < size; ++i) {
			r = rand.nextInt(255) / 255.0f;
			g = rand.nextInt(255) / 255.0f;
			b = rand.nextInt(255) / 255.0f;
			c[i] = new Color(r, g, b, alpha);
		}
		return c;
	}

	public static Color[][] makeRianwbowArray(int depth, int shift) {
		Color[][] m_rainbow = new Color[7][depth];

		shift = shift % 7;

		// red - orange
		m_rainbow[(0 + shift) % 7] = InfoVisUtil.makeColorArray(Color.red,
				Color.orange, 10, 0.95f);
		// orange - yellow
		m_rainbow[(1 + shift) % 7] = InfoVisUtil.makeColorArray(Color.orange,
				Color.yellow, 10, 0.95f);
		// yellow - green
		m_rainbow[(2 + shift) % 7] = InfoVisUtil.makeColorArray(Color.yellow,
				Color.green, 10, 0.95f);
		// green - blue
		m_rainbow[(3 + shift) % 7] = InfoVisUtil.makeColorArray(Color.green,
				Color.blue, 10, 0.95f);
		// blue - clan
		m_rainbow[(4 + shift) % 7] = InfoVisUtil.makeColorArray(Color.blue,
				Color.cyan, 10, 0.95f);
		// clan - puple
		m_rainbow[(5 + shift) % 7] = InfoVisUtil.makeColorArray(Color.cyan,
				Color.pink, 10, 0.95f);
		// puple - red
		m_rainbow[(6 + shift) % 7] = InfoVisUtil.makeColorArray(Color.pink,
				Color.red, 10, 0.95f);
		return m_rainbow;
	}
	
	public static Color[][] makeRianwbowArray(int depth, int shift, float alpha) {
		Color[][] m_rainbow = new Color[7][depth];

		shift = shift % 7;

		// red - orange
		m_rainbow[(0 + shift) % 7] = InfoVisUtil.makeColorArray(Color.red,
				Color.orange, 10, alpha);
		// orange - yellow
		m_rainbow[(1 + shift) % 7] = InfoVisUtil.makeColorArray(Color.orange,
				Color.yellow, 10, alpha);
		// yellow - green
		m_rainbow[(2 + shift) % 7] = InfoVisUtil.makeColorArray(Color.yellow,
				Color.green, 10, alpha);
		// green - blue
		m_rainbow[(3 + shift) % 7] = InfoVisUtil.makeColorArray(Color.green,
				Color.blue, 10, alpha);
		// blue - clan
		m_rainbow[(4 + shift) % 7] = InfoVisUtil.makeColorArray(Color.blue,
				Color.cyan, 10, alpha);
		// clan - puple
		m_rainbow[(5 + shift) % 7] = InfoVisUtil.makeColorArray(Color.cyan,
				Color.pink, 10, alpha);
		// puple - red
		m_rainbow[(6 + shift) % 7] = InfoVisUtil.makeColorArray(Color.pink,
				Color.red, 10, alpha);
		return m_rainbow;
	}
	
	public static Color[] makeHueArray(Color c0, Color c1, int size) {
		Color[] c = new Color[size];
		
		float[] shsb = Color.RGBtoHSB(c0.getRed(), c0.getGreen(), c0.getBlue(), null);
		float[] ehsb = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		
		for (int i = 0; i < size; i++) {
			c[i] = new Color(Color.HSBtoRGB(
					interpolate(shsb[0], ehsb[0], i, size), 
					interpolate(shsb[1], ehsb[1], i, size), 
					interpolate(shsb[2], ehsb[2], i, size)), true);
		}
		return c;
	}

	public static Color[] makeColorArray(Color c0, Color c1, int size) {
		Color[] c = new Color[size];
		for (int i = 0; i < size; i++)
			c[i] = new Color(interpolate(c0.getRed(), c1.getRed(), i, size),
					interpolate(c0.getGreen(), c1.getGreen(), i, size),
					interpolate(c0.getBlue(), c1.getBlue(), i, size));
		return c;
	}

	public static Color getColorByAlpha(Color c0, float frac) {
		return new Color(c0.getRed() / 255f, c0.getGreen() / 255f,
				c0.getBlue() / 255f, frac);
	}
	
	public static Color getColorBySaturation(Color c0, float frac) {
		float[] hsb = Color.RGBtoHSB(c0.getRed(), c0.getGreen(), c0.getBlue(),
				null);
		hsb[1] *= frac;
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), true);
	}
	
	public static Color getColorByBrightness(Color c0, float frac) {
		float[] hsb = Color.RGBtoHSB(c0.getRed(), c0.getGreen(), c0.getBlue(), null);
		hsb[2] = frac;
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), true);
	}
	
	public static Color getColorByBrightness(Color c0, float frac, float alpha) {
		float[] hsb = Color.RGBtoHSB(c0.getRed(), c0.getGreen(), c0.getBlue(), null);
		hsb[2] = frac;
		Color c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		c = new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue()/255.0f, alpha);
		return c;
	}


	public static Color[] makeColorArray(Color c0, Color c1, int size,
			float alpha) {
		Color[] c = new Color[size];
		for (int i = 0; i < size; i++)
			c[i] = new Color(
					interpolate(c0.getRed(), c1.getRed(), i, size) / 255f,
					interpolate(c0.getGreen(), c1.getGreen(), i, size) / 255f,
					interpolate(c0.getBlue(), c1.getBlue(), i, size) / 255f,
					alpha);
		return c;
	}
	
	public static Color[] makeColorArray(Color c, int alpha1, int alpha2, int size) {
		Color[] colors = new Color[size];
		for (int i = 0; i < size; i++) {
			colors[i] = new Color(
					c.getRed() / 255f, 
					c.getGreen() / 255f, 
					c.getBlue() / 255f, 
					interpolate(alpha1, alpha2, i, size) / 255f);
		}
		return colors;
	}

	public static Color[] makeColorArray(int s, int b, int size, int alpha) {
		Color[] c = new Color[size];
		for (int i = 0; i < size; ++i) {
			c[i] = getHSBColor(interpolate(0, 255, i, size) / 255f, s / 255f,
					b / 255f, alpha / 255);
		}
		return c;
	}

	public static Color[] makeColorArray(float s, float b, int size, float alpha) {
		Color[] c = new Color[size];
		for (int i = 0; i < size; ++i) {
			c[i] = getHSBColor(interpolate(0, 239, i, size) / 255f, s, b, alpha);
		}
		return c;
	}

	public static Color[] makeColorArray(int s, int b, int size) {
		Color[] c = new Color[size];
		for (int i = 0; i < size; ++i) {
			c[i] = getHSBColor(interpolate(0, 255, i, size) / 255f, s / 255f,
					b / 255f);
		}
		return c;
	}
	
	public static Color[] makeColorArray(int h1, int h2, int s, int b, int size) {
		Color[] c = new Color[size];
		for (int i = 0; i < size; ++i) {
			c[i] = getHSBColor(interpolate(h1, h2, i, size) / 255f, s / 255f,
					b / 255f);
		}
		return c;
	}

	public static Font[] makeFontArray(String name, int style, int size0,
			int size1, int num) {
		Font[] f = new Font[num];
		for (int i = 0; i < num; ++i) {
			f[i] = new Font(name, style, interpolate(size0, size1, i, num));
		}
		return f;
	}

	public static Stroke[] makeStrokeArray(float size0, float size1, int num) {
		int s1 = (int) (size0 * num);
		int s2 = (int) (size1 * num);
		Stroke[] s = new Stroke[num];

		for (int i = 0; i < num; ++i) {
			s[i] = new BasicStroke((float) interpolate(s1, s2, i, num) / num,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2);
		}
		return s;
	}

	public static final Color getHSBColor(float h, float s, float b) {
		return new Color(Color.HSBtoRGB(h, s, b));
	}

	public static final Color getHSBColor(float h, float s, float b, float alpha) {
		Color temp = new Color(Color.HSBtoRGB(h, s, b));
		return new Color(temp.getRed(), temp.getGreen(), temp.getBlue(),
				(int) (alpha * 255));
	}

	public static int interpolate(int a, int b, int i, int n) {
		return (a * (n - 1 - i) + b * i) / (n - 1);
	}
	
	public static float interpolate(float a, float b, int i, int n) {
		return i * (b - a) / n;
	}

}
