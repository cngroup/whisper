package vis.vjit.demo.ui.threadline;

import java.awt.geom.GeneralPath;

import vis.vjit.tweeflow.data.VisUser;
import davinci.Display;
import davinci.ILayout;
import davinci.data.elem.IEdge;

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
public class ThreadLineLayout implements ILayout {
	
	private static final int chargeRadius = 8; // in pixels
	private static final int arrowPlotSpacing = 2 * chargeRadius; // in pixels
	
	private double width = 0;
	private double height = 0;
	
	public class Force {
		public float x, y;
		public Force(float X, float Y) {
			x = X;
			y = Y;
		}
	}

	public ThreadLineLayout() {
	}
	
	public String getName() {
		return "ThreadLineLayout";
	}

	public void layout(Display disp) {
		
		ThreadLine graph = (ThreadLine)disp.getData("flow");
		if(graph == null || graph.isEmpty()) {
			return;
		}
		width = disp.getWidth();
		height = disp.getHeight();
		double cx = disp.getWidth() / 2.0;
		double cy = disp.getHeight() / 2.0;
		double miny = disp.getHeight() * 0.05;
		double maxy = disp.getHeight() * 0.95;
		double minx = disp.getWidth() * 0.1;
		double maxx = disp.getWidth() * 0.9;
		double ww = disp.getWidth() * 0.8;
		
		long end = graph.current;
		long start = Long.MAX_VALUE;
		VisUser[] users = graph.getNodes(new VisUser[0]);
		for(int i = 0; i < users.length; ++i) {
			start = Math.min(start, users[i].getTime());
		}
		
//		int interval = 11 * 60 * 1000;
//		//TODO: Hard code for comparision puropse only
//		if(start > 1331098080000l - interval) {
//			start = 1331098080000l - interval;
//		}
		graph.start = start;
		
		long duration = end - start;
		
		for(int i = users.length - 1; i >= 0; --i) {
			double wei = users[i].user().getFollowersCount();
			double size = Math.log(wei + 1);
			long off = graph.current - users[i].getTime();
			double xx = maxx - (off * ww) / duration + size / 2;
			users[i].setX(xx);
			users[i].setY(cy);
			users[i].setWidth(size);
			users[i].setHeight(size);
			users[i].updateLocation(1);
			users[i].updateSize(1);
		}
		
		IEdge<VisUser>[] edges = null;
		for(int i = users.length - 1; i >= 0; --i) {
			edges = graph.getEdgesFromSecondNode(users[i]);
			for(int j = 0; j < edges.length; ++j) {
				VisUser node1 = edges[j].getFirstNode();
				VisUser node2 = edges[j].getSecondNode();
				cx = (node1.getX() + node2.getX()) / 2;
				double dx = cx - node1.getX();
				double dy = cy - miny;
				double angle = Math.atan2(dy, dx);
				double alpha = 0; 
				if(i % 2 == 0) {
					alpha = -angle + (2 * angle * j / edges.length);
				} else {
					alpha = -angle + (2 * angle - 2 * angle * j / edges.length);
				}
				float x0 = (float)node1.getX() + (float) Math.cos(alpha);
				float y0 = (float)node1.getY() + (float) Math.sin(alpha);
				fluxline(edges[j], 1.0f, x0, y0);
			}
		}
	}
	
	private GeneralPath fluxline(IEdge<VisUser>edge, float sign, float x, float y) {
		int t = 0;
		
		
		VisUser node1 = edge.getFirstNode();
		VisUser node2 = edge.getSecondNode();
		
//		System.out.println(node1.getLabel() + ":" + node1.getTime() + ", " + node2.getLabel() + ":" + node2.getTime());
		
		GeneralPath p = new GeneralPath();
		p.moveTo(Math.round(x), Math.round(y));
		float forceMagnitude = 0;
		
		do {
			Force v = computeForce(node1, node2, x, y);
			v.x *= sign;
			v.y *= sign;
			
			forceMagnitude = (float) Math.sqrt(v.x * v.x + v.y * v.y);
//			System.out.println("forceMagnitude = " + forceMagnitude);
			if (forceMagnitude < 1E-3) {
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
			t ++;
		} while(forceMagnitude != 0);
		edge.put("curve", p);
		edge.put("lens", t);
		return p;
	}
	
	private Force computeForce(VisUser node1, VisUser node2, float x, float y) {
		
		node1.setCharge(10);
		node2.setCharge(-10);
		
		Force v = new Force(0.0f, 0.0f);
		float dx = (float) (x - node1.getX()) / (float) arrowPlotSpacing;
		float dy = (float) (y - node1.getY()) / (float) arrowPlotSpacing;
		float r2 = dx * dx + dy * dy;
		
		float r = (float) Math.sqrt(r2);
		float forceMagnitude = node1.getCharge() / r2;
		v.x += forceMagnitude * dx / r;
		v.y += forceMagnitude * dy / r;
		
		dx = (float) (x - node2.getX()) / (float) arrowPlotSpacing;
		dy = (float) (y - node2.getY()) / (float) arrowPlotSpacing;
		r2 = dx * dx + dy * dy;
		if (r2 < 0.2) {
			//continue;
			v.x = 0;
			v.y = 0;
			return v;
		}
		r = (float) Math.sqrt(r2);
		forceMagnitude = node2.getCharge() / r2;
		v.x += forceMagnitude * dx / r;
		v.y += forceMagnitude * dy / r;
		return v;
	}

	public void reset() {
	}

}
