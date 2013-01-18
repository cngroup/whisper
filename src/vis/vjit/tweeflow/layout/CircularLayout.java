package vis.vjit.tweeflow.layout;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import vis.vjit.tweeflow.Constant;
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
public class CircularLayout extends Activity implements ILayout {

	private static final long serialVersionUID = 4019389093334465445L;

	private static final FlowerNode[] EMPTY = new FlowerNode[0];

	// 1. parameter for sunflower
	public static final double PI2 = Math.PI * 2;
	public static final double PHI = (Math.sqrt(5) + 1) / 2;
	public static final double HALF_PIE = Math.PI / 2;
	private TwitterFlower m_flower = null;
	private GroupComparator m_gcomp = null;
	private TweetComparator m_tcomp = null;

	// 2. parameter for force layout of the sunflower
	private class MomentItem {
		public float mass = 1;
		public float moment = 0;
		public float angle = 0;
		public float omiga = 0;

		public String toString() {
			return mass + ", " + angle + ", " + moment + ", " + omiga;
		}
	}

	public static float DEFAULT_SPRING_COEFF = 6E-8f;
	public static float DEFAULT_SPRING_LENGTH = 0;
	private Map<String, MomentItem> m_items = null;
	private double m_energy = 0;
	private double m_delta = 0;
	private double m_tol = 1E-5;

	// 3. parameters for field layout
	public class Force {
		public float x, y;

		public Force(float X, float Y) {
			x = X;
			y = Y;
		}
	}

	private static final int chargeRadius = 8; // in pixels
	private static final int arrowPlotSpacing = 2 * chargeRadius; // in pixels
	private static final int fluxLineSpacing = 32; // 2 * arrowPlotSpacing; //
													// in pixels
	private int width, height;
	private int activesize = 0;

	// 4. terminate immediately
	private boolean bshutdown = false;

	public CircularLayout() {
		super(-1, 30);
		m_items = new HashMap<String, MomentItem>();
		m_gcomp = new GroupComparator();
		m_tcomp = new TweetComparator();
		setEnable(true);
	}

	public String getName() {
		return null;
	}

