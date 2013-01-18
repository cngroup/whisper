package vis.vjit.tweeflow.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

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
public class GraphicsUtil {
	public static Font labelFont = new Font("SansSerif", Font.PLAIN, 11);
	public static Font titleFont = new Font("SansSerif", Font.BOLD, 11);
	public static Font bigFont = new Font("SansSerif", Font.BOLD, 12);
	public static Color dataColor = new Color(80, 100, 120);
	public static Color negativeDataColor = new Color(150, 50, 50);
	public static Color nullDataColor = new Color(128, 128, 128, 128);
	public static Color selectionColor = new Color(250, 150, 50);
	public static Color mouseOverColor = new Color(200, 200, 70);
	public static Color mouseDownColor = new Color(255, 200, 0);
	public static Color axisColor = Color.gray;
	public static Color gridColor = new Color(225, 225, 225);
	public static Color labelColor = Color.darkGray;
	public static Color shadow = Color.darkGray;
	public static Color calloutLabelColor = labelColor;
	public static Color calloutShadow = new Color(0, 0, 0, 100);
	public static Color calloutBackgroundColor = Color.white;// new
	// Color(255,255,255,200);
	public static Color warningColor = new Color(200, 0, 0);
	public static Color weakLabelColor = new Color(255, 255, 255, 170);
	public static int yAxisLabelWidth = 50;
	public static int xAxisLabelHeight = 60;
	public static int rightMargin = 30;
	public static Color selectionBackground = new Color(230, 240, 255);
	/*
	 * static int[][] paletteInts={ {0xdc9cd4, 0xcc74ba, 0xa44c95, 0x901d7e},
	 * {0xc3c3ee, 0x9c9cdc, 0x7474b4, 0x54548b}, {0xc3eeec, 0x9cdcda, 0x74b4b2,
	 * 0x548b89}, {0xccdc9c, 0xb3cc6b, 0x8ca454, 0x6c7c44}, {0xe5d7a0, 0xe4bc54,
	 * 0xc59811, 0x856315}, {0xeaaab1, 0xd4646c, 0xac4c4c, 0x852e2e} };
	 */

	private static Color[] palette;

	private static float[] paletteHues = { 0.85f, 0.66f, 0.50f, 0.20f, 0.10f,
			0.0f };

	private static int[] paletteInts;

	// Colors chosen by Fernanda, 1/13/07
	// private static int[] paletteInts={-6513445, -9145163, -11709051,
	// -3417188,
	// -4928406, -7625901, -10192838, -1848428, -1852844,
	// -4350660, -7574220, -1862501, -2858133, -5551285, -8045508, -2384684,
	// -3380036, -6073453, -8567948};

	// from fernanda, 1/14/07, as matrix:
	// private static int[] paletteInts={
	// 0xdc9cd4, 0xcc74ba, 0xa44c95, 0x901d7e,
	// 0xc3c3ee, 0x9c9cdc, 0x7474b4, 0x54548b,
	// 0xc3eeec, 0x9cdcda, 0x74b4b2, 0x548b89,
	// 0xccdc9c, 0xb3cc6b, 0x8ca454, 0x6c7c44,
	// 0xe5d7a0, 0xe4bc54, 0xc59811, 0x856315,
	// 0xeaaab1, 0xd4646c, 0xac4c4c, 0x852e2e
	// };

	// from fernanda, 1/14/07, reordered for max distinguishability
	// private static int[] paletteInts={
	// 0xcc74ba,
	// 0x9c9cdc,
	// 0x9cdcda,
	// 0xb3cc6b,
	// 0xe4bc54,
	// 0xd4646c,
	//
	// 0x901d7e,
	// 0x54548b,
	// 0x548b89,
	// 0x6c7c44,
	// 0x856315,
	// 0x852e2e,
	//
	// 0xdc9cd4,
	// 0xc3c3ee,
	// 0xc3eeec,
	// 0xccdc9c,
	// 0xe5d7a0,
	// 0xeaaab1,
	//
	// 0xa44c95,
	// 0x7474b4,
	// 0x74b4b2,
	// 0x8ca454,
	// 0xc59811,
	// 0xac4c4c,
	// };

