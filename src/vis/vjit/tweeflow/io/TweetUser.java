package vis.vjit.tweeflow.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.User;

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

public class TweetUser implements User {

	private static final long serialVersionUID = -5295066786429395098L;
	long id = 0;
	String name = "";
	String screename = "";
	String location = "";
	String profileImage = null;
	
	public TweetUser() {
	}
	
	public int compareTo(User that) {
		return (int) (this.id - that.getId());
	}

	public RateLimitStatus getRateLimitStatus() {
		return null;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screename;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return null;
	}

	public boolean isContributorsEnabled() {
		return false;
	}

	public URL getProfileImageURL() {
		try {
            return new URL(profileImage);
        } catch (MalformedURLException ex) {
            return null;
        }
	}

	public URL getProfileImageUrlHttps() {
		return null;
	}

	public URL getURL() {
		return null;
	}

	public boolean isProtected() {
		return false;
	}

	public int getFollowersCount() {
		return 0;
	}

	public Status getStatus() {
		return null;
	}

	public String getProfileBackgroundColor() {
		return null;
	}

	public String getProfileTextColor() {
		return null;
	}

	public String getProfileLinkColor() {
		return null;
	}

	public String getProfileSidebarFillColor() {
		return null;
	}

	public String getProfileSidebarBorderColor() {
		return null;
	}

	public boolean isProfileUseBackgroundImage() {
		return false;
	}

	public boolean isShowAllInlineMedia() {
		return false;
	}

	public int getFriendsCount() {
		return 0;
	}

	public Date getCreatedAt() {
		return null;
	}

	public int getFavouritesCount() {
		return 0;
	}

	public int getUtcOffset() {
		return 0;
	}

	public String getTimeZone() {
		return null;
	}

	public String getProfileBackgroundImageUrl() {
		return null;
	}

	public String getProfileBackgroundImageUrlHttps() {
		return null;
	}

	public boolean isProfileBackgroundTiled() {
		return false;
	}

	public String getLang() {
		return null;
	}

	public int getStatusesCount() {
		return 0;
	}

	public boolean isGeoEnabled() {
		return false;
	}

	public boolean isVerified() {
		return false;
	}

	public boolean isTranslator() {
		return false;
	}

	public int getListedCount() {
		return 0;
	}

	public boolean isFollowRequestSent() {
		return false;
	}
	
	public int getAccessLevel() {
		return 0;
	}
}
