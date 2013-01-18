package vis.vjit.tweeflow.util;


public class Numbers {

	public static final double DOUBLE_PI = 2 * Math.PI;
	
	public static final double HALF_PI = Math.PI / 2;
	
	public static final double QUATER_PI = Math.PI / 4;
	
	/**
	 * Natural log of 10.
	 */
	public static final double LOG10 = Math.log(10);
	
	public static final double LOG2 = Math.log(2);
	
	/**
	 * The golden ratio.
	 */
	public static final double PHI = (1+Math.sqrt(5))/2;
	
	/**
	 * Whether a number is "bad", that is, is NaN or infinite.
	 * @param x
	 * @return
	 */
	public static final boolean isBad(double x)
	{
		return Double.isNaN(x) || Double.isInfinite(x);
	}
	
	/**
	 * Base-10 logarithm
	 * 
	 * @param x A double value.
	 * @return Its base 10 logarithm.
	 */
	public static final double log10(double x)
	{
		if (x<=0) throw new IllegalArgumentException("log of: "+x);
		return Math.log(x)/LOG10;
	}
	
	public static final double log2(double x)
	{
		if (x<=0) throw new IllegalArgumentException("log of: "+x);
		return Math.log(x)/LOG2;
	}
	
	/**
	 * Return min(max(x,low),high). Or in English: clamp the value of x between low and high.
	 * 
	 * @param x A double.
	 * @param low The lowest end of the range.
	 * @param high The highest end of the range.
	 * @return The value of x "clamped" in the range.
	 */
	public static final double clamp(double x, double low, double high)
	{
		return Math.max(low, Math.min(x, high));
	}
	
	/**
	 * Return min(max(x,low),high). Or in English: clamp the value of x between low and high.
	 * 
	 * @param x An int.
	 * @param low The lowest end of the range.
	 * @param high The highest end of the range.
	 * @return The value of x "clamped" in the range.
	 */
	public static final int clamp(int x, int low, int high)
	{
		return Math.max(low, Math.min(x, high));
	}
	
	/**
	 * Translate degrees to radians.
	 * 
	 * @param a An angle in degrees.
	 * @return The same angle in radians.
	 */
	public static final double d2r(double a)
	{
		return (a/180)*Math.PI;
	}
	
	/**
	 * Linear interpolation between two values: computes t*y+(1-t)*x.
	 * @param x First value.
	 * @param y Second value.
	 * @param t Interpolation constant.
	 * @return The interpolated value: t*y+(1-t)*x.
	 */
	public static double interpolate(double x, double y, double t)
	{
		return t*y+(1-t)*x;
	}
	
	public static double similarity(float[] v1, float[] v2) {
		float sum = 0;
		float aa = 0, bb = 0;
		if(v1 == null || v2 == null) {
			return 0;
		}
		for(int i = 0; i < v1.length; ++i) {
			sum += v1[i] * v2[i];
			aa += v1[i] * v1[i];
			bb += v2[i] * v2[i];
		}
		return (sum / (Math.sqrt(aa) * Math.sqrt(bb)));
	}
	
	public static double distance(float[] v1, float[] v2) {
		float sum = 0;
		if(v1 == null || v2 == null) {
			return 0;
		}
		for(int i = 0; i < v1.length; ++i) {
			sum += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return Math.sqrt(sum);
	}
	
	public static double distanceSq(float[] v1, float[] v2) {
		float sum = 0;
		if(v1 == null || v2 == null) {
			return 0;
		}
		for(int i = 0; i < v1.length; ++i) {
			sum += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return sum;
	}
	
	public static double dist(float[] v1, float[] v2, float n) {
		float sum = 0;
		if(v1 == null || v2 == null) {
			return 0;
		}
		for(int i = 0; i < v1.length; ++i) {
			sum += Math.pow((v1[i] - v2[i]), n);
		}
		return sum;
	}
}
