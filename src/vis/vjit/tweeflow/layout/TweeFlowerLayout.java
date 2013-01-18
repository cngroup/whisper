package vis.vjit.tweeflow.layout;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import vis.vjit.demo.test.Frame;
import vis.vjit.tweeflow.Constant;
import vis.vjit.tweeflow.TweeFlowVjit;
import vis.vjit.tweeflow.data.FlowerNode;
import vis.vjit.tweeflow.data.TwitterFlower;
import vis.vjit.tweeflow.data.VisTopic;
import vis.vjit.tweeflow.data.VisTube;
import vis.vjit.tweeflow.data.VisTweet;
import vis.vjit.tweeflow.voronoi.Pnt;
import vis.vjit.tweeflow.voronoi.Triangulation;
import davinci.Display;
import davinci.ILayout;
import davinci.activity.Activity;
import davinci.activity.ActivityManager;
import davinci.data.elem.IVisualNode;
import davinci.util.math.ExtMath;

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
public class TweeFlowerLayout extends Activity implements ILayout {

	private static final long serialVersionUID = -8764378426828966799L;

	// 1. parameter for sunflower
	public static final double PI2 = Math.PI * 2;
	public static final double PHI = (Math.sqrt(5) + 1) / 2;
	public static final double HALF_PIE = Math.PI / 2;
	private TwitterFlower m_flower = null;
	private TweetComparator m_tcomp = null;

	// 2. parameter for force layout of the sunflower
	private class MomentItem {
		public float mass = 1;
		public float moment = 0;
		public float angle = 0;
		public float omiga = 1;

		public String toString() {
			return mass + ", " + angle + ", " + moment + ", " + omiga;
		}
	}

	public static float DEFAULT_SPRING_COEFF = 6E-9f;
	public static float DEFAULT_SPRING_LENGTH = 0;
	private MomentItem m_titem = null;
	private double m_energy = 0;
	private double m_delta = 0;
	private double m_tol = 1E-5;
	private int iterator = 0;

	// 3. parameters for field layout
	private static final int chargeRadius = 8; // in pixels
	private static final int arrowPlotSpacing = 2 * chargeRadius; // in pixels
	private int width, height;
	private int activesize = 0;

	private boolean bshutdown = false;

	public TweeFlowerLayout() {
		super(-1, 30);
		m_titem = new MomentItem();
		m_tcomp = new TweetComparator();
		setEnable(false);
	}

	public String getName() {
		return "TweeFlowerLayout";
	}

	public void layout(Display disp) {
		if (null == disp) {
			return;
		}

		m_disp = disp;
		m_flower = (TwitterFlower) disp.getData("flower");
		if (m_flower == null) {
			return;
		}

		// optimize the topic node by force
		if (!isEnable()) {
			int it = 0;
			long t1 = System.currentTimeMillis();
			start();
			do {
				this.accumulate();
				this.integrate(5);
				this.update();
				it++;
			} while (!(bshutdown || m_energy < m_tol
					|| Math.abs(m_titem.omiga) < 1E-5));	
			finish();
			Frame.layout_cost += (System.currentTimeMillis() - t1);
			Frame.layout_count ++;
			
			int num_retweet = 0;
			
			VisTopic topic = (VisTopic)m_flower.topic();
			VisTweet[] tweets = topic.active(); 			
			for (int i = 0; i < tweets.length; ++i) {
				VisTube[] tubes = tweets[i].getTubes();
				for (int j = 0; j < tubes.length; ++j) {
					num_retweet += tubes[j].getTweets().length;
				}
			}
			
			int num_act = tweets.length;
			int num_inact = topic.inactive().length;

			Frame.avg_re_tweets += num_retweet;
			Frame.avg_act_tweets += num_act;
			Frame.avg_inact_tweets += num_inact;
			Frame.max_re_tweets = Math.max(num_retweet, Frame.max_re_tweets);
			Frame.max_act_tweets = Math.max(num_act, Frame.max_act_tweets);
			Frame.max_inact_tweets = Math.max(num_inact, Frame.max_inact_tweets);
			
		} else {
			ActivityManager manager = m_disp.getActivityManager();
			this.setDisplay(disp);
			this.setStartTime(System.currentTimeMillis());
			manager.addActivity(this);
		}
	}

