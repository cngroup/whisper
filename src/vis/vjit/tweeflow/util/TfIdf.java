package vis.vjit.tweeflow.util;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class TfIdf {

	private int docCount;
	private Hashtable<String, Double> tfIdfList;
	private Hashtable<String, Integer> docFreq;
	private Hashtable<String, Integer> termFreq;
	private PorterStemmer ps;
	private Set<String> m_terms = null;
	private MyComparator m_comp = new MyComparator();

	public TfIdf() {
		tfIdfList = new Hashtable<String, Double>();
		docFreq = new Hashtable<String, Integer>();
		termFreq = new Hashtable<String, Integer>();
		m_terms = new HashSet<String>();
		ps = PorterStemmer.getInstance();
	}
	
	public void setDocCount(int cnt) {
		this.docCount = cnt;
	}
	
	public int getDocCont() {
		return this.docCount;
	}
	
	public double similarity(String s1, String s2, boolean btfidf) {
		
		s1 = s1.replaceAll("[;.\\,#\\(\\)]", " ");
		s2 = s2.replaceAll("[;.\\,#\\(\\)]", " ");
		
		Set<String> terms = new HashSet<String>();
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		String term = "";
		String[] t1 = s1.split("\\s+");
		String[] t2 = s2.split("\\s+");
		for(int i = 0; i < t1.length; ++i) {
			/* Skip stop words (stemming is done inside StopWords module) */
			if (StopWords.isStopWord(t1[i])) {
				continue;
			}
			
			/* Get the stemming form of this word */
			term = ps.stem(t1[i]);
			if (StopWords.isStopWord(term)) {
				continue;
			}
			terms.add(term);
			set1.add(term);
		}
		for(int i = 0; i < t2.length; ++i) {
			/* Skip stop words (stemming is done inside StopWords module) */
			if (StopWords.isStopWord(t2[i])) {
				continue;
			}
			
			/* Get the stemming form of this word */
			term = ps.stem(t2[i]);
			if (StopWords.isStopWord(term)) {
				continue;
			}
			terms.add(term);
			set2.add(term);
		}
		
		String[] vector = terms.toArray(new String[]{});
		double dd = 0;
		double xx1 = 0, xx2 = 0;
		for(int i = 0; i < vector.length; ++i) {
			double x1 = 0; 
			double x2 = 0; 
			if(btfidf) {
				x1 = set1.contains(vector[i]) ? getTfIdf(vector[i]) : 0;
				x2 = set2.contains(vector[i]) ? getTfIdf(vector[i]) : 0;
			} else {
				x1 = set1.contains(vector[i]) ? 1 : 0;
				x2 = set2.contains(vector[i]) ? 1 : 0;
			}
			dd += x1 * x2;
			xx1 += x1 * x1;
			xx2 += x2 * x2;
		}
		// cosine similarity
		return dd / (Math.sqrt(xx1) * Math.sqrt(xx2));
	}

	public void processFL(String fpath) {
		File file = new File(fpath);
		String[] files = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return false;
			}
		});
		
		if(null == files || 0 == files.length) {
			return;
		}
		reset();
		docCount = files.length;
		
		BufferedReader in = null;
		
		for (int i = 1; i <= docCount; i++) {
			String docName = fpath + "/" + files[i];
			try {
				in = new BufferedReader(new FileReader(docName));
				String nextLine;
				while ((nextLine = in.readLine()) != null) {
					nextLine = nextLine.trim().toLowerCase();
					processLine(nextLine);
				}
				in.close();
			} catch (FileNotFoundException ex) {
				System.err.println("Doc file " + docName + " not found!");
			} catch (IOException ex) {
				System.err.println("Exception in reading file " + docName);
			} finally {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		calculate();
	}
	
	public void professDB(Connection con, String query, String[] args) throws SQLException {
		
		if(con == null) {
			return;
		}
		if(query == null || "".equals(query)) {
			return;
		}
		PreparedStatement ps_query = con.prepareStatement(query);
		if(args != null) {
			for(int i = 0; i < args.length; ++i) {
				ps_query.setString(i + 1, args[i]);
			}
		}
		ResultSet rs = ps_query.executeQuery();
		if(null == rs || !rs.next()) {
			return;
		}
		
		reset();
		
		String text = "";
		
		do {
			// title
			text = rs.getString(1).trim().toLowerCase();
			processLine(text);
			
			// abstract
//			text = rs.getString(2).trim().toLowerCase();	
//			processLine(text, m_terms);
			
			docCount++;
		} while(rs.next());
		
		calculate();
	}
	
	public void reset() {
		tfIdfList.clear();
		docFreq.clear();
		termFreq.clear();
		m_terms.clear();
		docCount = 0;
	}

	public double getTfIdf(String word) {
		if (word == null) {
			return 0.0;
		}
		word = ps.stem(word);
		Double value = (Double) tfIdfList.get(word);
		if (value != null) {
			return value.doubleValue();
		}
		return 1.0 / docCount * Math.log(docCount);
	}
	
	public double getTf(String word) {
		return ((Integer) termFreq.get(word)).doubleValue() / m_terms.size();
	}
	
	public String[] getTerms(boolean btfidf) {
		String[] terms = (String[])m_terms.toArray(new String[]{});
		m_comp.setTfIdfOrder(btfidf);
		Arrays.sort(terms, m_comp);
		return terms;
	}

	public void test() {
		Set words = termFreq.keySet();
		Iterator itr = words.iterator();
		while (itr.hasNext()) {
			String word = (String) itr.next();
			int tf = ((Integer) termFreq.get(word)).intValue();
			int df = ((Integer) docFreq.get(word)).intValue();
			double tfIdf = ((Double) tfIdfList.get(word)).doubleValue();
			System.out.println(word + "\t\t" + tf + "\t" + df + "\t" + tfIdf);
		}
	}
	
	public void processLine(String line) {
		if (line == null || m_terms == null) {
			return;
		}
		line = line.replaceAll("[;.\\,#\\(\\)]", " ");
		String[] words = line.trim().split("\\s+");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			
			/* Skip stop words (stemming is done inside StopWords module) */
			if (StopWords.isStopWord(word)) {
				continue;
			}
			
			/* Get the stemming form of this word */
			word = ps.stem(word);
			if (StopWords.isStopWord(word)) {
				continue;
			}
						
			/* Increment the term frequency of this word */
			Integer currFreq = (Integer) termFreq.get(word);
			if (currFreq == null) {
				termFreq.put(word, new Integer(1));
			} else {
				termFreq.put(word, new Integer(currFreq.intValue() + 1));
			}
			/* Increment document frequency only if first seen in this doc */
			if (m_terms.contains(word)) {
				continue;
			}
			m_terms.add(word);
			currFreq = (Integer) docFreq.get(word);
			if (currFreq == null) {
				docFreq.put(word, new Integer(1));
			} else {
				docFreq.put(word, new Integer(currFreq.intValue() + 1));
			}
		}
	}

	public void calculate() {
		Set words = termFreq.keySet();
		Iterator itr = words.iterator();
		while (itr.hasNext()) {
			String word = (String) itr.next();
			double tf = ((Integer) termFreq.get(word)).intValue();
			tf = 1.0 * tf / docCount;
			Integer df = (Integer) docFreq.get(word);
			if (df == null) {
				System.err.println("Word " + word
						+ " not found in docFreq list!");
				continue;
			}
			double idf = Math.log(1.0 * docCount / df.intValue());
			tfIdfList.put(word, new Double(tf * idf));
		}
	}
	
	private class MyComparator implements Comparator {
		
		private boolean tfidforder = true;
		public void setTfIdfOrder(boolean b) {
			tfidforder = b;
		}
		
		public int compare(Object t1, Object t2) {
			double v1 = tfidforder ? getTfIdf((String)t1) : getTf((String)t1);
			double v2 = tfidforder ? getTfIdf((String)t2) : getTf((String)t2);
			if(v1 > v2) {
				return -1;
			} else if(v1 < v2){
				return 1;
			}
			return 0;
		}
	}
	
	public static void main(String[] args) {
		String test = "1, 2, 3, 4, 5";
		String[] tokens = test.split("\\,\\s*");
		for(int i = 0; i < tokens.length; ++i) {
			System.out.println(tokens[i]);
		}
	}
}
