/**
 * GraphBrowser V1.0 : A visualization for large graphs.
 * 
 * @author NanCao  (all rights reserved.)
 * nancao@cse.ust.hk 
 * http://www.cse.ust.hk/~nancao/
 */

package vis.vjit.tweeflow.layout;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import davinci.data.PropHelper;
import davinci.data.elem.IEdge;
import davinci.data.elem.IVisualNode;
import davinci.data.graph.IGraph;

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
public class MDSLayout {

	private Random m_rand = null;

	private double m_stabParam;

	private double m_mdsTol;

	private double m_equationTol;

	// /*
	// * add for invisible edges.
	// */
	// private double m_phantomWeight = 10;

	/*
	 * Simply invisible edge weight.
	 */
	private double invisibleDist = 3;
	
	private static MDSLayout m_instance = null;

	private MDSLayout() {
		m_rand = new Random(2);
		m_stabParam = 0.5;
		m_mdsTol = 1e-5;
		m_equationTol = 1e-7;
	}
	
	public static MDSLayout getInstance() {
		if(m_instance == null) {
			m_instance = new MDSLayout();
		}
		return m_instance;
	}
	
	public void setStableParam(double ratio) {
		m_stabParam = ratio;
	}
	
	public double getStableParam() {
		return this.m_stabParam;
	}

	/**
	 * 
	 * @param graph
	 *            the snapshot
	 * @param ref
	 *            the reference position
	 * @param dep
	 *            the dependent position
	 * @return
	 */
	public Map<String, Point2D> layout(IGraph graph, Map<String, Point2D> ref, Map<String, Point2D> dep) {
		
		int ncnt = graph.getNodeCount();
		if(ncnt == 1) {
			Map<String, Point2D> coord = new HashMap<String, Point2D>();
			IVisualNode node = (IVisualNode)graph.getNode(0);
			coord.put(node.getID(), new Point2D.Double(0, 0));
			return coord;
		}

		// set depend coordinate.
		double[] dx = new double[ncnt];
		double[] dy = new double[ncnt];
		boolean[] bstab = new boolean[ncnt];
		if(null != dep) setDepend(graph, dx, dy, bstab, dep);

		// set reference coordinate (beginning point of iteration)
		double[] x = new double[ncnt];
		double[] y = new double[ncnt];
		setRef(graph, x, y, ref);

		/***********************************************************************
		 * layout the graph using modified kamada-kawai layout
		 **********************************************************************/
		Map<String, Integer> nodeToIndex = new HashMap<String, Integer>();
		for (int i = 0; i < ncnt; i++) {
			String id = PropHelper.getID(graph.getNode(i));
			nodeToIndex.put(id, i);
		}

		// special case : one node.
		if (1 == ncnt) {
			x[0] = 0;
			y[0] = 0;
			return transit(graph, x, y);
		}

		double[][] adjMat = getAdjMat(graph, nodeToIndex);

		boolean[][] isVirtual = new boolean[ncnt][ncnt];
		double[][] idealMat = getFullDistMatrix(graph, adjMat, isVirtual);
		double[][] weightMat = new double[ncnt][ncnt];
		double[][] deltaMat = new double[ncnt][ncnt];
		setWeightAndDelta(idealMat, weightMat, deltaMat, isVirtual);
		double[][] laplacian = getLaplacian(weightMat);

		// add stability to the laplacian matrix.
		for (int i = 0; i < ncnt; i++) {
			for (int j = 0; j < ncnt; j++) {
				laplacian[i][j] *= (1 - m_stabParam);
			}
			if (bstab[i]) {
				laplacian[i][i] += m_stabParam;
			}
		}

		double[][] lz = getLz(deltaMat, x, y);

		double energy = calculateEnergy(idealMat, weightMat, x, y);
		double ratio = 0;
		int cnt = 0;
		if (energy >= 1e-5) {
			while (energy >= 1e-12 && Math.abs(ratio - 1) > m_mdsTol
					&& cnt++ < 500) {
				// stabilize right hand.
				double[] loadX = stabilizeLoad(lz, x, dx, bstab);
				double[] loadY = stabilizeLoad(lz, y, dy, bstab);

				x = CGSolver(laplacian, loadX, m_equationTol);
				y = CGSolver(laplacian, loadY, m_equationTol);

				double localEnergy = calculateEnergy(idealMat, weightMat, x, y);
				ratio = localEnergy / energy;
				energy = localEnergy;

				lz = getLz(deltaMat, x, y);
			}
		}

		/*
		 * Keep the radix in the same position for eliminating the meaingless
		 * camera movement.
		 */
		return transit(graph, x, y);
	}
	