	// /////////////////////////////////////////////////////////////////
	// TODO: layout the sunflower icons
	// /////////////////////////////////////////////////////////////////
	public void source(VisTopic source) {

		VisTweet[] inactive = source.inactive();
		VisTweet[] actives = source.active();
		int size = inactive.length + (int) Math.sqrt(actives.length);
		// double d = 2 * Math.sqrt(Constant.CENTER_DISK_RADII * size);
		double d = 100;
		source.setWidth(d);
		source.setHeight(d);
		source.updateSize(1);
		double margin = d / 10;

		// layout inactive nodes();
		Arrays.sort(inactive);
		double alpha = 137.5 * Math.PI / 180; // ExtMath.DOUBLE_PI / PHI;
		double r1 = (source.getWidth() - margin) / 2;
		double ratio = r1 / Math.sqrt(inactive.length);
		for (int i = 0, j = inactive.length - 1; i < inactive.length; ++i, --j) {
			double theta = source.getAngle() + i * alpha;
			double r = Math.sqrt(i) * ratio;
			double xx = source.getX() + r * Math.cos(theta);
			double yy = source.getY() + r * Math.sin(theta);

			double wei = inactive[j].getWeight();
			double dd = 2 * Math.sqrt(Math.log(wei + 1)
					* Constant.FOLLOWER_CNT_RADII);

			inactive[j].setX(xx);
			inactive[j].setY(yy);
			inactive[j].setWidth(dd);
			inactive[j].setHeight(dd);
			inactive[j].updateLocation(1);
			inactive[j].updateSize(1);
		}

		margin = 20;
		activesize = actives.length;
		Arrays.sort(actives, m_tcomp);
		int ncnt = 0;
		int idx = 0;
		int level = 0;
		int ss = 0;
		double pangle = source.getAngle();
		do {
			idx = ss;
			double r2 = d / 2 + margin / 4;
			double clength = Math.PI * r2 * 2;
			ncnt = (int) (clength / 8);
			ss = Math.min(actives.length, idx + ncnt);
			double inner = ExtMath.DOUBLE_PI / (ss - idx);
			// double inner = ExtMath.DOUBLE_PI / ncnt;
			double angle = 0;
			double xx = 0;
			double yy = 0;
			for (int i = idx, j = 0; i < ss; ++i, ++j) {
				angle = pangle + inner * j;
				xx = source.getX() + r2 * Math.cos(angle);
				yy = source.getY() + r2 * Math.sin(angle);
				actives[i].setX(xx);
				actives[i].setY(yy);

				double wei = actives[i].getWeight();
				double dd = 2 * Math.sqrt(Math.log(wei + 1)
						* Constant.FOLLOWER_CNT_RADII);

				actives[i].setWidth(dd);
				actives[i].setHeight(dd);
				actives[i].updateLocation(1);
				actives[i].updateSize(1);
			}
			pangle = angle + Math.PI / ncnt;
			d += 20;
			level++;
		} while (ss < actives.length);
		// level --;
		source.setLevel(level);
	}

	public int sink(FlowerNode sink) {

		FlowerNode[] nodes = m_flower.leaves(sink, TwitterFlower.EMPTY);

		double d = 2 * Math.sqrt(Constant.CENTER_DISK_RADII * nodes.length);
		double margin = d / 10;

		double alpha = 137.5 * Math.PI / 180; // ExtMath.DOUBLE_PI / PHI;
		double r1 = (sink.getWidth() - margin) / 2;
		double ratio = r1 / Math.sqrt(nodes.length);
		for (int i = 0; i < nodes.length; i++) {
			IVisualNode node = nodes[i];
			double theta = i * alpha;
			double r = Math.sqrt(i) * ratio;
			double xx = sink.getX() + r * Math.cos(theta);
			double yy = sink.getY() + r * Math.sin(theta);
			double wei = node.getWeight();
			double size = 2 * Math.sqrt(Math.log(wei + 1)
					* Constant.FOLLOWER_CNT_RADII);
			node.setX(xx);
			node.setY(yy);
			node.setWidth(size);
			node.setHeight(size);
			node.updateLocation(1);
			node.updateSize(1);
		}
		
		return nodes.length;
	}

