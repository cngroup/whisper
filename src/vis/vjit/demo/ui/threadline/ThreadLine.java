package vis.vjit.demo.ui.threadline;

import twitter4j.User;
import twitter4j.UserMentionEntity;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.data.VisUser;
import davinci.data.elem.IEdge;
import davinci.data.graph.Graph;

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
public class ThreadLine extends Graph<VisUser> {

	private static final long serialVersionUID = 1072747417973377972L;

	public long current = 0;

	public long start = Long.MAX_VALUE;

	public ThreadLine() {
		this(null);
	}

	public ThreadLine(VisTube[] tube) {
		this.setTube(tube);
		this.setID("flow");
	}

	public VisUser getHead() {
		return m_nodes.isEmpty() ? null : m_nodes.get(0);
	}

	public void setTube(VisTube[] tubes) {
		if (null == tubes) {
			this.clear();
			return;
		}
		for (int idx = 0; idx < tubes.length; ++idx) {
			VisTube tube = tubes[idx];
			VisTweet t = tube.source();
			if (t == null || t.status() == null) {
				return;
			}
			User u = tube.source().status().getUser();
			VisUser orguser = getNode(u.getId() + "");
			if (null == orguser) {
				orguser = new VisUser(u, t.getTime());
				orguser.setSentiment(t.getSentiment());
				this.addNode(orguser);
			}

			VisTweet[] tweets = tube.getTweets();
			for (int i = 0; i < tweets.length; ++i) {
				u = tweets[i].status().getUser();
				VisUser user = getNode(u.getId() + "");
				if (user == null) {
					user = new VisUser(u, tweets[i].getTime());
					user.setSentiment(tweets[i].getSentiment());
					this.addNode(user);
				}
			}

			VisUser user1 = null, user2 = null;
			UserMentionEntity[] users = null;
			for (int i = 0; i < tweets.length; ++i) {
				user1 = getNode(tweets[i].status().getUser().getId() + "");
				user2 = null;
				users = tweets[i].status().getUserMentionEntities();
				if (users == null || users.length == 0) {
					continue;
				}
				for (int j = 0; j < users.length; ++j) {
					user2 = getNode(users[j].getId() + "");
					if (user2 != null) {
						break;
					}
				}
				if (user2 == null) {
					continue;
				}
				
				// user1 retweet user 2, user2's time is earlier than user1's
				// time
				IEdge<VisUser> edge = getEdge(user2, user1);
				if (edge == null) {
					edge = addEdge(user2, user1);
				}
				edge.setWeight(edge.getWeight() + 1);
			}
		}
	}

	public void clear() {
		super.clear();
//		this.current = 0;
//		this.start = Long.MAX_VALUE;
	}
}