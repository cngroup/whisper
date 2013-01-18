package vis.vjit.tweeflow.voronoi;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Triangulation extends AbstractSet<Triangle> {

	private Triangle mostRecent = null; // Most recently "active" triangle
	private Graph<Triangle> triGraph; // Holds triangles for navigation
	private List<Pnt> sites = null;
	private Triangle initialTriangle = null;
	
	private Shape boundary = null;
	private Area outline = null;
	private double weight = 0;
	private double m_tol = 0;
	
	public boolean debug = false;
	
	private class IdxItem {
		public double area = 0;
		public Pnt center = null;
	}

	/**
	 * All sites must fall within the initial triangle.
	 * 
	 * @param triangle
	 *            the initial triangle
	 */
	public Triangulation(Shape boundary, double weight) {
		this(boundary, weight, 0.01);
	}
	
	public Triangulation(Shape boundary, double weight, double tol) {
		sites = new ArrayList<Pnt>();
		setBoundary(boundary);
		triGraph = new Graph<Triangle>();
		triGraph.add(initialTriangle);
		mostRecent = initialTriangle;
		this.weight = weight;
		m_tol = tol;
	}
	
	public void setTol(double tol) {
		m_tol = tol;
	}
	
	public double getTol() {
		return m_tol;
	}

	public void setBoundary(Shape bounds) {
		this.boundary = bounds;
		this.outline = new Area(bounds);

		Rectangle2D rect = bounds.getBounds2D();
		double cx = rect.getCenterX();
		double cy = rect.getCenterY();
		double ww = rect.getWidth() * 2;
		double hh = rect.getHeight() * 2;
		
//		if(ww == 0 || hh == 0) {
//			return;
//		}

		double x1 = cx;
		double y1 = cy - (hh + Math.tan(Math.PI / 3) * ww) / 2;

		double x2 = cx - (ww / 2 + ww * Math.tan(Math.PI / 6));
		double y2 = cy + hh / 2;

		double x3 = cx + (ww / 2 + ww * Math.tan(Math.PI / 6));
		double y3 = cy + hh / 2;
		
//		System.err.println(x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + x3 + ", " + y3);
//		System.err.println(cx + ", " + cy + ", " + ww + ", " + hh);
//		System.out.println("x1 = " + x1 + ", y1 = " + y1 + ", x2 = " + x2 + ", y2 = " + y2 + ", x3 = " + x3 + ", y3 = " + y3);
		
		initialTriangle = new Triangle(new Pnt(x1, y1), new Pnt(x2, y2), new Pnt(x3, y3));
		if (null != sites && !sites.isEmpty()) {
			triGraph.clear();
			triGraph.add(initialTriangle);
			for (Pnt pt : sites) {
				Triangle triangle = locate(pt);
				// Give up if no containing triangle or if site is already in DT
				if (triangle == null)
					throw new IllegalArgumentException("No containing triangle");
				if (triangle.contains(pt)) {
					continue;
				}
				// Determine the cavity and update the triangulation
				Set<Triangle> cavity = getCavity(pt, triangle);
				mostRecent = update(pt, cavity);
			}
		}
	}

	public Shape getBoundary() {
		return boundary;
	}

	public Triangle getVirtialBundary() {
		return initialTriangle;
	}

	public Pnt[] getSites() {
		return sites.toArray(new Pnt[0]);
	}

	/* The following two methods are required by AbstractSet */
	@Override
	public Iterator<Triangle> iterator() {
		return triGraph.nodeSet().iterator();
	}

	@Override
	public int size() {
		return triGraph.nodeSet().size();
	}

	@Override
	public String toString() {
		return "Triangulation with " + size() + " triangles";
	}

	/**
	 * True iff triangle is a member of this triangulation. This method isn't
	 * required by AbstractSet, but it improves efficiency.
	 * 
	 * @param triangle
	 *            the object to check for membership
	 */
	public boolean contains(Object triangle) {
		return triGraph.nodeSet().contains(triangle);
	}

	/**
	 * Report neighbor opposite the given vertex of triangle.
	 * 
	 * @param site
	 *            a vertex of triangle
	 * @param triangle
	 *            we want the neighbor of this triangle
	 * @return the neighbor opposite site in triangle; null if none
	 * @throws IllegalArgumentException
	 *             if site is not in this triangle
	 */
	public Triangle neighborOpposite(Pnt site, Triangle triangle) {
		if (!triangle.contains(site))
			throw new IllegalArgumentException("Bad vertex; not in triangle");
		for (Triangle neighbor : triGraph.neighbors(triangle)) {
			if (!neighbor.contains(site))
				return neighbor;
		}
		return null;
	}

	/**
	 * Return the set of triangles adjacent to triangle.
	 * 
	 * @param triangle
	 *            the triangle to check
	 * @return the neighbors of triangle
	 */
	public Set<Triangle> neighbors(Triangle triangle) {
		return triGraph.neighbors(triangle);
	}

	/**
	 * Report triangles surrounding site in order (cw or ccw).
	 * 
	 * @param site
	 *            we want the surrounding triangles for this site
	 * @param triangle
	 *            a "starting" triangle that has site as a vertex
	 * @return all triangles surrounding site in order (cw or ccw)
	 * @throws IllegalArgumentException
	 *             if site is not in triangle
	 */
	public List<Triangle> surroundingTriangles(Pnt site, Triangle triangle) {
		if (!triangle.contains(site))
			throw new IllegalArgumentException("Site not in triangle");
		List<Triangle> list = new ArrayList<Triangle>();
		Triangle start = triangle;
		Pnt guide = triangle.getVertexButNot(site); // Affects cw or ccw
		while (true) {
			list.add(triangle);
			Triangle previous = triangle;
			triangle = this.neighborOpposite(guide, triangle); // Next triangle
			guide = previous.getVertexButNot(site, guide); // Update guide
			if (triangle == start)
				break;
		}
		return list;
	}

	/**
	 * Locate the triangle with point inside it or on its boundary.
	 * 
	 * @param point
	 *            the point to locate
	 * @return the triangle that holds point; null if no such triangle
	 */
	public Triangle locate(Pnt point) {
		Triangle triangle = mostRecent;
		if (!this.contains(triangle))
			triangle = null;

		// Try a directed walk (this works fine in 2D, but can fail in 3D)
		Set<Triangle> visited = new HashSet<Triangle>();
		while (triangle != null) {
			if (visited.contains(triangle)) { // This should never happen
				System.out.println("Warning: Caught in a locate loop");
				break;
			}
			visited.add(triangle);
			// Corner opposite point
			Pnt corner = point.isOutside(triangle.toArray(new Pnt[0]));
			if (corner == null)
				return triangle;
			triangle = this.neighborOpposite(corner, triangle);
		}
		// No luck; try brute force
		// System.out.println("Warning: Checking all triangles for " + point);
		for (Triangle tri : this) {
			if (point.isOutside(tri.toArray(new Pnt[0])) == null)
				return tri;
		}
		// No such triangle
		// System.out.println("Warning: No triangle holds " + point);
		return null;
	}

	/**
	 * Place a new site into the DT. Nothing happens if the site matches an
	 * existing DT vertex.
	 * 
	 * @param site
	 *            the new Pnt
	 * @throws IllegalArgumentException
	 *             if site does not lie in any triangle
	 */
	public boolean addSite(Pnt site) {
		// Uses straightforward scheme rather than best asymptotic time
		if (!boundary.contains(site.coord(0), site.coord(1))) {
			return false;
		}

		// Locate containing triangle
		Triangle triangle = locate(site);
		// Give up if no containing triangle or if site is already in DT
		if (triangle == null)
			throw new IllegalArgumentException("No containing triangle");
		if (triangle.contains(site))
			return false;

		sites.add(site);

		// Determine the cavity and update the triangulation
		Set<Triangle> cavity = getCavity(site, triangle);
		mostRecent = update(site, cavity);
		return true;
	}

	public void removeSite(Pnt site) {
		if (!sites.remove(site)) {
			return;
		}
		triGraph.clear();
		triGraph.add(initialTriangle);
		for (Pnt pt : sites) {
			Triangle triangle = locate(pt);
			// Give up if no containing triangle or if site is already in DT
			if (triangle == null)
				throw new IllegalArgumentException("No containing triangle");
			if (triangle.contains(pt)) {
				continue;
			}
			// Determine the cavity and update the triangulation
			Set<Triangle> cavity = getCavity(pt, triangle);
			mostRecent = update(pt, cavity);
		}
	}

	/**
	 * Determine the cavity caused by site.
	 * 
	 * @param site
	 *            the site causing the cavity
	 * @param triangle
	 *            the triangle containing site
	 * @return set of all triangles that have site in their circumcircle
	 */
	private Set<Triangle> getCavity(Pnt site, Triangle triangle) {
		Set<Triangle> encroached = new HashSet<Triangle>();
		Queue<Triangle> toBeChecked = new LinkedList<Triangle>();
		Set<Triangle> marked = new HashSet<Triangle>();
		toBeChecked.add(triangle);
		marked.add(triangle);
		while (!toBeChecked.isEmpty()) {
			triangle = toBeChecked.remove();
			if (site.vsCircumcircle(triangle.toArray(new Pnt[0])) == 1)
				continue; // Site outside triangle => triangle not in cavity
			encroached.add(triangle);
			// Check the neighbors
			for (Triangle neighbor : triGraph.neighbors(triangle)) {
				if (marked.contains(neighbor))
					continue;
				marked.add(neighbor);
				toBeChecked.add(neighbor);
			}
		}
		return encroached;
	}

	/**
	 * Update the triangulation by removing the cavity triangles and then
	 * filling the cavity with new triangles.
	 * 
	 * @param site
	 *            the site that created the cavity
	 * @param cavity
	 *            the triangles with site in their circumcircle
	 * @return one of the new triangles
	 */
	private Triangle update(Pnt site, Set<Triangle> cavity) {
		Set<Set<Pnt>> boundary = new HashSet<Set<Pnt>>();
		Set<Triangle> theTriangles = new HashSet<Triangle>();

		// Find boundary facets and adjacent triangles
		for (Triangle triangle : cavity) {
			theTriangles.addAll(neighbors(triangle));
			for (Pnt vertex : triangle) {
				Set<Pnt> facet = triangle.facetOpposite(vertex);
				if (boundary.contains(facet))
					boundary.remove(facet);
				else
					boundary.add(facet);
			}
		}
		theTriangles.removeAll(cavity); // Adj triangles only

		// Remove the cavity triangles from the triangulation
		for (Triangle triangle : cavity) {
			triGraph.remove(triangle);
		}

		// Build each new triangle and add it to the triangulation
		Set<Triangle> newTriangles = new HashSet<Triangle>();
		for (Set<Pnt> vertices : boundary) {
			vertices.add(site);
			Triangle tri = new Triangle(vertices);
			triGraph.add(tri);
			newTriangles.add(tri);
		}

		// Update the graph links for each new triangle
		theTriangles.addAll(newTriangles); // Adj triangle + new triangles
		for (Triangle triangle : newTriangles)
			for (Triangle other : theTriangles)
				if (triangle.isNeighbor(other))
					triGraph.add(triangle, other);

		// Return one of the new triangles
		return newTriangles.iterator().next();
	}

	//////////////////////////////////////////////////////////////////
	// the following codes are added by nancao to generate a vornoi treemap
	//////////////////////////////////////////////////////////////////
	public void clear() {
		triGraph.clear();
		sites.clear();
		triGraph.add(initialTriangle);
	}

	/**
	 * TODO: CVT with a set of sites with sizes and weights
	 * @author nancao
	 */
	public void optimize(float fitness) {
		double area = PolygonUtil.area(boundary, fitness);
		List<Pnt> newsites = new ArrayList<Pnt>();
		Map<Pnt, Point2D[]> idx = new HashMap<Pnt, Point2D[]>();
		double energy = adjust(newsites, area, weight, fitness, idx);
		double eval = evaluate(area, weight, fitness, idx);
		idx.clear();
		Pnt site = null;
		int iterator = 0;
		while ((energy >= 0.3 || eval > m_tol) && iterator < 1000) {
			this.clear();
			int size = newsites.size();
			for (int i = 0; i < size; ++i) {
				site = newsites.get(i);
				addSite(site);
			}
			eval = evaluate(area, weight, fitness, idx);
			energy = adjust(newsites, area, weight, fitness, idx);
			if(debug) System.out.println("eval = " + eval + ", energy = " + energy);
			iterator++;
			idx.clear();
		}
		this.clear();
		idx.clear();
		int size = newsites.size();
		for (int i = 0; i < size; ++i) {
			site = newsites.get(i);
			addSite(site);
		}
	}

	/*
	 * Calculate the energy for CVT 
	 * @author nancao
	 * @param newsites
	 * @return
	 */
	private double adjust(List<Pnt> newsites, double area, double weight, float fitness, Map<Pnt, Point2D[]> idx) {
		newsites.clear();
		double energy = 0;
		Pnt site = null, center = null;
		Shape s = null;
		Point2D[] pt = null;
		int size = this.sites.size();
		for (int i = 0; i < size; ++i) {
			site = sites.get(i);
			pt = idx.get(site);
			if(pt == null) {
				s = getVoronoiCell(site);
				pt = PolygonUtil.getOutline(s, fitness);
				idx.put(site, pt);
			}
			double darea = area * site.getWeight() / weight;			
			double sarea = getArea(pt);
			double ratio = (darea - sarea) / darea;
			double r = site.getRadii() * (1 + ratio);
			if(r < 1) {
				r = 1;
			}
			if(Math.abs(r - 1) < 1E-3) {
				r = darea * 0.3;
			}
			site.setRadii(r);
			center = getCenter(site, pt);
			energy += Math.sqrt(site.distanceSq(center));
			newsites.add(center);
		}
		
		Pnt s1 = null, s2 = null;
		double dist = 0;
		double ratio = Double.MAX_VALUE;
		double rr = 0;
		size = newsites.size();
		for(int i = 0; i < size; ++i) {
			s1 = newsites.get(i);
//			double r1 = Math.sqrt(s1.getRadii() / Math.PI);
			for(int j = i + 1; j < size; ++j) {
				s2 = newsites.get(j);
				dist = s1.distanceSq(s2);
//				double r2 = Math.sqrt(s2.getRadii() / Math.PI);
				rr = dist / (s1.getRadii() + s2.getRadii());
				ratio = Math.min(rr, ratio);
			}
		}
		if(ratio < 1) {
			if(debug) System.out.println("ration = " + ratio + ", size = " + size);
			double r = 0;
			for(int i = 0; i < size; ++i) {
				s1 = newsites.get(i);
				r = s1.getRadii();
				s1.setRadii(r * ratio);
				if(debug) System.out.println(s1.id + ", " + r + ", " + s1.getRadii());
			}
			if(debug) System.out.println("---------------------------------");
		}
		return energy / sites.size();
	}
	
	// add by nancao
	private double evaluate(double area, double weight, float fitness, Map<Pnt, Point2D[]> idx) {
		double energy = 0;
		Pnt site = null;
		Shape s = null;
		Point2D[] pt = null;
		int size = this.sites.size();
		for (int i = 0; i < size; ++i) {
			site = sites.get(i);
			pt = idx.get(site);
			if(pt == null) {
				s = getVoronoiCell(site);
				pt = PolygonUtil.getPolygon(s, fitness);
				idx.put(site, pt);
			}
			double a = getArea(pt);
			double darea = site.getWeight() * area / weight;
			double delta = darea - a;
			energy += Math.abs(delta);
		}
		return energy / (size * area);
//		return energy / size;
	}
	
	// add by nancao
	public Shape getVoronoiCell(Pnt site) {
		Triangle triangle = locate(site);
		if (triangle == null) {
			return null;
		}
		return getVoronoiCell(site, triangle);
	}

	// add by nancao
	public Shape getVoronoiCell(Pnt site, Triangle triangle) {
		List<Triangle> list = surroundingTriangles(site, triangle);
		List<Pnt> vertices = new ArrayList<Pnt>();
		for (Triangle tri : list) {
			vertices.add(tri.getCircumcenter());
		}
		Shape s = boundary(vertices);
		return s;
	}

	// add by nancao
	public Pnt getCenter(Pnt site, Point2D[] polygon) {
		Point2D center = PolygonUtil.centerOfMass(polygon);
		Pnt pnt = new Pnt(center.getX(), center.getY());
		pnt.setRadii(site.getRadii());
		pnt.setWeight(site.getWeight());
		pnt.id = site.id;
		return pnt;
	}
	
	// add by nancao
	public double getArea(Point2D[] p) {
		return Math.abs(PolygonUtil.area(p));
	}

	// add by nancao
	private Shape boundary(List<Pnt> polygon) {
		Area cell = new Area(getPolygon(polygon));
		cell.intersect(outline);
		return cell;
	}
	
	// add by nancao
	public static int[][] getPolygon(Pnt[] polygon) {
		int[][] p = new int[2][polygon.length];
		for (int i = 0; i < polygon.length; ++i) {
			p[0][i] = (int) polygon[i].coord(0);
			p[1][i] = (int) polygon[i].coord(1);
		}
		return p;
	}

	// add by nancao
	public static Shape getPolygon(List<Pnt> polygon) {
		int size = polygon.size();
		Pnt start = polygon.get(0);
		GeneralPath path = new GeneralPath();
		path.moveTo(start.coord(0), start.coord(1));
		for (int i = 1; i < size; ++i) {
			Pnt pt = polygon.get(i);
			path.lineTo(pt.coord(0), pt.coord(1));
		}
		path.lineTo(start.coord(0), start.coord(1));
		return path;
	}

	// add by nancao
	public static List<Pnt> getPolygon(Shape s, float fit) {
		List<Pnt> array = new ArrayList<Pnt>();
		PathIterator it = s.getPathIterator(new AffineTransform(), fit);
		final float[] coords = new float[6];
		while (!it.isDone()) {
			final int type = it.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				array.add(new Pnt(coords[0], coords[1]));
				break;
			case PathIterator.SEG_LINETO:
				array.add(new Pnt(coords[0], coords[1]));
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
			it.next();
		}
		return array;
	}

	/**
	 * Main program; used for testing.
	 */
	public static void main(String[] args) {
		Triangle tri = new Triangle(new Pnt(-10, 10), new Pnt(10, 10), new Pnt(
				0, -10));
		Rectangle2D rect = new Rectangle2D.Double();
		rect.setFrame(0, 0, 500, 500);
		System.out.println("Triangle created: " + tri);
		Triangulation dt = new Triangulation(rect, 1.0);
		System.out.println("DelaunayTriangulation created: " + dt);
		dt.addSite(new Pnt(0, 0));
		dt.addSite(new Pnt(1, 0));
		dt.addSite(new Pnt(0, 1));
		dt.optimize(1.0f);
		System.out.println("After adding 3 points, we have a " + dt);
		Triangle.moreInfo = true;
		System.out.println("Triangles: " + dt.triGraph.nodeSet());
	}
}