	public void sink2(FlowerNode sink) {
		Ellipse2D shape = new Ellipse2D.Double();
		shape.setFrameFromCenter(sink.getX(), sink.getY(),
				sink.getX() + sink.getWidth() / 2.0,
				sink.getY() + sink.getHeight() / 2.0);
		sink.put("boundary", shape);

		// long t1 = System.currentTimeMillis();
		IVisualNode[] nodes = m_flower.leaves(sink, TwitterFlower.EMPTY);
		// double wei = 0;
		// for(int i = 0; i < nodes.length; ++i) {
		// wei += nodes[i].getWeight();
		// }
		// System.out.println("-- get leaves : " + (System.currentTimeMillis() -
		// t1));
		// System.out.println(sink.getWeight() + ", " + wei);
		// long t2 = System.currentTimeMillis();
		Triangulation tr = new Triangulation(shape, sink.getWeight());
		double margin = 2;
		double r1 = (sink.getWidth() - margin) / 2;
		double ratio = r1 / Math.sqrt(nodes.length);
		for (int i = 0; i < nodes.length; i++) {
			double theta = i * ExtMath.DOUBLE_PI / PHI;
			double r = Math.sqrt(i) * ratio;
			double x = sink.getX() + r * Math.cos(theta);
			double y = sink.getY() + r * Math.sin(theta);

			Pnt site = new Pnt();
			site.id = nodes[i].getID();
			site.setWeight(nodes[i].getWeight());
			site.setCoords(x, y);
			tr.addSite(site);
		}
		// System.out.println("-- add sites : " + (System.currentTimeMillis() -
		// t2));
		// long t3 = System.currentTimeMillis();

		tr.optimize(1);
		// System.out.println("-- optimization : " + (System.currentTimeMillis()
		// - t3));
		// long t4 = System.currentTimeMillis();
		Pnt[] sites = tr.getSites();
		for (int i = 0; i < sites.length; ++i) {
			IVisualNode node = (IVisualNode) m_flower.getNode(sites[i].id);
			node.setX(sites[i].coord(0));
			node.setY(sites[i].coord(1));
			double rr = Math.sqrt(sites[i].getRadii() / Math.PI) * 0.95;
			node.setWidth(rr);
			node.setHeight(rr);
			node.updateLocation(1);
			node.updateSize(1);
			node.put("boundary", tr.getVoronoiCell(sites[i]));
		}
		// System.out.println("-- set boundary : " + (System.currentTimeMillis()
		// - t4));
		tr = null;
	}

	// ////////////////////////////////////////////////////////////////
	// TODO: layout vector fields
	// ////////////////////////////////////////////////////////////////
	public void timeline() {
		if (m_flower == null) {
			return;
		}
		FlowerNode[] groups = m_flower.getChildren(m_flower.focus());
		if (groups == null) {
			return;
		}
		double max = 0;
		for (int i = 0; i < groups.length; ++i) {
			VisTube[] tubes = groups[i].getTubes();
			for (int j = 0; j < tubes.length; ++j) {
				groups[i].setCharge(-40.0f);
				fluxline(groups, tubes[j], 1f);
				retweet(groups, tubes[j], 1f);
				groups[i].setCharge(-1.0f);
				max = Math.max(max, tubes[j].curvature());
			}
		}
	}

	private Force computeForce(FlowerNode[] groups, float x, float y) {
		Force v = new Force(0.0f, 0.0f);
		if (m_flower == null) {
			return v;
		}
		VisTopic topic = m_flower.focus();
		float dx = (float) (x - topic.getX()) / (float) arrowPlotSpacing;
		float dy = (float) (y - topic.getY()) / (float) arrowPlotSpacing;
		float r2 = dx * dx + dy * dy;
		float r = (float) Math.sqrt(r2);
		float forceMagnitude = topic.getCharge() / r2;
		v.x += forceMagnitude * dx / r;
		v.y += forceMagnitude * dy / r;
		for (int i = 0; i < groups.length; ++i) {
			dx = (float) (x - groups[i].getX()) / (float) arrowPlotSpacing;
			dy = (float) (y - groups[i].getY()) / (float) arrowPlotSpacing;
			r2 = dx * dx + dy * dy;
			if (r2 < 0.2) {
				// continue;
				v.x = 0;
				v.y = 0;
				return v;
			}
			r = (float) Math.sqrt(r2);
			forceMagnitude = groups[i].getCharge() / r2;
			v.x += forceMagnitude * dx / r;
			v.y += forceMagnitude * dy / r;
		}
		return v;
	}

