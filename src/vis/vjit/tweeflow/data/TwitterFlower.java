package vis.vjit.tweeflow.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import twitter4j.Status;
import twitter4j.User;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.data.filter.IFlowerFilter;
import vis.vjit.tweeflow.layout.GroupComparator;
import vis.vjit.tweeflow.util.SentimentAnalyzer;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import davinci.data.Dimension;
import davinci.data.IDimension;
import davinci.data.tree.Tree;

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
public class TwitterFlower extends Tree<FlowerNode> implements IFlowerFilter<VisTweet> {

	private static final long serialVersionUID = 192802634117739138L;

	public static final FlowerNode[] EMPTY = new FlowerNode[0];

	private Lock m_lock;

	private VisTopic m_focus = null;
	
	private IDimension<Double> m_weidim = null;
	
	private IFlowerFilter<VisTweet> m_filter = null;

	public TwitterFlower() {
		this(null);
	}

	public TwitterFlower(VisTopic topic) {
		m_focus = topic;
		m_lock = new ReentrantLock();
		m_weidim = new Dimension<Double>("weight", "weight", IDimension.META_TYPE_NUMERICAL);
		setComparator(new GroupComparator());
		setTreeRoot(topic);
		topic.setOwner(this);
		addDimension("nodes", m_weidim);
	}
	
	public void setFilter(IFlowerFilter<VisTweet> filter) {
		m_filter = filter;
	}
	
	public IFlowerFilter<VisTweet> getFilter() {
		return m_filter;
	}

	// FOR TRADITIONAL CIRCLE LAYOUT
	public synchronized void zoom(VisTopic topic) {
		String append = "(" + m_root.getLabel() + ")";
		m_focus.setCollapse(true);
		m_focus = topic;
		m_focus.setLabel(m_focus.getLabel() + append);
		m_focus.setCharge(1);
		m_focus.setCollapse(false);
	}

	public synchronized void zoomOut() {
		VisTopic topic = (VisTopic) getParent(m_focus);
		if (topic == null) {
			return;
		}
		String label = m_focus.getLabel();
		int idx = label.indexOf("(");
		label = label.substring(0, idx);
		m_focus.setLabel(label);
		m_focus.setCollapse(true);
		m_focus = topic;
		m_focus.setCharge(1);
		m_focus.setCollapse(false);
	}

	// FOR BALLOON LAYOUT
	// public void zoom(VisTopic topic) {
	// m_focus = topic;
	// topic.setCharge(1);
	// topic.setCollapse(false);
	// }
	//
	// public void zoomOut() {
	// VisTopic topic = (VisTopic)getParent(m_focus);
	// if(topic == null) {
	// return;
	// }
	// m_focus.setCollapse(true);
	// m_focus = topic;
	// m_focus.setCharge(1);
	// m_focus.setCollapse(false);
	// }

	public synchronized VisTopic focus() {
		return m_focus;
	}

	public void update(VisTopic topic, long time) {
		if(topic == null) {
			return;
		}
		topic.setTime(time);
		FlowerNode[] nodes = super.getChildren(topic, EMPTY);
		if (nodes != null) {
			for (int i = 0; i < nodes.length; ++i) {
				if (nodes[i] instanceof VisTopic) {
					update((VisTopic) nodes[i], time);
				}
			}
		}
	}
	
	public void addUser(VisUser user) {
		GeoInfoV3 info = user.getGeoInfo();
		VisTopic node = (VisTopic) getNode(info.country);
		if (node == null) {
			node = new VisTopic();
			node.setID(info.country);
			node.setLabel(info.country);
			node.setLongitude(info.clogitude);
			node.setCollapse(true);
			node.setOwner(this);
			addChild(m_root, node);
		}
		node.setTime(((VisTopic)m_root).getTime());
		
		node.setWeight(node.getWeight() + user.getWeight());
		String id = info.country + "/" + info.state;
		VisTopic area = (VisTopic) getNode(id);
		if (area == null) {
			area = new VisTopic();
			area.setID(id);
			area.setLabel(info.state);
			area.setLongitude(info.slogitude);
			area.setOrder(info.order);
			area.setCollapse(true);
			area.setOwner(this);
			addChild(node, area);
		}
		area.setTime(((VisTopic)m_root).getTime());
		user.setOwner(this);
		user.setLongitude(info.logitude == -1 ? (info.east + info.west) / 2 : info.logitude);
		addChild(area, user);
		m_root.setWeight(m_root.getWeight() + user.getWeight());
		node.setWeight(node.getWeight() + user.getWeight());
		area.setWeight(area.getWeight() + user.getWeight());
		user.setCollapse(true);
	}