	/**
	 * Setting the res.
	 * 
	 * @param graph
	 * @param res
	 * @param x
	 * @param y
	 */
	private void setResult(IGraph<IVisualNode> graph, Map<String, Point2D> res, double[] x, double[] y) {
		/*
		 * Setting the result.
		 */
		for (int i = 0; i < x.length; i++) {
			String id = graph.getNode(i).getID();
			Point2D p = new Point2D.Double(x[i], y[i]);
			res.put(id, p);
		}
	}

	/**
	 * Conjugate gradient method to solve the equation MatX = vector. It is the
	 * numeric method, we should assign the toleration which is the terminate
	 * condition of the program.
	 * 
	 * @param mat
	 * @param vec
	 * @param tol
	 * @return
	 */
	private double[] CGSolver(double[][] mat, double[] vec, final double tol) {

		double control;
		int cnt = 0;
		double nuo = 0, last_nuo = 0;
		double[] X = new double[vec.length];

		int len = X.length;

		double[] res = new double[len];

		double[] p = new double[len];

		double[] mat2vec = new double[len];

		for (int i = 0; i < len; i++) {
			res[i] = vec[i];
		}

		do {
			if (++cnt == 1) {
				nuo = 0;
				for (int i = 0; i < len; i++) {
					nuo += res[i] * res[i];
					p[i] = res[i];
				}
				if (Math.sqrt(nuo) < 1e-17) {
					break;
				}
			} else {
				double ratio = nuo / last_nuo;
				for (int i = 0; i < len; i++) {
					p[i] = res[i] + p[i] * ratio;
				}
			}

			mat2vec = MatrixMultiplyVector(mat, p);

			double temp = 0;
			for (int i = 0; i < len; i++) {
				temp += mat2vec[i] * p[i];
			}
			temp = nuo / temp;

			for (int i = 0; i < len; i++) {
				X[i] += temp * p[i];
				res[i] -= temp * mat2vec[i];
			}

			last_nuo = nuo;

			nuo = 0;
			for (int i = 0; i < len; i++) {
				nuo += res[i] * res[i];
			}

			control = Math.sqrt(nuo);
		} while (control > tol && cnt < 1000);

		return X;
	}

