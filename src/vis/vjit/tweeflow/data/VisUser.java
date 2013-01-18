package vis.vjit.tweeflow.data;

import twitter4j.User;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;

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
public class VisUser extends FlowerNode implements IWhisperElem {

	private static final long serialVersionUID = -93497541161732002L;

	private User m_user = null;
	
	private boolean bActive = false;
	
	private long m_lastActiveTime = 0;
	
	private GeoInfoV3 m_info = null;
	
	private long m_time = 0;
	
	public VisUser() {
		this(null);
	}
	
	public VisUser(User user) {
		this(user, 0);
	}
	
	public VisUser(User user, long time) {
		setUser(user);
		m_time = time;
	}
	
	public long getTime() {
		return m_time;
	}
	
	public GeoInfoV3 getGeoInfo() {
		return m_info;
	}
	
	public void setGeoInfo(GeoInfoV3 info) {
		m_info = info;
	}
	
	public void setUser(User user) {
		m_user = user;
		this.setID("" + user.getId());
		this.setLabel(user.getName());
	}
	
	public String getID() {
		return "" + m_user.getId();
	}
	
	public User user() {
		return m_user;
	}
	
	public void setActive(boolean bActive) {
		this.bActive = bActive;
	}
	
	public boolean isActive() {
		return bActive;
	}
	
	public void setLogitude(long log) {
		this.m_longitude = log;
	}
	
//	public void increaseWeight() {
//		double wei = getWeight();
//		setWeight(wei + 1);
//	}
//	
//	public void decreaseWeight() {
//		double wei = getWeight();
//		setWeight(wei - 1);
//	}
	
	public double getWeight() {
		return m_user == null ? super.getWeight() : m_user.getFollowersCount();
	}
	
	public void active(long time) {
		m_lastActiveTime = time;
	}
	
	public long getLastActiveTime() {
		return m_lastActiveTime;
	}
	
	public boolean isCollapsed() {
		return true;
	}
}
