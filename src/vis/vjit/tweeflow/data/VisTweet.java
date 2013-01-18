package vis.vjit.tweeflow.data;

import java.util.Vector;

import twitter4j.Status;
import davinci.data.elem.VisualNode;

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
public class VisTweet extends VisualNode implements IWhisperElem, Cloneable {

	private static final long serialVersionUID = -3627239748116769978L;
	
	private Status m_status = null;
	
	private boolean bActive = false;
	
	private long m_lastActiveTime = 0;
	
	private double m_sentiments = 0;
	
	private VisTopic m_cluster = null;
	
	private Vector<VisTube> m_tubes = null;
	
	private VisTweet m_next = null;
	
	private double m_angle = 0;
	
	public VisTweet() {
		this(null);
	}
	
	public VisTweet(Status status) {
		this(status, System.currentTimeMillis());
	}
	
	public VisTweet(Status status, long time) {
		setTweet(status);
		m_tubes = new Vector<VisTube>();
		m_lastActiveTime = time; 
	}
	
	public boolean isRetweet() {
		return m_status.isRetweet();
	}
	
	public void setNext(VisTweet tweet) {
		m_next = tweet;
	}
	
	public VisTweet next() {
		return m_next;
	}
		
	public void setSentiment(double s) {
		m_sentiments = s;
	}
	
	public double getSentiment() {
		return m_sentiments;
	}
	
	public void setTweet(Status status) {
		m_status = status;
		this.setID(m_status.getId() + "");
		this.setLabel(m_status.getText());
	}
	
	public Status status() {
		return m_status;
	}
	
	public boolean isActive() {
		return bActive;
	}
	
	public void setTopic(VisTopic t) {
		m_cluster = t;
	}
	
	public VisTopic getTopic() {
		return m_cluster;
	}
	
	public void setActive(boolean active) {
		this.bActive = active;
	}
	
	public void active(long time) {
		m_lastActiveTime = time;
	}
	
	public synchronized long getLastActiveTime() {
		return m_lastActiveTime;
	}
	
	public synchronized int getTubeSize() {
		return m_tubes.size();
	}
	
	public synchronized void addTube(VisTube tube) {
		m_tubes.add(tube);
	}
	
	public synchronized VisTube getTube(int i) {
		return m_tubes.get(i);
	}
	
	public synchronized boolean removeTube(VisTube tube) {
		return m_tubes.remove(tube);
	}
	
	public synchronized VisTube[] getTubes() {
		return m_tubes.toArray(new VisTube[]{});
	}
	
	public long getTime() {
		return m_status.getCreatedAt().getTime();
	}
	
	public void setAngle(double angle) {
		m_angle = angle;
	}
	
	public double getAngle() {
		return m_angle;
	}
	
	public double getWeight() {
		if(m_status == null) {
			return super.getWeight();
		}
		return m_status.getUser().getFollowersCount();
	}
}