	public VisTweet retweet(Status s, GeoInfoV3 info) {
		User retweeter = s.getUser();
		Status org = s.getRetweetedStatus();
		String id = org.getId() + "";
		VisTube tube = null, target = null;
		
		// add user into flower
		double value = SentimentAnalyzer.sentiment(s.getText());
		VisUser user = (VisUser)getNode(retweeter.getId() + "");
		if (user == null) {
			user = new VisUser(retweeter);
			user.setGeoInfo(info);
//			user.increaseWeight();
			addUser(user);
		} else {
//			user.increaseWeight();
//			VisTopic p = (VisTopic) getParent(user);
//			while (p != null) {
//				p.setWeight(p.getWeight() + user.getWeight());
//				p = (VisTopic) getParent(p);
//			}
		}
		user.setSentiment(value);

		VisTopic p = (VisTopic) getParent(user);
		user.active(p.getTime());
		FlowerNode group = null;
		VisTweet otweet = topic().getElement(id);
		if (otweet == null) {
			// create a new tweet
			group = user;
			while (p != null) {
				target = null;
				otweet = new VisTweet(org);
				otweet.setSentiment(value);
				p.add(otweet);
				p.active(otweet);
				p.setValid(false);
				target = new VisTube();
				target.attach(otweet, group);
				target.retweet(s).setSentiment(value);
				group = p;
				p = (VisTopic) getParent(p);
			}
		} else if (!otweet.isActive()) {
			// already exist but not active, then active it
			group = user;
			while (p != null) {
				target = null;
				otweet = p.getElement(id);
				if (otweet == null) {
					otweet = new VisTweet(org);
					otweet.setSentiment(value);
					p.add(otweet);
				}
				p.active(otweet);
				p.setValid(false);
				target = new VisTube();
				target.attach(otweet, group);
				target.retweet(s).setSentiment(value);
				group = p;
				p = (VisTopic) getParent(p);
			}
		} else {
			// it is actived, then find its tube
			group = user;
			while (p != null) {
				target = null;
				otweet = p.getElement(id);
				if (otweet == null) {
					otweet = new VisTweet(org);
					otweet.setSentiment(value);
					p.add(otweet);
					p.active(otweet);
					p.setValid(false);
				} else {
					VisTweet[] active = p.active();
					for (int i = 0; i < active.length; ++i) {
						int size = active[i].getTubeSize();
						for (int j = 0; j < size; ++j) {
							tube = active[i].getTube(j);
							if (tube.source() == otweet && tube.sink() == group) {
								target = tube;
								break;
							}
						}
					}
				}
				otweet.active(p.getTime());
				if (target == null) {
					target = new VisTube();
					target.attach(otweet, group);
				}
				target.retweet(s).setSentiment(value);
//				group.setWeight(group.getWeight() + 1);
				group = p;
				p = (VisTopic) getParent(p);
			}
		}
		return otweet;
	}

	public boolean clean() {
		long active = 0;
		int period = 0;
		long time = topic().getTime();
		boolean flag = false;
		VisTube tube = null;
		FlowerNode[] nodes = m_nodes.values().toArray(EMPTY);
		for (int i = 0; i < nodes.length; ++i) {
			if (nodes[i] instanceof VisTopic) {
				VisTopic topic = (VisTopic) nodes[i];
				flag = flag | topic.clean();
			} else {
				VisUser user = (VisUser) nodes[i];
				active = user.getLastActiveTime();
				period = (int) ((time - active) / Constant.TIME_INTERVAL);
				if (period >= Constant.ACTIVE_TWEET_LIFE) {
					FlowerNode p = user;
					FlowerNode next = null;
					while (p != m_root) {
						if(isLeaf(p)) {
							int tsize = p.getTubeCnt();
							for (int j = 0; j < tsize; ++j) {
								tube = p.getTube(0);
								tube.dettach();
								tube = null;
							}
							next = removeChild(p);
							p.clear();
							p = next;
						} else {
							p = getParent(p);
						}
						p.setWeight(p.getWeight() - user.getWeight());
					}
					p = null;
					flag = true;
				}
			}
		}
		return flag;
	}

	public void lock() {
		m_lock.lock();
	}

	public void unlock() {
		m_lock.unlock();
	}

	public boolean tryLock() {
		return m_lock.tryLock();
	}

	public void setTopic(VisTopic topic) {
		setTreeRoot(topic);
	}

	public VisTopic topic() {
		return (VisTopic) m_root;
	}

	public void clear() {
		super.clear();
		m_root = null;
		m_focus = null;
	}

//	public boolean accept(VisTweet tweet) {
//		return tweet.getWeight() >= m_threthold;
//	}
//
//	public void filter(int type, double w) {
//		m_threthold = w;
//	}
	
	public boolean accept(VisTweet tweet) {
		if(m_filter != null) {
			return m_filter.accept(tweet);
		}
		return true;
	}
	
	public FlowerNode[] getChildren(FlowerNode p) {
		List<FlowerNode> c = new ArrayList<FlowerNode>();
		FlowerNode[] nodes = getChildren(p, EMPTY);
		if(nodes == null) {
			return null;
		}
		for(int i = 0; i < nodes.length; ++i) {
			VisTube[] tubes = nodes[i].getTubes();
			if(tubes == null || tubes.length == 0) {
				continue;
			}
			c.add(nodes[i]);
		}
		return c.isEmpty() ? null : c.toArray(EMPTY);
	}

	protected void leaves(FlowerNode parent, List<FlowerNode> leaves) {
		List<FlowerNode> c = m_children.get(parent);
		FlowerNode[] nodes = getChildren(parent);
		if ((null == nodes || nodes.length == 0) && parent != m_root) {
			if(parent instanceof VisUser) {
				leaves.add(parent);
			}
		} else {
			if (null != c) {
				for (FlowerNode node : c) {
					leaves(node, leaves);
				}
			}
		}
	}

}
