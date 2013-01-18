package vis.vjit.tweeflow.io;

import java.util.Date;

import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

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

public class TweetStatus implements Status {

	private static final long serialVersionUID = 4254492365062293573L;
	
	private Tweet m_tweet = null;
	
	private TweetUser m_user = null;
	
	private boolean bretweet = false;
	
	private int retweetcnt = 0;
	
	private long[] contributors = null;
	
	private TweetStatus org = null;
	
	private long id = 0;
	
	public TweetStatus() {
	}
	
	public TweetStatus(Tweet tweet) {
		id = tweet.getId();
		m_tweet = tweet;
		m_user = new TweetUser();
		m_user.id = m_tweet.getFromUserId();
		m_user.name = m_tweet.getFromUser();
		m_user.screename = m_tweet.getFromUser();
		m_user.profileImage = m_tweet.getProfileImageUrl();
		m_user.location = m_tweet.getLocation();
		bretweet = m_tweet.getText().indexOf("RT") == 0;
		UserMentionEntity[] entity = m_tweet.getUserMentionEntities();
		if(entity != null && entity.length != 0) {
			contributors = new long[entity.length];
			for(int i = 0; i < entity.length; ++i) {
				contributors[i] = entity[i].getId();
			}
			retweetcnt = contributors.length;
			if(bretweet) {
				org = new TweetStatus();
				org.id = System.currentTimeMillis();
				org.m_tweet = tweet;
				org.m_user = new TweetUser();
				org.m_user.id = entity[0].getId();
				org.m_user.name = entity[0].getName();
				org.m_user.screename = entity[0].getScreenName();
				org.m_user.location = m_tweet.getLocation();
				org.m_user.profileImage = "";
			}
		}
	}
	
	public Tweet tweet() {
		return m_tweet;
	}
	
	public int compareTo(Status o) {
		return getCreatedAt().compareTo(o.getCreatedAt());
	}

	public HashtagEntity[] getHashtagEntities() {
		return m_tweet.getHashtagEntities();
	}

	public MediaEntity[] getMediaEntities() {
		return m_tweet.getMediaEntities();
	}

	public URLEntity[] getURLEntities() {
		return m_tweet.getURLEntities();
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		return m_tweet.getUserMentionEntities();
	}

	public Annotations getAnnotations() {
		return m_tweet.getAnnotations();
	}

	public long[] getContributors() {
		return contributors;
	}

	public Date getCreatedAt() {
		return m_tweet.getCreatedAt();
	}

	public GeoLocation getGeoLocation() {
		return m_tweet.getGeoLocation();
	}
	
	public long getId() {
		return id;
	}

	public Place getPlace() {
		return m_tweet.getPlace();
	}

	public long getRetweetCount() {
		return retweetcnt;
	}

	public Status getRetweetedStatus() {
		return org;
	}

	public String getSource() {
		return m_tweet.getSource();
	}

	public String getText() {
		return m_tweet.getText();
	}

	public User getUser() {
		return m_user;
	}
	
	public boolean isRetweet() {
		return bretweet;
	}
	
	public int getAccessLevel() {
		throw new RuntimeException("not supported!!");
	}

	public RateLimitStatus getRateLimitStatus() {
		throw new RuntimeException("not supported!!");
	}
	
	public String getInReplyToScreenName() {
		throw new RuntimeException("not supported!!");
	}

	public long getInReplyToStatusId() {
		throw new RuntimeException("not supported!!");
	}

	public long getInReplyToUserId() {
		throw new RuntimeException("not supported!!");
	}

	public boolean isFavorited() {
		throw new RuntimeException("not supported!!");
	}

	public boolean isRetweetedByMe() {
		throw new RuntimeException("not supported!!");
	}

	public boolean isTruncated() {
		throw new RuntimeException("not supported!!");
	}
}