	private GeneralPath fluxline(FlowerNode[] groups, VisTube tube, float sign) {
		int t = 0;
		VisTweet tweet = tube.source();
		float x = (float) tweet.getX();
		float y = (float) tweet.getY();

		GeneralPath p = new GeneralPath();
		p.moveTo(Math.round(x), Math.round(y));
		float forceMagnitude = 0;

		double kk = 0;

		float x1 = 0, y1 = 0;
		float x2 = 0, y2 = 0;
		float x3 = x, y3 = y;
		int loops = 0;

		do {
			Force v = computeForce(groups, x, y);
			v.x *= sign;
			v.y *= sign;

			forceMagnitude = (float) Math.sqrt(v.x * v.x + v.y * v.y);

			if (forceMagnitude == 0) {
				break;
			}
			// normalize the vector
			v.x /= forceMagnitude;
			v.y /= forceMagnitude;
			float new_x = x + v.x;
			float new_y = y + v.y;
			p.lineTo(Math.round(new_x), Math.round(new_y));

			x1 = x2;
			y1 = y2;
			x2 = x3;
			y2 = y3;
			x3 = new_x;
			y3 = new_y;
			if (!(x1 == 0 || y1 == 0 || x2 == 0 || y2 == 0 || x3 == 0 || y3 == 0)) {
				double c = Math.sqrt((x1 - x3) * (x1 - x3) + (y1 - y3)
						* (y1 - y3));
				double a1 = Math.atan2(y1 - y2, x1 - x2);
				if (a1 < 0) {
					a1 += ExtMath.DOUBLE_PI;
				}
				double a2 = Math.atan2(y3 - y2, x3 - x2);
				if (a2 < 0) {
					a2 += ExtMath.DOUBLE_PI;
				}
				double a = Math.abs(a2 - a1);
				a = a > Math.PI ? ExtMath.DOUBLE_PI - a : a;
				double k = (2 * Math.sin(a)) / c;
				kk = Math.max(kk, k);
			}

			x = new_x;
			y = new_y;
			if (x < 0 || x >= width || y < 0 || y >= height) {
				break;
			}
			t++;
		} while (forceMagnitude != 0 && t < 5000);
		tube.setCurve(p, t, kk / 2);

		return p;
	}

	public void retweet(FlowerNode[] groups, VisTube tube, float sign) {
		float forceMagnitude = 0;
		VisTweet tweet = tube.source();
		VisTopic topic = tweet.getTopic();
		if (topic == null) {
			return;
		}
		VisTweet glyph = null;
		float x = (float) tweet.getX();
		float y = (float) tweet.getY();
		int t = tube.curvelength();
		if (!tube.isEmpty()) {
			int j = tube.size() - 1;
			glyph = tube.getTweet(j);
			double length = 0;
			double k = 0;
			int idx = (int) ((topic.getTime() - glyph.getTime()) / Constant.TIME_INTERVAL);
			int pos = (int) (10 + idx * t / (float) Constant.ACTIVE_TWEET_LIFE);
			for (int i = 0; i <= t; ++i) {
				Force v = computeForce(groups, x, y);
				v.x *= sign;
				v.y *= sign;

				forceMagnitude = (float) Math.sqrt(v.x * v.x + v.y * v.y);

				v.x /= forceMagnitude;
				v.y /= forceMagnitude;
				float new_x = x + v.x;
				float new_y = y + v.y;

				double dx = new_x - x;
				double dy = new_y - y;
				double angle = Math.atan2(dy, dx);
				double dist = Math.sqrt(dx * dx + dy * dy);
				double tangent = dy / dx;
				k += tangent / dist;
				length += dist;

				x = new_x;
				y = new_y;

				int loc = 0;
				if (((TweeFlowVjit) m_disp).isDiffusion) {
					loc = i;
				} else {
					loc = t - i;
				}

				if (pos == loc) {
					double wei = glyph.getWeight();
					double size = 2 * Math.sqrt(Math.log(wei + 1)
							* Constant.FOLLOWER_CNT_RADII);
					glyph.setAngle(angle);
					glyph.setX(x);
					glyph.setY(y);
					glyph.setWidth(size);
					glyph.setHeight(size);
					glyph.updateLocation(1);
					glyph.updateSize(1);
					if (--j < 0) {
						break;
					}
					glyph = tube.getTweet(j);
					idx = (int) ((topic.getTime() - glyph.getTime()) / Constant.TIME_INTERVAL);
					pos = (int) (10 + idx * t
							/ (float) Constant.ACTIVE_TWEET_LIFE);
				}
			}
		}
	}

