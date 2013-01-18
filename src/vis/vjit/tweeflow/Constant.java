package vis.vjit.tweeflow;

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
public class Constant {
	
//	public static int USER_LIFE = 3;
	
	public static int ACTIVE_TWEET_LIFE = 3;

	public static int INACTIVE_TWEET_LIFE = 1; // real life is TIME_LIFE * TIME_INTERVAL
	
	public static long TIME_BIN_INTEVAL = 60 * 1000; 
	
	public static long LOADING_TIME_INTERVAL = 200;
	
	public static int TIME_WINDOW_SIZE = 3;
	
	public static int TIME_SUB_WINDOW_SIZE = 20;
	
	public static long TIME_INTERVAL = TIME_BIN_INTEVAL * TIME_WINDOW_SIZE / TIME_SUB_WINDOW_SIZE;
	
	public static double USER_GROUP_SQARE_RADII = 1.5;
	
	public static double FOLLOWER_CNT_RADII = 1;
	
	public static double CENTER_DISK_RADII = 35;
	
	public static void reset() {
		TIME_WINDOW_SIZE = 3;
		TIME_SUB_WINDOW_SIZE = 20;
		ACTIVE_TWEET_LIFE = TIME_SUB_WINDOW_SIZE;
		INACTIVE_TWEET_LIFE = ACTIVE_TWEET_LIFE / 2;
		TIME_BIN_INTEVAL = 60 * 1000;
		TIME_INTERVAL = TIME_BIN_INTEVAL * TIME_WINDOW_SIZE / TIME_SUB_WINDOW_SIZE;
		USER_GROUP_SQARE_RADII = 1.5;
		FOLLOWER_CNT_RADII = 1;
		CENTER_DISK_RADII = 35;
	}
	
	public static void setup(String dname) {
	}
	
}
