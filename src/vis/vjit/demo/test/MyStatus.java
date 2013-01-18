package vis.vjit.demo.test;

import java.util.Date;

import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

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
public class MyStatus implements Status {

	private static final long serialVersionUID = -8907049763546266160L;

	private Status m_core = null;
	
	private Date m_date = null;
	
	public MyStatus(Status s) {
		this(s, s.getCreatedAt().getTime());
	}
	
	public MyStatus(Status s, long time) {
		m_core = s;
		m_date = new Date(time);
	}
	
	@Override
	public int compareTo(Status o) {
		return m_core.compareTo(o);
	}

	@Override
	public int getAccessLevel() {
		return m_core.getAccessLevel();
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		return m_core.getRateLimitStatus();
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		return m_core.getHashtagEntities();
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		return m_core.getMediaEntities();
	}

	@Override
	public URLEntity[] getURLEntities() {
		return m_core.getURLEntities();
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		return m_core.getUserMentionEntities();
	}

	@Override
	public Annotations getAnnotations() {
		return m_core.getAnnotations();
	}

	@Override
	public long[] getContributors() {
		return m_core.getContributors();
	}
	
	public void setCreateAt(Date date) {
		m_date = date;
	}

	@Override
	public Date getCreatedAt() {
		return m_date;
	}

	@Override
	public GeoLocation getGeoLocation() {
		return m_core.getGeoLocation();
	}

	@Override
	public long getId() {
		return m_core.getId();
	}

	@Override
	public String getInReplyToScreenName() {
		return m_core.getInReplyToScreenName();
	}

	@Override
	public long getInReplyToStatusId() {
		return m_core.getInReplyToStatusId();
	}

	@Override
	public long getInReplyToUserId() {
		return m_core.getInReplyToUserId();
	}

	@Override
	public Place getPlace() {
		return m_core.getPlace();
	}

	@Override
	public long getRetweetCount() {
		return m_core.getRetweetCount();
	}

	@Override
	public Status getRetweetedStatus() {
		return m_core.getRetweetedStatus();
	}

	@Override
	public String getSource() {
		return m_core.getSource();
	}

	@Override
	public String getText() {
		return m_core.getText();
	}

	@Override
	public User getUser() {
		return m_core.getUser();
	}

	@Override
	public boolean isFavorited() {
		return m_core.isFavorited();
	}

	@Override
	public boolean isRetweet() {
		return m_core.isRetweet();
	}

	@Override
	public boolean isRetweetedByMe() {
		return m_core.isRetweetedByMe();
	}

	@Override
	public boolean isTruncated() {
		return m_core.isTruncated();
	}

}