	// ////////////////////////////////////////////////////////////////
	// TODO: force layout to optimize sunflower orientations
	// ////////////////////////////////////////////////////////////////
	public void rotate(double delta) {
		if (m_flower == null) {
			return;
		}
		VisTopic topic = m_flower.focus();
		double angle = (topic.getAngle() + delta) % ExtMath.DOUBLE_PI;
		if (angle < 0) {
			angle = ExtMath.DOUBLE_PI + angle;
		}
		topic.setAngle(angle);

		source(topic);
	}

	private void accumulate() {
		if (m_flower == null) {
			return;
		}
		double force = 0;
		FlowerNode group = null;
		VisTopic topic = m_flower.focus();
		m_titem.moment = 0;
		// System.out.println("error : accumulate() " + m_titem.toString());
		VisTweet[] actives = topic.active();
		if (actives.length != activesize) {
			System.out.println("accumulate() : unmatched");
		}

		VisTube tube = null;
		for (int i = 0; i < actives.length; ++i) {
			int size = actives[i].getTubeSize();
			for (int j = 0; j < size; ++j) {
				tube = actives[i].getTube(j);
				force = springforce(tube);
				group = tube.sink();
				VisTweet tweet = tube.source();
				double dx = tweet.getX() - topic.getX();
				double dy = tweet.getY() - topic.getY();
				double r = Math.sqrt(dx * dx + dy * dy);
				double theta1 = Math.atan2(dy, dx);

				dx = tweet.getX() - group.getX();
				dy = tweet.getY() - group.getY();
				double theta2 = Math.atan2(dy, dx);
				double theta = theta1 - theta2;
				m_titem.moment += (float) (force * Math.sin(theta) * r);
			}
		}
	}

	/**
	 * Eurlar integeration to compute accelerate
	 */
	private void integrate(long timestep) {
		if (m_flower == null) {
			return;
		}
		VisTopic topic = m_flower.focus();
		m_energy = Double.MAX_VALUE;
		m_titem.angle = (float) ((timestep * m_titem.omiga) % ExtMath.DOUBLE_PI);
		double angle = topic.getAngle();
		double delta = (m_titem.angle - angle) % ExtMath.DOUBLE_PI;
		m_energy = delta * delta;
		float coeff = timestep / m_titem.mass;
		m_titem.omiga += coeff * m_titem.moment;
	}

	/**
	 * update force
	 */
	private void update() {
		if (m_flower == null) {
			return;
		}
		VisTopic topic = m_flower.focus();
		m_delta = (m_titem.angle - topic.getAngle()) % ExtMath.DOUBLE_PI;
		rotate(m_delta);
		if (Math.abs(m_delta - ExtMath.DOUBLE_PI) < 1E-6) {
			m_delta = 0;
		}
	}