	// private static int[] paletteInts={
	//
	// 0xcc74ba,
	// 0x9c9cdc,
	// 0x9cdcda,
	// 0xb3cc6b,
	// 0xe4bc54,
	// 0xd4646c,
	//
	// 0xa44c95,
	// 0x7474b4,
	// 0x74b4b2,
	// 0x8ca454,
	// 0xc59811,
	// 0xac4c4c,
	//
	// 0xdc9cd4,
	// 0xc3c3ee,
	// 0xc3eeec,
	// 0xccdc9c,
	// 0xe5d7a0,
	// 0xeaaab1,
	//
	// 0x901d7e,
	// 0x54548b,
	// 0x548b89,
	// 0x6c7c44,
	// 0x856315,
	// 0x852e2e,
	//
	// };
	// static
	// {
	// palette=new Color[paletteIntsBasic.length];
	// for (int i=0; i<paletteIntsBasic.length; i++)
	// palette[i]=new Color(paletteIntsBasic[i]);
	// }

	// private static int[] paletteInts={
	// 0x9B0EEC,
	// 0xEC0ECF,
	// 0x0ECFEC,
	// 0x4384F4,
	// 0xEC0E5F,
	// 0x0EEC9B,
	// 0xF7CA7D,
	// 0xF4B343,
	// 0xEC2B0E,
	// 0x0EEC2B,
	// 0xCFEC0E
	// };

	// private static int[] paletteInts={
	// 0x669900,
	// 0xffcc00,
	// 0x0ECFEC,
	// 0xEC0E5F,
	// 0x0EEC9B,
	// 0xF7CA7D,
	// 0xF4B343,
	// 0xEC2B0E,
	// 0x0EEC2B,
	// 0x5FEC0E,
	// 0xEC9B0E
	// };

	// cao nan selected colors
	// private static int[] paletteInts={
	// 0x4f81bd,
	// 0xffcc00,
	// 0x92d050,
	// 0x93cddd
	// };

	// candy color
	// private static int[] paletteInts={
	// 0xd9007e,
	// 0xff00f6,
	// 0x8800cc,
	// 0x0099cc,
	// 0xff6600,
	// 0xffff00,
	// 0xace600
	// };

	// rainbow
	// private static int[] paletteInts = {
	// 0xea1801,
	// 0xeaa001,
	// 0xeae002,
	// 0x9ae028,
	// 0x23c5c8,
	// 0xb56cbd,
	// 0xe5458b
	// };

	static {
		// iphone theme
		paletteInts = new int[] { 0xFEFF01, 0x80FF09, 0x03FF11, 0x028088,
				0x0201FF, 0x4101C0, 0x810181, 0xC00042, 0xFF0003, 0xFF5502,
				0xFEAA02 };
		// jewels theme
		paletteInts = new int[] { 0xFF0099, 0x9618B1, 0x660099, 0x3366FF,
				0x33FFFF, 0x33FF00, 0xFFFF00, 0xFFD40E, 0xFF7030, 0xA50000 };
		// blue - yello - red
		paletteInts = new int[] { 0xFFD40E, 0xFFA40E, 0xFF740E, 0xFF190E,
				0xDE0C72, 0xA10BCF, 0x370BCF, 0x0B4FCF, 0x0B96CF, 0x0BCF27,
				0x99E40C, 0xF6FC0D };

		// candy color
		paletteInts = new int[] { 0xd9007e, 0xff00f6, 0x8800cc, 0x0099cc,
				0xff6600, 0xffff00, 0xace600 };

		// rainbow
		paletteInts = new int[] { 0xea1801, 0xeaa001, 0xeae002, 0x23c5c8,
				0xe5458b, 0x9ae028, 0xb56cbd, 0xA50000, 0x3366FF };

		// prefuse palette
		// paletteInts = ColorLib.getCategoryPalette(10);
		palette = new Color[paletteInts.length];
		for (int i = 0; i < paletteInts.length; i++) {
			palette[(i + 1) % paletteInts.length] = new Color(paletteInts[i]);
		}

		// for (int i=paletteInts.length-1; i>=0; i--) {
		// palette[paletteInts.length - i - 1]=new Color(paletteInts[i]);
		// }

	}

