package vis.vjit.tweeflow.util.time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
public class TimeHelper {

	public static final String PATTERN_DATE = "EEE MMM dd HH:mm:ss z yyyy";
	
	public static final String PATTERN_HHMMSSZ = "HH:mm:ss z";

	public static final String PATTERN_HHMMSS = "HH:mm:ss";

	public static final String PATTERN_MMSS = "mm:ss";

	private static SimpleDateFormat m_sdf = new SimpleDateFormat("", Locale.US);

	public static void applyPattern(String format) {
		m_sdf.applyPattern(format);
	}

	public static String format(long time) {
		return m_sdf.format(new Date(time));
	}

	public static String format(Date date) {
		return m_sdf.format(date);
	}

	public static void setTimeZom(String timezone) {
		m_sdf.setTimeZone(TimeZone.getTimeZone(timezone));
	}
}