	private double[] MatrixMultiplyVector(double[][] mat, double[] vec) {
		double[] result = new double[mat.length];

		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				result[i] += mat[i][j] * vec[j];
			}
		}

		return result;
	}

	private double[] stabilizeLoad(double[][] lz, double[] x, double[] stabX,
			boolean[] b_stab) {
		int len = x.length;
		double[] load = new double[len];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				load[i] += lz[i][j] * x[j];
			}
			if (b_stab[i]) {
				load[i] = load[i] * (1 - m_stabParam) + stabX[i] * m_stabParam;
			} else {
				load[i] = load[i] * (1 - m_stabParam);
			}
		}
		return load;
	}

	private double calculateEnergy(double[][] idealMat, double[][] weightMat, double[] x, double[] y) {
		double energy = 0;
		int len = x.length;
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				double dist = Math.sqrt((x[i] - x[j]) * (x[i] - x[j])
						+ (y[i] - y[j]) * (y[i] - y[j]));
				energy += (dist - idealMat[i][j]) * (dist - idealMat[i][j])
						* weightMat[i][j];
			}
		}
		return energy;
	}

	private double[][] getLz(double[][] deltaMat, double[] x, double[] y) {
		int len = x.length;
		double[][] lz = new double[len][];
		for (int i = 0; i < len; i++) {
			lz[i] = new double[len];
			for (int j = 0; j < i; j++) {
				double dist = (x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
						* (y[i] - y[j]);
				double w = deltaMat[i][j] / Math.sqrt(dist);
				lz[i][j] = -w;
				lz[j][i] = -w;
				lz[i][i] += w;
				lz[j][j] += w;
			}
		}
		return lz;
	}

	private double[][] getLaplacian(double[][] weightMat) {
		int len = weightMat.length;
		double[][] laplacian = new double[len][];

		for (int i = 0; i < len; i++) {
			laplacian[i] = new double[len];
			for (int j = 0; j < i; j++) {
				double w = weightMat[i][j];
				laplacian[i][j] = -w;
				laplacian[j][i] = -w;
				laplacian[i][i] += w;
				laplacian[j][j] += w;
			}
		}
		return laplacian;
	}

	/**
	 * set dependent position of graph, build radix simultaneously.
	 * 
	 * @param graph
	 * @param dx
	 * @param dy
	 * @param bstab
	 * @param depend
	 * @return
	 */
	private void setDepend(IGraph<IVisualNode> graph, double[] dx, double[] dy,
			boolean[] bstab, Map<String, Point2D> depend) {
			double rx = 0, ry = 0;
			for (int i = 0; i < dx.length; i++) {
				String id = graph.getNode(i).getID();
				Point2D p = depend.get(id);
				if (null == p) {
					continue;
				} else {
					dx[i] = p.getX() - rx;
					dy[i] = p.getY() - ry;
					bstab[i] = true;
				}
			}
	}

	/**
	 * Set reference position of graph(Initial value of graph).
	 * 
	 * @param graph
	 * @param x
	 * @param y
	 * @param ref
	 * @param radix
	 */
	private void setRef(IGraph<IVisualNode> graph, double[] x, double[] y, Map<String, Point2D> ref) {
		double rx = 0, ry = 0;
		if (null == ref || ref.isEmpty()) {
			rx = m_rand.nextDouble();
			ry = m_rand.nextDouble();
			for (int i = 0; i < x.length; i++) {
				do  {
					x[i] = m_rand.nextDouble() - rx;
					y[i] = m_rand.nextDouble() - ry;
				} while(x[i] == 0D && y[i] == 0D);
			}
		} else {
			String id = "";
			rx = m_rand.nextDouble();
			ry = m_rand.nextDouble();
			for (int i = 0; i < x.length; i++) {
				id = graph.getNode(i).getID();
				Point2D p = (Point2D) ref.get(id);
				if (null == p) {
					do {
						x[i] = m_rand.nextDouble() - rx;
						y[i] = m_rand.nextDouble() - ry;
					} while (x[i] == 0D && y[i] == 0D);
				} else {
					x[i] = p.getX() - rx;
					y[i] = p.getY() - ry;
					while(x[i] == 0D && y[i] == 0D) {
						x[i] = m_rand.nextDouble() - rx;
						y[i] = m_rand.nextDouble() - ry;
					}
				}
			}
		}
	}

	private Map<String, Point2D> transit(IGraph<IVisualNode> graph, double[] x, double[] y) {
		Map<String, Point2D> map = new HashMap<String, Point2D>();
		setResult(graph, map, x, y);
		return map;
	}

	/**
	 * set the weight by idealMat^-2.
	 * 
	 * @param idealMat
	 * @param weightMat
	 * @param deltaMat
	 * @param isVirtual
	 */
	private void setWeightAndDelta(final double[][] idealMat,
			double[][] weightMat, double[][] deltaMat, boolean[][] isVirtual) {
		int len = idealMat.length;

		for (int i = 0; i < len; i++) {
			for (int j = 0; j < i; j++) {
				/*
				 * use the default value weight = 1/(ideal)^2
				 */
				weightMat[i][j] = 1 / (idealMat[i][j] * idealMat[i][j]);
				if (isVirtual[i][j]) {
					weightMat[i][j] /= 5;
				}

				deltaMat[i][j] = weightMat[i][j] * idealMat[i][j];

				weightMat[j][i] = weightMat[i][j];
				deltaMat[j][i] = deltaMat[i][j];
			}
		}
	}

	/**
	 * Specialize for add invisible edges.
	 * 
	 * @param graph
	 * @param nodeToIndex
	 * @return
	 */
	private double[][] getAdjMat(IGraph<IVisualNode> graph, Map<String, Integer> nodeToIndex) {
		int nodeNum = graph.getNodeCount();
		double[][] res = new double[nodeNum][nodeNum];

//		Object n1 = null, n2 = null;
		for (int i = 0; i < nodeNum; i++) {
			IVisualNode node = graph.getNode(i);
			IEdge[] fromEdges = graph.getEdgesFromSecondNode(node);
			for (IEdge<IVisualNode> curEdge : fromEdges) {
				int p = (nodeToIndex.get(curEdge.getFirstNode().getID())).intValue();
				int q = (nodeToIndex.get(curEdge.getSecondNode().getID())).intValue();
				double w = 1;// (Double)curEdge.getProperty(IWeight.PROP_WEIGHT);
//				if (null != curEdge.getProperty("ewei")) {
//					w = (Double) curEdge.getProperty("ewei");
//				}
//				n1 = curEdge.getFirstNode();
//				n2 = curEdge.getSecondNode();
//				w = 1 + 5 * 2.0 / (graph.getDegrees(n1) + graph.getDegrees(n2));
				
				res[p][q] = w;
				res[q][p] = w;

				res[p][p] += w;
				res[q][q] += w;
			}
		}

		return res;
	}

	/**
	 * Modified for invisible edges.
	 * 
	 * @param isVirtual
	 */
	public double[][] getFullDistMatrix(IGraph<IVisualNode> graph, double[][] adjmat,
			boolean[][] isVirtual) {
		int nodeNum = adjmat.length;
		double[][] dist = new double[nodeNum][];
		for (int i = 0; i < nodeNum; i++) {
			dist[i] = new double[nodeNum];
			for (int j = 0; j < i; j++) {
				if (!Double.valueOf(adjmat[i][j]).equals(0.0)) {
					/**
					 * modify the relation between adjacent matrix and distance
					 * matrix here.
					 */
					// dist[i][j] = 1 / (adjmat[i][j] * adjmat[i][j]);
					// dist[i][j] = adjmat[i][j];
					dist[i][j] = 1 / adjmat[i][j];
				} else {
					dist[i][j] = Double.POSITIVE_INFINITY;
				}
				dist[j][i] = dist[i][j];
			}
			dist[i][i] = Double.POSITIVE_INFINITY;
		}

		for (int k = 0; k < nodeNum; k++) {
			for (int i = 0; i < nodeNum; i++) {
				for (int j = 0; j < nodeNum; j++) {
					double minDist;
					if (dist[i][k] == Double.POSITIVE_INFINITY
							|| dist[k][j] == Double.POSITIVE_INFINITY) {
						continue;
					} else {
						minDist = (dist[i][j] < dist[i][k] + dist[k][j]) ? dist[i][j]
								: dist[i][k] + dist[k][j];
					}
					dist[i][j] = minDist;
				}
			}
		}

		/***********************************************************************
		 * modified for add the invisible edges.
		 **********************************************************************/
		for (int i = 0; i < nodeNum; i++) {
			for (int j = 0; j < nodeNum; j++) {
				if (j == i)
					continue;
				if (Double.POSITIVE_INFINITY == dist[i][j]) {
					dist[i][j] = invisibleDist;
					// String fromNodeId = PropOperator.getInstance().getID(
					// graph.getNode(i));
					// String toNodeId = PropOperator.getInstance().getID(
					// graph.getNode(j));
					// int fromIndex = (Integer) superIndex.get(fromNodeId);
					// int toIndex = (Integer) superIndex.get(toNodeId);
					//					
					// dist[i][j] = m_superdist[fromIndex][toIndex]
					// * m_phantomWeight;
					// isVirtual[i][j] = true;
				}
			}

		}
		return dist;
	}
	
	public void reset() {
		m_rand = new Random(System.currentTimeMillis());
	}
}