	/**
	 * Create a shortened version of the given string that fits in the specified
	 * number of pixels with the specified FontMetrics.
	 * 
	 * @param s
	 *            The String to fit.
	 * @param pixels
	 *            The available pixels.
	 * @param fm
	 *            The FontMetrics to measure with.
	 * @return A shortened version of the String that fits in the available
	 *         space.
	 */
	public static String fit(String s, int pixels, FontMetrics fm) {
		return fit(s, pixels, fm, 1);
	}

	/**
	 * Create a shortened version of the given string that fits in the specified
	 * number of pixels with the specified FontMetrics. If the "allOrNothing"
	 * parameter is true, the function returns an empty string if the original
	 * doesn't fit. This option is appropriate for numeric labels, where
	 * truncating would be bad.
	 * 
	 * @param s
	 *            The String to fit.
	 * @param pixels
	 *            The available pixels.
	 * @param fm
	 *            The FontMetrics to measure with.
	 * @param allOrNothing
	 *            Whether to return an empty String if it doesn't fit.
	 * @return A shortened version of the String that fits in the available
	 *         space.
	 */
	public static String fit(String s, int pixels, FontMetrics fm,
			boolean allOrNothing) {
		return fit(s, pixels, fm, s.length());
	}

	/**
	 * Create a shortened version of the given string that fits in the specified
	 * number of pixels with the specified FontMetrics. The minSize parameter
	 * lets you specify a minimum string size (in characters) below which it is
	 * not worth truncating and the function should just return an empty string.
	 * 
	 * @param s
	 *            The String to fit.
	 * @param pixels
	 *            The available pixels.
	 * @param fm
	 *            The FontMetrics to measure with.
	 * @param minSize
	 *            The minimum size, in characters.
	 * @return
	 */
	public static String fit(String s, int pixels, FontMetrics fm, int minSize) {
		int sw = fm.stringWidth(s);
		if (sw < pixels)
			return s;
		if (pixels > 15 && s.length() > minSize) // only make an effort if
		// possibly could be enough
		// space.
		{
			for (int i = s.length() - 1; i >= minSize; i--) {
				s = s.substring(0, i) + "...";
				if (fm.stringWidth(s) < pixels)
					return s;
			}
		}
		return "";
	}

	/**
	 * Draw text vertically, starting at the given coordinates.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param s
	 *            The string.
	 * @param x
	 *            The x-coordinate to draw at.
	 * @param y
	 *            The y-coordinate to draw at.
	 */
	public static void drawVertical(Graphics2D g, String s, int x, int y) {
		AffineTransform t = g.getTransform();
		g.rotate(-Math.PI / 2);
		g.drawString(s, -y, x);
		g.setTransform(t);
	}

	/**
	 * Draw text vertically and centered.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param s
	 *            The string.
	 * @param fm
	 *            The FontMetrics to use in measuring the String.
	 * @param x
	 *            The x-coordinate to draw at.
	 * @param y
	 *            The y-coordinate to draw at.
	 */
	public static void drawVerticalCenter(Graphics2D g, String s,
			FontMetrics fm, int x, int y) {
		drawVertical(g, s, x, y + fm.stringWidth(s) / 2);
	}

	/**
	 * Draw text horizontally centered.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param s
	 *            The string.
	 * @param fm
	 *            The FontMetrics to use in measuring the String.
	 * @param x
	 *            The x-coordinate of the center.
	 * @param y
	 *            The y-coordinate to draw at.
	 */
	public static void drawCenter(Graphics g, String s, FontMetrics fm, int x,
			int y) {
		g.drawString(s, x - fm.stringWidth(s) / 2, y);
	}