	public void layout(Display disp) {
		if (null == disp) {
			return;
		}

		m_disp = disp;

		// optimize the topic node by force
		if (!isEnable()) {
			int it = 0;
			start();
			do {
				step(m_flower.topic());
				it++;
			} while (m_energy >= m_tol);
			finish();
		} else {
			if (!this.isPerforming()) {
				ActivityManager manager = m_disp.getActivityManager();
				this.setDisplay(disp);
				this.setStartTime(System.currentTimeMillis());
				manager.addActivity(this);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////
	// TODO: layout the sunflower icons
	// /////////////////////////////////////////////////////////////////
	public void source(VisTopic source) {
		double margin = 20;
		Ellipse2D shape = new Ellipse2D.Double();
		shape.setFrameFromCenter(source.getX(), source.getY(), source.getX()
				+ source.getWidth() / 2.0, source.getY() + source.getHeight()
				/ 2.0);
		
		source.put("boundary", shape);
		VisTweet[] nodes = source.inactive();
		VisTweet[] inactive = null;
		inactive = nodes;
		// layout inactive nodes();
		double r1 = (source.getWidth() - margin) / 2;
		double ratio = r1 / Math.sqrt(inactive.length);
		for (int i = 0; i < inactive.length; i++) {
			IVisualNode node = inactive[i];
			double theta = source.getAngle() + i * ExtMath.DOUBLE_PI / PHI;
			double r = Math.sqrt(i) * ratio;
			double xx = source.getX() + r * Math.cos(theta);
			double yy = source.getY() + r * Math.sin(theta);
			node.setX(xx);
			node.setY(yy);
			node.setWidth(6);
			node.setHeight(6);
			node.updateLocation(1);
			node.updateSize(1);
		}
	}

	public void sink(FlowerNode sink) {
		Ellipse2D shape = new Ellipse2D.Double();
		shape.setFrameFromCenter(sink.getX(), sink.getY(),
				sink.getX() + sink.getWidth() / 2.0,
				sink.getY() + sink.getHeight() / 2.0);
		sink.put("boundary", shape);
		IVisualNode[] nodes = m_flower.leaves(sink, new FlowerNode[0]);

		// //////////////////////

		Triangulation tr = new Triangulation(shape, sink.getWeight());
		double margin = 10;
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

		tr.optimize(1);

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
	}

	private void circle(TwitterFlower flower, VisTopic parent, FlowerNode[] nodes, double px,
			double py, double pradii) {
		
		double angle = Math.max(0, ExtMath.HALF_PI * (1 - 2.0 / nodes.length));
		double cradii = pradii * Math.cos(angle) / (1 + Math.cos(angle));
		double radius = pradii - cradii;
		parent.setRadii(radius);

		double delta = ExtMath.DOUBLE_PI / nodes.length;

		for (int i = 0; i < nodes.length; i++) {
			double theta = i * delta;
			double cx = px + radius * Math.cos(theta);
			double cy = py + radius * Math.sin(theta);
			double wei = nodes[i].getWeight();
			double d = 2 * Math.sqrt(Constant.USER_GROUP_SQARE_RADII * wei);

			nodes[i].setX(cx);
			nodes[i].setY(cy);
			nodes[i].updateLocation(1);
			
			if (nodes[i].isCollapsed() || flower.isLeaf(nodes[i])) {
				nodes[i].setWidth(d);
				nodes[i].setHeight(d);
				nodes[i].updateSize(1);
				sink(nodes[i]);
			} else {
				VisTopic topic = (VisTopic)nodes[i];
				// layout the center topic
				topic.setWidth(pradii / 8);
				topic.setHeight(pradii / 8);
				topic.updateSize(1);
				
				source(topic);

				// layout the surrounding groups according to their longitude
				double margin = 20;
				VisTweet[] actives = topic.active();
				double r2 = topic.getWidth() / 2 + margin / 4;
				double inner = ExtMath.DOUBLE_PI / actives.length;
				double alpha = 0;
				double xx = 0;
				double yy = 0;
				Arrays.sort(actives, m_tcomp);
				for (int j = 0; j < actives.length; ++j) {
					alpha = topic.getAngle() + inner * j;
					xx = cx + r2 * Math.cos(alpha);
					yy = cy + r2 * Math.sin(alpha);
					actives[j].setX(xx);
					actives[j].setY(yy);
					actives[j].setWidth(10);
					actives[j].setHeight(10);
					actives[j].updateLocation(1);
					actives[j].updateSize(1);
				}

				FlowerNode[] children = flower.getChildren(topic);
				circle(flower, topic, children, cx, cy, cradii);
			}
		}
	}

	// ////////////////////////////////////////////////////////////////
	// TODO: layout vector fields
	// ////////////////////////////////////////////////////////////////
	private void timeline(VisTopic topic, FlowerNode[] nodes) {
		if (nodes == null || nodes.length <= 0) {
			return;
		}
	
//		Arrays.fill(map, 0);
		
		topic.setCharge(1.0f);
		for (int i = 0; i < nodes.length; ++i) {
			VisTube[] tubes = nodes[i].getTubes();
			for (int j = 0; j < tubes.length; ++j) {
				nodes[i].setCharge(-20.0f);
				fluxline(topic, nodes, tubes[j], 1f);
				retweet(topic, nodes, tubes[j], 1f);
				nodes[i].setCharge(-1.0f);
			}
		}
	}

	private GeneralPath fluxline(VisTopic topic, FlowerNode[] groups,
			VisTube tube, float sign) {
		int len = 0;
		VisTweet tweet = tube.source();
		float x = (float) tweet.getX();
		float y = (float) tweet.getY();

		GeneralPath p = new GeneralPath();
		p.moveTo(Math.round(x), Math.round(y));
		float forceMagnitude = 0;
		do {
			Force v = computeForce(topic, groups, x, y);
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

			x = new_x;
			y = new_y;
			if (x < 0 || x >= width || y < 0 || y >= height) {
				break;
			}
			len++;
		} while (forceMagnitude != 0);

		tube.setCurve(p, len, 0);

		return p;
	}

	public void retweet(VisTopic topic, FlowerNode[] groups, VisTube tube,
			float sign) {
		float forceMagnitude = 0;
		VisTweet tweet = tube.source();
		VisTweet glyph = null;
		float x = (float) tweet.getX();
		float y = (float) tweet.getY();
		int len = tube.curvelength();
		if (!tube.isEmpty()) {
			int j = tube.size() - 1;
			glyph = tube.getTweet(j);
			glyph.setWeight(1);

			int idx = (int) ((topic.getTime() - glyph.getTime()) / Constant.TIME_INTERVAL);
			int pos = (int) (10 + idx * len
					/ (float) Constant.ACTIVE_TWEET_LIFE);
			for (int i = 0; i <= len; ++i) {
				Force v = computeForce(topic, groups, x, y);
				v.x *= sign;
				v.y *= sign;

				forceMagnitude = (float) Math.sqrt(v.x * v.x + v.y * v.y);

				v.x /= forceMagnitude;
				v.y /= forceMagnitude;
				float new_x = x + v.x;
				float new_y = y + v.y;

				x = new_x;
				y = new_y;

				if (pos == i) {
					double size = 2 * Math.sqrt(Constant.USER_GROUP_SQARE_RADII);
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
					glyph.setWeight(1);
					idx = (int) ((topic.getTime() - glyph.getTime()) / Constant.TIME_INTERVAL);
					pos = (int) (10 + idx * len
							/ (float) Constant.ACTIVE_TWEET_LIFE);
				}
			}
		}
	}

	private Force computeForce(VisTopic topic, FlowerNode[] groups, float x,
			float y) {
		Force v = new Force(0.0f, 0.0f);
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

	// ////////////////////////////////////////////////////////////////
	// TODO: force layout to optimize sunflower orientations
	// ////////////////////////////////////////////////////////////////
	private void step(VisTopic topic) {
		if (topic.isCollapsed()) {
			return;
		}
		accumulate(topic);
		integrate(5, topic);
		update(topic);
		FlowerNode[] nodes = m_flower.getChildren(topic);
		if (nodes == null || nodes.length <= 0) {
			return;
		}
		timeline(topic, nodes);
		for (int i = 0; i < nodes.length; ++i) {
			if (m_flower.isLeaf(nodes[i])) {
				continue;
			}
			step((VisTopic) nodes[i]);
		}
	}

	private void accumulate(VisTopic topic) {
		double force = 0;
		FlowerNode group = null;
		MomentItem item = getItem(topic);
		item.moment = 0;
		// System.out.println("error : accumulate() " + m_titem.toString());
		VisTweet[] actives = topic.active();
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
				item.moment += (float) (force * Math.sin(theta) * r);
			}
		}
	}

	/**
	 * Eurlar integeration to compute accelerate
	 */
	private void integrate(long timestep, VisTopic topic) {
		MomentItem item = getItem(topic);
		item.angle = (float) ((timestep * item.omiga) % ExtMath.DOUBLE_PI);
		double angle = topic.getAngle();
		double delta = (item.angle - angle) % ExtMath.DOUBLE_PI;
		m_energy += delta * delta;
		float coeff = timestep / item.mass;
		item.omiga += coeff * item.moment;
	}

	/**
	 * update force
	 */
	private void update(VisTopic topic) {
		MomentItem item = getItem(topic);
		double delta = (item.angle - topic.getAngle()) % ExtMath.DOUBLE_PI;
		if (Math.abs(delta - ExtMath.DOUBLE_PI) < 1E-6) {
			delta = 0;
		}
		m_delta += delta;
		
		System.out.println("update : " + topic.getID());
		
		// rotate
		double angle = (topic.getAngle() + delta) % ExtMath.DOUBLE_PI;
		if (angle < 0) {
			angle = ExtMath.DOUBLE_PI + angle;
		}
		topic.setAngle(angle);
		source(topic);
		double margin = 20;
		VisTweet[] tweets = topic.active();
		double r2 = topic.getWidth() / 2 + margin / 4;
		double dangle = ExtMath.DOUBLE_PI / tweets.length;
		angle = 0;
		Arrays.sort(tweets, m_tcomp);
		for (int i = 0; i < tweets.length; ++i) {
			angle = topic.getAngle() + dangle * i;
			double xx = topic.getX() + r2 * Math.cos(angle);
			double yy = topic.getY() + r2 * Math.sin(angle);
			tweets[i].setX(xx);
			tweets[i].setY(yy);
			tweets[i].setWidth(10);
			tweets[i].setHeight(10);
			tweets[i].updateLocation(1);
			tweets[i].updateSize(1);
			if (Double.isNaN(xx) || Double.isNaN(yy)) {
				System.err.println(getItem(topic).toString());
				System.exit(-1);
			}
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

		System.out.println("layout : lock");
		m_flower.lock();

		width = m_disp.getWidth();
		height = m_disp.getHeight();
		
		double cx = width / 2.0;
		double cy = height / 2.0;
		double radii = Math.min(width / 2, height / 2);

		// layout the center topic
		VisTopic topic = m_flower.topic();
		topic.setX(cx);
		topic.setY(cy);
		topic.setWidth(height / 6);
		topic.setHeight(height / 6);
		topic.updateLocation(1);
		topic.updateSize(1);
		source(topic);

		// layout the surrounding groups according to their longitude
		double margin = 20;
		VisTweet[] actives = topic.active();
		activesize = actives.length;
		double r2 = topic.getWidth() / 2 + margin / 4;
		double inner = ExtMath.DOUBLE_PI / activesize;
		double angle = 0;
		double xx = 0;
		double yy = 0;
		Arrays.sort(actives, m_tcomp);
		for (int i = 0; i < actives.length; ++i) {
			angle = topic.getAngle() + inner * i;
			xx = cx + r2 * Math.cos(angle);
			yy = cy + r2 * Math.sin(angle);
			actives[i].setX(xx);
			actives[i].setY(yy);
			actives[i].setWidth(10);
			actives[i].setHeight(10);
			actives[i].updateLocation(1);
			actives[i].updateSize(1);
		}

		FlowerNode[] groups = null;
		groups = m_flower.getChildren(topic);
		if (groups == null) {
			return false;
		}
		Arrays.sort(groups, m_gcomp);
		circle(m_flower, topic, groups, cx, cy, radii);
		m_energy = 0;
		m_delta = 0;
		step(m_flower.topic());
		m_energy = m_energy / (m_items.isEmpty() ? 1 : m_items.size());
		m_delta = m_delta / (m_items.isEmpty() ? 1 : m_items.size());
		return true;
	}

	public void perform(double frac) {
		m_energy = 0;
		m_delta = 0;
		step(m_flower.topic());
		m_energy = m_energy / (m_items.isEmpty() ? 1 : m_items.size());
		m_delta = m_delta / (m_items.isEmpty() ? 1 : m_items.size());
		
		if (bshutdown || m_energy < m_tol ) {
			this.terminate();
		}
		m_disp.repaint();
		super.perform(frac);
	}

	public synchronized void shutdown() {
		bshutdown = true;
	}

	public void finish() {
		isPerforming = false;
		if (m_flower != null) {
			step(m_flower.topic());
			m_disp.repaint();
			m_flower.unlock();
		}
		System.out.println("layout : unlock");
		super.finish();
	}

	private synchronized MomentItem getItem(VisTopic topic) {
		MomentItem item = m_items.get(topic.getID());
		if (null == item) {
			item = new MomentItem();
			m_items.put(topic.getID(), item);
		}
		return item;
	}

	public void reset() {

	}
}
