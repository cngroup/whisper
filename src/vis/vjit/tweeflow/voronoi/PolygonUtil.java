/*
 * DaVinci V1.0 is a light weighted visualization framework and toolkit. 
 * The design of DaVinci based on the information visualization reference model.
 * Some of the packages are ported from prefuse (http://prefuse.org/). All these packages 
 * keep the original file headers and copyright information. Please read and follow it 
 * if you want to use them.
 * 
 * The original motivation of creating this project is to design a light weighted, 
 * simple and easy to use information visualization framework that facilitates the 
 * Ph.D study of the author. For any other purposes please notify the author through 
 * email.
 * 
 * DaVinci V1.0 is under the MIT opensource license.
 * 
 * This project is designed as an example of using DavinCi
 * 
 * Author : Nan Cao
 * Email: nan.cao@gmail.com
 * Homepage: http://www.cse.ust.hk/~nancao/
 * Project Homepage: http://www.cse.ust.hk/~nancao/architecture.html
 * V1 Release Date : 1st June 2010
 */
package vis.vjit.tweeflow.voronoi;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import davinci.data.elem.IVisualNode;

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
public class PolygonUtil {

	public static double area(Point2D[] polyPoints) {
		int i, j, n = polyPoints.length;
		double area = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += polyPoints[i].getX() * polyPoints[j].getY();
			area -= polyPoints[j].getX() * polyPoints[i].getY();
		}
		area /= 2.0;
		return area;
	}
	
	public static double area(Polygon p) {
		int i, j, n = p.npoints;
		double area = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += p.xpoints[i] * p.ypoints[j];
			area -= p.xpoints[j] * p.ypoints[i];
		}
		area /= 2.0;
		return Math.abs(area);
	}
	
	
	public static double area(IVisualNode[] nodes) {
		int i, j, n = nodes.length;
		double area = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += nodes[i].getX() * nodes[j].getY();
			area -= nodes[j].getX() * nodes[i].getY();
		}
		area /= 2.0;
		return Math.abs(area);
	}
	
	public static double area(Shape s, float fitness) {
		return Math.abs(area(getOutline(s, fitness)));
	}
	
	public static GeneralPath getPolyline(GeneralPath aPath, float[][] shape) {
		
		if(null == shape) {
			return null;
		}
		if(null == aPath) {
			aPath = new GeneralPath();
		} else {
			aPath.reset();
		}
		
		aPath.moveTo(shape[0][0], shape[0][1]);
		for(int i = 1; i < shape.length; ++i) {
			aPath.lineTo(shape[i][0], shape[i][1]);
		}
		aPath.lineTo(shape[0][0], shape[0][1]);
		aPath.closePath();
		return aPath;
	}

	public static Point2D centerOfMass(Point2D[] polyPoints) {
		double cx = 0, cy = 0;
		double area = area(polyPoints);
		// could change this to Point2D.Float if you want to use less memory
		Point2D res = new Point2D.Double();
		int i, j, n = polyPoints.length;

		double factor = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			factor = (polyPoints[i].getX() * polyPoints[j].getY()
					- polyPoints[j].getX() * polyPoints[i].getY());
			cx += (polyPoints[i].getX() + polyPoints[j].getX()) * factor;
			cy += (polyPoints[i].getY() + polyPoints[j].getY()) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		res.setLocation(cx, cy);
		return res;
	}
	
	public static Point2D centerOfMass(IVisualNode[] nodes) {
		double cx = 0, cy = 0;
		double area = area(nodes);
		// could change this to Point2D.Float if you want to use less memory
		Point2D res = new Point2D.Double();
		int i, j, n = nodes.length;

		double factor = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			factor = (nodes[i].getX() * nodes[j].getY()
					- nodes[j].getX() * nodes[i].getY());
			cx += (nodes[i].getX() + nodes[j].getX()) * factor;
			cy += (nodes[i].getY() + nodes[j].getY()) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		res.setLocation(cx, cy);
		return res;
	}
	
	public static Point2D centerOfMass(Polygon polygon) {
		double cx = 0, cy = 0;
		double area = area(polygon);
		// could change this to Point2D.Float if you want to use less memory
		Point2D res = new Point2D.Double();
		int i, j, n = polygon.npoints;

		double factor = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			factor = (polygon.xpoints[i] * polygon.ypoints[j]
					- polygon.xpoints[j] * polygon.ypoints[i]);
			cx += (polygon.xpoints[i] + polygon.xpoints[j]) * factor;
			cy += (polygon.ypoints[i] + polygon.ypoints[j]) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		res.setLocation(cx, cy);
		return res;
	}
	
	public static double radii(Polygon p, double cx, double cy) {
		Line2D edge = new Line2D.Double();
		int n = p.npoints;
		int k = 0;
		double d = 0;
		double size = Double.POSITIVE_INFINITY;
		for(int j = 0; j < n; ++j) {
			k = (j + 1) % n;
			edge.setLine(p.xpoints[j], p.ypoints[j], p.xpoints[k], p.ypoints[k]);
			d = Math.abs(edge.ptSegDist(cx, cy));
			if(Double.isNaN(d)) {
				continue;
			}
			size = Math.min(d, size);
		}
		return size;
	}
	
	public static double radii(Point2D[] p, double cx, double cy) {
		Line2D edge = new Line2D.Double();
		int n = p.length;
		int k = 0;
		double d = 0;
		double size = Double.POSITIVE_INFINITY;
		for(int j = 0; j < n; ++j) {
			k = (j + 1) % n;
			edge.setLine(p[j].getX(), p[j].getY(), p[k].getX(), p[k].getY());
			d = Math.abs(edge.ptSegDist(cx, cy));
			if(Double.isNaN(d)) {
				continue;
			}
			size = Math.min(d, size);
		}
		return size;
	}
	
	public static Point2D[] getOutline(Shape s, float fitness) {
		List<Point2D> polygon = new ArrayList<Point2D>(); 
		PathIterator it = s.getPathIterator(new AffineTransform(), fitness);
		final float[] coords = new float[6];
		while (!it.isDone()) {
			final int type = it.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				polygon.add(new Point2D.Double(coords[0], coords[1]));
				break;
			case PathIterator.SEG_LINETO:
				polygon.add(new Point2D.Double(coords[0], coords[1]));
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
			it.next();
		}
		return polygon.toArray(new Point2D[0]);
	}
	
	public static Shape getPolygon(Point2D[] polygon) {
		Point2D start = polygon[0];
		GeneralPath path = new GeneralPath();
		path.moveTo(start.getX(), start.getY());
		for (int i = 1; i < polygon.length; ++i) {
			Point2D pt = polygon[i];
			path.lineTo(pt.getX(), pt.getY());
		}
		path.lineTo(start.getX(), start.getY());
		return path;
	}

	public static Point2D[] getPolygon(Shape s, float fit) {
		List<Point2D> array = new ArrayList<Point2D>();
		PathIterator it = s.getPathIterator(new AffineTransform(), fit);
		final float[] coords = new float[6];
		while (!it.isDone()) {
			final int type = it.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				array.add(new Point2D.Double(coords[0], coords[1]));
				break;
			case PathIterator.SEG_LINETO:
				array.add(new Point2D.Double(coords[0], coords[1]));
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
			it.next();
		}
		return array.toArray(new Point2D[0]);
	}
}