	private double springforce(VisTube tube) {
		IVisualNode n1 = tube.source();
		IVisualNode n2 = tube.sink();
		double ww = 1;
		double dx = n1.getX() - n2.getX();
		double dy = n1.getY() - n2.getY();
		double r = Math.sqrt(dx * dx + dy * dy);
		double d = r - DEFAULT_SPRING_LENGTH;
		double coeff = ww * DEFAULT_SPRING_COEFF * d / r;
		dx = coeff * dx;
		dy = coeff * dy;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean start() {
		if (!super.start()) {
			return false;
		}
		if (null == m_disp) {
			return false;
		}
		m_flower = (TwitterFlower) m_disp.getData("flower");
		if (null == m_flower) {
			return false;
		}

		bshutdown = false;
		// long t1 = System.currentTimeMillis();
		m_flower.lock();
		
		width = m_disp.getWidth();
		height = m_disp.getHeight();
		double cx = width / 2.0;
		double cy = height / 2.0;
		double radii = 0.8 * Math.min(width / 2, height / 2);
		
		// layout the center topic
		VisTopic topic = m_flower.focus();
		topic.setX(cx);
		topic.setY(cy);
		topic.updateLocation(1);
		topic.updateSize(1);
		source(topic);
		
		// layout the surrounding groups according to their longitude
		if (!grouplayout(m_flower, cx, cy, radii)) {
			return false;
		}
		
		this.iterator = 0;
		this.accumulate();
		this.integrate(5);
		this.update();
		m_disp.repaint();
		if (bshutdown || m_energy < m_tol || Math.abs(m_titem.omiga) < 1E-5) {
			return false;
		}
		
//		System.out.println("start : " + m_energy + ", " + m_titem.omiga + ", " + m_delta);
		
		return true;
	}

	public void perform(double frac) {
		this.accumulate();
		this.integrate(5);
		this.update();
		this.timeline();
		if (bshutdown || m_energy < m_tol || Math.abs(m_titem.omiga) < 1E-5
				|| Math.abs(m_delta) < 1E-5) {
			this.terminate();
		}
		iterator++;
		m_disp.repaint();
		super.perform(frac);
	}

	public synchronized void shutdown() {
		bshutdown = true;
	}

	public void finish() {
		setEnable(false);
		isPerforming = false;
		timeline();
		m_disp.repaint();
		if (m_flower != null) {
			m_flower.unlock();
		}
		super.finish();
//		System.out.println("finish : " + m_energy + ", " + m_titem.omiga + ", " + m_delta);
	}

	public void reset() {
		m_flower = null;
	}

	private boolean grouplayout(TwitterFlower flower, double cx, double cy, double radii) {
		FlowerNode[] nodes = flower.getChildren(flower.focus());
		if (nodes == null) {
			return false;
		}
		if (((TweeFlowVjit) m_disp).isEncodeLongitude && flower.focus() == flower.getTreeRoot()) {
			// first rounds to compute the range
			double beta = 45;
			double start = -180;
			double end = start + beta;
			int i = 0, j = 0;
			
			FlowerNode uk = null;
			FlowerNode jp = null;
			
			while(i < nodes.length) {
				
				// TODO: hard code for screen capture
				if("United Kingdom".equals(nodes[i].getLabel())) {
					uk = nodes[i];
				} else if("Japan".equals(nodes[i].getLabel())) {
					jp = nodes[i];
				}
				
				double longi = nodes[i].getLongitude();
				double angle = 0;
				if(start <= longi && longi < end) {
					i++;
					if(i < nodes.length) {
						continue;
					}
				}
				int cnt = i - j;
				if(cnt == 0) {
					start = end;
					end += beta;
				} else {
					double delta = (end - start) / (cnt + 2);
					int cur = j;
					while(j < i) {
						angle = ExtMath.HALF_PI - (start + (j - cur + 1) * delta) * Math.PI / 180.0;
						double xx = cx + radii * Math.cos(angle);
						double yy = cy + radii * Math.sin(angle);
						nodes[j].setX(xx);
						nodes[j].setY(yy);
						nodes[j].updateLocation(1);
						double wei = nodes[j].getWeight();
//						double d = 2 * Math.sqrt(Math.log(wei + 1) * Constant.USER_GROUP_SQARE_RADII);
						double d = 2 * (Math.log(wei + 1) * Constant.USER_GROUP_SQARE_RADII);
						nodes[j].setWidth(d);
						nodes[j].setHeight(d);
						nodes[j].updateSize(1);
						sink(nodes[j]);
						j++;
					}
					start = end;
					end += beta;
				}
			}
			
			//TODO: hardcode for screen capture
			if(uk != null) {
				double xx = cx + radii * Math.cos(ExtMath.HALF_PI);
				double yy = cy + radii * Math.sin(ExtMath.HALF_PI);
				uk.setX(xx);
				uk.setY(yy);
				uk.updateLocation(1);
				sink(uk);
			}
			
			if(jp != null) {
				double xx = cx + radii * Math.cos(-ExtMath.QUATER_PI);
				double yy = cy + radii * Math.sin(-ExtMath.QUATER_PI);
				jp.setX(xx);
				jp.setY(yy);
				jp.updateLocation(1);
				sink(jp);
			}
			
		} else {
			int users = 0;
			double delta = ExtMath.DOUBLE_PI / nodes.length;
			for (int i = 0; i < nodes.length; ++i) {
				double angle = ExtMath.HALF_PI - i * delta;
				double wei = nodes[i].getWeight();
//				double d = 2 * Math.sqrt(Math.log(wei + 1) * Constant.USER_GROUP_SQARE_RADII);
				double d = 2 * (Math.log(wei + 1) * Constant.USER_GROUP_SQARE_RADII);
				double xx = cx + radii * Math.cos(angle);
				double yy = cy + radii * Math.sin(angle);
				nodes[i].setX(xx);
				nodes[i].setY(yy);
				nodes[i].updateLocation(1);
				nodes[i].setWidth(d);
				nodes[i].setHeight(d);
				nodes[i].updateSize(1);
				users += sink(nodes[i]);
			}
			Frame.avg_users += users;
			Frame.avg_groups += nodes.length;
			Frame.max_users = Math.max(Frame.max_users, users);
			Frame.max_groups = Math.max(Frame.max_groups, nodes.length);
			
		}
		return true;
	}

}