	/**
	 * Draw the given label, centered, within the given leftX and right X
	 * limits.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param s
	 *            The string.
	 * @param fm
	 *            The FontMetrics to use in measuring the String.
	 * @param leftX
	 *            The leftmost x limit.
	 * @param x
	 *            The target x coordinate of center of string.
	 * @param rightX
	 *            The rightmost x limit.
	 * @param y
	 *            The y-coordinate to draw at.
	 */
	public static void drawCenterFit(Graphics g, String s, FontMetrics fm,
			int leftX, int x, int rightX, int y) {
		s = fit(s, rightX - leftX, fm);
		int sw = fm.stringWidth(s) / 2;
		if (x - sw >= leftX && x + sw <= rightX)
			g.drawString(s, x - sw, y);
		else if (x - sw < leftX)
			g.drawString(s, leftX, y);
		else
			g.drawString(s, rightX - 2 * sw, y);
	}

	/**
	 * Draw text flush right.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param s
	 *            The string.
	 * @param fm
	 *            The FontMetrics to use in measuring the String.
	 * @param x
	 *            The x-coordinate of the rightmost part of the String.
	 * @param y
	 *            The y-coordinate to draw at.
	 */
	public static void drawRight(Graphics g, String s, FontMetrics fm, int x,
			int y) {
		g.drawString(s, x - fm.stringWidth(s), y);
	}

	/**
	 * Draw x-coordinate labels, possibly skipping or drawing diagonally if
	 * there is little room. If the skipIfNoRoom variable is false, then the
	 * labels will be diagonal when room runs out. If there is just no decent
	 * room (like 2 pix per label) then labels will be skipped no matter what.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param fm
	 *            The FontMetrics to use in measuring the String.
	 * @param labels
	 *            The String labels.
	 * @param x1
	 *            The left edge of the area to label.
	 * @param x2
	 *            The right edge of the area to label.
	 * @param y
	 *            The y-coordinate of the area, below which the labels should
	 *            fall.
	 * @param skipIfNoRoom
	 *            Whether to skip labels if there is not room for them.
	 */
	public static void drawXLabels(Graphics2D g, FontMetrics fm,
			String[] labels, int x1, int x2, int y, boolean skipIfNoRoom) {
		// This is complicated because if the labels don't fit, different
		// situations
		// require different tactics. For numerical or date info, it's OK to
		// just skip labels.
		// For categorical data, it's not OK (in fact this is a classic computer
		// infographics mistake).
		// In order to avoid skipping labels or truncating them unduly, we draw
		// them diagonally slanting
		// downwards. This is not a wonderful solution, but it's the best I can
		// think of.
		// We could just always do bar graphs horizontally instead of
		// vertically, but that seems
		// limiting.

		// check room.
		int room = (x2 - x1) / labels.length;
		boolean enoughRoom = true;
		for (int i = 0; i < labels.length; i++)
			if (fm.stringWidth(labels[i]) > room - 4) {
				enoughRoom = false;
				break;
			}

		// if room, draw them horizontally.
		if (enoughRoom) {
			for (int i = 0; i < labels.length; i++) {
				int x = labels.length == 1 ? (x1 + x2) / 2 : x1
						+ (i * (x2 - x1)) / (labels.length - 1);
				drawCenter(g, labels[i], fm, x, y);
			}
			return;
		}

		if (skipIfNoRoom || labels.length > (x2 - x1) / 8) // skip labels to
		// make room
		{
			int rx = -1000;
			for (int i = 0; i < labels.length; i++) {
				int x = labels.length == 1 ? (x1 + x2) / 2 : x1
						+ (i * (x2 - x1)) / (labels.length - 1);
				int sw = fm.stringWidth(labels[i]);
				if (x - sw / 2 > rx) {
					drawCenter(g, labels[i], fm, x, y);
					rx = x + sw / 2 + 3;
				}
			}
			return;
		}

		// otherwise, draw diagonally.
		x1 += 10;
		x2 += 10;
		AffineTransform t = g.getTransform();
		g.rotate(Math.PI / 4);
		double s = Math.sqrt(.5);
		for (int i = 0; i < labels.length; i++) {
			int x = labels.length == 1 ? (x1 + x2) / 2 : x1 + (i * (x2 - x1))
					/ (labels.length - 1);
			g.drawString(labels[i], (int) (s * (x + y)) - 17,
					(int) (s * (y - x)));
			// g.drawString(labels[i], (int)(s*(x-y)), (int)(s*(x+y)));
		}
		g.setTransform(t);
	}

	/**
	 * Get the color for the i-th element in a series.
	 * 
	 * @param i
	 *            The index of the element.
	 * @return The color for this index.
	 */
	public static Color getColor(int i) {
		// new method: use fernanda's
		int n = palette.length;
		if (i < n)
			return palette[i];
		Color c = palette[i % n];
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				new float[3]);
		return new Color(Color.HSBtoRGB(
				(float) (hsb[0] + ((Numbers.PHI * (i / n)) % 1) / n), hsb[1],
				hsb[2]));
		// return new
		// Color(Color.HSBtoRGB((float)(hsb[0]+(Numbers.PHI*(i/n)))%1, hsb[1],
		// hsb[2]));
		// old, purely calculated method:
		// float hue=(float)(.3f+i*Numbers.PHI/7)%1;
		// return new Color(Color.HSBtoRGB(hue, .2f,
		// .6f+.35f*(1-hue)-.1f*(i%2)));
	}

	public static float getHue(int i) {
		int n = paletteHues.length;
		if (i < n)
			return paletteHues[i];
		return (paletteHues[i % n]);
	}

	// I think this is obsolete!
	/**
	 * Get the highlight color for the i-th element in a series.
	 * 
	 * @param i
	 *            The index of the element.
	 * @return The color for this index.
	 */
	// public Color getHighlightColor(int i)
	// {
	// Color c=getColor(i).darker();
	// return c;
	// old, purely calculated method:
	// float hue=(float)(.3f+i*Numbers.PHI/9)%1;
	// return new Color(Color.HSBtoRGB(hue, .9f, .7f+.25f*(1-hue)-.15f*(i%2)));
	// }

	/**
	 * Draw a callout (or mouseover text box) with the given text for the given
	 * shape. The callout will remain inside the given component and avoid
	 * overlapping the given shape.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param shape
	 *            The Shape to draw the callout around.
	 * @param text
	 *            The messages to draw in the box.
	 * @param c
	 *            The component to draw on.
	 */
	public static void drawCallout(Graphics g, Shape shape, String[] text,
			Component c) {
		int w = c.getSize().width, h = c.getSize().height;
		drawCallout(g, shape, text, null, c, w, h);
	}

	/**
	 * Draw a callout (or mouseover text box) with the given text for the given
	 * shape. The callout will remain inside the given component and avoid
	 * overlapping the given shape.
	 * 
	 * @param g
	 *            The Graphics context.
	 * @param shape
	 *            The Shape to draw the callout around.
	 * @param text
	 *            The messages to draw in the box.
	 * @param colors
	 *            The bullet colors to use for each of the messages, or null if
	 *            none.
	 * @param c
	 *            The component to draw on.
	 */
	public static void drawCallout(Graphics g, Shape shape, String[] text,
			Color[] colors, Component c) {
		int w = c.getSize().width, h = c.getSize().height;
		drawCallout(g, shape, text, colors, c, w, h);
	}

	/**
	 * Draw a callout (or mouseover text box) with the given text for the given
	 * shape. The callout will remain inside the given (w,h) bounds and will
	 * avoid overlapping the given shape. Colored dots can be specified for
	 * entry item in text.
	 * 
	 * @param g1
	 *            The Graphics context.
	 * @param shape
	 *            The Shape to draw the callout around.
	 * @param text
	 *            The messages to draw in the box.
	 * @param colors
	 *            Any bullet colors for each of the messages or null if no
	 *            colors
	 * @param c
	 *            The component to draw on.
	 * @param w
	 *            The width to stay within.
	 * @param h
	 *            The height to stay within.
	 */
	public static void drawCallout(Graphics g1, Shape shape, String[] text,
			Color[] colors, Component c, int w, int h) {
		Graphics2D g = (Graphics2D) g1;

		FontMetrics fm = c.getFontMetrics(g.getFont());
		int mw = 40;
		for (int i = 0; i < text.length; i++) {
			if (text[i].length() > 80)
				text[i] = text[i].substring(0, 79) + "...";
			mw = Math.max(mw, fm.stringWidth(text[i]));
		}
		int p = 7;
		mw += 2 * p;
		int mh = p / 2 + fm.getHeight() * text.length;
		Rectangle r = shape.getBounds();
		Point textCorner = new Point(r.x + r.width + 2, Math.max(0, r.y - mh));
		if (textCorner.x + mw >= w)
			textCorner.x = r.x - mw - 2;
		if (textCorner.x < 0)
			textCorner.x = 0;
		if (textCorner.y + mh >= h)
			textCorner.y = h - 1 - mh;

		g.setColor(calloutShadow);
		int rr = 12;
		g.fillRoundRect(textCorner.x + 3, textCorner.y + 3, mw, mh, rr, rr);
		g.setColor(calloutBackgroundColor);
		g.fillRoundRect(textCorner.x, textCorner.y, mw, mh, rr, rr);
		g.setColor(calloutLabelColor);
		g.drawRoundRect(textCorner.x, textCorner.y, mw, mh, rr, rr);
		int ty = p + fm.getHeight() / 2;

		for (int i = 0; i < text.length; i++) {
			int pp = p;
			if (colors != null) {
				if (colors[i] != null) {
					pp = pp + 8;
					g.setColor(colors[i]);
					g.fillRect(textCorner.x + p, textCorner.y + ty - 8, 7, 7);
				}
			}
			g.setColor(calloutLabelColor);
			g.drawString(text[i], textCorner.x + pp, textCorner.y + ty);
			ty += fm.getHeight();
		}
	}

	/**
	 * Return a version of the given color with the specified alpha value. Alpha
	 * here is a double value between 0 and 1, with 0 transparent and 1 opaque.
	 * 
	 * @param c
	 *            The original color.
	 * @param alpha
	 *            A double value between 0 and 1, with 0 transparent and 1
	 *            opaque.
	 * @return A version of the given color with the specified alpha value.
	 */
	public static Color alpha(Color c, double alpha) {
		if (alpha == 1)
			return c;
		return new Color(c.getRed(), c.getGreen(), c.getBlue(),
				(int) (255 * alpha));
	}

	public static void setSplitPaneDividerColor(JSplitPane splitPane,
			Color newDividerColor) {

		UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
		uidefs.put("SplitPane.background", new ColorUIResource(Color.white));
		uidefs.put("SplitPane.shadow", new ColorUIResource(Color.lightGray));
		uidefs.put("SplitPane.darkShadow", new ColorUIResource(Color.lightGray));
		uidefs.put("SplitPane.highlight", new ColorUIResource(Color.lightGray));

		SplitPaneUI splitUI = splitPane.getUI();
		if (splitUI instanceof BasicSplitPaneUI) { // obviously this will not
													// work if the ui doen't
													// extend Basic...
			BasicSplitPaneDivider div = ((BasicSplitPaneUI) splitUI)
					.getDivider();
			assert div != null;
			Border divBorder = div.getBorder();
			Border newBorder = null;
			Border colorBorder = null;

			class BGBorder implements Border {
				private Color color;
				private final Insets NO_INSETS = new Insets(0, 0, 0, 0);

				private BGBorder(Color color) {
					this.color = color;
				}

				Rectangle r = new Rectangle();

				public void paintBorder(Component c, Graphics g, int x, int y,
						int width, int height) {
					g.setColor(color);
					g.fillRect(x, y, width, height);
					if (c instanceof Container) {
						Container cont = (Container) c;
						for (int i = 0, n = cont.getComponentCount(); i < n; i++) {
							Component comp = cont.getComponent(i);
							comp.getBounds(r);
							Graphics tmpg = g.create(r.x, r.y, r.width,
									r.height);
							comp.paint(tmpg);
							tmpg.dispose();
						}
					}
				}

				public Insets getBorderInsets(Component c) {
					return NO_INSETS;
				}

				public boolean isBorderOpaque() {
					return true;
				}
			}

			colorBorder = new BGBorder(newDividerColor);

			if (divBorder == null) {
				newBorder = colorBorder;
			} else {
				newBorder = BorderFactory.createCompoundBorder(divBorder,
						colorBorder);
			}
			div.setBorder(newBorder);
		}
	}
}
